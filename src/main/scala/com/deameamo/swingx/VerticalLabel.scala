package com.deameamo.swingx

import java.awt.{Graphics, Graphics2D}
import javax.swing.JPanel

class VerticalLabel extends JPanel {
  
  var text: String = ""
  
  def setText(text: String): Unit = this.text = text
  
  var xTranslate = 0
  var yTranslate = 0

  override def paintComponent(g: Graphics) {
    val g2 = g.asInstanceOf[Graphics2D]
    g2.rotate((3.0 / 2) * Math.PI, getWidth / 2.0, getHeight / 2.0)
    g2.translate(-getHeight / 2.0 + xTranslate, yTranslate)
    g2.drawString(text, getWidth  / 2, getHeight  / 2)
  }
}