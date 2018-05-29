package net.divlight.qiita.ui.item

import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.divlight.qiita.R
import net.divlight.qiita.databinding.FragmentItemBinding
import net.divlight.qiita.extension.arch.observeNonNull
import net.divlight.qiita.ui.SearchResultActivity

class ItemFragment : Fragment() {
    companion object {
        private const val ARGS_QUERY = "query"

        fun newInstance(query: String? = null): ItemFragment = ItemFragment().apply {
            arguments = Bundle().apply { putString(ARGS_QUERY, query) }
        }
    }

    private lateinit var binding: FragmentItemBinding
    private lateinit var viewModel: ItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this,
            ItemViewModel.Factory(requireActivity().application, arguments?.getString(ARGS_QUERY))
        ).get(ItemViewModel::class.java)
        viewModel.openUrlEvent.observeNonNull(this) {
            launchCustomTabs(it.url)
        }
        viewModel.openSearchResultEvent.observeNonNull(this) {
            startActivity(SearchResultActivity.createIntent(requireContext(), it.query))
        }
        viewModel.showSnackbarEvent.observeNonNull(this) {
            Snackbar.make(binding.root, it.text, Snackbar.LENGTH_SHORT).show()
        }
        viewModel.scrollToTopEvent.observeNonNull(this) {
            binding.recyclerView.scrollToPosition(0)
        }
        lifecycle.addObserver(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemBinding.inflate(inflater, container, false).apply {
            viewModel = this@ItemFragment.viewModel
            swipeRefreshLayout.setColorSchemeResources(android.R.color.white)
            swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.primary)
            recyclerView.adapter = ItemAdapter(requireContext(), this@ItemFragment.viewModel)
        }
        return binding.root
    }

    private fun launchCustomTabs(url: String) {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.primary))
            .setStartAnimations(requireContext(), R.anim.slide_in_right, R.anim.slide_out_left)
            .setExitAnimations(
                requireContext(),
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .build()
            .launchUrl(requireContext(), Uri.parse(url))
    }
}
