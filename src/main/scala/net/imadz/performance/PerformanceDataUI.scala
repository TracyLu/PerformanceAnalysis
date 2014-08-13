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

  val header = Source.fromFile(sources.head).getLines().take(1).toList.head.split("\t")
  val validcolumns = header.drop(1)
  val validcolumnpairs = 1 to header.length zip validcolumns

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

    private val gridPanel: GridPanel = new GridPanel(validcolumns.length, 1) {
      validcolumnpairs foreach (
        x => contents += wrap(new PGraph(sourceMetadata(x._2), perfData(x._1)))
        )
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
