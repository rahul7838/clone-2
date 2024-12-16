# Quora Sync Service

This implementation:

- Captures MongoDB changes via Debezium
- Publishes changes to Kafka topics
- Consumes changes from Kafka
- Updates Elasticsearch accordingly

Benefits:

- Event replay capability
- Decoupled services
- Better scalability
- Message persistence
- Multiple consumers possible
- Better error handling and recovery

To use this:

- Start Kafka and Zookeeper
- Start MongoDB with replica set
- Start Elasticsearch
- Start the sync service

The flow will be:

- MongoDB Change → Debezium → Kafka → ElasticsearchSyncService → Elasticsearch

## We need to start kafka and zookeeper before starting the sync service.


Yes, you need to set up a separate Kafka server with ZooKeeper. The sync-service is just a client that produces and consumes Kafka messages - it doesn't include the actual Kafka broker.

Here's what you need to set up:

1. First, download Kafka (includes ZooKeeper):
```bash
wget https://downloads.apache.org/kafka/3.6.1/kafka_2.13-3.6.1.tgz
tar -xzf kafka_2.13-3.6.1.tgz
cd kafka_2.13-3.6.1
```

2. Start ZooKeeper first:
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

3. Start Kafka server:
```bash
bin/kafka-server-start.sh config/server.properties
```

4. Create the required topics:
```bash
bin/kafka-topics.sh --create --topic quora.changes.questions --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin/kafka-topics.sh --create --topic quora.changes.answers --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

The complete architecture will be:
```
MongoDB → Debezium (in sync-service) → Kafka Broker → ElasticsearchSyncService (in sync-service) → Elasticsearch
```

Where:
- MongoDB is your primary database
- Kafka + ZooKeeper run as separate services
- sync-service contains:
  - Debezium connector (produces to Kafka)
  - Kafka consumer (consumes from Kafka and updates Elasticsearch)
- Elasticsearch is your search database

For development, you can run Kafka and ZooKeeper locally. For production, you might want to:
1. Use a managed Kafka service (like Confluent Cloud, AWS MSK)
2. Or set up a proper Kafka cluster with multiple brokers
3. Configure proper replication and partitioning
4. Set up monitoring and alerting

Would you like me to provide a docker-compose file to easily spin up Kafka and ZooKeeper?
