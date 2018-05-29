package net.divlight.qiita.ui.common.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import net.divlight.qiita.R

class ProgressFooterViewHolder(
    context: Context,
    root: ViewGroup? = null
) : RecyclerView.ViewHolder(
    LayoutInflater.from(context).inflate(R.layout.progress_footer, root, false)
) {
    var progressBarShown: Boolean
        set(value) {
            progressBar.visibility = if (value) View.VISIBLE else View.INVISIBLE
        }
        get() = (progressBar.visibility == View.VISIBLE)

    private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)

    init {
        itemView.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        progressBarShown = false
    }
}
