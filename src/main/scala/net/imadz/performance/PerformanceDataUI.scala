package net.imadz.performance

import scala.io.Source
import scala.swing._
import scalaz.std.string._
import scala.collection.immutable.List
import net.imadz.performance.metadata.SourceMetadata
import net.imadz.performance.graph.PGraph
import net.imadz.performance.metadata.DiskIO
import scala.swing.Component._
import java.awt.image.BufferedImage
import java.awt.Graphics2D
import javax.imageio.ImageIO
import java.io.File

/**
 * Created by geek on 14-8-8.
 */
object PerformanceDataUI extends SimpleSwingApplication {

  val sourceFile1 = "testdata/Query_against_Binary_UUID/io.log.updated"
  val sourceFile2 = "testdata/Query_against_AutoIncremental_PK/io.log.updated"
  val sourceFile3 = "testdata/Query_against_16Char/io.log.updated"

  val sources = sourceFile1 :: sourceFile2 :: sourceFile3 :: Nil

  def perfData(col: Int): List[(String, List[Double])] = {

    val data = sources map { source =>
      Source.fromFile(source).getLines().drop(1).map { line =>
        parseDouble(line.split("\t")(col)).toOption.get
      }.toList
    }

    sources zip data

  }

  def sourceMetadata(dimensionName: String) = new SourceMetadata(DiskIO(), dimensionName)


  def top = new MainFrame {


    private val gridPanel: GridPanel = new GridPanel(14, 1) {
      contents += wrap(new PGraph(sourceMetadata("%util"), perfData(13)))
      contents += wrap(new PGraph(sourceMetadata("svctm"), perfData(12)))
      contents += wrap(new PGraph(sourceMetadata("w_await"), perfData(11)))
      contents += wrap(new PGraph(sourceMetadata("r_await"), perfData(10)))
      contents += wrap(new PGraph(sourceMetadata("await"), perfData(9)))
      contents += wrap(new PGraph(sourceMetadata("avgqu-sz"), perfData(8)))
      contents += wrap(new PGraph(sourceMetadata("avgrq-sz"), perfData(7)))
      contents += wrap(new PGraph(sourceMetadata("wKB/s"), perfData(6)))
      contents += wrap(new PGraph(sourceMetadata("rKB/s"), perfData(5)))
      contents += wrap(new PGraph(sourceMetadata("w/s"), perfData(4)))
      contents += wrap(new PGraph(sourceMetadata("r/s"), perfData(3)))
      contents += wrap(new PGraph(sourceMetadata("wrqm/s"), perfData(2)))
      contents += wrap(new PGraph(sourceMetadata("rrqm/s"), perfData(1)))
    }
    contents = new scala.swing.ScrollPane(gridPanel)

    size = new Dimension(800, 600)
    visible = true

    val bi = new BufferedImage(gridPanel.size.getWidth.toInt, gridPanel.size.getHeight.toInt, BufferedImage.TYPE_INT_ARGB);
    val g2d = bi.createGraphics();
    gridPanel.paint(g2d);
    ImageIO.write(bi, "PNG", new File("frame.png"));
  }

}
