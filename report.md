# Qwen Code Analysis Report

This report details the inner workings of the `qwen-code` command-line tool, providing a comprehensive overview of its architecture, tool usage, API interactions, and other mechanisms. The findings are intended to assist in the development of a similar AI coding assistant for Android.

## 1. High-Level Architecture

The `qwen-code` tool is a Node.js application built on a modular architecture, forked from `gemini-cli`. It is composed of two primary packages:

*   **`packages/cli`**: The frontend of the application. It is responsible for rendering the user interface in the terminal, handling user input, managing display themes, and presenting the final output.
*   **`packages/core`**: The backend or "engine" of the application. It orchestrates the entire workflow, including managing the conversation with the AI model, defining and executing tools, and handling the application's state.

### Interaction Flow

The typical flow of a user interaction is as follows:

1.  **User Input**: The user enters a prompt in the CLI.
2.  **Request to Core**: The `cli` package sends the user's prompt to the `core` package.
3.  **Prompt Construction**: The `core` constructs a detailed prompt for the AI model. This includes the user's message, the conversation history, and a list of all available tools (with their names, descriptions, and parameter schemas).
4.  **API Call**: The `core` sends the request to the Qwen model via an OpenAI-compatible API.
5.  **Model Response**: The model processes the prompt and returns a response. This can be:
    *   A direct text answer.
    *   A request to execute one or more tools (`FunctionCall`).
6.  **Tool Execution**:
    *   If the model requests a tool, the `CoreToolScheduler` in the `core` package takes over.
    *   For tools that can modify the system (e.g., writing files, running shell commands), the user is prompted for confirmation, often with a `diff` preview of the changes.
    *   The `core` executes the tool and captures its output.
7.  **Return to Model**: The result of the tool execution is formatted into a `functionResponse` and sent back to the model in a new API call.
8.  **Final Answer**: The model uses the tool's output to generate a final, user-facing answer.
9.  **Display**: The `core` sends the final answer back to the `cli`, which displays it to the user.

This entire process, from user input to final display, is managed within a `Turn`. The model can request multiple tools in a single turn, and the system will execute them in parallel.

## 2. The Qwen Model and API

`qwen-code` uses an OpenAI-compatible API to communicate with the Qwen model. This is handled by the `OpenAIContentGenerator` class.

### API Configuration

The connection is configured via environment variables:

*   `OPENAI_API_KEY`: Your API key.
*   `OPENAI_BASE_URL`: The base URL for the API (e.g., `https://dashscope.aliyuncs.com/compatible-mode/v1`).
*   `OPENAI_MODEL`: The specific model to use (e.g., `qwen3-coder-plus`).

### Request/Response Format

The application internally uses a format similar to the Google Gemini API but translates it to the OpenAI format before sending requests.

**Request (to OpenAI-compatible API):**

A `messages` array is constructed, containing objects with `role` and `content`.

*   `role`: Can be `system`, `user`, `assistant`, or `tool`.
*   `content`: The text of the message.
*   `tool_calls`: If the assistant is requesting a tool, this array will be present.
*   `tool_call_id`: For `tool` role messages, this links the response to a specific `tool_calls` request.

**Example `messages` array:**

```json
[
  {
    "role": "system",
    "content": "You are a helpful assistant with access to tools..."
  },
  {
    "role": "user",
    "content": "What files are in the src directory?"
  },
  {
    "role": "assistant",
    "content": null,
    "tool_calls": [
      {
        "id": "call_123",
        "type": "function",
        "function": {
          "name": "list_directory",
          "arguments": "{\"path\":\"/path/to/project/src\"}"
        }
      }
    ]
  },
  {
    "role": "tool",
    "tool_call_id": "call_123",
    "content": "Directory listing for /path/to/project/src:\n[DIR] components\nindex.ts"
  }
]
```

## 3. Function Calling and Tools

This is the core of `qwen-code`'s functionality. The Qwen Coder model fully supports function calling (referred to as "tools").

### Tool Definition

A tool is an object that implements the `Tool` interface. The key properties are:

*   `name` (string): The internal name of the tool (e.g., `write_file`).
*   `description` (string): A detailed explanation of what the tool does. This is critical, as the model uses this description to decide when to use the tool.
*   `parameterSchema` (JSON Schema): A schema defining the parameters the tool accepts. The model uses this to construct the correct arguments for the tool call.
*   `execute()` (method): The function that contains the actual logic to be executed. It receives the parameters from the model and returns a `ToolResult`.

### Built-in Tools

The application comes with a rich set of pre-defined tools:

*   **File System**: `list_directory`, `read_file`, `write_file`, `replace` (edit), `glob` (find files), `search_file_content` (grep).
*   **Shell**: `run_shell_command` for executing arbitrary shell commands.
*   **Web**: `web_fetch` and `google_web_search`.
*   **Memory**: `save_memory` to retain information across sessions.

### Custom Tools and Extensibility

**Yes, you can define and use custom tools.** This is the most powerful feature for developers looking to build on this architecture. There are two primary methods:

1.  **Command-based Discovery**: You can configure a `toolDiscoveryCommand` in the settings. This command, when run, should output a JSON array of `FunctionDeclaration` objects. The CLI will then dynamically make these tools available to the model.
2.  **Model Context Protocol (MCP) Server**: This is the most robust and flexible method. You can run a separate server application that exposes tools over a defined protocol. The `qwen-code` CLI can connect to this server to discover and execute its tools.

#### The Model Context Protocol (MCP)

For an Android app, implementing an MCP server is the recommended approach. Your Android app (or a backend it communicates with) can run an MCP server to expose device-specific tools to the AI.

*   **Functionality**: An MCP server exposes a list of available tools and provides an endpoint to execute them.
*   **Transports**: The CLI can connect to MCP servers via:
    *   **Stdio**: Spawning the server as a subprocess.
    *   **Server-Sent Events (SSE)**: Connecting to an HTTP SSE endpoint.
    *   **Streamable HTTP**: Connecting to a streaming HTTP endpoint.

For an Android application, you could implement a lightweight HTTP server inside your app that exposes tools over SSE or Streamable HTTP.

#### Example: Android MCP Server in Java/Kotlin

Here is a conceptual example of how you might set up a simple MCP server on Android using a library like `Ktor`.

**1. Define a Tool:**

```kotlin
// In your Android App
data class DeviceInfo(val model: String, val androidVersion: String, val batteryLevel: Int)

// This tool gets device information
class GetDeviceInfoTool {
    val schema = """
    {
        "name": "get_device_info",
        "description": "Gets information about the Android device, such as model, OS version, and battery level.",
        "parameters": {
            "type": "object",
            "properties": {},
            "required": []
        }
    }
    """.trimIndent()

    fun execute(): DeviceInfo {
        val model = android.os.Build.MODEL
        val version = android.os.Build.VERSION.RELEASE
        // You would need to implement battery level fetching logic
        val batteryLevel = 85
        return DeviceInfo(model, version, batteryLevel)
    }
}
```

**2. Set up a Ktor Server:**

```kotlin
// In your Android App, running in a background service
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.google.gson.Gson

fun startMcpServer() {
    val deviceInfoTool = GetDeviceInfoTool()
    val gson = Gson()

    embeddedServer(Netty, port = 8080) {
        routing {
            // Endpoint to list available tools
            get("/mcp/tools") {
                val toolList = listOf(gson.fromJson(deviceInfoTool.schema, Any::class.java))
                call.respond(mapOf("tools" to toolList))
            }

            // Endpoint to execute a tool
            post("/mcp/execute/{toolName}") {
                val toolName = call.parameters["toolName"]
                if (toolName == "get_device_info") {
                    val result = deviceInfoTool.execute()
                    call.respond(mapOf("result" to result))
                } else {
                    call.respond(mapOf("error" to "Tool not found"))
                }
            }
        }
    }.start(wait = true)
}
```

This is a simplified concept. The actual MCP protocol is more structured, but this illustrates the core idea: expose tool schemas and execution endpoints over HTTP.

## 4. File Uploading

There are two distinct mechanisms for handling files.

### A. Local File Access (CLI Tool)

The `qwen-code` CLI itself does not "upload" files in the traditional sense. When you use the `@path/to/file.txt` syntax, the following happens:

1.  The `read_many_files` tool is invoked.
2.  This tool uses the Node.js `fs` module to read the file's content directly from your local disk.
3.  The content is then placed into the prompt and sent to the model. For binary files like images, the content is base64 encoded.

This method is simple and effective for a terminal tool but is not a true file upload.

### B. CDN-Based File Upload (Qwen API Service)

Analysis of log files (`qwen_thinking_search.txt`) reveals that the underlying Qwen web service uses a more traditional, multi-step upload process. This is the model your Android app should follow.

**The process is:**

1.  **Get an Upload URL**: Your application first needs to call an API endpoint to get a temporary, secure upload URL. The Android source code in the repo suggests this involves getting an STS (Security Token Service) token first.
2.  **Upload the File**: Your app then performs an HTTP `PUT` or `POST` request to this temporary URL with the file's content.
3.  **Receive File Metadata**: The upload service responds with a JSON object containing the file's ID, its permanent URL on a CDN (e.g., `cdn.qwenlm.ai`), and other metadata.
4.  **Send Chat Message with File Reference**: You then send your message to the chat API. In the JSON payload, you include a `files` array containing the metadata object received in the previous step.

The model then uses this reference to access the file content on its end.

#### Example: File Upload in Java/Android

Here's a conceptual example using OkHttp.

```java
// In your Android App
import okhttp3.*;
import java.io.File;
import java.io.IOException;

public class QwenFileUploader {

    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    // Represents the response from the 'get upload url' step
    class UploadCredentials {
        String uploadUrl;
        // ... other fields like headers might be needed
    }

    // Represents the final file object to be included in the chat message
    class QwenFile {
        String id;
        String url;
        String name;
        // ... other metadata
    }

    public QwenFile uploadFile(File file) throws IOException {
        // Step 1: Get the temporary upload URL from your backend/Qwen's API
        // This is a placeholder for the actual API call
        Request credsRequest = new Request.Builder().url("https://api.qwen.com/getUploadCredentials").build();
        Response credsResponse = client.newCall(credsRequest).execute();
        UploadCredentials creds = gson.fromJson(credsResponse.body().string(), UploadCredentials.class);

        // Step 2: Upload the file to the temporary URL
        RequestBody requestBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));
        Request uploadRequest = new Request.Builder()
            .url(creds.uploadUrl)
            .put(requestBody)
            .build();
        Response uploadResponse = client.newCall(uploadRequest).execute();

        if (!uploadResponse.isSuccessful()) {
            throw new IOException("Failed to upload file: " + uploadResponse);
        }

        // Step 3: The response body of the upload should contain the final file metadata
        QwenFile qwenFile = gson.fromJson(uploadResponse.body().string(), QwenFile.class);
        return qwenFile;
    }

    public void sendMessageWithFile(String message, QwenFile file) {
        // Step 4: Include the QwenFile object in your chat message payload
        // ... build your chat request JSON here ...
    }
}
```

## 5. Summary and Key Takeaways for Android Development

*   **Adopt the Core/Tool Architecture**: The separation of the main logic (`core`) from the UI (`cli`) is a strong pattern. Your Android app would be the UI, and you could have a "core" module in your app that handles the logic.
*   **Embrace Function Calling**: This is the key to making your AI agent powerful. Define tools for any actions you want the AI to be able to take on the Android device (e.g., read contacts, get GPS location, send notifications, etc.).
*   **Use an MCP Server**: For exposing device-specific tools, implement a lightweight HTTP server (like Ktor) inside your app. This provides a clean, extensible way to add new tools without changing the core logic.
*   **Follow the CDN Upload Model**: Do not try to send file content directly in the chat prompt. Implement the multi-step process of getting an upload URL, uploading the file, and then referencing it in your chat message.
*   **Study the `OpenAIContentGenerator`**: This class is the perfect reference for how to translate between the application's internal state and the OpenAI-compatible API request/response format. Your Android app's networking layer will do a similar job.
*   **Confirmation is Key**: For any tool that modifies user data or takes a sensitive action, implement a confirmation prompt, just as `qwen-code` does. Show the user exactly what the AI is proposing to do and get their approval.
