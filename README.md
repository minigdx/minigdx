# Mini GDX

Kotlin/Multiplaform game engine.

Platform supported: 
- JVM
- JS

Platform expected:
- Android
- iOS 
- Native (Windows/Linux/MacOS)

## Build

The build depends of packages hoster on Github.
You'll need them to build the project. 

Create a github access token with `read:packages` authorization.
Create environments variables: 

```kotlin
export GITHUB_USERNAME=<your githubusername>
export GITHUB_TOKEN=<your read access token>
```

```bash
./gradlew build
```

## Blender support

Export collada with global orientation settings: X FORWARD / Y UP
