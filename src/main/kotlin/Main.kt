import java.io.File

fun main(args: Array<String>) {
  val testing = args.contains("test")

  // aoc2015.day1.complex1()
  // aoc2015.day1.complex2()

  // aoc2015.day3.complex1(testing)
  // aoc2015.day3.complex2(testing)
  // aoc2015.day4.complex1(testing)
  aoc2015.day4.complex2(testing)
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
