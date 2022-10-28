import java.io.File

fun main(args: Array<String>) {
  // aoc2015.day1.complex1()
  aoc2015.day1.complex2()
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
}
