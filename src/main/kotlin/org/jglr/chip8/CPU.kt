package org.jglr.chip8

import unsigned.Ubyte
import unsigned.Ushort
import unsigned.toUbyte
import unsigned.ub

class CPU(val memory: Memory, val display: Display, val input: Input) {

    fun executeNextInstruction() {
        val highInstruction = memory.readRAM()
        val lowInstruction = memory.readRAM()
        val instruction = (highInstruction.toUshort() shl 8) or lowInstruction.toUshort()
        val instructionInt = instruction.toInt()
        //println("instruction: ${Integer.toHexString(instructionInt)} ${memory.PC}")
        when(instructionInt) {
            0x00E0 -> cls()
            0x00EE -> ret()
            in 0x1000..0x1FFF -> jp(instruction and 0xFFF)
            in 0x2000..0x2FFF -> call(instruction and 0xFFF)
            in 0x3000..0x3FFF -> se(highInstruction and 0xF, lowInstruction)
            in 0x4000..0x4FFF -> sne(highInstruction and 0xF, lowInstruction)
            in 0x5000..0x5FFF -> seRegisters(highInstruction and 0xF, (lowInstruction shr 1) and 0xF)
            in 0x6000..0x6FFF -> ld(highInstruction and 0xF, lowInstruction)
            in 0x6000..0x6FFF -> add(highInstruction and 0xF, lowInstruction)
            in 0x8000..0x8FFF -> {
                val register1 = highInstruction and 0xF
                val register2 = (lowInstruction shr 1) and 0xF
                when(lowInstruction.toInt() and 0xF) {
                    0 -> ld(register1, memory.registers[register2])
                    1 -> or(register1, register2)
                    2 -> and(register1, register2)
                    3 -> xor(register1, register2)
                    4 -> addRegisters(register1, register2)
                    5 -> sub(register1, register2)
                    6 -> shr(register1)
                    7 -> subn(register1, register2)
                    0xE -> shl(register1)
                }
            }
            in 0x9000..0x9FFF -> sne(highInstruction and 0xF, memory.registers[(lowInstruction shr 1) and 0xF])
            in 0xA000..0xAFFF -> ldi(instruction and 0xFFF)
            in 0xB000..0xBFFF -> jp((instruction and 0xFFF) + memory.registers[0].toInt())
            in 0xC000..0xCFFF -> rnd(highInstruction and 0xF, lowInstruction)
            in 0xD000..0xDFFF -> drw(highInstruction and 0xF, (lowInstruction shr 1) and 0xF, lowInstruction and 0xF)
            in 0xE000..0xEFFF -> {
                val x = highInstruction and 0xF
                when(lowInstruction.toInt() and 0xFF) {
                    0x9E -> skp(x)
                    0xA1 -> sknp(x)
                }
            }
            in 0xF000..0xFFFF -> {
                val x = highInstruction and 0xF
                when(lowInstruction.toInt()) {
                    0x07 -> ld(x, memory.delayTimerRegister)
                    0x0A -> ld_keypress(x)
                    0x15 -> memory.delayTimerRegister = memory.registers[x]
                    0x18 -> memory.soundTimerRegister = memory.registers[x]
                    0x1E -> memory.I += memory.registers[x].toInt()
                    0x29 -> memory.I = display.locationDigit(memory.registers[x].toInt())
                    0x33 -> bcd(x)
                    0x55 -> ldi_range(x)
                    0x65 -> read_range(x)
                }
            }
        }
    }

    fun bcd(x: Ubyte) {
        val value = memory.registers[x]
        memory.ram[memory.I] = value / 100
        memory.ram[memory.I + 1] = value / 10 % 10
        memory.ram[memory.I + 2] = value % 100 % 10
    }

    fun read_range(x: Ubyte) {
        val to = x.toInt()
        for (index in 0..to) {
            memory.registers[index] = memory.ram[memory.I.toInt() + index]
        }
    }

    fun ldi_range(x: Ubyte) {
        val to = x.toInt()
        for (index in 0..to) {
            memory.ram[memory.I.toInt() + index] = memory.registers[index]
        }
    }

    fun ld_keypress(x: Ubyte) {
        memory.registers[x] = input.waitKeypress().ub
    }

    fun sknp(x: Ubyte) {
        if( ! input.isKeyPressed(memory.registers[x].toInt()))
            memory.PC += 2 // skip
    }

    fun skp(x: Ubyte) {
        if(input.isKeyPressed(memory.registers[x].toInt()))
            memory.PC += 2 // skip
    }

    fun drw(x: Ubyte, y: Ubyte, n: Ubyte) {
        display.draw(memory.registers[x].toInt(), memory.registers[y].toInt(), n.toInt())
    }

    fun rnd(register: Ubyte, mask: Ubyte) {
        val b = Math.floor(Math.random()*255).toInt().toUbyte()
        memory.registers[register] = b and mask
    }

    fun ldi(value: Ushort) {
        memory.I = value
    }

    fun shl(x: Ubyte) {
        val vx = memory.registers[x]
        memory.registers[0xF] = if((vx.toInt() and 0x80) != 0) 1.ub else 0.ub
        memory.registers[x] = vx*2
    }

    fun subn(x: Ubyte, y: Ubyte) {
        val vx = memory.registers[x]
        val vy = memory.registers[y]
        memory.registers[0xF] = (if(vx.toInt() < vy.toInt()) 1 else 0).ub // not borrow
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
        memory.registers[0xF] = (if(vx.toInt() > vy.toInt()) 1 else 0).ub // not borrow
        memory.registers[x] -= vy
    }

    fun addRegisters(register1: Ubyte, register2: Ubyte) {
        val v1 = memory.registers[register1]
        val v2 = memory.registers[register2]
        if(v1.toInt() + v2.toInt() >= 256) {
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
        memory.push(memory.PC +2)
        memory.PC = address
    }

    fun jp(address: Ushort) {
        memory.PC = address
    }

    fun cls() {
        display.clear()
    }

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
