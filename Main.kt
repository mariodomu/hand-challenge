import kotlin.streams.toList
import Instruction.NEXT
import Instruction.PREV
import Instruction.INCR
import Instruction.DECR
import Instruction.PUSH
import Instruction.PULL
import Instruction.DISPLAY

fun main() {
    Mirror().apply("👇🤜👇👇👇👇👇👇👇👉👆👈🤛👉👇👊👇🤜👇👉👆👆👆👆👆👈🤛👉👆👆👊👆👆👆👆👆👆👆👊👊👆👆👆👊")
    println()
    Mirror().apply("👉👆👆👆👆👆👆👆👆🤜👇👈👆👆👆👆👆👆👆👆👆👉🤛👈👊👉👉👆👉👇🤜👆🤛👆👆👉👆👆👉👆👆👆🤜👉🤜👇👉👆👆👆👈👈👆👆👆👉🤛👈👈🤛👉👇👇👇👇👇👊👉👇👉👆👆👆👊👊👆👆👆👊👉👇👊👈👈👆🤜👉🤜👆👉👆🤛👉👉🤛👈👇👇👇👇👇👇👇👇👇👇👇👇👇👇👊👉👉👊👆👆👆👊👇👇👇👇👇👇👊👇👇👇👇👇👇👇👇👊👉👆👊👉👆👊")
}

class Mirror {
    private var index = 0
    private val memory: MutableList<Int> = intArrayOf(0).toMutableList()
    private val stackPush = ArrayDeque<Int>()

    fun apply(instructionSet: String) {
        val instructions = instructionSet.codePoints().toList().map { Instruction[(String(Character.toChars(it)))] }
        var i = 0
        while (i < instructions.size) {
            when (instructions[i]) {
                NEXT -> next()
                PREV -> index--
                INCR -> updateCell { it.inc() }
                DECR -> updateCell { it.dec() }
                PUSH -> {
                    stackPush.addFirst(i)
                    if (memory[index] == 0) {
                        i = findPullFor(i, instructions)
                        stackPush.removeFirst()
                    }
                }
                PULL -> {
                    if (memory[index] != 0) {
                        i = stackPush.first()
                    } else {
                        stackPush.removeFirst()
                    }
                }
                DISPLAY -> print(memory[index].toChar())
            }
            i++
        }
    }

    private fun next() {
        if (memory.size <= ++index) {
            memory.add(0)
        }
    }

    private fun updateCell(f: (Int) -> Int) {
        memory[index] = Math.floorMod(f(memory[index]), 256)
    }

    private fun findPullFor(pushInd: Int, instructions: List<Instruction?>): Int {
        for (i in pushInd until instructions.size) {
            when (instructions[i]) {
                PUSH -> stackPush.addFirst(i)
                PULL -> {
                    if (stackPush.removeFirst() == pushInd)
                        return i
                }
            }
        }
        throw Exception("Malformed Instruction set")
    }
}

enum class Instruction(val code: String) {
    NEXT("👉"),
    PREV("👈"),
    INCR("👆"),
    DECR("👇"),
    PUSH("🤜"),
    PULL("🤛"),
    DISPLAY("👊");

    companion object {
        private val map = Instruction.values().associateBy(Instruction::code)
        operator fun get(value: String) = map[value]
    }
}