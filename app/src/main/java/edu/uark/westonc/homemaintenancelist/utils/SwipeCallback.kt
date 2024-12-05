package edu.uark.westonc.homemaintenancelist.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import edu.uark.westonc.homemaintenancelist.Helpers.TaskListAdapter
import edu.uark.westonc.homemaintenancelist.ViewModels.TaskViewModel

class SwipeCallback(private val adapter: TaskListAdapter, private val taskViewModel: TaskViewModel, private val userID: String) : ItemTouchHelper.Callback() {

    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val paint = Paint() // Paint object for the swipe background color

    //**********************************************************************************************
    //  getMovementFlags
    //  Description: This function is called to specify the directions in which the view holder can
    //  be swiped.
    //**********************************************************************************************
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(0, swipeFlags)
    }

    //**********************************************************************************************
    //  onMove
    //  Description: This function is called when the user moves a view holder (Task).
    //**********************************************************************************************
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    //**********************************************************************************************
    //  onSwiped
    //  Description: This function is called when the user swipes a view holder (Task).
    //**********************************************************************************************
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val task = adapter.getTaskAtPosition(position)

        when (direction) {
            ItemTouchHelper.LEFT -> {
                firebaseDB.collection("users").document(userID).collection("tasks").document(task.id!!).delete()
                // Handle left swipe (e.g., delete task)
                taskViewModel.delete(task)
                adapter.removeItem(position)
            }
            ItemTouchHelper.RIGHT -> {
                firebaseDB.collection("users").document(userID).collection("tasks").document(task.id!!).update("taskComplete", true)
                // Handle right swipe (e.g., mark task as complete)
                taskViewModel.complete(task)
                adapter.notifyItemChanged(position)
            }
        }
    }

    //**********************************************************************************************
    //  onChildDraw
    //  Description: This function is called when the user swipes a view holder (Task) and draws the
    //  background color for the swipe.
    //**********************************************************************************************
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            val itemHeight = itemView.bottom - itemView.top

            if (dX > 0) { // Swiping to the right
                paint.color = Color.argb(217, 67,255,100)
                c.drawRect(
                    itemView.left.toFloat(),
                    itemView.top.toFloat(),
                    itemView.left + dX,
                    itemView.bottom.toFloat(),
                    paint
                )
            } else { // Swiping to the left
                paint.color = Color.argb(255, 247, 2, 2)
                c.drawRect(
                    itemView.right + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat(),
                    paint
                )
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

}