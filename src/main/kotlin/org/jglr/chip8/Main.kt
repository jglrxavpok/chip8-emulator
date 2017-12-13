package org.jglr.chip8

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil
import java.awt.Toolkit

fun main(args: Array<String>) {
    val glfwInit = glfwInit()
    if (!glfwInit) {
        return
    }

    GLFWErrorCallback.createPrint(System.err).set()

    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
/*    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0)*/
    //glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE)
    val scale = 10
    val windowHandle = glfwCreateWindow(64*scale, 32*scale, "Chip8 emulator", MemoryUtil.NULL, MemoryUtil.NULL)
    if (windowHandle == MemoryUtil.NULL)
        return

    glfwMakeContextCurrent(windowHandle)
    glfwShowWindow(windowHandle)

    GL.createCapabilities()

    glfwSwapInterval(1) // enables V-Sync


    val rom = CPU::class.java.getResourceAsStream("/SCTEST.chip8").buffered().use { it.readBytes() }
    val memory = Memory()
    memory.fillRam(rom)
    val input = Input()
    connectInput(input, windowHandle)
    val display = Display(memory)
    val cpu = CPU(memory, display, input)
    display.init()
    while(!glfwWindowShouldClose(windowHandle)) {
        glfwPollEvents()
        repeat(10000) { // 1000*60 instructions per second approx.
            cpu.executeNextInstruction()
        }

        if(memory.delayTimerRegister > 0) {
            memory.delayTimerRegister--
        }
        if(memory.soundTimerRegister > 0) {
            memory.soundTimerRegister--
        }

        display.drawToScreen()
        glfwSwapBuffers(windowHandle)
    }
}

fun connectInput(input: Input, windowHandle: Long) {
    glfwSetKeyCallback(windowHandle, object : GLFWKeyCallback() {
        override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
            val index = when(key) {
                in GLFW_KEY_1..GLFW_KEY_3 -> key - GLFW_KEY_1
                GLFW_KEY_4 -> 3

                GLFW_KEY_Q -> 4
                GLFW_KEY_W -> 5
                GLFW_KEY_E -> 6
                GLFW_KEY_R -> 7

                GLFW_KEY_A -> 8
                GLFW_KEY_S -> 9
                GLFW_KEY_D -> 10
                GLFW_KEY_F -> 11

                GLFW_KEY_Z -> 12
                GLFW_KEY_X -> 13
                GLFW_KEY_C -> 14
                GLFW_KEY_V -> 15
                else -> -1
            }
            if(index < 0)
                return
            if (action == GLFW_PRESS) {
                input.onPressed(index)
            } else if (action == GLFW_RELEASE) {
                input.onReleased(index)
            }
        }
    })
}
