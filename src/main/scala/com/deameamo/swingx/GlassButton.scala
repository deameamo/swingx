package com.deameamo.swingx

import java.awt.event.{MouseEvent, MouseListener}
import java.awt.{Graphics, Graphics2D}
import javax.swing.{ImageIcon, JPanel}

import com.deameamo.event.CanFireActionEvent

class GlassButton(command: String, enabledImage: ImageIcon, disabledImage: ImageIcon, width: Int, height: Int)
  extends JPanel with MouseListener with CanFireActionEvent {
  
  def this(command: String, enabledImage: ImageIcon) = this(command, enabledImage, enabledImage, 16, 16)
  def this(command: String, enabledImage: ImageIcon, width: Int, height: Int) = this(command, enabledImage, enabledImage, width, height)
  
  val highlightedMask = new ImageIcon("icons/highlighted.png")
  val pressedMask = new ImageIcon("icons/pressed.png")
  setBounds(0, 0, width, width)
  setOpaque(false)
  
  var state: Int = GlassButton.NORMAL
  var highlighted = false
  var pressed = false
  var exited = false
  var listenerAdded = false
  setEnabled(true)
  
  def setState(state: Int) { this.state = state }
  
////  val colors = Seq(0xffffff, 0xfdfdfd, 0xfbfbfb, 0xf9f9f9, 0xf7f7f7, 0xf6f6f6, 0xf5f5f5, 0xf4f4f4, 0xf4f4f4,
////      0xf4f4f4, 0xf4f4f4, 0xe7e7e7, 0xececec, 0xf2f2f2, 0xf8f8f8, 0xfcfcfc, 0xfefefe, 0xffffff)
//  val colors = Seq(0xe9f0f7, 0xd2dfed, 0xcddbea, 0xc6d6e6, 0xc0d1e3, 0xc0d2e4, 0xbbcee2, 0xb7cce0, 0xb7cce0,
//      0xb7cce0, 0xb7cce0, 0xa8bdd2, 0xacc9d7, 0xb1c6dc, 0xb5cbe2, 0xb7cee5, 0xb8d0e7, 0xd8e5f2)
  
  override def paint(g: Graphics) {
    super.paint(g)
    val g2 = g.asInstanceOf[Graphics2D]
//    
//    val gradientPaint = new GradientPaint(0, 15, new Color(0xB4CDE6), 0, 0, new Color(0xE9F0F7), false)
////    val gradientPaint = new GradientPaint(0, 15, Color.gray, 0, 0, new Color(0xB4CDE6), false)
////    g2.setPaint(gradientPaint)
////    g2.fillRoundRect(0, 0, 19, 18, 5, 5)
//    val bg = 0xB4CDE6
////    val bg = 0xffffff
//    colors.zipWithIndex.foreach{ case (c, i)=> {
//      g2.setColor(new Color(c))
//      g2.drawLine(1, i + 1, 19, i + 1)
//      val rgb = RGB(bg)
//      val a = RGB(c)
//      println(s"r: ${rgb.r - a.r}, g: ${rgb.g - a.g}, b: ${rgb.b - a.b}")
//    }}
////    colors.foreach { x => println(x) }
//    g2.setColor(Color.GRAY)
//    g2.drawRoundRect(0, 0, 19, 18, 5, 5)
//    drawImage(g2, enabledImage)
    
    if(isEnabled) {
      state match {
        case GlassButton.NORMAL =>
          if(highlighted)
            drawImage(g2, highlightedMask)
          if(pressed)
            drawImage(g2, pressedMask)
          drawImage(g2, enabledImage)
        case GlassButton.PRESSED =>
          drawImage(g2, pressedMask)
          drawImage(g2, enabledImage)
        case GlassButton.HIGHLIGHTED =>
          drawImage(g2, highlightedMask)
          drawImage(g2, enabledImage)
      }
    }
    else {
      drawImage(g2, disabledImage)
    }
  }
  
  override def setEnabled(enabled: Boolean) {
    super.setEnabled(enabled)
    if(isEnabled) {
      if(!listenerAdded) {
        listenerAdded = true
        addMouseListener(this)
      }
    }
    else {
      if(listenerAdded) {
        listenerAdded = false
        removeMouseListener(this)
      }
    }
    repaint()
  }
  
  def mousePressed(e: MouseEvent): Unit = {
    pressed = true
    repaint()
  }
  
  def mouseReleased(e: MouseEvent): Unit = {
    pressed = false
    repaint()
    if(!exited) {
      fireActionEvent(command)
    }
  }
  
  def mouseEntered(e: MouseEvent): Unit = {
    exited = false
    highlighted = true
    repaint()
  }
  
  def mouseExited(e: MouseEvent): Unit = {
    exited = true
    highlighted = false
    pressed = false
    repaint()
  }
  
  def mouseClicked(e: MouseEvent): Unit = {}
  
  private def drawImage(g2: Graphics2D, image: ImageIcon) { g2.drawImage(image.getImage, (width - image.getIconWidth) / 2, (height - image.getIconHeight) / 2, null) }
  
  case class RGB(hex: Int) {
    val r: Int = (hex & 0xff0000) >> 16
    val g: Int = (hex & 0xff00) >> 8
    val b: Int = hex & 0xff
  }
}

object GlassButton {
  val NORMAL = 1
  val PRESSED = 2
  val HIGHLIGHTED = 3
}