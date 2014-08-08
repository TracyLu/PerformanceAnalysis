package net.imadz.performance

import net.imadz.performance.graph.PGraph
import scala.io.Source
import java.awt._
import scala.swing._
import scalaz.std.string._
import scala.collection.immutable.List

/**
 * Created by geek on 14-8-8.
 */
object PerformanceDataUI extends SimpleSwingApplication {

  val perfData: List[List[Double]] = {

    val data = Source.fromFile("testdata/handled/autoincremental_io.log").getLines().drop(1).map { line =>
      parseDouble(line.split("\t")(7)).toOption.get
    }.toList

    List(data)
  }

  def top = new MainFrame {
    this.preferredSize = Toolkit.getDefaultToolkit().getScreenSize();
    contents = new BoxPanel(Orientation.Vertical) {
      contents += scala.swing.Component.wrap(new PGraph(perfData))
      contents += scala.swing.Component.wrap(new PGraph(perfData))
      contents += scala.swing.Component.wrap(new PGraph(perfData))
    }
  }

}
