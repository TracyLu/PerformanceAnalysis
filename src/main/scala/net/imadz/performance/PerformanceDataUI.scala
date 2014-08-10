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

  val perfData: List[(String, List[Double])] = {

    val firstdata = Source.fromFile("testdata/handled/autoincremental_io.log").getLines().drop(1).map { line =>
      parseDouble(line.split("\t")(7)).toOption.get
    }.toList

    val seconddata = Source.fromFile("testdata/handled/binary_io.log").getLines().drop(1).map { line =>
      parseDouble(line.split("\t")(8)).toOption.get
    }.toList

    val thirddata = Source.fromFile("testdata/handled/binary_io.log").getLines().drop(1).map { line =>
      parseDouble(line.split("\t")(9)).toOption.get
    }.toList
    List(("autoincremental", firstdata),("binary", seconddata), ("hex", thirddata))
  }

  val sourceMetadata = new SourceMetadata(DiskIO(), "queesize")

  def top = new MainFrame {
    this.preferredSize = Toolkit.getDefaultToolkit().getScreenSize();
    contents = new BoxPanel(Orientation.Vertical) {
      contents += scala.swing.Component.wrap(new PGraph(sourceMetadata,perfData))
      contents += scala.swing.Component.wrap(new PGraph(sourceMetadata,perfData))
      contents += scala.swing.Component.wrap(new PGraph(sourceMetadata,perfData))
    }
  }

}
