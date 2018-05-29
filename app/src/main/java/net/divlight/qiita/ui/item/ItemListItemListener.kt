package net.divlight.qiita.ui.item

import net.divlight.qiita.network.response.Item

interface ItemListItemListener {
    fun onItemClick(item: Item)
    fun onTagClick(tag: Item.Tag)
}
