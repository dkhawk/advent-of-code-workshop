package aoc2015.day4

import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.yield

fun complex1(testing: Boolean) {
  val testInputs = listOf(
    "abcdef" to 609043,
    "pqrstuv" to 1048970,
  )

  if (testing) {
    testInputs.forEach { (key, expected) ->
      val result = mine(key)
      if (result != expected) {
        println("expected $expected, actual $result")
      }
    }
  } else {
    println(mine("bgvyzdsv"))
  }
}

fun complex2(testing: Boolean) {
  val testInputs = listOf(
    "abcdef" to 609043,
    "pqrstuv" to 1048970,
  )

  if (testing) {
    testInputs.forEach { (key, expected) ->
      val result = mine(key, numZeros = 6)
      if (result != expected) {
        println("expected $expected, actual $result")
      }
    }
  } else {
    println(mine("bgvyzdsv", numZeros = 6))
  }
}

private fun mine(key: String, numZeros: Int = 5): Int = runBlocking {
  val start = "0" * numZeros
  val numJobs = 10
  val channel = Channel<Int>()
  val atomicInteger = AtomicInteger(numJobs)

  val mutex = Mutex()

  var foundAnswer = Int.MAX_VALUE
  val milestoneStep = 1000
  var nextMilestone = milestoneStep

  val jobs = (0 until numJobs).map {
    launch(Dispatchers.Default) {
      val md = MessageDigest.getInstance("MD5")
      var answer = it
      while (answer < foundAnswer) {
        val trial = createString(key, answer)
        val hash = hash(md, trial)
        if (hash.startsWith(start)) {
          channel.send(answer)
          break
        } else {
          answer += numJobs
          if (answer > nextMilestone) {
            atomicInteger.getAndDecrement()
            yield()
          }
        }
      }
      println("max number tested: $answer")
    }
  }

  val supervisor = launch {
    while (foundAnswer == Int.MAX_VALUE) {
      while (atomicInteger.get() > 0) {
        delay(1L)
      }

      atomicInteger.set(numJobs)
      nextMilestone += milestoneStep
    }
  }

  val answerJob = launch {
    val results = mutableListOf<Int>()
    while (isActive) {
      results.add(channel.receive())
      foundAnswer = results.minOrNull() ?: foundAnswer
    }
  }

  jobs.forEach { it.cancelAndJoin() }
  answerJob.cancel()
  supervisor.cancel()
  foundAnswer
}

private operator fun String.times(reps: Int): String {
  val container = mutableListOf<String>()
  repeat(reps) {
    container.add(this)
  }

  return container.joinToString("")
}

fun hash(md: MessageDigest, trial: String): String {
  return md.digest(trial.toByteArray()).joinToString("") { "%02x".format(it) }
}

fun createString(key: String, value: Int): String {
  return key + value.toString()
}
