package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.input.InputHandler
import kotlinx.cinterop.useContents
import platform.AppKit.NSApplication
import platform.AppKit.NSApplicationActivationPolicy
import platform.AppKit.NSApplicationDelegateProtocol
import platform.AppKit.NSBackingStoreBuffered
import platform.AppKit.NSColor
import platform.AppKit.NSScreen
import platform.AppKit.NSWindow
import platform.AppKit.NSWindowBackingLocationVideoMemory
import platform.AppKit.NSWindowDelegateProtocol
import platform.AppKit.NSWindowStyleMaskClosable
import platform.AppKit.NSWindowStyleMaskMiniaturizable
import platform.AppKit.NSWindowStyleMaskResizable
import platform.AppKit.NSWindowStyleMaskTitled
import platform.AppKit.preferredBackingLocation
import platform.Foundation.NSMakeRect
import platform.Metal.MTLCreateSystemDefaultDevice
import platform.MetalKit.MTKView
import platform.darwin.NSObject

class AppDelegate(val title: String, val w: Int, val h: Int) : NSObject(), NSApplicationDelegateProtocol {

    private val window: NSWindow

    init {
        val mainDisplayRect = NSScreen.mainScreen()!!.frame
        val windowRect = mainDisplayRect.useContents {
            NSMakeRect(
                origin.x + w * 0.25,
                origin.y + h * 0.25,
                w.toDouble(),
                h.toDouble()
            )
        }

        val windowStyle = NSWindowStyleMaskTitled or NSWindowStyleMaskMiniaturizable or
                NSWindowStyleMaskClosable or NSWindowStyleMaskResizable

        window = NSWindow(windowRect, windowStyle, NSBackingStoreBuffered, false).apply {
            title = this@AppDelegate.title
            opaque = true
            hasShadow = true
            preferredBackingLocation = NSWindowBackingLocationVideoMemory
            hidesOnDeactivate = false
            backgroundColor = NSColor.grayColor()
            releasedWhenClosed = false
            // mainWindow = true

            delegate = object : NSObject(), NSWindowDelegateProtocol {
                override fun windowShouldClose(sender: NSWindow): Boolean {
                    NSApplication.sharedApplication().stop(this)
                    return true
                }
            }
        }

        val view = MTKView()
        window.contentView?.addSubview(view)

        val device = MTLCreateSystemDefaultDevice()
        view.device = device

        window.makeMainWindow()
    }
}

actual class GLConfiguration(
    val title: String,
    val width: Int,
    val height: Int
)

actual class GLContext actual constructor(val configuration: GLConfiguration) {

    internal actual fun createContext(): GL {
        return MetalGL()
    }

    actual fun run(gameFactory: () -> Game) {

        val app = NSApplication.sharedApplication()

        app.delegate = AppDelegate(
            title = configuration.title,
            w = configuration.width,
            h = configuration.height
        )
        app.setActivationPolicy(NSApplicationActivationPolicy.NSApplicationActivationPolicyRegular)
        app.activateIgnoringOtherApps(true)

        app.run()
    }

    internal actual fun createFileHandler(): FileHandler {
        return FileHandler()
    }

    internal actual fun createInputHandler(): InputHandler {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
