package org.jglr.chip8

import unsigned.Ubyte
import unsigned.Ushort
import unsigned.toUbyte
import unsigned.ub

class CPU(val memory: Memory) {

    fun executeNextInstruction() {
        val highInstruction = memory.readRAM()
        val lowInstruction = memory.readRAM()
        val instruction = highInstruction.toUshort() shl 8 or lowInstruction.toUshort()
        val instructionInt = instruction.toInt()
        when(instructionInt) {
            0x00E0 -> cls()
            in 0x1000..0x1FFF -> jp(instruction and 0xFFF)
            in 0x2000..0x2FFF -> call(instruction and 0xFFF)
            in 0x3000..0x3FFF -> se(highInstruction and 0xF, lowInstruction.toUbyte())
            in 0x4000..0x4FFF -> sne(highInstruction and 0xF, lowInstruction.toUbyte())
            in 0x5000..0x5FFF -> seRegisters(highInstruction and 0xF, lowInstruction and 0xF0 shr 1)
            in 0x6000..0x6FFF -> ld(highInstruction and 0xF, lowInstruction)
            in 0x6000..0x6FFF -> add(highInstruction and 0xF, lowInstruction)
            in 0x8000..0x8FFF -> {
                val register1 = highInstruction and 0xF
                val register2 = lowInstruction and 0xF0 shr 1
                when {
                    instructionInt and 1 == 0 -> ld(register1, memory.registers[register2])
                    instructionInt and 1 == 1 -> or(register1, register2)
                    instructionInt and 1 == 2 -> and(register1, register2)
                    instructionInt and 1 == 3 -> xor(register1, register2)
                    instructionInt and 1 == 4 -> addRegisters(register1, register2)
                    instructionInt and 1 == 5 -> sub(register1, register2)
                    instructionInt and 1 == 6 -> shr(register1)
                    instructionInt and 1 == 7 -> subn(register1, register2)
                    instructionInt and 1 == 8 -> shl(register1)
                    instructionInt and 1 == 9 -> sne(register1, memory.registers[register2])
                }
            }
            in 0xA000..0xAFFF -> ldi(instruction and 0xFFF)
            in 0xB000..0xBFFF -> jp(instruction and 0xFFF + memory.registers[0].toInt())
            in 0xC000..0xCFFF -> rnd(instruction and 0xFFF, lowInstruction)
        }
    }

    fun rnd(register: Ushort, mask: Ubyte) {
        val b = Math.floor(Math.random()*255).toInt().toUbyte()
        memory.registers[register] = b and mask
    }

    fun ldi(value: Ushort) {
        memory.I = value
    }

    fun shl(x: Ubyte) {
        val vx = memory.registers[x]
        memory.registers[0xF] = if((vx.toInt() and 0b1000000) != 0) 1.ub else 0.ub
        memory.registers[x] = vx*2
    }

    fun subn(x: Ubyte, y: Ubyte) {
        val vx = memory.registers[x]
        val vy = memory.registers[y]
        memory.registers[0xF] = (1 - if(vx > vy) 1 else 0).ub // not borrow
        memory.registers[x] -= vy
    }

    fun shr(x: Ubyte) {
        val vx = memory.registers[x]
        memory.registers[0xF] = vx and 0x1 // not borrow
        memory.registers[x] = vx/2
    }

    fun sub(x: Ubyte, y: Ubyte) {
        val vx = memory.registers[x]
        val vy = memory.registers[y]
        memory.registers[0xF] = (if(vx > vy) 1 else 0).ub // not borrow
        memory.registers[x] -= vy
    }

    fun addRegisters(register1: Ubyte, register2: Ubyte) {
        val v1 = memory.registers[register1]
        val v2 = memory.registers[register2]
        if(v1.toInt() and 0xF0 + v2.toInt() and 0xF0 >= 0x100) {
            memory.registers[0xF] = 1.ub
        } else {
            memory.registers[0xF] = 0.ub
        }
        memory.registers[register1] += v2
    }

    fun and(register1: Ubyte, register2: Ubyte) {
        memory.registers[register1] = memory.registers[register1] and memory.registers[register2]
    }

    fun xor(register1: Ubyte, register2: Ubyte) {
        memory.registers[register1] = memory.registers[register1] xor memory.registers[register2]
    }

    fun or(register1: Ubyte, register2: Ubyte) {
        memory.registers[register1] = memory.registers[register1] or memory.registers[register2]
    }

    fun add(register: Ubyte, value: Ubyte) {
        memory.registers[register] += value
    }

    fun ld(register: Ubyte, value: Ubyte) {
        memory.registers[register] = value
    }

    fun seRegisters(register1: Ubyte, register2: Ubyte) {
        if(memory.registers[register1] == memory.registers[register2]) {
            memory.PC += 2 // skip next instruction
        }
    }

    fun sne(register: Ubyte, value: Ubyte) {
        if(memory.registers[register] != value) {
            memory.PC += 2 // skip next instruction
        }
    }

    fun se(register: Ubyte, value: Ubyte) {
        if(memory.registers[register] == value) {
            memory.PC += 2 // skip next instruction
        }
    }

    fun call(address: Ushort){
        memory.PC++
        memory.push(memory.PC)
        memory.PC = address
    }

    fun jp(address: Ushort) {
        memory.PC = address
    }

    fun cls(): Unit = TODO("clear screen")

    fun ret() {
        memory.PC = memory.pop()
    }
}

private operator fun <T> Array<T>.set(ubyte: Ubyte, value: T) {
    this[ubyte.toInt()] = value
}

private operator fun <T> Array<T>.get(ubyte: Ubyte): T = this[ubyte.toInt()]

private operator fun <T> Array<T>.set(ushort: Ushort, value: T) {
    this[ushort.toInt()] = value
}

private operator fun <T> Array<T>.get(ushort: Ushort): T = this[ushort.toInt()]
