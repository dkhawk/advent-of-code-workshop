package aoc2015.day3

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun complex1(testing: Boolean) = runBlocking {
  if (testing) {
    val testInputs = mapOf(
      ">" to 2,
      "^>v<" to 4,
      "^v^v^v^v^v" to 2
    )

    testInputs.forEach { (input, expected) ->
      val totalVisited = runScenario(input)
      println(totalVisited)
      if (totalVisited != expected) {
        println("Oops: $totalVisited != $expected")
      }
    }
  } else {
    val input = Input.readFile(2015, 3)
    val totalVisited = runScenario(input)
    println(totalVisited)
  }
}

fun complex2(testing: Boolean) = runBlocking {
  if (testing) {
    val testInputs = mapOf(
      "^v" to 3,
      "^>v<" to 3,
      "^v^v^v^v^v" to 11
    )

    testInputs.toList().drop(2).take(1).forEach { (input, expected) ->
      val totalVisited = runScenario2b(input)
      println(totalVisited)
      if (totalVisited != expected) {
        println("Oops: $totalVisited != $expected")
      }
    }
  } else {
    val input = Input.readFile(2015, 3)
    val totalVisited = runScenario2a(input)
    println(totalVisited)
  }
}

private suspend fun runScenario(input: String): Int {
  val elf = Elf(input)
  val santa = Santa()
  visitedPlaces.clear()
  visitedPlaces.add(Vector(0, 0))

  santa.follow(elf.getRadio())

  return visitedPlaces.size
}

private suspend fun runScenario2a(input: String): Int {
  val elf = Elf(input)
  val santa = Santa(0)
  val roboSanta = Santa(1)
  visitedPlaces.clear()
  visitedPlaces.add(Vector(0, 0))

  santa.follow(elf.getRadio())
  roboSanta.follow(elf.getRadio())

  return visitedPlaces.size
}

private suspend fun runScenario2b(input: String): Int {
  val elf = Elf(input)
  visitedPlaces.clear()
  visitedPlaces.add(Vector(0, 0))

  val santaJob = GlobalScope.launch {
    val santa = Santa(0)
    santa.follow(elf.transmitter)
  }

  val roboJob = GlobalScope.launch {
    val santa = Santa(1)
    santa.follow(elf.transmitter)
  }

  val elfJob = GlobalScope.launch {
    delay(100L)
    elf.transmitDirections(1)
  }

  // roboSanta.follow(elf.transmitter)

  println("joining")
  santaJob.join()
  println("joining 2")
  roboJob.join()
  println("joined")
  elfJob.cancel()

  return visitedPlaces.size
}

class Elf(private val moves: String) {
  private val directions = moves.mapNotNull {
    directionMap[it]
  }

  fun getRadio(): Flow<Vector> {
    return flow {
      directions.forEach { emit(it) }
    }
  }

  private val _transmitter = MutableSharedFlow<Vector>() // private mutable shared flow
  val transmitter = _transmitter.asSharedFlow() // publicly exposed as read-only shared flow

  suspend fun transmitDirections(numSubscribers: Int) {
    directions.forEach {
      println("elf transmitting $it")
      // delay(10L)
      _transmitter.emit(it)
    }
    _transmitter.emit(Terminator)
    _transmitter.emit(Terminator)

    // _transmitter.subscriptionCount
    //   .map { count ->
    //     println("subscribers: $count")
    //     count >= numSubscribers
    //   } // map count into active/inactive flag
    //   .distinctUntilChanged() // only react to true<->false changes
    //   .onEach { isActive -> // configure an action
    //     if (isActive) {
    //       directions.forEach {
    //         println("elf transmitting $it")
    //         _transmitter.emit(it)
    //       }
    //       _transmitter.emit(Terminator)
    //     }
    //   }
  }
}

/*
north (^), south (v), east (>), or west (<)
 */

val visitedPlaces = mutableSetOf(
  Vector(0, 0)
)

class Santa(val modulo: Int? = null) {
  var currentLocation = Vector(0, 0)

  fun move(vector: Vector) {
    currentLocation += vector
    visitedPlaces.add(currentLocation)
  }

  suspend fun follow(radio: Flow<Vector>) {
    radio.withIndex().filter {
      modulo == null || (it.index % 2 == modulo)
    }.takeWhile {
      val notTerm = it.value != Terminator
      if (!notTerm) {
        println("Got terminator")
      }
      notTerm
    }.collect {
      println("$modulo collected: $it")
      val old = currentLocation
      move(it.value)
      delay(5L - (modulo ?: 0))
      // println("$modulo moved from $old to $currentLocation via $it")
    }
  }
}

data class Vector(val x: Int, val y: Int) {
  operator fun plus(vector: Vector): Vector = Vector(x + vector.x, y + vector.y)
}

val North = Vector(0, -1)
val South = Vector(0, 1)
val East = Vector(1, 0)
val West = Vector(-1, 0)

val Terminator = Vector(Int.MAX_VALUE, Int.MAX_VALUE)

val directionMap = mapOf(
  '^' to North,
  'v' to South,
  '<' to West,
  '>' to East
)