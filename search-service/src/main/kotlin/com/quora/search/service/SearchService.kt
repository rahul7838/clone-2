package com.quora.search.service

import com.quora.search.model.SearchResult
import com.quora.search.model.SearchResultPage
import org.elasticsearch.index.query.MultiMatchQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val elasticsearchOperations: ElasticsearchOperations,
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private val logger = LoggerFactory.getLogger(SearchService::class.java)
    
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

    @Cacheable(value = ["search_questions"], key = "#query")
    fun searchQuestions(query: String, pageable: Pageable): Page<SearchResult> {
        val searchQuery = NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.multiMatchQuery(query)
                .field("title", 2.0f)
                .field("content")
                .field("tags")
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS))
            .withPageable(pageable)
            .build()

        return executeSearch(searchQuery, "questions", pageable)
    }

    @Cacheable(value = ["search_answers"], key = "#query")
    fun searchAnswers(query: String, pageable: Pageable): Page<SearchResult> {
        val searchQuery = NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.multiMatchQuery(query)
                .field("content", 1.0f)
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS))
            .withPageable(pageable)
            .build()

        return executeSearch(searchQuery, "answers", pageable)
    }

    @Cacheable(value = ["search_by_tag"], key = "#tag")
    fun searchQuestionsByTag(tag: String, pageable: Pageable): Page<SearchResult> {
        val searchQuery = NativeQueryBuilder()
            .withQuery(co.elastic.clients.elasticsearch._types.query_dsl.Query.of {
                it._custom("tags", tag)
            })
            .withPageable(pageable)
            .build()

        return executeSearch(searchQuery, "questions", pageable)
    }

    private fun executeSearch(query: Query, index: String, pageable: Pageable): Page<SearchResult> {
        try {
            val searchHits: SearchHits<SearchResult> = elasticsearchOperations.search(
                query,
                SearchResult::class.java
            )

            val results = searchHits.searchHits.map { it.content }
            val total = searchHits.totalHits

            return SearchResultPage(
                content = results,
                pageable = pageable,
                total = total
            ) as Page<SearchResult>
        } catch (e: Exception) {
            logger.error("Error executing search: ${e.message}", e)
            throw e
        }
    }
} 