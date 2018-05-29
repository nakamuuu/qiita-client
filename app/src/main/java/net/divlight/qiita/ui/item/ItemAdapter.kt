package net.divlight.qiita.ui.item

import android.content.Context
import android.databinding.Observable
import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import net.divlight.qiita.R
import net.divlight.qiita.databinding.ListItemItemBinding
import net.divlight.qiita.network.response.Item
import net.divlight.qiita.ui.common.recyclerview.ProgressFooterViewHolder
import net.divlight.qiita.ui.common.recyclerview.SimpleViewHolder

class ItemAdapter(
    private val context: Context,
    private val viewModel: ItemViewModel
) : RecyclerView.Adapter<ViewHolder>(), ItemListItemListener {
    companion object {
        private const val ITEM_VIEW_TYPE_ITEM = 0
        private const val ITEM_VIEW_TYPE_PROGRESS_FOOTER = 1
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val tagRecycledViewPool = RecyclerView.RecycledViewPool()
    private val onListChangedCallback =
        object : ObservableList.OnListChangedCallback<ObservableList<Item>>() {
            override fun onChanged(sender: ObservableList<Item>) {
                notifyDataSetChanged()
            }

            override fun onItemRangeRemoved(
                sender: ObservableList<Item>,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeMoved(
                sender: ObservableList<Item>,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                notifyItemRangeRemoved(fromPosition, itemCount)
                notifyItemRangeInserted(toPosition, itemCount)
            }

            override fun onItemRangeInserted(
                sender: ObservableList<Item>,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeChanged(
                sender: ObservableList<Item>,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeChanged(positionStart, itemCount)
            }
        }
    private val onFetchStatusChangedCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable, propertyId: Int) {
            if (itemCount != 0) {
                notifyItemChanged(itemCount - 1)
            }
        }
    }

    init {
        viewModel.items.addOnListChangedCallback(onListChangedCallback)
        viewModel.fetchStatus.addOnPropertyChangedCallback(onFetchStatusChangedCallback)
    }

    override fun getItemCount() = if (viewModel.items.isEmpty()) 0 else viewModel.items.size + 1

    override fun getItemViewType(position: Int) = if (position == itemCount - 1) {
        ITEM_VIEW_TYPE_PROGRESS_FOOTER
    } else {
        ITEM_VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        ITEM_VIEW_TYPE_ITEM -> ItemViewHolder(
            ListItemItemBinding.inflate(inflater, parent, false),
            tagRecycledViewPool
        )
        ITEM_VIEW_TYPE_PROGRESS_FOOTER -> ProgressFooterViewHolder(context, parent)
        else -> throw IllegalArgumentException("Unknown viewType passed.")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                holder.binding.item = viewModel.items[position]
                holder.binding.listener = this
                holder.binding.executePendingBindings()
                holder.tagAdapter.tags = viewModel.items[position].tags
                holder.tagAdapter.notifyDataSetChanged()
            }
            is ProgressFooterViewHolder -> {
                val fetchStatus = viewModel.fetchStatus.get()
                holder.progressBarShown = fetchStatus?.shouldShowFooterProgress == true
            }
        }
    }

    //
    //  ItemListItemListener
    //

    override fun onItemClick(item: Item) {
        viewModel.onListItemClick(item)
    }

    override fun onTagClick(tag: Item.Tag) {
        viewModel.onTagClick(tag)
    }

    private class ItemViewHolder(
        val binding: ListItemItemBinding,
        tagRecycledViewPool: RecyclerView.RecycledViewPool
    ) : ViewHolder(binding.root) {
        val tagAdapter: TagAdapter = TagAdapter(itemView.context).apply {
            this.onTagClick = { binding.listener?.onTagClick(it) }
        }

        init {
            binding.tagRecyclerView.layoutManager = FlexboxLayoutManager().apply {
                flexWrap = FlexWrap.WRAP
            }
            binding.tagRecyclerView.adapter = tagAdapter
            binding.tagRecyclerView.recycledViewPool = tagRecycledViewPool
        }
    }

    class TagAdapter(private val context: Context) : RecyclerView.Adapter<ViewHolder>() {
        var tags: List<Item.Tag> = ArrayList()
        var onTagClick: ((tag: Item.Tag) -> Unit)? = null

        override fun getItemCount() = tags.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SimpleViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.list_item_item_tag, parent, false)
        ).apply {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onTagClick?.invoke(tags[adapterPosition])
                }
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            (holder.itemView as TextView).text = tags[position].name
        }
    }
}
