require "kafka"

$kafka = Kafka.new(
  # At least one of these nodes must be available:
  seed_brokers: ["localhost:9092"],
  # seed_brokers: ["kafka1:9092", "kafka2:9092"],

  # Set an optional client id in order to identify the client to Kafka:
  logger: Rails.logger,
  )

# `async_producer` will create a new asynchronous producer.
$kafka_producer = $kafka.async_producer(
  # Trigger a delivery once 10 messages have been buffered.
  delivery_threshold: 10,
  # Trigger a delivery every 1 second.
  delivery_interval: 1,
)

at_exit { $kafka_producer.shutdown }
