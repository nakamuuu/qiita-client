@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package net.divlight.qiita.extension.databinding

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import net.divlight.qiita.ui.common.recyclerview.OnScrollToEndListenerAdapter

//
// ImageView
//

@BindingAdapter(
    value = ["glide_src", "glide_placeholder", "glide_error", "glide_circleCrop", "glide_crossFade"],
    requireAll = false
)
fun ImageView.loadWithGlide(
    source: Any?,
    placeholder: Drawable?,
    error: Drawable?,
    circleCrop: Boolean?,
    crossFade: Boolean?
) {
    Glide.with(this).load(source)
        .apply(RequestOptions().apply {
            placeholder?.let { placeholder(it) }
            error?.let { error(it) }
            if (circleCrop == true) circleCrop()
        })
        .apply { if (crossFade == true) transition(DrawableTransitionOptions.withCrossFade()) }
        .into(this)
}

//
// SwipeRefreshLayout
//

@BindingAdapter("refreshing")
fun SwipeRefreshLayout.setRefreshing(refreshing: Boolean) {
    isRefreshing = refreshing
}

@BindingAdapter("onRefresh")
fun SwipeRefreshLayout.setOnRefreshListener(listener: SwipeRefreshLayout.OnRefreshListener) {
    setOnRefreshListener(listener)
}

//
// RecyclerView
//

@BindingAdapter("onScrollToEnd")
fun RecyclerView.setOnScrollToEndListener(listener: OnScrollToEndListener) {
    addOnScrollListener(object : OnScrollToEndListenerAdapter() {
        override fun onScrollToEnd() {
            listener.onScrollToEnd()
        }
    })
}

interface OnScrollToEndListener {
    fun onScrollToEnd()
}
