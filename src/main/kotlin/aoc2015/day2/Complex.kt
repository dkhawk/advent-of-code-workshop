package aoc2015.day2

import kotlin.system.measureNanoTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun complex1() {
  val input = Input.readLines(2015, 2)
  // val input = listOf("2x3x4", "1x1x10")
  val boxes = input.toSortBoxes()

  val simpleMillis = measureNanoTime {
    val total = requiredPaperSimple(boxes)
    println(total)
  }
  println("simple ms: ${simpleMillis/1_000_000f}")

  // val mapMillis = measureNanoTime {
  //   val total = parseInputWithMap(boxes)
  //   println(total)
  // }
  // println("map ms: ${mapMillis/1_000_000f}")

  val coroutinesMs = measureNanoTime {
    val total = parseInputCoroutines(boxes)
    println(total)
  }
  println("coroutine ms: ${coroutinesMs/1_000_000f}")

  // println(ribbonCalc(boxes))
  // println(ribbonCalcCoroutines(boxes))
}

private fun List<String>.toSortBoxes(): List<Box> {
  return this.map {
    val dims = it.split("x").map(String::toInt).sorted()
    Box(dims[0], dims[1], dims[2])
  }
}

data class Box(
  val length: Int,
  val width: Int,
  val height: Int,
) {
  suspend fun requiredPaper(): Int {
    val minArea = length * width
    val sideAreas = listOf(
      minArea,
      width * height,
      height * length
    )

    delay(10)
    return sideAreas.sum() * 2 + minArea
  }

  fun requiredRibbon(): Int {
    val smallestPerimeter = 2 * (length + width)
    val volume = length * width * height
    return smallestPerimeter + volume
  }
}

fun parseInputCoroutines(input: List<Box>) = runBlocking {
  input.map { box -> async(Dispatchers.Default) { box.requiredPaper() } }.awaitAll().sum()
}

fun requiredPaperSimple(input: List<Box>): Int {
  return runBlocking {  input.sumOf { box ->  box.requiredPaper() } }
}

// fun parseInputWithMap(input: List<Box>): Int {
//   val boxMap = mutableMapOf<Box, Int>()
//   return input.sumOf { box ->
//     boxMap.getOrElse(box) {
//       box.requiredPaper().also { boxMap[box] = it }
//     }
//   }
// }

fun ribbonCalc(boxes: List<Box>): Int = boxes.sumOf { box -> box.requiredRibbon() }

fun ribbonCalcCoroutines(boxes: List<Box>): Int = runBlocking {
  boxes.map { box -> async { box.requiredRibbon() } }.awaitAll().sum()
}