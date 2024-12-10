package com.quora.search.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val elasticsearchOperations: ElasticsearchOperations,
    private val redisTemplate: RedisTemplate<String, Any>
) {
    
    @Cacheable(value = ["search"], key = "#query")
    fun search(query: String): List<SearchResult> {
        val searchQuery = NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.multiMatchQuery(query)
                .field("title", 2.0f)
                .field("content")
                .field("tags"))
            .build()
            
        return elasticsearchOperations.search(searchQuery, SearchResult::class.java)
            .searchHits
            .map { it.content }
    }
} 