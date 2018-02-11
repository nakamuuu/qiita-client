package net.divlight.qiita.ui.item

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import net.divlight.qiita.R
import net.divlight.qiita.network.response.Item
import net.divlight.qiita.ui.common.recyclerview.SimpleViewHolder

class ItemTagAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var tags: List<Item.Tag> = ArrayList()
    var onTagClick: ((tag: Item.Tag) -> Unit)? = null

    override fun getItemCount(): Int = tags.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val inflater = LayoutInflater.from(context)
        val holder = SimpleViewHolder(inflater.inflate(R.layout.list_item_item_tag, parent, false))
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onTagClick?.invoke(tags[position])
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as? TextView)?.text = tags[position].name
    }
}
