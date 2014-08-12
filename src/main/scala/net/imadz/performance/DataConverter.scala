package net.imadz.performance

/**
 * Created by Scala on 14-8-4.
 */

import scalaz.stream._
import scalaz.concurrent.Task

object DataConverter{
  def space2tab: (String) => String = line => line.replaceAll(" +", "\t")

  def ioconverterfunc(sourceFileName: String, targetFileName: String): Task[Unit] = {
    this.ioconverterfunc(0, 0, sourceFileName, targetFileName)
  }
  def ioconverterfunc(dropLines: Int, wantedLines: Int, sourceFileName: String, targetFileName: String): Task[Unit]= {
    def linesR: Process[Task, String] = io.linesR(sourceFileName)
    val beforeHeader: Int = 2
    val headerLine: Int = 1

    val header: Process[Task, String] = linesR.drop(beforeHeader).take(headerLine).map {
      space2tab
    }
    var data: Process[Task, String] = linesR
      .filter { x => !x.startsWith("Linux") && !x.trim.isEmpty && !x.startsWith("Device") && !x.contains("%")}
      .map(space2tab)
    if (dropLines > 0) data = data.drop(dropLines)
    if (wantedLines > 0) data = data.take(wantedLines)

    header ++ data intersperse ("\n") pipe (text.utf8Encode) to (io.fileChunkW(targetFileName)) run
  }

}