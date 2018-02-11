package net.divlight.qiita.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import net.divlight.qiita.R
import net.divlight.qiita.ui.item.ItemFragment

class SearchResultActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_QUERY = "query"

        fun createIntent(context: Context, query: String? = null): Intent =
                Intent(context, SearchResultActivity::class.java).apply {
                    putExtra(EXTRA_QUERY, query)
                }
    }

    @BindView(R.id.toolbar)
    internal lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        val query = intent.extras.getString(EXTRA_QUERY)
        supportActionBar?.subtitle = query
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, ItemFragment.newInstance(query))
                    .commit()
        }
    }

    //
    // Options Menu
    //

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
