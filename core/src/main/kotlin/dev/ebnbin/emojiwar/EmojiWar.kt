package dev.ebnbin.emojiwar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ScreenUtils
import com.mazatech.gdx.SVGAssetsConfigGDX
import com.mazatech.gdx.SVGAssetsGDX
import dev.ebnbin.kgdx.Game
import dev.ebnbin.kgdx.game
import java.util.zip.ZipInputStream

val emojiWar: EmojiWar
    get() = game as EmojiWar

class EmojiWar : Game() {
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var texture: Texture

    override fun create() {
        super.create()
        spriteBatch = SpriteBatch()
        texture = createEmojiTexture("1F600")
    }

    override fun render() {
        super.render()
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f)
        spriteBatch.begin()
        spriteBatch.drawEmojiTexture(texture)
        spriteBatch.end()
    }

    override fun dispose() {
        texture.dispose()
        spriteBatch.dispose()
        super.dispose()
    }

    companion object {
        private fun createEmojiTexture(name: String): Texture {
            val zipFileHandler = Gdx.files.internal("openmoji-svg-color.zip")
            ZipInputStream(zipFileHandler.read()).use { zipInputStream ->
                while (true) {
                    val zipEntry = zipInputStream.nextEntry ?: break
                    if (zipEntry.name != "$name.svg") {
                        zipInputStream.closeEntry()
                        continue
                    }
                    val svgAssetsConfigGDX = SVGAssetsConfigGDX(
                        Gdx.graphics.backBufferWidth,
                        Gdx.graphics.backBufferHeight,
                        Gdx.graphics.ppiX,
                    )
                    val svgAssetsGDX = SVGAssetsGDX(svgAssetsConfigGDX)
                    val svgText = zipInputStream.bufferedReader().readText()
                    val svgDocument = svgAssetsGDX.createDocument(svgText)
                    val texture = svgAssetsGDX.createTexture(svgDocument, 72, 72)
                    svgDocument.dispose()
                    svgAssetsGDX.dispose()
                    zipInputStream.closeEntry()
                    return texture
                }
            }
            error(Unit)
        }

        private fun SpriteBatch.drawEmojiTexture(texture: Texture) {
            draw(texture, 0f, 0f, texture.width.toFloat(), texture.height.toFloat(), 0, 0, texture.width,
                texture.height, false, true)
        }
    }
}
