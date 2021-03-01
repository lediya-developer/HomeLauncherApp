package com.lediya.homelauncher.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lediya.homelauncher.R
import com.lediya.homelauncher.databinding.ListItemBinding
import android.widget.Filter;
import com.lediya.appmanager.model.AppDetail
import java.util.*
import kotlin.collections.ArrayList


class AppAdapter: RecyclerView.Adapter<AppAdapter.ViewHolder>() {
    private lateinit var binding: ListItemBinding
    private lateinit var items: MutableList<AppDetail>
    private lateinit var itemsFiltered : MutableList<AppDetail>

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        binding = createBinding(parent)
        return ViewHolder(binding)
    }
    private fun createBinding(parent: ViewGroup): ListItemBinding {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item,
                parent,
                false
        )
    }
    fun setItems(itemList: List<AppDetail>) {
        this.items = itemList as MutableList<AppDetail>
        this.itemsFiltered = itemList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return itemsFiltered.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemsFiltered[position]
        holder.binding.listItem.isLongClickable = true
        holder.binding.itemAppLabel.text = item.label
        holder.binding.itemAppIcon.setImageDrawable(item.icon)
        holder.binding.itemAppName.text = item.packageName
        holder.binding.itemAppLauncher.text = item.launcherClass
        holder.binding.itemAppVersionCode.text = item.versionCode.toString()
        holder.binding.itemAppVersionName.text = item.versionName.toString()
        holder.binding.itemAppIcon.setOnClickListener{
            onItemClick(holder.adapterPosition)
        }
        holder.binding.listItem.setOnClickListener{
            onItemClick(holder.adapterPosition)
        }
        holder.binding.listItem.setOnLongClickListener{
            onItemLongClick(holder.adapterPosition)
            return@setOnLongClickListener(true)
        }
    }
    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    itemsFiltered = items
                } else {
                    val filteredList: MutableList<AppDetail> = ArrayList()
                    for (row in items) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.label.toString().toLowerCase(Locale.ROOT)
                                .contains(charString.toLowerCase(Locale.ROOT))
                        ) {
                            filteredList.add(row)
                        }
                    }
                    itemsFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = itemsFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                itemsFiltered = filterResults.values as MutableList<AppDetail>
                // refresh the list with filtered data
                notifyDataSetChanged()
            }
        }
    }
    class ViewHolder(val binding: ListItemBinding) :
            RecyclerView.ViewHolder(binding.root)
    var onItemClick: (Int) -> Unit = {}
    var onItemLongClick: (Int) -> Unit = {}
}