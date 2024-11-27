package dev.ebnbin.emojiwar.war

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.random.Random

class WarBackgroundActor(
    textureList: List<Texture>,
    rows: Int,
    columns: Int,
    density: Float,
    scaleMin: Float,
    scaleMax: Float,
    offsetAbs: Float,
    private val alpha: Float,
) : Actor() {
    init {
        setSize(columns.toFloat(), rows.toFloat())
    }

    private val shapeRenderer: ShapeRenderer = ShapeRenderer().also {
        it.color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f)
    }

    private data class Tile(
        val texture: Texture,
        val x: Float,
        val y: Float,
        val scale: Float,
        val rotation: Float,
    )

    private val tileList: List<Tile> = mutableListOf<Tile>().also { tileList ->
        for (y in 0 until rows) {
            for (x in 0 until columns) {
                if (Random.nextFloat() > density) continue
                val tile = Tile(
                    texture = textureList.random(),
                    x = x.toFloat() + Random.nextFloat() * offsetAbs * 2 - offsetAbs,
                    y = y.toFloat() + Random.nextFloat() * offsetAbs * 2 - offsetAbs,
                    scale = Random.nextFloat() * (scaleMax - scaleMin) + scaleMin,
                    rotation = Random.nextFloat() * 360f,
                )
                tileList.add(tile)
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        batch.end()
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.projectionMatrix = batch.projectionMatrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.rect(x, y, width, height)
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
        batch.begin()
        batch.color = color.cpy().also { it.a *= parentAlpha * alpha }
        tileList.forEach { tile ->
            batch.draw(
                tile.texture,
                x + tile.x,
                y + tile.y,
                0.5f,
                0.5f,
                1f,
                1f,
                tile.scale,
                tile.scale,
                tile.rotation,
                0,
                0,
                tile.texture.width,
                tile.texture.height,
                false,
                true,
            )
        }
    }
}
