package dev.ebnbin.emojiwar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.mazatech.gdx.SVGAssetsConfigGDX
import com.mazatech.gdx.SVGAssetsGDX
import dev.ebnbin.emojiwar.war.WarStage
import dev.ebnbin.kgdx.Game
import dev.ebnbin.kgdx.game
import dev.ebnbin.kgdx.scene.Screen
import java.util.zip.ZipInputStream

val emojiWar: EmojiWar
    get() = game as EmojiWar

class EmojiWar : Game() {
    lateinit var emojiTextureMap: Map<String, Texture>
        private set

    override fun create() {
        super.create()
        emojiTextureMap = createEmojiTextureMap()
        setScreen {
            Screen(listOf(WarStage()))
        }
    }

    override fun dispose() {
        setScreen(null)
        emojiTextureMap.values.forEach(Texture::dispose)
        super.dispose()
    }

    companion object {
        private fun createEmojiTextureMap(): Map<String, Texture> {
            val nameList = mutableListOf<String>()
            val zipFileHandler = Gdx.files.internal("openmoji-svg-color.zip")
            ZipInputStream(zipFileHandler.read()).use { zipInputStream ->
                while (true) {
                    val zipEntry = zipInputStream.nextEntry ?: break
                    if (zipEntry.name.endsWith(".svg")) {
                        nameList.add(zipEntry.name.removeSuffix(".svg"))
                    }
                }
            }
            nameList.shuffle()
            return nameList.take(10).associateWith {
                createEmojiTexture(it)
            }
        }

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
    }
}
