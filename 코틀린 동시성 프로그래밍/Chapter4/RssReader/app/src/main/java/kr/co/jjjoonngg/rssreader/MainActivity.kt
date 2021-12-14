package kr.co.jjjoonngg.rssreader

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kr.co.jjjoonngg.rssreader.adapter.ArticleAdapter
import kr.co.jjjoonngg.rssreader.model.Article
import kr.co.jjjoonngg.rssreader.model.Feed
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    @ObsoleteCoroutinesApi
    private val netDispatcher = newSingleThreadContext(name = "ServiceCall")
    private val factory = DocumentBuilderFactory.newInstance()
    @ObsoleteCoroutinesApi
    private val dispatcher = newFixedThreadPoolContext(2, "IO")

    private val feeds = listOf(
        Feed("npr", "https://www.npr.org/rss/rss.php?id=1001"),
        Feed("cnn", "http://rss.cnn.com/rss/cnn_topstories.rss"),
        Feed("fox", "http://feeds.foxnews.com/foxnews/politics?format=xml"),
        Feed("inv", "htt:myNewsFeed")
    )

    private lateinit var articles: RecyclerView
    private lateinit var viewAdapter: ArticleAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = ArticleAdapter()
        articles = findViewById<RecyclerView>(R.id.articles).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }


        asyncLoadNews()
    }

    @SuppressLint("SetTextI18n")
    @ExperimentalCoroutinesApi
    private fun asyncLoadNews() =
        GlobalScope.launch(dispatcher) {
            val requests = mutableListOf<Deferred<List<Article>>>()

            feeds.mapTo(requests) {
                asyncFetchArticles(it, dispatcher)
            }

            requests.forEach {
                it.join()
            }

            val articles = requests
                .filter { !it.isCancelled }
                .flatMap { it.getCompleted() }
            val failedCount = requests
                .filter { it.isCancelled }
                .size

            val obtained = requests.size - failedCount

            launch(Dispatchers.Main) {
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                viewAdapter.add(articles)
            }
        }

    private fun asyncFetchArticles(
        feed: Feed,
        dispatcher: CoroutineDispatcher
    ) = GlobalScope.async(dispatcher) {
        delay(1000)
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse(feed.url)
        val news = xml.getElementsByTagName("channel").item(0)

        (0 until news.childNodes.length)
            .map { news.childNodes.item(it) }
            .filter { Node.ELEMENT_NODE == it.nodeType }
            .map { it as Element }
            .filter { "item" == it.tagName }
            .map {
                val title = it.getElementsByTagName("title")
                    .item(0)
                    .textContent
                var summary = it.getElementsByTagName("description")
                    .item(0)
                    .textContent

                if (!summary.startsWith("<div") && summary.contains("<div")) {
                    summary = summary.substring(0, summary.indexOf("<div"))
                }

                Article(feed.name, title, summary)
            }
    }
}