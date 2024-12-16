package com.quora.search.config

import org.elasticsearch.client.RestHighLevelClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.erhlc.AbstractElasticsearchConfiguration
import org.springframework.data.elasticsearch.client.erhlc.RestClients
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@Configuration
@EnableElasticsearchRepositories(basePackages = ["com.quora.search.repository"])
class ElasticsearchConfig : AbstractElasticsearchConfiguration() {

    @Bean
    override fun elasticsearchClient(): RestHighLevelClient {
        val clientConfiguration = ClientConfiguration.builder()
            .connectedTo("localhost:9200")
            // .withBasicAuth("user", "password") // Uncomment and set if needed
            .build()
        return RestClients.create(clientConfiguration).rest()
    }
} 