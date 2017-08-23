package com.deameamo.swingx

import java.awt.{Event, Toolkit}
import java.awt.datatransfer.DataFlavor
import java.awt.event.{ActionEvent, ActionListener, MouseEvent, MouseListener}
import javax.swing.{JMenuItem, JPopupMenu, JTextField}

class PopupMenuTextField extends JTextField with ActionListener with MouseListener {

  val mb = new MenuBuilder("com.deameamo.swingx.PopupMenu")
  val menu = new JPopupMenu()
  val copyItem: JMenuItem = mb.createMenuItem("ItemCopy", this, PopupMenuTextField.COPY, "ItemCopyAcc", Event.CTRL_MASK)
  menu.add(copyItem)
  val pasteItem: JMenuItem = mb.createMenuItem("ItemPaste", this, PopupMenuTextField.PASTE, "ItemPasteAcc", Event.CTRL_MASK)
  menu.add(pasteItem)
  setComponentPopupMenu(menu)

  addMouseListener(this)

  def getClipboardText: String = {
    val cb = Toolkit.getDefaultToolkit.getSystemClipboard
    val trans = cb.getContents(null)
    if(trans != null) {
      if(trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        try {
          return trans.getTransferData(DataFlavor.stringFlavor).asInstanceOf[String]
        } catch {
          case ex: Exception => ex.printStackTrace()
        }
      }
    }
    null
  }

  def actionPerformed(e: ActionEvent) {
    e.getActionCommand match {
      case PopupMenuTextField.COPY => copy()
      case PopupMenuTextField.PASTE => paste()
    }
  }

  def mousePressed(e: MouseEvent) {
    if(e.getButton == MouseEvent.BUTTON3) {
      val selected = getSelectedText
      if(selected != null) {
        copyItem.setEnabled(true)
      }
      else {
        copyItem.setEnabled(false)
      }
      val clipboardText = getClipboardText
      if(clipboardText != null) {
        pasteItem.setEnabled(true)
      }
      else {
        pasteItem.setEnabled(false)
      }
    }
  }

  def mouseReleased(e: MouseEvent) {}
  def mouseClicked(e: MouseEvent) {}
  def mouseEntered(e: MouseEvent) {}
  def mouseExited(e: MouseEvent) {}
}

object PopupMenuTextField {
  val COPY = "COPY"
  val PASTE = "PASTE"
}
