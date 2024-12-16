package com.quora.sync.config

import com.quora.sync.service.ChangeEventHandler
import io.debezium.config.Configuration
import io.debezium.embedded.EmbeddedEngine
import org.apache.kafka.connect.source.SourceRecord
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration as SpringConfiguration

@SpringConfiguration
class DebeziumConfig {

    @Bean
    fun debeziumConfiguration(): Configuration {
        return Configuration.create()
            .with("name", "quora-mongodb-connector")
            .with("connector.class", "io.debezium.connector.mongodb.MongoDbConnector")
            .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
            .with("offset.storage.file.filename", "./offsets.dat")
            .with("offset.flush.interval.ms", "60000")
            //begin connector property
            .with("mongodb.connection.string", "mongodb://localhost:27017/?replicaSet=rs0")
            .with("mongodb.hosts", "rs0/localhost:27017")
            .with("database.include.list", "quora_questions")
            .with("collection.include.list", "quora_questions.questions,quora_questions.answers")
            .with("database.history", "io.debezium.relational.history.MemoryDatabaseHistory")
            .with("topic.prefix", "quora")
            .with("mongodb.members.auto.discover", "true")
            .with("capture.scope", "deployment")
            .with("snapshot.mode", "initial")
            .with("max.batch.size", "1")
            .build()
    }

    @Bean
    fun debeziumEngine(changeEventHandler: ChangeEventHandler): EmbeddedEngine {
        return EmbeddedEngine.EngineBuilder()
            .using(debeziumConfiguration().asProperties())
            .notifying { t -> changeEventHandler.handleChangeEvent(t as SourceRecord) }
            .build() as EmbeddedEngine
    }
} 