package com.deameamo.swingx

import javax.swing.border.AbstractBorder
import java.awt._

class RoundBorder(
    thickness: Int,
    borderColor: Color,
    outerColor: Color,
    innerColor: Color,
    gradient: Boolean,
    gradientColor: Color) 
    extends AbstractBorder {
  def this(thickness: Int) = this(thickness, new Color(0xF0F0F0), Color.DARK_GRAY, new Color(0xF0F0F0), false, null)
  def this(thickness: Int, borderColor: Color, outerColor: Color, innerColor: Color) = this(thickness, borderColor, outerColor, innerColor, false, null)
  
  override def paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
    val g2 = g.asInstanceOf[Graphics2D]
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
//    g2.setColor(borderColor)
    
    if(gradient) {
      val gradientPaint = new GradientPaint(0, thickness, borderColor, 0, 0, gradientColor, false)
      g2.setPaint(gradientPaint)
    }
    else
      g2.setColor(borderColor)
    g2.fillRect(thickness, 0, width - thickness * 2, thickness)
    g2.fillArc(0, 0, thickness * 2, thickness * 2, 90, 90)
    g2.fillArc(width - thickness * 2, 0, thickness * 2, thickness * 2, 0, 90)
    
    g2.setColor(borderColor)
    g2.fillRect(0, thickness, thickness, height - thickness * 2)
    g2.fillRect(width - thickness, thickness, thickness, height - thickness * 2)
    
    if(gradient) {
      val gradientPaint = new GradientPaint(0, height - thickness, borderColor, 0, height, gradientColor, false)
      g2.setPaint(gradientPaint)
    }
    else
      g2.setColor(borderColor)
    g2.fillRect(thickness, height - thickness, width - thickness * 2, thickness)
    g2.fillArc(0, height - thickness * 2, thickness * 2, thickness * 2, -90, -90)
    g2.fillArc(width - thickness * 2, height - thickness * 2, thickness * 2, thickness * 2, 0, -90)
    
    
    g2.setColor(innerColor)
    g2.drawRect(thickness - 1, thickness - 1, width - thickness * 2 + 1, height - thickness * 2 + 1)
//    g2.drawLine(thickness / 2 - 1, thickness / 2 - 1, width - thickness / 2 + 1, thickness / 2 - 1)
//    g2.drawLine(thickness / 2 - 1, thickness / 2, thickness / 2 - 1, height - thickness / 2)
//    g2.drawLine(width - thickness / 2 + 1, thickness / 2, width - thickness / 2 + 1, height - thickness / 2)
//    g2.drawLine(thickness / 2 - 1, height - thickness / 2 + 1, width - thickness / 2 + 1, height - thickness / 2 + 1)
    
    g2.setColor(outerColor)
    g2.drawLine(0, thickness, 0, height - thickness)
    g2.drawLine(thickness, 0, width - thickness, 0)
    g2.drawLine(width - 1, thickness, width - 1, height - thickness)
    g2.drawLine(thickness, height - 1, width - thickness, height - 1)
    
    g2.drawArc(0, 0, thickness * 2, thickness * 2, 90, 90)
    g2.drawArc(width - thickness * 2 - 1, 0, thickness * 2, thickness * 2, 0, 90)
    g2.drawArc(0, height - thickness * 2 - 1, thickness * 2, thickness * 2, -90, -90)
    g2.drawArc(width - thickness * 2 - 1, height - thickness * 2 - 1, thickness * 2, thickness * 2, 0, -90)
    
  }
  
  override def getBorderInsets(c: Component): Insets = {
    new Insets(thickness, thickness, thickness, thickness)
  }
}