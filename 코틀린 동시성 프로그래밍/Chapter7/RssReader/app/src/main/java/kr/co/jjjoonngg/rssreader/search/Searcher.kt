package kr.co.jjjoonngg.rssreader.search

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kr.co.jjjoonngg.rssreader.model.Article
import kr.co.jjjoonngg.rssreader.model.Feed
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

/*
* Created by JJJoonngg
*/

class Searcher() {

    val dispatcher = newFixedThreadPoolContext(3, "IO-Search")
    val factory = DocumentBuilderFactory.newInstance()

    val feeds = listOf(
        Feed("npr", "https://www.npr.org/rss/rss.php?id=1001"),
        Feed("cnn", "http://rss.cnn.com/rss/cnn_topstories.rss"),
        Feed("fox", "http://feeds.foxnews.com/foxnews/politics?format=xml")
    )

    fun search(query: String): ReceiveChannel<Article> {
        val channel = Channel<Article>(150)

        feeds.forEach { feed ->
            CoroutineScope(dispatcher).launch {
                search(feed, channel, query)
            }
        }
        return channel
    }

    private suspend fun search(
        feed: Feed,
        channel: SendChannel<Article>,
        query: String
    ) {
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse(feed.url)
        val news = xml.getElementsByTagName("channel").item(0)

        (0 until news.childNodes.length)
            .map { news.childNodes.item(it) }
            .filter { Node.ELEMENT_NODE == it.nodeType }
            .map { it as Element }
            .filter { "item" == it.tagName }
            .forEach {
                val title = it.getElementsByTagName("title")
                    .item(0)
                    .textContent
                var summary = it.getElementsByTagName("description")
                    .item(0)
                    .textContent

                if (title.contains(query) || summary.contains(query)) {
                    if (summary.contains("<div")) {
                        summary = summary.substring(0, summary.indexOf("<div"))
                    }

                    val article = Article(feed.name, title, summary)
                    channel.send(article)
                }
            }
    }
}