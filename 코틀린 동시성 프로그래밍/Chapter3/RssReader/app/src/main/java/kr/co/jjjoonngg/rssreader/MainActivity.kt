package kr.co.jjjoonngg.rssreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.coroutines.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    private val netDispatcher = newSingleThreadContext(name = "ServiceCall")
    private val factory = DocumentBuilderFactory.newInstance()
    private val dispatcher = newFixedThreadPoolContext(2, "IO")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        asyncLoadNews()
    }

    @ExperimentalCoroutinesApi
    private fun asyncLoadNews() =
        GlobalScope.launch(dispatcher) {
            val requests = mutableListOf<Deferred<List<String>>>()

            val feeds = listOf(
                "https://www.npr.org/rss/rss.php?id=1001",
                "http://rss.cnn.com/rss/cnn_topstories.rss",
                "http://feeds.foxnews.com/foxnews/politics?format=xml"
            )

            feeds.mapTo(requests) {
                asyncFetchHeadlines(it, dispatcher)
            }

            requests.forEach {
                it.await()
            }

            val headlines = requests.flatMap {
                it.getCompleted()
            }
            val newsCount = findViewById<TextView>(R.id.newsCount)
            launch(Dispatchers.Main) {
                newsCount.text = "Found ${headlines.size} News" +
                        "in ${requests.size} feeds"
            }
        }

    private fun asyncFetchHeadlines(
        feed: String,
        dispatcher: CoroutineDispatcher
    ) = GlobalScope.async(dispatcher) {
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse(feed)
        val news = xml.getElementsByTagName("channel").item(0)

        (0 until news.childNodes.length)
            .map { news.childNodes.item(it) }
            .filter { Node.ELEMENT_NODE == it.nodeType }
            .map { it as Element }
            .filter { "item" == it.tagName }
            .map {
                it.getElementsByTagName("title").item(0).textContent
            }
    }
}