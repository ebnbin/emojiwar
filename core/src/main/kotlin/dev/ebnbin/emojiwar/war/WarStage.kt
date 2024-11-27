package dev.ebnbin.emojiwar.war

import com.badlogic.gdx.utils.viewport.ScreenViewport
import dev.ebnbin.emojiwar.emojiWar
import dev.ebnbin.kgdx.scene.LifecycleStage
import kotlin.math.max

class WarStage : LifecycleStage(object : ScreenViewport() {
    override fun update(screenWidth: Int, screenHeight: Int, centerCamera: Boolean) {
        unitsPerPixel = max(TILES_PER_SCREEN / screenWidth, TILES_PER_SCREEN / screenHeight)
        super.update(screenWidth, screenHeight, centerCamera)
    }
}) {
    private val cameraHelper: WarCameraHelper = WarCameraHelper(
        camera = camera,
        rows = ROWS,
        columns = COLUMNS,
        marginHorizontal = MARGIN_HORIZONTAL,
        marginBottom = MARGIN_BOTTOM,
        marginTop = MARGIN_TOP,
    )

    init {
        WarBackgroundActor(
            textureList = emojiWar.emojiTextureMap.values.toList().subList(0, BACKGROUND_EMOJI_DIVERSITY),
            rows = ROWS,
            columns = COLUMNS,
            density = BACKGROUND_EMOJI_DENSITY,
            scaleMin = BACKGROUND_EMOJI_SCALE_MIN,
            scaleMax = BACKGROUND_EMOJI_SCALE_MAX,
            offsetAbs = BACKGROUND_EMOJI_OFFSET_ABS,
            alpha = BACKGROUND_EMOJI_ALPHA,
        ).also {
            addActor(it)
        }
    }

    private val actor: WarActor = WarActor(
        rows = ROWS,
        columns = COLUMNS,
        characterSpeed = SPEED,
        characterTexture = emojiWar.emojiTextureMap.values.last(),
    ).also {
        addActor(it)
    }

    override fun resize(width: Float, height: Float) {
        super.resize(width, height)
        cameraHelper.resize(width, height)
    }

    override fun act(delta: Float) {
        super.act(delta)
        cameraHelper.act(
            characterX = actor.getCharacterX(),
            characterY = actor.getCharacterY(),
        )
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
