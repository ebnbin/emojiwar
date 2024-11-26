@file:JvmName("HeadlessLauncher")

package dev.ebnbin.emojiwar.headless

import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration
import dev.ebnbin.emojiwar.EmojiWar

/** Launches the headless application. Can be converted into a server application or a scripting utility. */
fun main() {
    HeadlessApplication(EmojiWar(), HeadlessApplicationConfiguration().apply {
        // When this value is negative, EmojiWar#render() is never called:
        updatesPerSecond = -1
    })
}
