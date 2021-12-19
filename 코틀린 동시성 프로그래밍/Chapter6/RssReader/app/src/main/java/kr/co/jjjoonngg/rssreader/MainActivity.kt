package kr.co.jjjoonngg.rssreader

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kr.co.jjjoonngg.rssreader.adapter.ArticleAdapter
import kr.co.jjjoonngg.rssreader.adapter.ArticleLoader
import kr.co.jjjoonngg.rssreader.producer.ArticleProducer

class MainActivity : AppCompatActivity(), ArticleLoader {
    private lateinit var articles: RecyclerView
    private lateinit var viewAdapter: ArticleAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = ArticleAdapter(this)
        articles = findViewById<RecyclerView>(R.id.articles).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        Log.d("TAG_A", "onCreate Called, and binding done")

        CoroutineScope(Dispatchers.Default).launch {
            Log.d("TAG_A", "before call loadMore()")
            loadMore()
        }
    }

    override suspend fun loadMore() {
        val producer = ArticleProducer.producer

        Log.d("TAG_A", "loadMore Called, and is empty? ${producer.isEmpty}")
        if (!producer.isClosedForReceive) {
            val articles = producer.receive()

            CoroutineScope(Dispatchers.Main).launch {
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                viewAdapter.add(articles)
                Log.d("TAG_A", "Test for the load")
            }
        }
    }
}