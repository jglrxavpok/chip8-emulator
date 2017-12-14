package org.jglr.chip8

import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import unsigned.Ushort
import unsigned.ub
import unsigned.us
import kotlin.experimental.xor

class Display(val memory: Memory) {

    companion object {
        val BLACK: Byte = 0
        val WHITE: Byte = 1
    }

    val screen = ByteArray(64*32)

    fun draw(x: Int, y: Int, n: Int) {
        memory.registers[0xF] = 0.ub
        val address = memory.I.toInt()
        for(yline in 0 until n) {
            var data = memory.ram[address+yline].toInt()
            for(xline in 0 until 8) {
                val pixel = (data and 0x80)
                if(pixel != 0) {
                    val posx = (x+xline) % 64
                    val posy = (y+yline) % 32
                    val index = posx+posy*64
                    if(screen[index] == WHITE) {
                        memory.registers[0xF] = 1.ub // set collision flag
                    }
                    screen[index] = /*screen[index] xor */1
                } else {
                }
                data = data shl 1
            }
        }
    }

    fun clear() {
        for(i in 0 until screen.size) {
            screen[i] = BLACK
        }
    }

    fun fillDigits() {
        var i = 0
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub

        memory.ram[memory.digitsStart + (i++)] = 0x20.ub
        memory.ram[memory.digitsStart + (i++)] = 0x60.ub
        memory.ram[memory.digitsStart + (i++)] = 0x20.ub
        memory.ram[memory.digitsStart + (i++)] = 0x20.ub
        memory.ram[memory.digitsStart + (i++)] = 0x70.ub

        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x10.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x80.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub

        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x10.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x10.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub

        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x10.ub
        memory.ram[memory.digitsStart + (i++)] = 0x10.ub

        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x80.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x10.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub

        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x80.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub

        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x10.ub
        memory.ram[memory.digitsStart + (i++)] = 0x20.ub
        memory.ram[memory.digitsStart + (i++)] = 0x40.ub
        memory.ram[memory.digitsStart + (i++)] = 0x40.ub

        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub

        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x10.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub

        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub

        memory.ram[memory.digitsStart + (i++)] = 0xE0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0xE0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0xE0.ub

        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x80.ub
        memory.ram[memory.digitsStart + (i++)] = 0x80.ub
        memory.ram[memory.digitsStart + (i++)] = 0x80.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub

        memory.ram[memory.digitsStart + (i++)] = 0xE0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0x90.ub
        memory.ram[memory.digitsStart + (i++)] = 0xE0.ub

        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x80.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x80.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub

        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x80.ub
        memory.ram[memory.digitsStart + (i++)] = 0xF0.ub
        memory.ram[memory.digitsStart + (i++)] = 0x80.ub
        memory.ram[memory.digitsStart + (i++)] = 0x80.ub
    }

    fun locationDigit(index: Int): Ushort {
        return (memory.digitsStart + index*5).us
    }

    fun drawToScreen() {
        updateTexture()
        glClearColor(0f, 0f, 1f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)
        glEnable(GL_TEXTURE_2D)
        glBegin(GL_QUADS)
        glBindTexture(GL_TEXTURE_2D, texID)
        glTexCoord2f(0f, 1f)
        glVertex2f(-1f, -1f)

        glTexCoord2f(1f, 1f)
        glVertex2f(1f, -1f)

        glTexCoord2f(1f, 0f)
        glVertex2f(1f, 1f)

        glTexCoord2f(0f, 0f)
        glVertex2f(-1f, 1f)

        glBindTexture(GL_TEXTURE_2D, 0)
        glEnd()
    }

    fun updateTexture() {
        glBindTexture(GL_TEXTURE_2D, texID)
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.malloc(64*32*3)
            for (pixel in screen) {
                val color = (pixel * 0xF0).toByte()
                buffer.put(color)
                buffer.put(color)
                buffer.put(color)
            }
            buffer.rewind()
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 64, 32, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer)
        }
    }

    private var texID: Int = -1

    fun init() {
        fillDigits()
        texID = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, texID)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        updateTexture()
    }
}