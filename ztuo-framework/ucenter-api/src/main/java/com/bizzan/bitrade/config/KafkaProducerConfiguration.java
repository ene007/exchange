package com.bizzan.bitrade.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/***
 * 这段代码配置了一个 Kafka 消息生产者，主要功能包括：
 *
 * 配置 Kafka 生产者的连接参数，包括 Kafka 集群地址、重试次数、批量大小、缓冲区内存等。
 * 创建生产者工厂：通过 ProducerFactory 创建 Kafka 生产者。
 * 使用 KafkaTemplate 发送消息：KafkaTemplate 是一个用于发送 Kafka 消息的高层 API。
 * 消息的键值对序列化：使用 StringSerializer 来序列化消息的键和值，确保消息为字符串类型。
 */
@Configuration
@EnableKafka
public class KafkaProducerConfiguration {

	@Value("${spring.kafka.bootstrap-servers}")
	private String servers;
	@Value("${spring.kafka.producer.retries}")
	private int retries;
	@Value("${spring.kafka.producer.batch.size}")
	private int batchSize;
	@Value("${spring.kafka.producer.linger}")
	private int linger;
	@Value("${spring.kafka.producer.buffer.memory}")
	private int bufferMemory;

	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
		props.put(ProducerConfig.RETRIES_CONFIG, retries);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
		props.put(ProducerConfig.LINGER_MS_CONFIG, linger);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
//		props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.bizzan.bitrade.kafka.kafkaPartitioner");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return props;
	}

	public ProducerFactory<String, String> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		return new KafkaTemplate<String, String>(producerFactory());
	}

}
