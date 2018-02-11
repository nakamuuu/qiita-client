package net.divlight.qiita.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import net.divlight.qiita.R
import net.divlight.qiita.ui.item.ItemFragment
import net.divlight.qiita.ui.search.SearchActivity

class MainActivity : AppCompatActivity() {
    @BindView(R.id.toolbar)
    internal lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, ItemFragment.newInstance())
                    .commit()
        }
    }

    //
    // Options Menu
    //

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.search -> {
            startActivity(SearchActivity.createIntent(this))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
