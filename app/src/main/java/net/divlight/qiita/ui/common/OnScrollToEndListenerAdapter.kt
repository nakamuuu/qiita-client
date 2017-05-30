package net.divlight.qiita.ui.common

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

abstract class OnScrollToEndListenerAdapter : RecyclerView.OnScrollListener() {
    abstract fun onScrollToEnd()

    override final fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
    }

    override final fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        recyclerView ?: return

        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                ?: throw IllegalArgumentException("A parent RecyclerView must use " + LinearLayoutManager::class.java)

        if (dy > 0) {
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                onScrollToEnd()
            }
        }
    }
}
