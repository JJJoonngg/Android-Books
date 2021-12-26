package kr.co.jjjoonngg.rssreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kr.co.jjjoonngg.rssreader.adapter.ArticleAdapter
import kr.co.jjjoonngg.rssreader.search.ResultsCounter
import kr.co.jjjoonngg.rssreader.search.Searcher

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class SearchActivity : AppCompatActivity() {

    private lateinit var articles: RecyclerView
    private lateinit var viewAapter: ArticleAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val searcher = Searcher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewManager = LinearLayoutManager(this)
        viewAapter = ArticleAdapter()
        articles = findViewById<RecyclerView>(R.id.searchArticles).apply {
            layoutManager = viewManager
            adapter = viewAapter
        }

        findViewById<Button>(R.id.searchButton).setOnClickListener {
            viewAapter.clear()
            CoroutineScope(Dispatchers.Default).launch {
                ResultsCounter.reset()
                search()
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            updateCounter()
        }
    }

    @ExperimentalCoroutinesApi
    private suspend fun search() {
        val query = findViewById<EditText>(R.id.searchText).text.toString()
        val channel = searcher.search(query)

        while (!channel.isClosedForReceive) {
            val article = channel.receive()

            CoroutineScope(Dispatchers.Main).launch {
                viewAapter.add(article)
            }
        }
    }

    private suspend fun updateCounter() {
        val notifications = ResultsCounter.getNotificationChannel()
        val results = findViewById<TextView>(R.id.searchResult)

        while (!notifications.isClosedForReceive) {
            val newAmount = notifications.receive()

            withContext(Dispatchers.Main) {
                results.text = "Results : $newAmount"
            }
        }
    }
}