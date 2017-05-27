package net.divlight.qiita.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import net.divlight.qiita.R
import net.divlight.qiita.model.Item
import net.divlight.qiita.ui.common.DateDiffStringGenerator
import net.divlight.qiita.ui.common.ProgressFooterViewHolder

class ItemAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val ITEM_VIEW_TYPE_ITEM = 0
        private const val ITEM_VIEW_TYPE_PROGRESS_FOOTER = 1
    }

    var items: List<Item> = emptyList()
        set(value) {
            field = value
            // FIXME: Use notifyItemRangeInserted or DiffUtils
            notifyDataSetChanged()
        }
    var onItemClick: ((item: Item) -> Unit)? = null
    var onTagClick: ((tag: Item.Tag) -> Unit)? = null
    var progressFooterShown: Boolean = false
        set(value) {
            field = value
            if (itemCount > 0) {
                notifyItemChanged(itemCount - 1)
            }
        }

    override fun getItemCount(): Int = if (!items.isEmpty()) items.size + 1 else 0

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            in 0..(items.size - 1) -> ITEM_VIEW_TYPE_ITEM
            else -> ITEM_VIEW_TYPE_PROGRESS_FOOTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> {
                val itemView = LayoutInflater.from(context)
                        .inflate(R.layout.list_item_item, parent, false)
                ItemViewHolder(itemView, onTagClick).apply {
                    itemView.setOnClickListener {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onItemClick?.invoke(items[adapterPosition])
                        }
                    }
                }
            }
            ITEM_VIEW_TYPE_PROGRESS_FOOTER -> ProgressFooterViewHolder(context)
            else -> null
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.setItem(items[position])
        (holder as? ProgressFooterViewHolder)?.progressBarShown = progressFooterShown
    }

    class ItemViewHolder constructor(itemView: View, onTagClick: ((tag: Item.Tag) -> Unit)?) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.profile_image)
        private val titleView: TextView = itemView.findViewById(R.id.title)
        private val detailView: TextView = itemView.findViewById(R.id.detail)
        private val tagRecyclerView: RecyclerView = itemView.findViewById(R.id.tag_recycler_view)
        private val tagAdapter: ItemTagAdapter

        init {
            tagRecyclerView.layoutManager = FlexboxLayoutManager().apply {
                flexWrap = FlexWrap.WRAP
            }
            tagAdapter = ItemTagAdapter(itemView.context).apply {
                this.onTagClick = onTagClick
            }
            tagRecyclerView.adapter = tagAdapter
        }

        fun setItem(item: Item) {
            val context = itemView.context
            Glide.with(profileImageView)
                    .load(item.user.profileImageUrl)
                    .apply(RequestOptions().circleCrop().placeholder(R.drawable.circle_placeholder))
                    .into(profileImageView)
            titleView.text = item.title
            detailView.text = context.getString(R.string.item_detail_label_format,
                    item.user.id,
                    DateDiffStringGenerator(context, item.createdAt).toCreatedAtDiffString())

            tagAdapter.tags = item.tags
            tagAdapter.notifyDataSetChanged()
        }
    }
}
