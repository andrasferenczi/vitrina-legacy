package stoyck.vitrina.ui.recyclerview

interface TouchHelperAdapter {

    fun onItemMove(fromPosition: Int, toPosition: Int)

     fun onItemDismiss(position: Int)

}