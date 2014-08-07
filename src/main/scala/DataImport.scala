/**
 * Created by Scala on 14-8-4.
 */

import java.io.{RandomAccessFile, BufferedReader, FileReader, File}
import java.util.regex.Pattern
import scala.io.Source
import scala.Some
import scalaz.std.string._
import scalaz.stream._
import scalaz.concurrent.Task
import scalaz.stream.process1._
import org.apache.commons.io._

object DataImport extends App{

  val sourceFileName = "/Users/Scala/Workspaces/Logs/20140805/select_copy/autoincremental_io.log";
  val targetFileName = "/Users/Scala/Workspaces/Logs/20140805/select_copy_handled/autoincremental_io_withdrop.log";
  var result:Stream[Iostatx] = Stream()

  val ioconverter: Task[Unit] =ioconverterfunc(8, 415,sourceFileName, targetFileName)

  // at the end of the universe...
  ioconverter.run

//  val f = new RandomAccessFile(new File(targetFileName), "rw");
//  f.seek(0); // to the beginning
//  f.write(getExpectedLine(3, sourceFileName).getBytes());
//  f.close();


//  /Users/Scala/Workspaces/Logs/20140805/select_copy_handled/autoincremental_io_withdrop.log
  def ioconverterfunc(dropLines: Int, wantedLines: Int, sourceFileName: String, targetFileName: String) = {
      io.linesR(sourceFileName).drop(4).map(line => line.replaceAll(" +", "\t")).take(1).++(io.linesR(sourceFileName)
      .filter(x => !x.startsWith("Linux") && !x.trim.isEmpty && !x.startsWith("Device"))
      .map(line => line.replaceAll(" +", "\t"))
      .intersperse("\n")
      .drop(dropLines * 2)
      .take(wantedLines * 2))
      .pipe(text.utf8Encode)
      .to(io.fileChunkW(targetFileName)).
      run
  }

//  def getExpectedLine(expectedlineNumber: Int, fileName: String) = {
//    val it = IOUtils.lineIterator(
//      new BufferedReader(new FileReader(fileName)));
//    var lineNumber = 0
//
//    var line = ""
//    def expectedLine (){
//        while (it.hasNext()) {
//        lineNumber += 1
//          println("lineNumber:" + lineNumber)
//        line = it.next().toString + "\n"
//          println(line)
//        if (lineNumber == expectedlineNumber)
//          return
//      }
//    }
//    expectedLine
//    line.replaceAll(" +", "\t")
//  }
//
//  println (getExpectedLine(3, sourceFileName))

//  for (line <- Source.fromFile(filename).getLines()) {
//    if(!line.contains("sda")) ""
//    else {
//      val tempResult:Array[String] = line.split(" +")
//
//      1 until tempResult.length foreach {
//        x => parseDouble(tempResult(x))
//      }
//
//
//      val rrqms:Double =  parseDouble(tempResult(1)).toOption.get
//      val wrqms: Double = parseDouble(tempResult(2)).toOption.get
//      val rs: Double = parseDouble(tempResult(3)).toOption.get
//      val ws:Double = parseDouble(tempResult(4)).toOption.get
//      val rKBs: Double = parseDouble(tempResult(5)).toOption.get
//      val wkBs: Double = parseDouble(tempResult(6)).toOption.get
//      val avgrqsz:Double = parseDouble(tempResult(7)).toOption.get
//      val avgqusz:Double = parseDouble(tempResult(8)).toOption.get
//      val await:Double = parseDouble(tempResult(9)).toOption.get
//      val rawait:Double = parseDouble(tempResult(10)).toOption.get
//      val wawait:Double = parseDouble(tempResult(11)).toOption.get
//      val svctm:Double = parseDouble(tempResult(12)).toOption.get
//      val util:Double = parseDouble(tempResult(13)).toOption.get
//      val tempRowResult = new Iostatx(rrqms, wrqms, rs, ws, rKBs, wkBs, avgrqsz, avgqusz, await, rawait, wawait, svctm, util)
//
//      result = tempRowResult #:: result
//    }
//  }
//
//
//
//  result.take(1).toString()

}
