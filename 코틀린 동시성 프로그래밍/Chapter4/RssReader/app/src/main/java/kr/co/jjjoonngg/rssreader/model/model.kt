package kr.co.jjjoonngg.rssreader.model

/*
* Created by JJJoonngg
*/

data class Feed(
    val name: String,
    val url: String
)

data class Article(
    val feed: String,
    val title: String,
    val summary: String
)