package kr.co.jjjoonngg.rssreader.producer

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kr.co.jjjoonngg.rssreader.model.Article
import kr.co.jjjoonngg.rssreader.model.Feed
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.lang.Exception
import javax.xml.parsers.DocumentBuilderFactory

/*
* Created by JJJoonngg
*/
object ArticleProducer {
    private val feeds = listOf(
        Feed("npr", "https://www.npr.org/rss/rss.php?id=1001"),
        Feed("cnn", "http://rss.cnn.com/rss/cnn_topstories.rss"),
        Feed("fox", "http://feeds.foxnews.com/foxnews/politics?format=xml")
    )

    val producer = CoroutineScope(Dispatchers.IO).produce {
        feeds.forEach {
            Log.d("TAG_A", "feed : $it")
            try {
                send(fetchArticles(it))
            } catch (e: Exception) {
                Log.d("TAG_A", "exception erupt! $e")
            }
        }
    }

    private fun fetchArticles(feed: Feed): List<Article> {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val xml = builder.parse(feed.url)
        val news = xml.getElementsByTagName("channel").item(0)

        return (0 until news.childNodes.length)
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

                if (summary.contains("<div")) {
                    summary = summary.substring(0, summary.indexOf("<div"))
                }

                Article(feed.name, title, summary)
            }
    }

}