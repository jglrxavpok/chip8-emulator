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

    val digitsStart = 0x0

    var I = 0.us
    var delayTimerRegister = 0.ub
    var soundTimerRegister = 0.ub
    var PC = 0x200.us
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
        SP--
        return stack[SP.toInt()]
    }

    fun fillRam(rom: ByteArray) {
        for(i in 0 until rom.size) {
            ram[i+0x200] = rom[i].ub
        }
    }

    fun dumpRegisters() {
        println("=== START REGISTERS ===")
        for(i in 0..15) {
            println("V${Integer.toHexString(i)} = 0x${Integer.toHexString(registers[i].toInt())}")
        }
        println("=== END REGISTERS ===")
    }

}