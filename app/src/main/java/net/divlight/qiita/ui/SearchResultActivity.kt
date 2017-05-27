package net.divlight.qiita.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

class SearchResultActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_QUERY = "query"

        fun createIntent(context: Context, query: String? = null): Intent =
                Intent(context, SearchResultActivity::class.java).apply {
                    putExtra(EXTRA_QUERY, query)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val query = intent.extras.getString(EXTRA_QUERY)
        supportActionBar?.subtitle = query
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, ItemFragment.newInstance(query))
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
