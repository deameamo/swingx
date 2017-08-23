package com.deameamo.swingx

import java.awt.{Component, Font}
import javax.swing.{ImageIcon, JLabel, JPanel, SwingConstants}

class HorizontalRelocableItem(val content: Component) extends ResizableBox(ResizableBox.VERTICAL, false) with Relocable {item =>
  
  val titleBar = new TitleBar
  setBorderLength(0)
  
  addRigidItem(titleBar, 15)
  addItem(content)
  
  addHandle(titleBar.iconBox)
  addHandle(titleBar.handle)
  
  def setTitleBarHeight(height: Int) { setCellDisplayLength(0, height) }
  
  def setTitle(title: String): Unit = titleBar.setTitle(title)
  
  def setTitleFont(font: Font): Unit = titleBar.setTitleFont(font)
  
  def setSmallIcon(image: ImageIcon) { titleBar.setIcon(image) }
  
  def setSmallIcon(imagePath: String) { titleBar.setIcon(imagePath) }
  
  def hideTitleBar() { removeItem(titleBar) }
  
  class TitleBar extends ResizableBox(ResizableBox.HORIZONTAL, false) {
    
    setBorderLength(0)
    val iconBox = new ResizableBox(ResizableBox.HORIZONTAL, false)
    val icon = new Icon
    iconBox.addRigidItem(icon, 0)
    iconBox.addItem(new JPanel)
    val handle = new ResizableBox(ResizableBox.HORIZONTAL, false)
    val label = new JLabel
    label.setHorizontalAlignment(SwingConstants.CENTER)
    handle.addItem(label)
    
    addRigidItem(iconBox, 0)
    addItem(handle)
    
    def setTitle(title: String): Unit = label.setText(title)
    
    def setTitleFont(font: Font): Unit = label.setFont(font)
    
    def setIcon(image: ImageIcon) {
      icon.setImage(image)
      iconBox.setCellDisplayLength(0, image.getIconHeight)
      iconBox.validate()
    }
    
    def setIcon(imagePath: String) { setIcon(new ImageIcon(imagePath)) }
  }
}