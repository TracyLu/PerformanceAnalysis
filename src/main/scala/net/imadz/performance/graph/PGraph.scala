package net.imadz.performance.graph

import javax.swing.JComponent
import java.awt.{Font, Color, Graphics, Dimension}

/**
 * Created by geek on 14-8-8.
 */
class PGraph(val perfDataSources: List[List[Double]]) extends JComponent {

  val minWidth = 400
  val minHeight = 200

  setMinimumSize(new Dimension(minWidth, minHeight))
  val margin = 30
  val yScaleCount = 10
  val xScaleCount = 30

  def xScaleMax : Int = perfDataSources.map(_.length).foldLeft(0)((acc, x) => Math.max(acc, x))

  def screenSize = getSize(null)

  def imageWidth = screenSize.getWidth() - 2 * margin;

  def imageHeight = screenSize.getHeight() - 2 * margin;

  def yBottomLine = imageHeight + margin toInt

  def yMaxSampleDataSetValue = perfDataSources.flatten.foldLeft(0D)((acc, x) => Math.max(acc, x))
  def xMaxSampleDataSetLength = xScaleMax
  def heightFactor = imageHeight.toDouble / yMaxSampleDataSetValue
  def widthFactor = imageWidth.toDouble / xMaxSampleDataSetLength

  val colors = Array(Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.BLACK)

  def coordernatesOf(source: List[Double]) = {
    val xs: Array[Int] = 1 to source.length map (x => margin + x * widthFactor toInt) toArray
    val ys: Array[Int] = source map (y => yBottomLine - y * heightFactor toInt) toArray

    (xs, ys)
  }

  override def paintComponent(g: Graphics): Unit = {
    drawAxis(g)

    var streamIndex = 0;
    perfDataSources foreach { streamSource =>
      g.setColor(colors(streamIndex))
      val (xs, ys) = coordernatesOf(streamSource)
      g.drawPolyline(xs, ys, streamSource.length)
      streamIndex += 1
    }

  }


  lazy val yScalePixel = (0 until yScaleCount).toList.map { y => imageHeight - y * imageHeight / yScaleCount toInt} toArray

  lazy val xScalePixel = (0 until xScaleCount).toList.map { x => x * imageWidth / xScaleCount} toArray
  lazy val yInterval = yMaxSampleDataSetValue / yScaleCount
  lazy val totalSeconds = xMaxSampleDataSetLength
  lazy val xInterval = totalSeconds.toDouble/ xScaleCount.toDouble

  private def drawAxis(g: Graphics): Unit = {

    //draw x, y axis
    g.setColor(Color.BLACK)
    g.drawLine(margin - 1, margin, margin - 1, yBottomLine + 1 toInt)
    g.drawLine(margin - 1, yBottomLine + 1 toInt, screenSize.getWidth - margin toInt, yBottomLine + 1 toInt)

    val fixedGap = 5
    val font = new Font(Font.SANS_SERIF, Font.PLAIN, 14)
    g.setFont(font)
    val fm = getFontMetrics(font)

    0 to yScaleCount foreach { y =>
      val yScaleValue: Int = y * yInterval toInt
      val chars = String.valueOf(yScaleValue).toCharArray
      val yScaleWidth = fm.charsWidth(chars, 0, chars.length)
      val yScaleX = margin - fixedGap - 1 - yScaleWidth
      val yScaleY = (yBottomLine - y * imageHeight / yScaleCount) toInt

      g.setColor(Color.BLACK)
      g.drawChars(chars, 0, chars.length, yScaleX, yScaleY)
      g.drawLine(margin - 3, yScaleY, margin, yScaleY)
    }

    0 to xScaleCount foreach { x =>
      val xScaleValue: Int = x * xInterval toInt
      val chars = String.valueOf(xScaleValue).toCharArray
      val xScaleHeight = fm.getHeight
      val xScaleX = margin + x * imageWidth / xScaleCount toInt
      val xScaleY = yBottomLine + fixedGap + 1 + xScaleHeight toInt

      g.setColor(Color.BLACK)
      g.drawChars(chars, 0, chars.length, xScaleX, xScaleY)
      g.drawLine(xScaleX, yBottomLine, xScaleX, yBottomLine + 3)
    }
  }

}
