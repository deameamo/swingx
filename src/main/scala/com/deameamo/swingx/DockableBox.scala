package com.deameamo.swingx

import java.awt.Component
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.BorderFactory

import scala.collection.mutable

class DockableBox(alignment: Int, resizable: Boolean)
    extends RelocableBox(alignment: Int, resizable: Boolean)
    with ActionListener { box =>
  
  def this() = this(ResizableBox.HORIZONTAL, true)
  def this(alignment: Int) = this(alignment, true)
  
  val DOCK_WIDTH = 55
  val DOCKED_ITEM_HEIGHT = 70

  val contentBox = new RelocableBox
  contentBox.setPaddings(10)
  contentBox.setMinCellLength(16)
  val contentItem = new HorizontalRelocableItem(contentBox)
  contentItem.hideTitleBar()
  val dock = new Dock
  val dockItem = new HorizontalRelocableItem(dock)
  addItem(contentItem)

  val dockedItems = new mutable.MutableList[DockedItem]

  var defaultFlexibleDockable: Dockable = _
  def setOriginalFlexibleItem(item: Dockable) { if(defaultFlexibleDockable == null) defaultFlexibleDockable = item}

  var snapshots: mutable.MutableList[ProportionalCellSnapshot] = _

  var maximizedDockableItem: Dockable = _

  def addItem(dockableItem: Dockable) {
    contentBox.addItem(dockableItem.asInstanceOf[Relocable])
    setOriginalFlexibleItem(dockableItem)
    addDockableItem(dockableItem)
  }

  def addItem(dockableItem: Dockable, percentage: Double) {
    contentBox.addItem(dockableItem.asInstanceOf[Relocable], percentage)
    addDockableItem(dockableItem)
  }

  def addItem(dockableItem: Dockable, length: Int) {
    contentBox.addItem(dockableItem.asInstanceOf[Relocable], length)
    addDockableItem(dockableItem)
  }

  private def addDockableItem(dockableItem: Dockable) {
    dockableItem.addActionListener(this)
    val dockedItem = new DockedItem(dockableItem, contentBox.getCell(dockableItem.asInstanceOf[Component]).makeSnapshot)
    dockedItem.addActionListener(this)
    dockedItems += dockedItem
  }

  def dockItem(item: Dockable) {
    minimize(item)
    item.minimizeButtonClicked()
  }

  def actionPerformed(e: ActionEvent) {
    e.getActionCommand match {
      case WindowAction.MINIMIZE => minimize(e.getSource.asInstanceOf[Dockable])
      case WindowAction.MAXIMIZE => maximize(e.getSource.asInstanceOf[Dockable])
      case WindowAction.RESTORE => restore(e.getSource.asInstanceOf[Dockable])
      case DockedItem.DOCKED_ITEM_CLICKED => dockedItemClicked(e.getSource.asInstanceOf[DockedItem])
      case _ =>
    }
  }

  private def minimize(dockable: Dockable) {
    maximizedDockableItem = null
    dock.addDockedItem(dockable)
    contentBox.removeItem(dockable.asInstanceOf[Component], autoValidate = true)
    update()
  }

  private def maximize(dockable: Dockable) {
    maximizedDockableItem = dockable
    contentBox.cells.foreach { cell => {
      if(cell.item != dockable.asInstanceOf[Component])
        dock.addDockedItem(cell.item.asInstanceOf[Dockable])
    }}
    snapshots = contentBox.makeSnapshots
    contentBox.removeAllItems()
    contentBox.addCellDynamically(getDockedItem(dockable).snapshot)
    update()
  }

  private def restore(dockable: Dockable) {
    maximizedDockableItem = null
    snapshots.foreach { snapshot => {
      dock.removeDockedItem(snapshot.item.asInstanceOf[Dockable])
    }}
    contentBox.replaceAllCells(snapshots)
    update()
  }

  private def dockedItemClicked(item: DockedItem) {
    if(maximizedDockableItem != null)
      maximizedDockableItem.switchButton()
    dock.removeDockedItem(item)
    contentBox.addCellDynamically(item.snapshot)
    update()
  }

  private def update() {
    dock.update()
    contentBox.validate()
    contentBox.repaint()
//    validate
//    repaint()
  }

  private def getDockedItem(dockable: Dockable): DockedItem = {
    var target: DockedItem = null
    var i = 0
    while(i < dockedItems.size && target == null) {
      if(dockedItems.get(i).get.dockable == dockable)
        target = dockedItems.get(i).get
      else
        i += 1
    }
    target
  }

  class Dock extends ResizableBox(ResizableBox.VERTICAL, false) {
    setBorder(BorderFactory.createLoweredBevelBorder())

    def addDockedItem(dockable: Dockable) {
      val item = getDockedItem(dockable)
      addRigidItem(item, DOCKED_ITEM_HEIGHT)
    }
    
    def removeDockedItem(dockable: Dockable) { removeDockedItem(getDockedItem(dockable)) }
    
    def removeDockedItem(item: DockedItem) {
      if(item != null) {
        dock.removeItem(item, autoValidate = true)
      }
    }
  
    var dockIndex = 1
    
    var dockVisible = false
    
    def update() {
      if(cells.nonEmpty) {
        showDock()
      }
      else
        hideDock()
      validate()
      repaint()
    }
    
    private def hideDock() {
      if(dockVisible) {
        dockVisible = false
        dockIndex = getCellIndex(dock)
        box.removeItem(dock, autoValidate = true)
      }
    }
    
    private def showDock() {
      if(!dockVisible) {
        dockVisible = true
        box.addRigidItem(dock, DOCK_WIDTH)
      }
    }
  }
}