@file:JvmName("DesktopLauncher")

package dev.ebnbin.emojiwar.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.glutils.HdpiMode
import dev.ebnbin.emojiwar.EmojiWar

fun main() {
    if (StartupHelper.startNewJvmIfRequired()) return
    val listener = EmojiWar()
    val config = Lwjgl3ApplicationConfiguration().also {
        it.setMaximized(true)
        it.setHdpiMode(HdpiMode.Pixels)
    }
    Lwjgl3Application(listener, config)
}
