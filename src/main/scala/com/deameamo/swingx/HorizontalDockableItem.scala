package com.deameamo.swingx

import java.awt._
import java.awt.event.{ActionEvent, ActionListener, MouseEvent}
import javax.swing.{ImageIcon, JComponent}
import javax.swing.border.AbstractBorder

import com.deameamo.event.MouseAdapter

class HorizontalDockableItem(c: Component, val title: String, font: Font, val smallIcon: ImageIcon, val bigIcon: ImageIcon)
    extends HorizontalRelocableItem(c: Component) with ActionListener with Dockable with MouseAdapter {

  def this(content: Component, title: String, font: Font, smallIconPath: String, bigIconPath: String) = this(content, title, font, new ImageIcon(smallIconPath), new ImageIcon(bigIconPath))
  
  def this(content: Component) = this(content, "", null, null: ImageIcon, null: ImageIcon)
  
  def this(content: Component, title: String) = this(content, title, null, null: ImageIcon, null: ImageIcon)
  
  def this(content: Component, title: String, font: Font) = this(content, title, font, null: ImageIcon, null: ImageIcon)
  
  
  setTitleBarHeight(30)
  if(font != null)
    setTitleFont(font)
  setTitle(title)
  
//  setBorder(new RoundBorder(16, new Color(0xF0F0F0), Color.DARK_GRAY, new Color(0xF0F0F0)))
  titleBar.setBorder(new TitleBorder(8))
  titleBar.handle.setBackground(new Color(0xF0F0F0))
  c.asInstanceOf[JComponent].setBorder(new ContentBorder(8))
  titleBar.setOpaque(false)
  c.asInstanceOf[JComponent].setOpaque(false)
  setOpaque(false)
  
  if(smallIcon != null)
    setSmallIcon(smallIcon)
    
  val minimizeButton = new Button(WindowAction.MINIMIZE)
  minimizeButton.setImageBase("icons/minus.png")
  minimizeButton.addActionListener(this)
  
  val maximizeButton = new Button(WindowAction.MAXIMIZE)
  maximizeButton.setImageBase("icons/square.png")
  maximizeButton.addActionListener(this)
  
  val restoreButton = new Button(WindowAction.RESTORE)
  restoreButton.setImageBase("icons/restore.png")
  restoreButton.addActionListener(this)
  
  val buttonBox = new ResizableBox(ResizableBox.HORIZONTAL, false)
  buttonBox.setPaddings(0, 0, 0, 2)
  buttonBox.setBorderLength(1)
  buttonBox.addRigidItem(minimizeButton, 16)
  buttonBox.addRigidItem(maximizeButton, 16)
  
  titleBar.setCellDisplayLength(0, 34)
  titleBar.handle.addRigidItem(buttonBox, 34)
  buttonBox.setBackground(new Color(0xF0F0F0))
  buttonBox.setVisible(false)
  
  var overButtonBox = false
  titleBar.addMouseListener(this)
  buttonBox.addMouseListener(this)
  minimizeButton.addMouseListener(this)
  maximizeButton.addMouseListener(this)
  restoreButton.addMouseListener(this)
  
  override def mouseEntered(e: MouseEvent) {
    buttonBox.setVisible(true)
  }
  
  override def mouseExited(e: MouseEvent) {
    buttonBox.setVisible(false)
  }
  
  var maximized = false
  
  def switchButton() {
    if(maximized) {
      maximized = false
      buttonBox.removeItem(restoreButton)
      buttonBox.addRigidItem(maximizeButton, 16)
    }
    else {
      maximized = true
      buttonBox.removeItem(maximizeButton)
      buttonBox.addRigidItem(restoreButton, 16)
    }
  }
  
  def minimizeButtonClicked() {
    buttonBox.setVisible(false)
    minimizeButton.setCurrentImage(minimizeButton.normalImage)
    if(maximized)
      switchButton()
    buttonBox.validate()
  }
  
  def maximizeButtonClicked() {
    maximizeButton.setCurrentImage(maximizeButton.normalImage)
    switchButton()
    buttonBox.validate()
  }
  
  def restoreButtonClicked() {
    restoreButton.setCurrentImage(restoreButton.normalImage)
    switchButton()
    buttonBox.validate()
  }
  
  def actionPerformed(event: ActionEvent) {
    event.getActionCommand match {
      case WindowAction.MINIMIZE => minimizeButtonClicked()
      case WindowAction.MAXIMIZE => maximizeButtonClicked()
      case WindowAction.RESTORE => restoreButtonClicked()
    }
    fireActionEvent(new ActionEvent(this, 0, event.getActionCommand))
  }
  
  class TitleBorder(
      thickness: Int,
      borderColor: Color,
      outerColor: Color) 
      extends AbstractBorder {
    def this(thickness: Int) = this(thickness, new Color(0xF0F0F0), new Color(0xB6BCCC))
    
    override def paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
      val g2 = g.asInstanceOf[Graphics2D]
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setColor(borderColor)
      g2.fillRect(0, thickness, thickness, height)
      g2.fillRect(thickness, 0, width - thickness * 2, thickness)
      g2.fillRect(width - thickness, thickness, thickness, height)
      
      g2.fillArc(0, 0, thickness * 2, thickness * 2, 90, 90)
      g2.fillArc(width - thickness * 2, 0, thickness * 2, thickness * 2, 0, 90)
      
      g2.setColor(outerColor)
      g2.drawLine(0, thickness, 0, height)
      g2.drawLine(thickness, 0, width - thickness, 0)
      g2.drawLine(width - 1, thickness, width - 1, height)
      g2.drawArc(0, 0, thickness * 2, thickness * 2, 90, 90)
      g2.drawArc(width - thickness * 2 - 1, 0, thickness * 2, thickness * 2, 0, 90)
      
      g2.setColor(new Color(0xA0A0A0))
      g2.drawLine(0, height - 1, width, height - 1)
      
      
    }
    
    override def getBorderInsets(c: Component): Insets = {
      new Insets(thickness, thickness, 1, thickness)
    }
  }
  
  class ContentBorder(
      thickness: Int,
      borderColor: Color,
      outerColor: Color) 
      extends AbstractBorder {
    def this(thickness: Int) = this(thickness, new Color(0xF0F0F0), new Color(0xB6BCCC))
    
    override def paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
      val g2 = g.asInstanceOf[Graphics2D]
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setColor(borderColor)
      g2.fillRect(0, 0, thickness, height - thickness)
      g2.fillRect(width - thickness, 0, thickness, height - thickness)
      g2.fillRect(thickness, height - thickness, width - thickness * 2, thickness)
      
      g2.fillArc(0, height - thickness * 2, thickness * 2, thickness * 2, -90, -90)
      g2.fillArc(width - thickness * 2, height - thickness * 2, thickness * 2, thickness * 2, 0, -90)
      
      g2.setColor(outerColor)
      g2.drawLine(0, 0, 0, height - thickness)
      g2.drawLine(width - 1, 0, width - 1, height - thickness)
      g2.drawLine(thickness, height - 1, width - thickness, height - 1)
      
      g2.drawArc(0, height - thickness * 2 - 1, thickness * 2, thickness * 2, -90, -90)
      g2.drawArc(width - thickness * 2 - 1, height - thickness * 2 - 1, thickness * 2, thickness * 2, 0, -90)
      
    }
    
    override def getBorderInsets(c: Component): Insets = {
      new Insets(0, thickness, thickness, thickness)
    }
  }
}
