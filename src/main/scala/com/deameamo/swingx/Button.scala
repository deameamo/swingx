package com.deameamo.swingx

import java.awt.event.{MouseEvent, MouseListener}
import java.awt.{Graphics, Graphics2D, Image}
import javax.swing.{ImageIcon, JPanel}

import com.deameamo.event.CanFireActionEvent
import com.deameamo.util.FileUtil

class Button(private var command: String) extends JPanel with MouseListener with CanFireActionEvent {
  
  def this() = this(null)

  var currImage: Image = _
  var normalImage: Image = _
  var disabledImage: Image = _
  var highlightedImage: Image = _
  var pressedImage: Image = _
  setEnabled(true)
  
  def setCommand(command: String) {this.command = command}
  
  override def setEnabled(enabled: Boolean) {
    super.setEnabled(enabled)
    if(isEnabled) {
      currImage = normalImage
      addMouseListener(this)
    }
    else {
      currImage = if(disabledImage != null) disabledImage else normalImage
      removeMouseListener(this)
    }
    repaint()
  }
  
  def setCurrentImage(image: Image) {
    currImage = image
    repaint()
  }
  
  def setImageBase(basePath: String) {
    val baseInfo = FileUtil.splitFileName(basePath)
    setNormalImage(s"${baseInfo.main}_normal.${baseInfo.ext}")
    setHighlightedImage(s"${baseInfo.main}_highlighted.${baseInfo.ext}")
    setPressedImage(s"${baseInfo.main}_pressed.${baseInfo.ext}")
    setDisabledImage(s"${baseInfo.main}_disabled.${baseInfo.ext}")
  }
  
  def setNormalImage(path: String) { setNormalImage(new ImageIcon(path).getImage) }
  
  def setNormalImage(image: Image) {
    normalImage = image
    currImage = normalImage
  }
  
  def setDisabledImage(path: String) {
    if(FileUtil.exists(path))
      setDisabledImage(new ImageIcon(path).getImage)
    else
      setDisabledImage(normalImage)
  }
  
  def setDisabledImage(image: Image) { disabledImage = image }
  
  def setHighlightedImage(path: String) {
    if(FileUtil.exists(path))
      setHighlightedImage(new ImageIcon(path).getImage)
    else
      setDisabledImage(normalImage)
  }
  
  def setHighlightedImage(image: Image) { highlightedImage = image }
  
  def setPressedImage(path: String) {
    if(FileUtil.exists(path))
      setPressedImage(new ImageIcon(path).getImage)
    else
      setDisabledImage(normalImage)
  }
  
  def setPressedImage(image: Image) { pressedImage = image }
  
  override def paintComponent(g: Graphics) {
    val g2 = g.asInstanceOf[Graphics2D]
    super.paintComponent(g2)
    g2.drawImage(currImage, 0, 0, null)
  }
  
  var pressed = false
  var exited = false
  
  def mousePressed(e: MouseEvent): Unit = {
    if(isEnabled) {
      pressed = true
      setCurrentImage(if(pressedImage != null) pressedImage else normalImage)
    }
  }
  
  def mouseReleased(e: MouseEvent): Unit = {
    if(isEnabled) {
      pressed = false
      setCurrentImage({
        if(exited)
          normalImage
        else
          if(highlightedImage != null) highlightedImage else normalImage
      })
      if(!exited) {
        fireActionEvent(command)
      }
    }
  }
  
  def mouseEntered(e: MouseEvent): Unit = {
    exited = false
    if(isEnabled) {
      setCurrentImage(if(highlightedImage != null) highlightedImage else normalImage)
    }
  }
  
  def mouseExited(e: MouseEvent): Unit = {
    exited = true
    if(isEnabled && !pressed) {
      setCurrentImage(normalImage)
    }
  }
  
  def mouseClicked(e: MouseEvent): Unit = {}
}