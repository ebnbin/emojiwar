package dev.ebnbin.emojiwar.war

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class WarActor(
    rows: Int,
    columns: Int,
    characterSpeed: Float,
    characterTexture: Texture,
) : Actor() {
    private val engine: WarEngine = WarEngine(
        rows = rows,
        columns = columns,
        characterSpeed = characterSpeed,
        characterTexture = characterTexture,
    )

    fun getCharacterX(): Float {
        return requireNotNull(engine.characterEntity.getComponent(PositionComponent::class.java)).x
    }

    fun getCharacterY(): Float {
        return requireNotNull(engine.characterEntity.getComponent(PositionComponent::class.java)).y
    }

    override fun act(delta: Float) {
        super.act(delta)
        val inputDirectionX = when {
            Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> Direction.NEGATIVE
            Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT) -> Direction.POSITIVE
            else -> Direction.ZERO
        }
        val inputDirectionY = when {
            Gdx.input.isKeyPressed(Input.Keys.DOWN) && !Gdx.input.isKeyPressed(Input.Keys.UP) -> Direction.NEGATIVE
            Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN) -> Direction.POSITIVE
            else -> Direction.ZERO
        }
        engine.context.inputDirectionX = inputDirectionX
        engine.context.inputDirectionY = inputDirectionY
        engine.context.systemState = SystemState.ACT
        engine.update(delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        batch.color = color
        engine.context.systemState = SystemState.DRAW
        engine.context.batch = batch
        engine.update(0f)
        engine.context.batch = null
    }
}

private enum class SystemState {
    NONE,
    ACT,
    DRAW,
    ;
}

private enum class Direction {
    ZERO,
    NEGATIVE,
    POSITIVE,
    ;
}

private class WarEngine(
    rows: Int,
    columns: Int,
    characterSpeed: Float,
    characterTexture: Texture,
) : Engine() {
    val context: WarContext = WarContext(
        rows = rows,
        columns = columns,
    )

    val characterEntity: CharacterEntity = CharacterEntity(
        x = columns / 2f,
        y = rows / 2f,
        speed = characterSpeed,
        texture = characterTexture,
    )

    init {
        addEntity(characterEntity)
        addSystem(ActSystem(context))
        addSystem(DrawSystem(context))
    }
}

private class WarContext(
    val rows: Int,
    val columns: Int,
    var systemState: SystemState = SystemState.NONE,
    var batch: Batch? = null,
    var inputDirectionX: Direction = Direction.ZERO,
    var inputDirectionY: Direction = Direction.ZERO,
)

private class PositionComponent(
    var x: Float,
    var y: Float,
) : Component

private class SpeedComponent(
    var speed: Float,
) : Component

private class TextureComponent(
    var texture: Texture,
) : Component

private class CharacterEntity(
    x: Float,
    y: Float,
    speed: Float,
    texture: Texture,
) : Entity() {
    init {
        add(PositionComponent(x, y))
        add(SpeedComponent(speed))
        add(TextureComponent(texture))
    }
}

private class ActSystem(
    private val context: WarContext,
) : IteratingSystem(allOf(
    PositionComponent::class,
    SpeedComponent::class,
).get()) {
    override fun checkProcessing(): Boolean {
        return context.systemState == SystemState.ACT
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val position = mapperFor<PositionComponent>().get(entity)
        val speed = mapperFor<SpeedComponent>().get(entity)
        val velocityX = when (context.inputDirectionX) {
            Direction.ZERO -> 0f
            Direction.NEGATIVE -> -speed.speed
            Direction.POSITIVE -> speed.speed
        }
        val velocityY = when (context.inputDirectionY) {
            Direction.ZERO -> 0f
            Direction.NEGATIVE -> -speed.speed
            Direction.POSITIVE -> speed.speed
        }
        val minX = 0.5f
        val maxX = context.columns - 0.5f
        val minY = 0.5f
        val maxY = context.rows - 0.5f
        position.x = (position.x + velocityX * deltaTime).coerceIn(minX, maxX)
        position.y = (position.y + velocityY * deltaTime).coerceIn(minY, maxY)
    }
}

private class DrawSystem(
    private val context: WarContext,
) : IteratingSystem(allOf(
    PositionComponent::class,
    TextureComponent::class,
).get()) {
    override fun checkProcessing(): Boolean {
        return context.systemState == SystemState.DRAW
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val position = mapperFor<PositionComponent>().get(entity)
        val texture = mapperFor<TextureComponent>().get(entity)
        val batch = context.batch ?: return
        batch.draw(
            texture.texture,
            position.x - 0.5f,
            position.y - 0.5f,
            0.5f,
            0.5f,
            1f,
            1f,
            1f,
            1f,
            0f,
            0,
            0,
            texture.texture.width,
            texture.texture.height,
            false,
            true,
        )
    }
}
