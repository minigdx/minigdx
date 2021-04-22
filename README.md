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

## Showcases

Try some example of what can be build using MiniGDX on the [MiniGDX Showcase site](https://minigdx.github.io/minigdx-showcase/)

![2D platformer game](docs/2dgame.webm)

[Try it!](https://minigdx.github.io/minigdx-showcase/2021/03/20/2D-platformer.html)

![3D platformer game](docs/3dgame.webm)

[Try it!](https://minigdx.github.io/minigdx-showcase/2021/03/28/3D-example.html)

![Danse using Skeleton animation](docs/danse.webm)

[Try it!](https://minigdx.github.io/minigdx-showcase/2021/03/28/Dance.html)


## Features matrix

|      Feature       | JVM | Web | Android | iOS |
|--------------------|-----|-----|---------|-----|
| 2D / 3D Rendering  | ✅   | ✅   | ✅       | ⛔️  |
| Skeleton Animation | ✅   | ✅   | ✅       | ⛔️  |
| Keyboard Input     | ✅   | ✅   | ✅       | ⛔️  |
| Mouse/Touch Input  | ✅   | ✅   | ✅       | ⛔️  |
| Sound (MP3)        | ✅   | ✅   | ⛔️      | ⛔️  |
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
