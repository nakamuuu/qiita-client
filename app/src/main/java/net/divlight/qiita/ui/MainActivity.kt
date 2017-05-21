package net.divlight.qiita.ui

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import butterknife.BindView
import butterknife.ButterKnife
import net.divlight.qiita.R
import net.divlight.qiita.model.Item
import net.divlight.qiita.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), LifecycleRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: ItemAdapter

    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView
    @BindView(R.id.progress_bar) lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        adapter = ItemAdapter(this).apply {
            onItemClick = { launchCustomTabs(it.url) }
            onTagClick = {
                // TODO: Open tag detail screen
            }
        }
        recyclerView.adapter = adapter

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.items.observe(this, Observer { updateViews(it) })

        updateViews(null)
    }

    private fun updateViews(items: List<Item>?) {
        adapter.items = items ?: emptyList()
        adapter.notifyDataSetChanged()

        if (items != null) {
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
        } else {
            recyclerView.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun launchCustomTabs(url: String) {
        CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setToolbarColor(ContextCompat.getColor(this, R.color.primary))
                .setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
                .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .build()
                .launchUrl(this, Uri.parse(url))
    }

    //
    // LifecycleRegistryOwner
    //

    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry
}
