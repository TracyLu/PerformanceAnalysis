/**
 * Created by Scala on 14-8-4.
 */

import scalaz.stream._
import scalaz.concurrent.Task

object DataImport extends App {

  val sourceFileName = "testdata/autoincremental_io.log";
  val targetFileName = "testdata/handled/autoincremental_io.log";
  var result: Stream[Iostatx] = Stream()

  val ioconverter: Task[Unit] = ioconverterfunc(8, 415, sourceFileName, targetFileName)

  ioconverter.run

  def space2tab: (String) => String = line => line.replaceAll(" +", "\t")

  def ioconverterfunc(dropLines: Int, wantedLines: Int, sourceFileName: String, targetFileName: String) = {
    def linesR: Process[Task, String] = io.linesR(sourceFileName)
    val beforeHeader: Int = 2
    val headerLine: Int = 1

    val header: Process[Task, String] = linesR.drop(beforeHeader).take(headerLine).map {
      space2tab
    }
    val data: Process[Task, String] = linesR
      .filter { x => !x.startsWith("Linux") && !x.trim.isEmpty && !x.startsWith("Device")}
      .map(space2tab)
      .drop(dropLines)
      .take(wantedLines)

    header ++ data intersperse ("\n") pipe (text.utf8Encode) to (io.fileChunkW(targetFileName)) run
  }
}
