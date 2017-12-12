package org.jglr.chip8

import unsigned.Ubyte
import unsigned.Ushort
import unsigned.ub
import unsigned.us

class Memory {

    val ram = Array(4096) {
        0.ub
    }

    val registers = Array(16) {
        0.ub
    }

    var I = 0.us
    var delayTimerRegister = 0.ub
    var soundTimerRegister = 0.ub
    var PC = 0.us
    var SP = 0.ub
    val stack = Array(16) {
        0.us
    }

    fun incrPC(): Ushort {
        val value = PC
        PC++
        return value
    }

    fun readRAM() = ram[incrPC().toInt()]

    fun push(value: Ushort) {
        stack[SP.toInt()] = value
        SP++
    }

    fun pop(): Ushort {
        val value = stack[SP.toInt()]
        SP--
        return value
    }

}