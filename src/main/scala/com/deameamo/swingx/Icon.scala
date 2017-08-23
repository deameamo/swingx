package com.deameamo.swingx

import java.awt.{Graphics, Graphics2D}
import javax.swing.{ImageIcon, JPanel}

class Icon extends JPanel {
  
  var image: ImageIcon = _
  
  def setImage(image: ImageIcon) { this.image = image }
  
  override def paintComponent(g: Graphics) {
    val g2 = g.asInstanceOf[Graphics2D]
    super.paintComponent(g2)
    
    g2.drawImage(image.getImage, (getWidth - image.getIconWidth) / 2, (getHeight - image.getIconHeight) / 2, null)
  }
}