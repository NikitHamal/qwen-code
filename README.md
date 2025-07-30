# AgentC - AI-Powered Code Assistant

AgentC is an advanced Android application that provides a graphical user interface for AI-powered code assistance. Built with modern Android development practices and Material Design 3, it offers a seamless experience for developers to interact with AI models for coding tasks.

## Features

### 🤖 AI-Powered Code Assistance
- Real-time chat interface with AI models
- Streaming responses for immediate feedback
- Support for multiple AI models (Qwen, etc.)
- Context-aware conversations

### 📁 File Management
- File upload and attachment capabilities
- Built-in file browser for device storage
- Support for various file types
- Secure file handling with STS tokens

### 💻 Code Editor
- Syntax highlighting for multiple languages
- Code viewing and editing capabilities
- File content display
- Support for various programming languages

### ⚙️ Settings & Customization
- Dark mode support
- Auto-scroll preferences
- Sound notification settings
- User preferences management

### 🎨 Modern UI/UX
- Material Design 3 components
- Responsive layout design
- Intuitive navigation
- Progress indicators and error handling

## Screenshots

[Add screenshots here]

## Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 34
- Java 17 or later
- Gradle 8.4

### Building from Source

1. Clone the repository:
```bash
git clone https://github.com/codex-agent/agentc.git
cd agentc
```

2. Open the project in Android Studio or build from command line:
```bash
./gradlew assembleDebug
```

3. Install on device:
```bash
./gradlew installDebug
```

### Download Pre-built APK

Download the latest release from the [Releases page](https://github.com/codex-agent/agentc/releases).

## Development

### Project Structure
```
app/
├── src/main/
│   ├── java/com/codex/agent/
│   │   ├── MainActivity.java          # Main chat interface
│   │   ├── QwenApiClient.java        # API client implementation
│   │   ├── ChatAdapter.java          # RecyclerView adapter
│   │   ├── ChatMessage.java          # Message data model
│   │   ├── FileListActivity.java     # File browser
│   │   ├── CodeEditorActivity.java   # Code editor
│   │   ├── SettingsActivity.java     # Settings screen
│   │   └── AboutActivity.java        # About screen
│   ├── res/
│   │   ├── layout/                   # UI layouts
│   │   ├── values/                   # Strings, colors, styles
│   │   ├── drawable/                 # Vector drawables
│   │   └── menu/                     # Menu resources
│   └── AndroidManifest.xml
└── build.gradle                      # App-level build config
```

### Key Components

#### MainActivity
The main chat interface that handles:
- User input and message sending
- File attachments
- Real-time AI responses
- Progress indicators
- Error handling

#### QwenApiClient
API client that manages:
- HTTP requests to AI services
- File upload with STS tokens
- Streaming responses
- Authentication and headers

#### ChatAdapter
RecyclerView adapter for displaying:
- User and bot messages
- Different message types
- Dynamic content updates

### Dependencies

```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'com.squareup.okhttp3:okhttp:4.10.0'
    implementation 'com.google.code.gson:gson:2.9.0'
    implementation 'io.github.kbiakov:codeview-android:1.4.2'
}
```

## CI/CD

The project includes GitHub Actions for automated builds:

- **Trigger**: Push to any branch or pull request
- **Build**: Debug and Release APKs
- **Signing**: Automatic APK signing with generated keystore
- **Naming**: APKs renamed with commit hash
- **Release**: Automatic releases on main branch

### Workflow Features
- ✅ Multi-branch support
- ✅ Automatic keystore generation
- ✅ APK signing and optimization
- ✅ Commit hash naming
- ✅ Artifact upload
- ✅ GitHub releases

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Android coding conventions
- Use Material Design 3 components
- Add proper error handling
- Include unit tests for new features
- Update documentation as needed

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built with [Material Design 3](https://m3.material.io/)
- Uses [OkHttp](https://square.github.io/okhttp/) for networking
- Code highlighting powered by [CodeView](https://github.com/kbiakov/CodeView-Android)
- Icons from [Material Icons](https://fonts.google.com/icons)

## Support

- 📧 Email: support@codex-agent.com
- 🐛 Issues: [GitHub Issues](https://github.com/codex-agent/agentc/issues)
- 📖 Documentation: [Wiki](https://github.com/codex-agent/agentc/wiki)

---

**AgentC** - Empowering developers with AI-powered code assistance on Android.
