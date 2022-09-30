```
███╗   ███╗██╗███╗   ██╗██╗ ██████╗ ██████╗ ██╗  ██╗
████╗ ████║██║████╗  ██║██║██╔════╝ ██╔══██╗╚██╗██╔╝
██╔████╔██║██║██╔██╗ ██║██║██║  ███╗██║  ██║ ╚███╔╝
██║╚██╔╝██║██║██║╚██╗██║██║██║   ██║██║  ██║ ██╔██╗
██║ ╚═╝ ██║██║██║ ╚████║██║╚██████╔╝██████╔╝██╔╝ ██╗
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝
```

Minimalist Kotlin/Multiplaform Game Engine.

Platform supported: 
- JVM
- JS
- Android

Platform expected:
- iOS 
- Native (Windows/Linux/MacOS)

🎮  Try it now by creating a game using [this game template](https://github.com/minigdx/minigdx-game-template) 🎮
## Showcases

Try some example of what can be build using MiniGDX on the [MiniGDX Showcase site](https://minigdx.github.io/minigdx-showcase/)

https://user-images.githubusercontent.com/373097/115747729-d1c90980-a395-11eb-9b94-b19f4ccb6c9e.mp4

[Try it!](https://minigdx.github.io/minigdx-showcase/2021/03/20/2D-platformer.html)

https://user-images.githubusercontent.com/373097/115747808-e3aaac80-a395-11eb-9c0d-75c3a0e7723d.mp4

[Try it!](https://minigdx.github.io/minigdx-showcase/2021/03/28/3D-example.html)

https://user-images.githubusercontent.com/373097/115747845-eb6a5100-a395-11eb-8d76-639886c14322.mp4

[Try it!](https://minigdx.github.io/minigdx-showcase/2021/03/28/Dance.html)


## Features matrix

|      Feature       | JVM | Web | Android | iOS |
|--------------------|-----|-----|---------|-----|
| 2D / 3D Rendering  | ✅   | ✅   | ✅       | ⛔️  |
| Skeleton Animation | ✅   | ✅   | ✅       | ⛔️  |
| Keyboard Input     | ✅   | ✅   | ✅       | ⛔️  |
| Mouse/Touch Input  | ✅   | ✅   | ✅       | ⛔️  |
| Sound (MP3)        | ✅   | ✅   | ✅      | ⛔️  |
| AABB Collision  | ✅   | ✅   | ✅       | ⛔️  |
| SAT Collision  | ✅   | ✅   | ✅       | ⛔️  |
| Scripting  | ✅   | ✅   | ✅       | ⛔️  |

## Build

```
make build
```

MiniGDX can be updated while creating a game by using [Gradle composite build](https://docs.gradle.org/current/userguide/composite_builds.html)

Insert in the `settings.gradle.kts`: 
```
includeBuild('...path to minigdx...')
```
