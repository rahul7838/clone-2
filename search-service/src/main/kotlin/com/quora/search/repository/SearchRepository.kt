package com.quora.search.repository

import com.quora.search.model.SearchDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface SearchRepository : ElasticsearchRepository<SearchDocument, String> {
    
    @Query("""
        {
            "multi_match": {
                "query": "?0",
                "fields": ["title^2", "content", "tags"],
                "type": "best_fields",
                "fuzziness": "AUTO"
            }
        }
    """)
    fun searchByQueryString(query: String, pageable: Pageable): Page<SearchDocument>
    
    @Query("""
        {
            "bool": {
                "must": [
                    { "term": { "tags": "?0" } }
                ]
            }
        }
    """)
    fun searchByTag(tag: String, pageable: Pageable): Page<SearchDocument>
} 