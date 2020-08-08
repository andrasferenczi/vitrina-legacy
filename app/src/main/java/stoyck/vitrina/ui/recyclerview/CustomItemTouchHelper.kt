package stoyck.vitrina.ui.recyclerview

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class CustomItemTouchHelper(
    callback: Callback
) : ItemTouchHelper(callback), OnStartDragListener {

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        this.startDrag(viewHolder)
    }
}