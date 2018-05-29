package net.divlight.qiita.ui.item

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.support.annotation.IntRange
import net.divlight.qiita.R
import net.divlight.qiita.extension.arch.getString
import net.divlight.qiita.network.QiitaServiceCreator
import net.divlight.qiita.network.response.Item
import net.divlight.qiita.ui.common.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemViewModel(
    application: Application,
    private val query: String? = null
) : AndroidViewModel(application), LifecycleObserver {
    companion object {
        private const val ITEMS_PER_PAGE = 30
    }

    val items = ObservableArrayList<Item>()
    val fetchStatus = ObservableField(FetchStatus.Initial)
    val errorState = ObservableField(ErrorState.None)

    val openUrlEvent: SingleLiveEvent<OpenUrlEvent> = SingleLiveEvent()
    val openSearchResultEvent: SingleLiveEvent<OpenSearchResultEvent> = SingleLiveEvent()
    val showSnackbarEvent: SingleLiveEvent<ShowSnackbarEvent> = SingleLiveEvent()
    val scrollToTopEvent: SingleLiveEvent<EmptyEventObject> = SingleLiveEvent()

    private var nextPage: Int = 1
    private var hasNextPage = true
    private var call: Call<List<Item>>? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        if (fetchStatus.get() == FetchStatus.Initial) {
            fetchStatus.set(FetchStatus.Fetching)
            fetchItems()
        }
    }

    override fun onCleared() {
        call?.cancel()
    }

    fun onListItemClick(item: Item) {
        openUrlEvent.postValue(OpenUrlEvent(item.url))
    }

    fun onTagClick(tag: Item.Tag) {
        openSearchResultEvent.postValue(OpenSearchResultEvent("tag:${tag.name}"))
    }

    fun onSwipeRefresh() {
        if (fetchStatus.get()?.shouldEnableSwipeRefresh == true) {
            fetchStatus.set(FetchStatus.Refreshing)
            fetchItems(isRefresh = true)
        }
    }

    fun onScrollToEnd() {
        if (fetchStatus.get() == FetchStatus.Fetched && hasNextPage) {
            fetchStatus.set(FetchStatus.FetchingNext)
            fetchItems(nextPage)
        }
    }

    private fun fetchItems(@IntRange(from = 1) page: Int = 1, isRefresh: Boolean = false) {
        call = QiitaServiceCreator.createService().getItems(page, ITEMS_PER_PAGE, query)
        call?.enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                if (isRefresh) {
                    items.clear()
                }

                val fetchedItems = response?.body() ?: emptyList()
                items.addAll(fetchedItems)
                nextPage = page + 1
                hasNextPage = (fetchedItems.size == ITEMS_PER_PAGE)
                fetchStatus.set(FetchStatus.Fetched)
                errorState.set(if (items.isEmpty()) ErrorState.Empty else ErrorState.None)
                if (isRefresh) {
                    scrollToTopEvent.postValue(EmptyEventObject)
                }
            }

            override fun onFailure(call: Call<List<Item>>?, t: Throwable?) {
                fetchStatus.set(FetchStatus.Fetched)
                errorState.set(if (items.isEmpty()) ErrorState.Error else ErrorState.None)
                showSnackbarEvent.postValue(ShowSnackbarEvent(getString(R.string.item_fetch_error)))
            }
        })
    }

    enum class FetchStatus {
        Initial, Fetching, Fetched, FetchingNext, Refreshing;

        val shouldShowContent get() = this == Fetched || this == FetchingNext || this == Refreshing
        val shouldShowProgressBar get() = this == Fetching
        val shouldEnableSwipeRefresh get() = this == Fetched
        val shouldShowRefreshIndicator get() = this == Refreshing
        val shouldShowFooterProgress get() = this == FetchingNext
    }

    enum class ErrorState {
        None, Error, Empty;

        val shouldShowErrorText get() = this == Error
        val shouldShowEmptyText get() = this == Empty
    }

    data class OpenUrlEvent(val url: String)

    data class OpenSearchResultEvent(val query: String?)

    data class ShowSnackbarEvent(val text: String)

    object EmptyEventObject

    class Factory(
        private val application: Application,
        private val query: String?
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = modelClass
            .getConstructor(Application::class.java, String::class.java)
            .newInstance(application, query)
    }
}
