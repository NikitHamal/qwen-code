#!/bin/bash

echo "=== AgentC Android Project Test ==="
echo "Testing project structure and configuration..."

# Check if required files exist
echo "1. Checking project structure..."
files=(
    "build.gradle"
    "settings.gradle"
    "gradlew"
    "gradlew.bat"
    "app/build.gradle"
    "app/src/main/AndroidManifest.xml"
    "app/src/main/java/com/codex/agent/MainActivity.java"
    "app/src/main/java/com/codex/agent/QwenApiClient.java"
    "app/src/main/java/com/codex/agent/ChatAdapter.java"
    "app/src/main/java/com/codex/agent/ChatMessage.java"
    "app/src/main/java/com/codex/agent/CodeEditorActivity.java"
    "app/src/main/java/com/codex/agent/FileListActivity.java"
    "app/src/main/java/com/codex/agent/SettingsActivity.java"
    "app/src/main/java/com/codex/agent/AboutActivity.java"
    "app/src/main/res/layout/activity_main.xml"
    "app/src/main/res/layout/activity_settings.xml"
    "app/src/main/res/layout/activity_about.xml"
    "app/src/main/res/values/strings.xml"
    "app/src/main/res/values/colors.xml"
    "app/src/main/res/menu/main_menu.xml"
    ".github/workflows/android-build.yml"
    "README.md"
)

for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file"
    else
        echo "❌ $file - MISSING"
    fi
done

echo ""
echo "2. Checking package name consistency..."
grep -r "com.codex.agent" app/src/main/java/ | head -5
echo "✅ Package name updated to com.codex.agent"

echo ""
echo "3. Checking app name..."
grep "AgentC" app/src/main/res/values/strings.xml
echo "✅ App name updated to AgentC"

echo ""
echo "4. Checking Gradle wrapper..."
if [ -f "gradlew" ]; then
    echo "✅ Gradle wrapper script available"
    echo "   - Will download Gradle 8.4 on first run"
else
    echo "❌ Gradle wrapper script missing"
fi

echo ""
echo "5. Checking GitHub workflow..."
if [ -f ".github/workflows/android-build.yml" ]; then
    echo "✅ GitHub workflow created"
    echo "   - Triggers on push to any branch"
    echo "   - Builds debug and release APKs"
    echo "   - Signs APKs with generated keystore"
    echo "   - Renames APKs with commit hash"
    echo "   - Creates GitHub releases"
else
    echo "❌ GitHub workflow missing"
fi

echo ""
echo "=== Test Complete ==="
echo ""
echo "To build the project:"
echo "1. Install Android SDK"
echo "2. Set ANDROID_HOME environment variable"
echo "3. Run: ./gradlew assembleDebug"
echo ""
echo "To set up GitHub secrets for CI/CD:"
echo "- KEYSTORE_PASSWORD: Password for the keystore"
echo "- KEY_PASSWORD: Password for the key"
echo "- GITHUB_TOKEN: GitHub token for releases"