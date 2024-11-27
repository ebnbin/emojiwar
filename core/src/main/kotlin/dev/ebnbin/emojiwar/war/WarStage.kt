package dev.ebnbin.emojiwar.war

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import dev.ebnbin.emojiwar.emojiWar
import dev.ebnbin.kgdx.scene.LifecycleStage
import ktx.actors.alpha
import kotlin.math.max
import kotlin.random.Random

class WarStage : LifecycleStage(object : ScreenViewport() {
    override fun update(screenWidth: Int, screenHeight: Int, centerCamera: Boolean) {
        unitsPerPixel = max(TILES_PER_SCREEN / screenWidth, TILES_PER_SCREEN / screenHeight)
        super.update(screenWidth, screenHeight, centerCamera)
    }
}) {
    init {
        camera.position.set(COLUMNS / 2f, ROWS / 2f, 0f)
        camera.update()
    }

    private val shapeRenderer: ShapeRenderer = ShapeRenderer().also {
        it.color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f)
    }

    init {
        val emojiTextureList = emojiWar.emojiTextureMap.values.toList().subList(0, BACKGROUND_EMOJI_DIVERSITY)
        for (y in 1 until ROWS - 1) {
            for (x in 1 until COLUMNS - 1) {
                if (Random.nextFloat() > BACKGROUND_EMOJI_DENSITY) continue
                val texture = emojiTextureList.random()
                Image(texture).also {
                    it.setSize(1f, 1f)
                    it.setPosition(x.toFloat(), y.toFloat())
                    it.setOrigin(Align.center)
                    it.scaleY = -1f
                    it.alpha = BACKGROUND_EMOJI_ALPHA
                    it.rotation = Random.nextFloat() * 360f
                    it.setScale(Random.nextFloat() * (BACKGROUND_EMOJI_SCALE_MAX - BACKGROUND_EMOJI_SCALE_MIN) +
                        BACKGROUND_EMOJI_SCALE_MIN)
                    it.moveBy(
                        Random.nextFloat() * BACKGROUND_EMOJI_OFFSET_ABS * 2 - BACKGROUND_EMOJI_OFFSET_ABS,
                        Random.nextFloat() * BACKGROUND_EMOJI_OFFSET_ABS * 2 - BACKGROUND_EMOJI_OFFSET_ABS,
                    )
                    addActor(it)
                }
            }
        }
    }

    private val image: Image = Image(emojiWar.emojiTextureMap.values.last()).also {
        it.setSize(1f, 1f)
        it.setPosition(COLUMNS / 2f, ROWS / 2f, Align.center)
        it.setOrigin(Align.center)
        it.scaleY = -1f
        addActor(it)
    }

    override fun act(delta: Float) {
        super.act(delta)
        val minImageX = 0.5f
        val maxImageX = COLUMNS - 0.5f
        val minImageY = 0.5f
        val maxImageY = ROWS - 0.5f
        val minCameraX = width / 2f - MARGIN_HORIZONTAL
        val maxCameraX = COLUMNS - width / 2f + MARGIN_HORIZONTAL
        val minCameraY = height / 2f - MARGIN_BOTTOM
        val maxCameraY = ROWS - height / 2f + MARGIN_TOP
        val offsetX = if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            -delta * SPEED
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            delta * SPEED
        } else {
            0f
        }
        val offsetY = if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !Gdx.input.isKeyPressed(Input.Keys.UP)) {
            -delta * SPEED
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            delta * SPEED
        } else {
            0f
        }
        val imageX = (image.getX(Align.center) + offsetX).coerceIn(minImageX, maxImageX)
        val imageY = (image.getY(Align.center) + offsetY).coerceIn(minImageY, maxImageY)
        image.setPosition(imageX, imageY, Align.center)
        camera.position.set(
            if (minCameraX > maxCameraX) {
                COLUMNS / 2f
            } else {
                imageX.coerceIn(minCameraX, maxCameraX)
            },
            if (minCameraY > maxCameraY) {
                (ROWS - MARGIN_BOTTOM + MARGIN_TOP) / 2f
            } else {
                imageY.coerceIn(minCameraY, maxCameraY)
            },
            0f,
        )
        camera.update()
    }

    override fun draw() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.rect(0f, 0f, COLUMNS.toFloat(), ROWS.toFloat())
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
        super.draw()
    }

    override fun dispose() {
        shapeRenderer.dispose()
        super.dispose()
    }

    companion object {
        private const val TILES_PER_SCREEN = 10f
        private const val ROWS = 21
        private const val COLUMNS = 21
        private const val BACKGROUND_EMOJI_DENSITY = 0.1f
        private const val BACKGROUND_EMOJI_DIVERSITY = 5
        private const val BACKGROUND_EMOJI_ALPHA = 0.2f
        private const val BACKGROUND_EMOJI_SCALE_MIN = 0.25f
        private const val BACKGROUND_EMOJI_SCALE_MAX = 0.75f
        private const val BACKGROUND_EMOJI_OFFSET_ABS = 0.25f
        private const val MARGIN_HORIZONTAL = 1f
        private const val MARGIN_BOTTOM = 0.5f
        private const val MARGIN_TOP = 1.5f
        private const val SPEED = 3f
    }
}
