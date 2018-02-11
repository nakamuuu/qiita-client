package net.divlight.qiita.ui.item

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModel
import android.support.annotation.IntRange
import net.divlight.qiita.network.QiitaServiceCreator
import net.divlight.qiita.network.response.Item
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemViewModel : ViewModel(), LifecycleObserver {
    companion object {
        private const val ITEMS_PER_PAGE = 30
    }

    var query: String? = null
    val items: MutableLiveData<List<Item>> = MutableLiveData()
    val status: MutableLiveData<FetchStatus> = MutableLiveData()

    private var nextPage: Int = 1
    private var hasNextPage = true
    private var call: Call<List<Item>>? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        if (items.value?.isEmpty() != false) {
            status.value = FetchStatus.FIRST_PAGE_FETCHING
            fetchItems()
        }
    }

    override fun onCleared() {
        call?.cancel()
    }

    fun onRefreshItems() {
        if (status.value?.isFetching != true) {
            status.value = FetchStatus.FIRST_PAGE_RELOADING
            fetchItems()
        }
    }

    fun onScrollToEnd() {
        if (status.value?.isFetching != true && hasNextPage) {
            status.value = FetchStatus.NEXT_PAGE_FETCHING
            fetchItems(nextPage)
        }
    }

    private fun fetchItems(@IntRange(from = 1) page: Int = 1) {
        call = QiitaServiceCreator.createService().getItems(page, ITEMS_PER_PAGE, query)
        call?.enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                val fetchedItems = response?.body() ?: emptyList()
                if (page == 1) {
                    items.value = fetchedItems
                } else {
                    items.value = ((items.value ?: emptyList()).toMutableList()).apply {
                        addAll(fetchedItems)
                    }.toList()
                }
                status.value = FetchStatus.INITIAL
                nextPage = page + 1
                hasNextPage = (fetchedItems.size == ITEMS_PER_PAGE)
            }

            override fun onFailure(call: Call<List<Item>>?, t: Throwable?) {
                // TODO: error handling
                status.value = FetchStatus.INITIAL
            }
        })
    }

    enum class FetchStatus(val isFetching: Boolean) {
        INITIAL(false),
        FIRST_PAGE_FETCHING(true),
        FIRST_PAGE_RELOADING(true),
        NEXT_PAGE_FETCHING(true)
    }
}
