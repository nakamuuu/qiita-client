package net.divlight.qiita.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import net.divlight.qiita.model.Item
import net.divlight.qiita.service.QiitaServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    val items: MutableLiveData<List<Item>> = MutableLiveData()
    var status: MutableLiveData<FetchStatus> = MutableLiveData()

    private var nextPage: Int = 1
    private var call: Call<List<Item>>? = null

    init {
        status.value = FetchStatus.FIRST_PAGE_FETCHING
        fetchItems()
    }

    override fun onCleared() {
        call?.cancel()
    }

    fun fetchNextPage(): Boolean {
        return if (!(status.value?.isFetching ?: false)) {
            status.value = FetchStatus.NEXT_PAGE_FETCHING
            fetchItems(nextPage)
            true
        } else {
            false
        }
    }

    private fun fetchItems(page: Int = 1) {
        call = QiitaServiceCreator.createService().getItems(page, 20)
        call?.enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                status.value = FetchStatus.INITIAL
                nextPage = page + 1
                items.value = ((items.value ?: emptyList()).toMutableList()).apply {
                    addAll(response?.body() ?: emptyList())
                }.toList()
            }

            override fun onFailure(call: Call<List<Item>>?, t: Throwable?) {
                // TODO: error handling
                status.value = FetchStatus.INITIAL
            }
        })
    }

    enum class FetchStatus(val isFetching: Boolean) {
        INITIAL(false), FIRST_PAGE_FETCHING(true), NEXT_PAGE_FETCHING(true)
    }
}
