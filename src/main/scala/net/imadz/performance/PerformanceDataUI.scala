package net.imadz.performance

import net.imadz.performance.graph.PGraph
import scala.io.Source
import java.awt._
import scala.swing._
import scalaz.std.string._
import scala.collection.immutable.List
import net.imadz.performance.metadata.{DiskIO, SourceMetadata}

/**
 * Created by geek on 14-8-8.
 */
object PerformanceDataUI extends SimpleSwingApplication {

  var sourceFile1 = ""
  var sourceFile2 = ""
  def setSourceFile1(filePath: String) = sourceFile1 = filePath
  def setSourceFile2(filePath: String)=  sourceFile2 = filePath

  def perfData(col: Int): List[(String, List[Double])] = {

    val firstdata = Source.fromFile(sourceFile1).getLines().drop(1).map { line =>
      parseDouble(line.split("\t")(col)).toOption.get
    }.toList

    val seconddata = Source.fromFile(sourceFile2).getLines().drop(1).map { line =>
      parseDouble(line.split("\t")(col)).toOption.get
    }.toList

    List(("autoincremental", seconddata),("binary", firstdata))
  }

  def sourceMetadata(dimensionName: String) = new SourceMetadata(DiskIO(), dimensionName)

  def top = new MainFrame {
    this.preferredSize = Toolkit.getDefaultToolkit().getScreenSize();
    contents = new BoxPanel(Orientation.Vertical) {
      contents += scala.swing.Component.wrap(new PGraph(sourceMetadata("r/s"),perfData(3)))
      contents += scala.swing.Component.wrap(new PGraph(sourceMetadata("avgqu-sz"),perfData(8)))
      contents += scala.swing.Component.wrap(new PGraph(sourceMetadata("%util"),perfData(13)))
    }
  }

}
