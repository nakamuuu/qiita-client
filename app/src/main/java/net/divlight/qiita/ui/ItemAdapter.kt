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
import net.divlight.qiita.R
import net.divlight.qiita.model.Item
import net.divlight.qiita.ui.common.DateDiffStringGenerator

class ItemAdapter(val context: Context, val listener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<Item> = ArrayList()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val inflater = LayoutInflater.from(context)
        val holder = ItemViewHolder(inflater.inflate(R.layout.list_item_item, parent, false))
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(items[position])
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        (holder as? ItemViewHolder)?.let { holder ->
            holder.titleView.text = item.title
            holder.detailView.text = context.getString(R.string.item_detail_label_format,
                    item.user.id,
                    DateDiffStringGenerator(context, item.createdAt).toCreatedAtDiffString())
            Glide.with(holder.profileImageView)
                    .load(item.user.profileImageUrl)
                    .apply(RequestOptions().circleCrop().placeholder(R.color.placeholder))
                    .into(holder.profileImageView)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder?) {
        super.onViewRecycled(holder)
        (holder as? ItemViewHolder)?.profileImageView?.setImageBitmap(null)
    }

    interface OnItemClickListener {
        fun onItemClick(item: Item)
    }

    class ItemViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        var profileImageView: ImageView = view.findViewById(R.id.profile_image)
        var titleView: TextView = view.findViewById(R.id.title)
        var detailView: TextView = view.findViewById(R.id.detail)
    }
}
