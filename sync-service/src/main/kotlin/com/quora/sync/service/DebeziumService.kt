package com.quora.sync.service

import io.debezium.embedded.EmbeddedEngine
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Service
import java.util.concurrent.Executors

@Service
class DebeziumService(private val engine: EmbeddedEngine) {
    
    private val executor = Executors.newSingleThreadExecutor()

    @PostConstruct
    fun start() {
        executor.execute(engine)
    }

    @PreDestroy
    fun stop() {
        engine.stop()
        executor.shutdown()
    }
} 