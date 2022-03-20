package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Fragments.Recycler

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Model.ServerData
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.R
import java.util.ArrayList


class ServerCellAdapter(l: ArrayList<ServerData>) : RecyclerView.Adapter<ServerCellAdapter.ViewHolder>() {
    private val dataSet: Array<ServerData> = l.toArray(arrayOfNulls<ServerData>(l.size))
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textViewServerCell)

        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.server_status_cell, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        var symbol = ResourcesCompat.getDrawable(viewHolder.itemView.context.resources, R.drawable.status_dot_orange, null)
        when(dataSet[position].status) {
            0 -> symbol = ResourcesCompat.getDrawable(viewHolder.itemView.context.resources, R.drawable.status_dot_red, null)
            1 -> symbol = ResourcesCompat.getDrawable(viewHolder.itemView.context.resources, R.drawable.status_dot_orange, null)
            2 -> symbol = ResourcesCompat.getDrawable(viewHolder.itemView.context.resources, R.drawable.status_dot_green, null)
        }

        viewHolder.textView.text = dataSet[position].toString()
        //val symbol = viewHolder.itemView.context.resources.getDrawable()
        viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(symbol, null, null, null)

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}