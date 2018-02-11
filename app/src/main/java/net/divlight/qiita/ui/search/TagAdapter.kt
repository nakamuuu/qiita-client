package net.divlight.qiita.ui.search

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import net.divlight.qiita.R
import net.divlight.qiita.network.response.Tag

class TagAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var tags: List<Tag> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onTagClick: ((tag: Tag) -> Unit)? = null

    override fun getItemCount(): Int = tags.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.list_item_tag, parent, false)
        return ItemViewHolder(itemView).apply {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onTagClick?.invoke(tags[adapterPosition])
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.setTag(tags[position])
    }

    class ItemViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.icon_image)
        private val titleView: TextView = itemView.findViewById(R.id.title)
        private val itemCountView: TextView = itemView.findViewById(R.id.item_count)

        fun setTag(tag: Tag) {
            val context = itemView.context
            Glide.with(iconImageView)
                .load(tag.iconUrl)
                .apply(RequestOptions().centerCrop().placeholder(R.drawable.circle_placeholder))
                .into(iconImageView)
            titleView.text = tag.id
            itemCountView.text = context.getString(
                R.string.search_tags_item_count_format,
                tag.itemsCount
            )
        }
    }
}
