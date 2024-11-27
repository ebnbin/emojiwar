package dev.ebnbin.emojiwar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ScreenUtils
import com.mazatech.gdx.SVGAssetsConfigGDX
import com.mazatech.gdx.SVGAssetsGDX
import dev.ebnbin.emojiwar.war.WarStage
import dev.ebnbin.kgdx.Game
import dev.ebnbin.kgdx.game
import java.util.zip.ZipInputStream

val emojiWar: EmojiWar
    get() = game as EmojiWar

class EmojiWar : Game() {
    lateinit var emojiTextureMap: Map<String, Texture>
        private set

    private lateinit var warStage: WarStage

    override fun create() {
        super.create()
        emojiTextureMap = createEmojiTextureMap()
        warStage = WarStage()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        warStage.viewport.update(width, height, true)
    }

    override fun render() {
        super.render()
        warStage.act(Gdx.graphics.deltaTime)
        ScreenUtils.clear(Color.CLEAR)
        warStage.viewport.apply()
        warStage.draw()
    }

    override fun dispose() {
        warStage.dispose()
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
