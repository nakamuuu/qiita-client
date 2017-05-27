package net.divlight.qiita.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.IntRange
import net.divlight.qiita.model.Item
import net.divlight.qiita.service.QiitaServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemViewModel : ViewModel() {
    var query: String? = null
    val items: MutableLiveData<List<Item>> by lazy {
        MutableLiveData<List<Item>>().apply {
            status.value = FetchStatus.FIRST_PAGE_FETCHING
            fetchItems()
        }
    }
    val status: MutableLiveData<FetchStatus> = MutableLiveData()

    private var nextPage: Int = 1
    private var call: Call<List<Item>>? = null

    override fun onCleared() {
        call?.cancel()
    }

    fun reloadFirstPage() {
        if (!(status.value?.isFetching ?: false)) {
            status.value = FetchStatus.FIRST_PAGE_RELOADING
            fetchItems()
        }
    }

    fun fetchNextPage() {
        if (!(status.value?.isFetching ?: false)) {
            status.value = FetchStatus.NEXT_PAGE_FETCHING
            fetchItems(nextPage)
        }
    }

    private fun fetchItems(@IntRange(from = 1) page: Int = 1) {
        call = QiitaServiceCreator.createService().getItems(page, 20, query)
        call?.enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                if (page == 1) {
                    items.value = response?.body()
                } else {
                    items.value = ((items.value ?: emptyList()).toMutableList()).apply {
                        addAll(response?.body() ?: emptyList())
                    }.toList()
                }
                status.value = FetchStatus.INITIAL
                nextPage = page + 1
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
