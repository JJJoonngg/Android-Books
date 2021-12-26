package kr.co.jjjoonngg.rssreader.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.co.jjjoonngg.rssreader.R
import kr.co.jjjoonngg.rssreader.model.Article

/*
* Created by JJJoonngg
*/

interface ArticleLoader {
    suspend fun loadMore()
}

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    private val articles: MutableList<Article> = mutableListOf()
    private var loading = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.article, parent, false) as LinearLayout
        val feed = layout.findViewById<TextView>(R.id.feed)
        val title = layout.findViewById<TextView>(R.id.title)
        val summary = layout.findViewById<TextView>(R.id.summary)

        return ViewHolder(layout, feed, title, summary)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]

        holder.feed.text = article.feed
        holder.title.text = article.title
        holder.summary.text = article.summary
    }

    override fun getItemCount() = articles.size

    @SuppressLint("NotifyDataSetChanged")
    fun add(article: Article) {
        this.articles.add(article)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear(){
        this.articles.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(
        val layout: LinearLayout,
        val feed: TextView,
        val title: TextView,
        val summary: TextView
    ) : RecyclerView.ViewHolder(layout)
}