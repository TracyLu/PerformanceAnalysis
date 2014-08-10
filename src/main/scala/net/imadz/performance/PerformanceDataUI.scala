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

  def perfData(col: Int): List[(String, List[Double])] = {

    val firstdata = Source.fromFile("/Users/Scala/Workspaces/multitenant-uuid/report/20140810052524/Query_a˜gainst_AutoIncremental_PK/io.log.updated").getLines().drop(1).map { line =>
      parseDouble(line.split("\t")(col)).toOption.get
    }.toList

    val seconddata = Source.fromFile("/Users/Scala/Workspaces/multitenant-uuid/report/20140810052337/Query_against_Binary_UUID/io.log.updated").getLines().drop(1).map { line =>
      parseDouble(line.split("\t")(col)).toOption.get
    }.toList

//    val thirddata = Source.fromFile("testdata/handled/binary_io.log").getLines().drop(1).map { line =>
//      parseDouble(line.split("\t")(9)).toOption.get
//    }.toList
    List(("autoincremental", firstdata),("binary", seconddata))
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
