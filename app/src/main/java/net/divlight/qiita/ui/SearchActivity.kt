package net.divlight.qiita.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import net.divlight.qiita.R
import net.divlight.qiita.ui.common.TextWatcherAdapter

class SearchActivity : AppCompatActivity() {
    companion object {
        private val REQUEST_CODE_VOICE_RECOGNIZER = Activity.RESULT_FIRST_USER

        fun createIntent(context: Context): Intent = Intent(context, SearchActivity::class.java)
    }

    @BindView(R.id.toolbar) internal lateinit var toolbar: Toolbar
    private lateinit var queryEditView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        queryEditView = EditText(this).apply {
            background = null
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setSingleLine(true)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            hint = getString(R.string.search_query_hint)
            setHintTextColor(ContextCompat.getColor(this@SearchActivity, R.color.hint_text))
            addTextChangedListener(object : TextWatcherAdapter() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    invalidateOptionsMenu()
                }
            })
            setOnEditorActionListener({ _, actionId, _ ->
                val query = queryEditView.text.toString()
                if (actionId == EditorInfo.IME_ACTION_SEARCH && !query.isNullOrEmpty()) {
                    startActivity(SearchResultActivity.createIntent(this@SearchActivity, query))
                }
                true
            })
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(queryEditView, ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_VOICE_RECOGNIZER) {
            if (resultCode == Activity.RESULT_OK) {
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)?.let {
                    queryEditView.setText(it)
                    queryEditView.setSelection(it.length)
                    invalidateOptionsMenu()
                    startActivity(SearchResultActivity.createIntent(this, it))
                }
            } else {
                if (queryEditView.requestFocus()) {
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                            .showSoftInput(queryEditView, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
    }


    //
    // Options Menu
    //

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val isEmptyQuery = queryEditView.text.isNullOrEmpty()
        menu?.findItem(R.id.voice)?.isVisible = isEmptyQuery
        menu?.findItem(R.id.clear)?.isVisible = !isEmptyQuery
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        R.id.voice -> {
            try {
                startActivityForResult(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                }, REQUEST_CODE_VOICE_RECOGNIZER)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this@SearchActivity, "Error : ActivityNotFoundException", Toast.LENGTH_LONG).show()
            }
            true
        }
        R.id.clear -> {
            queryEditView.setText("")
            invalidateOptionsMenu()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
