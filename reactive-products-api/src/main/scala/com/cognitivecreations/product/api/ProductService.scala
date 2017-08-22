package com.cognitivecreations.product.api

import java.util.UUID

import akka.NotUsed
import com.example.hello.api.GreetingMessageChanged
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

object ProductService {
  val TOPIC_NAME = "productService"
}


trait ProductService extends Service {

  val SORT_NATURAL = "natural"
  val SORT_BY_NAME = "name"
  val SORT_BY_BRAND = "brand"
  val SORT_LOW_TO_HIGH = "lowhigh"
  val SORT_HIGH_TO_LOW = "highlow"

  def getSingleProductById(id: Int): ServiceCall[NotUsed, Product]
  def getProductsForVendor(vendorId: String, pageSize: Option[Int], pageNumber: Option[Int]): ServiceCall[NotUsed, List[Product]]
  def getProductsInCategoryIdForResellerWithSort(reseller: Int,  categoryId: Int, sortType: Option[String], pageSize: Option[Int], pageNumber: Option[Int]): ServiceCall[NotUsed, List[Product]]
  def getProductsInCategoryNameForResellerWithSort(reseller: Int,  categoryNumber: String, sortType: Option[String], pageSize: Option[Int], pageNumber: Option[Int]): ServiceCall[NotUsed, List[Product]]
  def getProductsInCategoryIdForReseller(reseller: Int,  categoryId: Int): ServiceCall[NotUsed, List[Product]] = {
    getProductsInCategoryIdForResellerWithSort(reseller, categoryId, Some(SORT_NATURAL), Some(-1), Some(0))
  }

  def getProductsInCategoryNameForReseller(reseller: Int,  categoryNumber: String): ServiceCall[NotUsed, List[Product]] = {
    getProductsInCategoryNameForResellerWithSort(reseller, categoryNumber, Some(SORT_NATURAL), Some(-1), Some(0))
  }

  def deleteItemForVendor(id: Int): ServiceCall[NotUsed, Boolean] = {

  }

  def addItemForVendor(id: Int): ServiceCall[NotUsed, UUID] = {

  }

  /**
    * This gets published to Kafka.
    */
  def greetingsTopic(): Topic[GreetingMessageChanged]

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("productapi")
        .withCalls(
          pathCall("/api/product/:id", getSingleProductById _),
          pathCall("/api/products/vendor/vendorId:?pageNumber&pageSize", getProductsForVendor _),
          pathCall("/api/products/reseller/resllerId:/categoryId/:categoryId?sortType&pageNumber&pageSize", getProductsInCategoryIdForResellerWithSort _),
          pathCall("/api/products/reseller/resllerId:/category/:categoryName?sortType&pageNumber&pageSize", getProductsInCategoryNameForResellerWithSort _),
          pathCall("/api/products/reseller/resllerId:/categoryId/:categoryId", getProductsInCategoryIdForReseller _),
          pathCall("/api/products/reseller/resllerId:/category/:categoryName", getProductsInCategoryNameForReseller _),
          restCall(Method.DELETE, "/api/product/vendor/:vendorId/product/:id", deleteItemForVendor _),
          restCall(Method.PUT, "/api/product/vendor/:vendorId/product/:id", deleteItemForVendor _),
          restCall(Method.PUT, "/api/product/vendor/:vendorId", addItemForVendor _)
        )
        .withTopics(
          topic(ProductService.TOPIC_NAME, greetingsTopic _)
              // Kafka partitions messages, messages within the same partition will
              // be delivered in order, to ensure that all messages for the same user
              // go to the same partition (and hence are delivered in order with respect
              // to that user), we configure a partition key strategy that extracts the
              // name as the partition key.
              .addProperty(
            KafkaProperties.partitionKeyStrategy,
            PartitionKeyStrategy[GreetingMessageChanged](_.name)
          )
        )
        .withAutoAcl(true)
    // @formatter:on
  }

}
