package edu.uark.westonc.homemaintenancelist.Helpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.uark.westonc.homemaintenancelist.Models.Task
import edu.uark.westonc.homemaintenancelist.R


class TaskListAdapter(val onItemClicked:(id:Long)->Unit) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksComparator()) {

    //******************************************************************************************************
    // onCreateViewHolder
    // Description: Creates a new TaskViewHolder
    // Parameters: ViewGroup, Int
    // Returns: TaskViewHolder
    //******************************************************************************************************
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent)
    }

    //******************************************************************************************************
    // onBindViewHolder
    // Description: Binds the TaskViewHolder to the Task and sets the click listener for the item
    // Parameters: TaskViewHolder, Int
    // Returns: Unit
    //******************************************************************************************************
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            current.localID?.let { it1 -> onItemClicked(it1) }
        }
        holder.bind(current)
    }

    //******************************************************************************************************
    // removeItem
    // Description: Removes an item from the list
    // Parameters: Int
    // Returns: Unit
    //******************************************************************************************************
    fun removeItem(position: Int) {
        val task = getItem(position)
        // remove the item from the list
        submitList(currentList.minus(task))
    }

    //******************************************************************************************************
    // getTaskAtPosition
    // Description: Gets the task at a specific position
    // Parameters: Int
    // Returns: Task
    //******************************************************************************************************
    fun getTaskAtPosition(position: Int): Task {
        return getItem(position)
    }

    //******************************************************************************************************
    // TaskViewHolder Class
    // Description: ViewHolder for the TaskListAdapter
    //******************************************************************************************************
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskItemView: TextView = itemView.findViewById(R.id.textView)
        private val taskImageView: ImageView = itemView.findViewById(R.id.imageView)

        //******************************************************************************************************
        // bind
        // Description: Binds the Task to the ViewHolder, set a long click listener to share the task, and sets the
        // image based on the task completion status
        // Parameters: Task
        // Returns: Unit
        //******************************************************************************************************
        fun bind(task: Task?) {
            if (task != null) {
                taskItemView.text = task.taskTitle
                if (task.taskComplete) {
                    taskImageView.setImageResource(R.drawable.baseline_check_circle_outline_24)
                } else {
                    taskImageView.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
                }

                itemView.setOnLongClickListener {
                    shareTask(task)
                    true
                }
            }
        }

        //******************************************************************************************************
        // shareTask
        // Description: Shares the task via an intent
        // Parameters: Task
        // Returns: Unit
        //******************************************************************************************************
        private fun shareTask(task: Task) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Task: ${task.taskTitle}\nDetails: ${task.taskDescription}\nDeadline: ${task.taskDeadline}\nComplete: ${task.taskComplete}")
                type = "text/plain"
            }
            itemView.context.startActivity(Intent.createChooser(shareIntent, "Share task via"))
        }

        companion object {
            fun create(parent: ViewGroup): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return TaskViewHolder(view)
            }
        }
    }

    //******************************************************************************************************
    // TasksComparator Class
    // Description: Compares two tasks for equality
    //******************************************************************************************************
    class TasksComparator : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.taskTitle == newItem.taskTitle && oldItem.taskDescription == newItem.taskDescription && oldItem.taskDeadline == newItem.taskDeadline && oldItem.taskComplete == newItem.taskComplete
        }
    }

}