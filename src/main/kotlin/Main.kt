import java.io.File

fun main() {
  // aoc2015.day1.complex1()
  // aoc2015.day1.complex2()
  aoc2015.day2.complex1()
}

object Configuration {
  const val inputDir: String = "/Users/dkhawk/IdeaProjects/codeworkshop/inputs"
}

object Input {
  fun readFile(year: Int, day: Int): String {
    val filename = String.format("%02d.txt", day)
    val path = "${Configuration.inputDir}/$year/$filename"
    return readFile(path)
  }

  fun readFile(fileName: String): String = File(fileName).readText()

  fun readLines(year: Int, day: Int): List<String> {
    val filename = String.format("%02d.txt", day)
    val path = "${Configuration.inputDir}/$year/$filename"
    return readLines(path)
  }

  fun readLines(fileName: String): List<String> = File(fileName).readLines()
}
