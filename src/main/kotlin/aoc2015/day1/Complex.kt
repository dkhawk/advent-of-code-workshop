package aoc2015.day1

import Input
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun complex1() = runBlocking {
  val input = Input.readFile(2015, 1)

  val upJob = async {
    val c = input.count { it == '(' }
    c
  }

  val downJob = async {
    delay(50)
    val c = input.count { it == ')' }
    c
  }

  val counter = launch {
    var i = 0
    while (true) {
      println(i++)
      delay(100)
    }
  }

  val answer = upJob.await() - downJob.await()
  println(answer)
  counter.cancel()
}

fun complex2() = runBlocking {
  // val test1 = "()())" to 5
  // part2test(test1.first, test1.second)

  val input = Input.readFile(2015, 1)
  part2test(input)
}

suspend fun part2test(input: String, expected: Int? = null) {
  val flow = flow {
    input.forEach { emit(it) }
  }

  var floor = 0
  var index = 0

  flow
    .takeWhile { floor != -1 }
    .map {
      when (it) {
        '(' -> 1
        ')' -> -1
        else -> 0
      }
    }.withIndex()
    .collect() {
      floor += it.value
      index = it.index
    }

  val position = index + 1
  if (expected != null) {
    if (position != expected) {
      println("Expect $expected, actual $position")
    } else {
      println("Success")
    }
  } else {
    println("floor: $floor")
    println("position: $position")
  }
}
