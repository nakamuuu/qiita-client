package net.divlight.qiita.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import net.divlight.qiita.model.Item
import net.divlight.qiita.service.QiitaServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    val items: MutableLiveData<List<Item>?> by lazy {
        MutableLiveData<List<Item>?>().also {
            fetchItems()
        }
    }

    private var call: Call<List<Item>>? = null

    override fun onCleared() {
        call?.run { cancel() }
    }

    private fun fetchItems() {
        call = QiitaServiceCreator.createService().getItems(1, 20)
        call?.enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                items.value = response?.body()
            }

            override fun onFailure(call: Call<List<Item>>?, t: Throwable?) {
                // TODO: error handling
            }
        })
    }
}
