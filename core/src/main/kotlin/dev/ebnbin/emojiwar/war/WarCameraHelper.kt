package dev.ebnbin.emojiwar.war

import com.badlogic.gdx.graphics.Camera

class WarCameraHelper(
    private val camera: Camera,
    private val rows: Int,
    private val columns: Int,
    private val marginHorizontal: Float,
    private val marginBottom: Float,
    private val marginTop: Float,
) {
    private val defaultX: Float = columns / 2f
    private val defaultY: Float = (rows - marginBottom + marginTop) / 2f

    private var minX: Float = defaultX
    private var maxX: Float = defaultX
    private var minY: Float = defaultY
    private var maxY: Float = defaultY

    init {
        camera.position.set(defaultX, defaultY, 0f)
        camera.update()
    }

    fun resize(width: Float, height: Float) {
        minX = width / 2f - marginHorizontal
        maxX = columns - width / 2f + marginHorizontal
        minY = height / 2f - marginBottom
        maxY = rows - height / 2f + marginTop
    }

    fun act(characterX: Float, characterY: Float) {
        camera.position.x = if (minX > maxX) {
            defaultX
        } else {
            characterX.coerceIn(minX, maxX)
        }
        camera.position.y = if (minY > maxY) {
            defaultY
        } else {
            characterY.coerceIn(minY, maxY)
        }
        camera.update()
    }
}
