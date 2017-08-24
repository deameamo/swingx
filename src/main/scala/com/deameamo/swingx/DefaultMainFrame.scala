package com.deameamo.swingx

import java.awt.{Frame, Toolkit}
import java.awt.event.{WindowEvent, WindowStateListener}
import javax.swing.{BorderFactory, JFrame, SwingUtilities, UIManager}

class DefaultMainFrame extends JFrame with WindowStateListener {
  
  val mainBox = new ResizableBox(ResizableBox.HORIZONTAL, true)
  mainBox.setBorder(BorderFactory.createEtchedBorder())
  
  add(mainBox)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  
//  addWindowStateListener(this)
  
//  val plaf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"
  val plaf = "com.jtattoo.plaf.aero.AeroLookAndFeel"
  try {
    UIManager.setLookAndFeel(plaf)
    SwingUtilities.updateComponentTreeUI(this)
  } catch {
    case ex: Exception => ex.printStackTrace()
  }
  
  def windowStateChanged(e: WindowEvent) {
    if(e.getOldState == Frame.MAXIMIZED_BOTH && e.getNewState == Frame.NORMAL) {
      val kit = Toolkit.getDefaultToolkit
      val size = kit.getScreenSize
      setBounds(0, 0, size.getWidth.toInt, size.getHeight.toInt)
//      mainBox.validate
//      mainBox.repaint()
    }
  }

  def maximize() {
    val kit = Toolkit.getDefaultToolkit
    val size = kit.getScreenSize
    setBounds(0, 0, size.getWidth.toInt, size.getHeight.toInt)
    setExtendedState(Frame.MAXIMIZED_BOTH)
  }
}