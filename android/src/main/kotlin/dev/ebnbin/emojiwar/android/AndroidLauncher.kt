package dev.ebnbin.emojiwar.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import dev.ebnbin.emojiwar.EmojiWar

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listener = EmojiWar()
        val config = AndroidApplicationConfiguration()
        initialize(listener, config)
    }
}
