package net.divlight.qiita.ui.item

import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import net.divlight.qiita.R
import net.divlight.qiita.extension.arch.observeNonNull
import net.divlight.qiita.ui.SearchResultActivity
import net.divlight.qiita.ui.common.recyclerview.OnScrollToEndListenerAdapter

class ItemFragment : Fragment() {
    companion object {
        private const val ARGS_QUERY = "query"

        fun newInstance(query: String? = null): ItemFragment = ItemFragment().apply {
            arguments = Bundle().apply { putString(ARGS_QUERY, query) }
        }
    }

    private lateinit var unbinder: Unbinder
    private lateinit var viewModel: ItemViewModel
    private lateinit var adapter: ItemAdapter

    @BindView(R.id.swipe_refresh_layout)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.progress_bar)
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater?.inflate(R.layout.fragment_item, container, false)?.apply {
            unbinder = ButterKnife.bind(this@ItemFragment, this)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)
        viewModel.query = arguments.getString(ARGS_QUERY)
        viewModel.items.observeNonNull(this, { adapter.items = it })
        viewModel.status.observeNonNull(this, { status ->
            swipeRefreshLayout.isRefreshing =
                    (status == ItemViewModel.FetchStatus.FIRST_PAGE_RELOADING)
            adapter.progressFooterShown = (status == ItemViewModel.FetchStatus.NEXT_PAGE_FETCHING)
            if (status == ItemViewModel.FetchStatus.FIRST_PAGE_FETCHING) {
                recyclerView.visibility = View.INVISIBLE
                progressBar.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
            }
        })

        swipeRefreshLayout.setOnRefreshListener { viewModel.reloadFirstPage() }
        swipeRefreshLayout.setColorSchemeResources(android.R.color.white)
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.primary)

        adapter = ItemAdapter(context).apply {
            onItemClick = { launchCustomTabs(it.url) }
            onTagClick = {
                startActivity(
                    SearchResultActivity.createIntent(
                        context,
                        "tag:" + it.name
                    )
                )
            }
        }
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : OnScrollToEndListenerAdapter() {
            override fun onScrollToEnd() {
                recyclerView.post { viewModel.fetchNextPage() }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    private fun launchCustomTabs(url: String) {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setToolbarColor(ContextCompat.getColor(context, R.color.primary))
            .setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
            .setExitAnimations(
                context,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .build()
            .launchUrl(context, Uri.parse(url))
    }
}
