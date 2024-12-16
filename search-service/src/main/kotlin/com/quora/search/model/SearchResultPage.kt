package com.quora.search.model

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.function.Function

class SearchResultPage(
    private val content: List<SearchResult>,
    private val pageable: Pageable,
    private val total: Long
) : Page<SearchResult> {

    override fun getContent(): List<SearchResult> = content

    override fun getTotalElements(): Long = total

    override fun getTotalPages(): Int {
        if (pageable.pageSize == 0) return 1
        return ((total - 1) / pageable.pageSize + 1).toInt()
    }

    override fun getNumber(): Int = pageable.pageNumber

    override fun getSize(): Int = pageable.pageSize

    override fun getNumberOfElements(): Int = content.size

    override fun isFirst(): Boolean = pageable.pageNumber == 0

    override fun isLast(): Boolean = pageable.pageNumber >= getTotalPages() - 1

    override fun hasNext(): Boolean = !isLast

    override fun hasPrevious(): Boolean = !isFirst

    override fun getPageable(): Pageable = pageable

    override fun getSort(): Sort = pageable.sort

    override fun isEmpty(): Boolean = content.isEmpty()

    override fun <U : Any?> map(converter: Function<in SearchResult, out U>): Page<U> {
        val convertedContent = content.map { converter.apply(it) } as List<SearchResult>
        return SearchResultPage(
            content = convertedContent,
            pageable = pageable,
            total = total
        ) as Page<U>
    }

    override fun hasContent(): Boolean = content.isNotEmpty()

    override fun nextPageable(): Pageable {
        return if (hasNext()) {
            pageable.next()
        } else {
            Pageable.unpaged()
        }
    }

    override fun previousPageable(): Pageable {
        return if (hasPrevious()) {
            pageable.previousOrFirst()
        } else {
            Pageable.unpaged()
        }
    }

    override fun iterator(): MutableIterator<SearchResult> {
        return content.toMutableList().iterator()
    }
} 