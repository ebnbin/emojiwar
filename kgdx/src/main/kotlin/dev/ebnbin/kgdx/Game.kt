package dev.ebnbin.kgdx

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.ScreenUtils
import dev.ebnbin.kgdx.scene.LifecycleStage.Companion.act
import dev.ebnbin.kgdx.scene.LifecycleStage.Companion.dispose
import dev.ebnbin.kgdx.scene.LifecycleStage.Companion.draw
import dev.ebnbin.kgdx.scene.LifecycleStage.Companion.pause
import dev.ebnbin.kgdx.scene.LifecycleStage.Companion.resize
import dev.ebnbin.kgdx.scene.LifecycleStage.Companion.resume
import dev.ebnbin.kgdx.scene.Screen

private var singleton: Game? = null

val game: Game
    get() = requireNotNull(singleton)

abstract class Game : ApplicationListener {
    private var canRender: Boolean = false
    private var resumed: Boolean = false

    private var screen: Screen? = null

    fun setScreen(createScreen: (() -> Screen)?) {
        val resumed = resumed
        val oldScreen = screen
        if (resumed) {
            oldScreen?.stageList?.pause()
        }
        oldScreen?.stageList?.dispose()
        val newScreen = createScreen?.invoke()
        screen = newScreen
        newScreen?.stageList?.resize(Gdx.graphics.width, Gdx.graphics.height)
        if (resumed) {
            newScreen?.stageList?.resume()
        }
    }

    override fun create() {
        singleton = this
        canRender = true
    }

    override fun resize(width: Int, height: Int) {
        screen?.stageList?.resize(width, height)
    }

    override fun resume() {
        canRender = true
    }

    override fun render() {
        if (!canRender) return
        if (!resumed) {
            resumed = true
            screen?.stageList?.resume()
        }
        screen?.stageList?.act(Gdx.graphics.deltaTime)
        ScreenUtils.clear(Color.CLEAR)
        screen?.stageList?.draw()
    }

    override fun pause() {
        if (resumed) {
            screen?.stageList?.pause()
            resumed = false
        }
        canRender = false
    }

    override fun dispose() {
        setScreen(null)
        singleton = null
    }
}
