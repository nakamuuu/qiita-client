package net.divlight.qiita.ui.search

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModel
import android.support.annotation.IntRange
import net.divlight.qiita.network.QiitaServiceCreator
import net.divlight.qiita.network.response.Tag
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel(), LifecycleObserver {
    companion object {
        private const val TAGS_PER_PAGE = 100
    }

    val tags: MutableLiveData<List<Tag>> = MutableLiveData()
    val status: MutableLiveData<FetchStatus> = MutableLiveData()

    private var call: Call<List<Tag>>? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        if (tags.value?.isEmpty() != false) {
            status.value = FetchStatus.FETCHING
            fetchItems()
        }
    }

    override fun onCleared() {
        call?.cancel()
    }

    private fun fetchItems(@IntRange(from = 1) page: Int = 1) {
        call = QiitaServiceCreator.createService().getTags(page, TAGS_PER_PAGE, "count")
        call?.enqueue(object : Callback<List<Tag>> {
            override fun onResponse(call: Call<List<Tag>>?, response: Response<List<Tag>>?) {
                tags.value = response?.body() ?: emptyList()
                status.value = FetchStatus.INITIAL
            }

            override fun onFailure(call: Call<List<Tag>>?, t: Throwable?) {
                status.value = FetchStatus.ERROR
            }
        })
    }

    enum class FetchStatus {
        INITIAL, FETCHING, ERROR
    }
}
