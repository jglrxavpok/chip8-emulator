package org.jglr.chip8

import org.lwjgl.glfw.GLFW

class Input {

    private var lastPressed = 0
    private var waitingOnKeypress = false
    val keys = BooleanArray(16)

    fun isKeyPressed(index: Int) = keys[index]

    fun waitKeypress(): Int {
        waitingOnKeypress = true
        while(waitingOnKeypress) {
            GLFW.glfwPollEvents() // the key press event should fire

            Thread.sleep(1)
        }
        return lastPressed
    }

    fun onPressed(index: Int) {
        lastPressed = index
        if(waitingOnKeypress) {
            waitingOnKeypress = false
        }
        keys[index] = true
    }

    fun onReleased(index: Int) {
        keys[index] = false
    }
}