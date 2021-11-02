package com.example.notesapplocal.room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapplocal.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyUserListAdapter(var listUsers: List<User>) :
    RecyclerView.Adapter<MyUserListAdapter.ViewHolder>() {

    private var selectPointer: Int = -1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var checkBox: CheckBox = view.findViewById(R.id.cb_username)

        init {
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                run {
                    if (isChecked) {
                        notifyItemChanged(selectPointer, selectPointer)
                        selectPointer = adapterPosition
                    }
                }
            }

        }

        fun bind(position: Int) {
            val name = listUsers[position].firstName + " " + listUsers[position].lastName
            checkBox.text = name
        }

        fun removeSelection() {
            if (checkBox.isChecked) checkBox.isChecked = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.isNotEmpty()) {
            if (payloads[0] == position) {
                holder.removeSelection()
            }
        }
    }

    override fun getItemCount(): Int {
        return listUsers.size
    }

    fun getSelectedPosition(): Int {
        return selectPointer
    }

    fun getIdOfSelection(position: Int): Int {
        return listUsers[position].uid
    }

}