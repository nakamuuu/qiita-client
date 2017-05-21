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

class ItemAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<Item> = ArrayList()
    var onItemClick: ((item: Item) -> Unit)? = null
    var onTagClick: ((tag: Item.Tag) -> Unit)? = null

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val inflater = LayoutInflater.from(context)
        val holder = ItemViewHolder(inflater.inflate(R.layout.list_item_item, parent, false), onTagClick)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClick?.invoke(items[position])
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.setItem(items[position])
    }

    class ItemViewHolder constructor(view: View, onTagClick: ((tag: Item.Tag) -> Unit)?) : RecyclerView.ViewHolder(view) {
        private val profileImageView: ImageView = view.findViewById(R.id.profile_image)
        private val titleView: TextView = view.findViewById(R.id.title)
        private val detailView: TextView = view.findViewById(R.id.detail)
        private val tagRecyclerView: RecyclerView = view.findViewById(R.id.tag_recycler_view)
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
