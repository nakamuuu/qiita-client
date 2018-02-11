package net.divlight.qiita.ui.common.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import net.divlight.qiita.R

class ProgressFooterViewHolder(context: Context) : RecyclerView.ViewHolder(
    View.inflate(context, R.layout.progress_footer, null)
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
