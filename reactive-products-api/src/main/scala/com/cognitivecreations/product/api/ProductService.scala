package com.cognitivecreations.product.api

import java.util.UUID

import akka.NotUsed
import com.example.hello.api.GreetingMessageChanged
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

case class PageFilter(filter: Option[String])
case class PageSort(sortType: Option[String] = Some(PageSort.SORT_NATURAL), pageSize: Option[Int] = Some(-1), pageNumber: Option[Int] = Some(1))

object PageSort {
  val SORT_NATURAL = "natural"
  val SORT_BY_NAME = "name"
  val SORT_BY_BRAND = "brand"
  val SORT_LOW_TO_HIGH = "lowhigh"
  val SORT_HIGH_TO_LOW = "highlow"
}

object ProductService {
  val TOPIC_NAME = "productService"
}

trait ProductService extends Service {

  def getSingleProductById(id: String): ServiceCall[NotUsed, Product]
  def getProductsInCategoryIdForResellerSorted(reseller: String,  categoryId: String, pageSort: PageSort): ServiceCall[NotUsed, List[Product]]
  def getProductsInCategoryNameForResellerSorted(reseller: String,  categoryNumber: Int, pageSort: PageSort): ServiceCall[NotUsed, List[Product]]

  def getProductsForVendorWithSortAndFilter(vendorId: String, pageSort: PageSort, filter: PageFilter): ServiceCall[NotUsed, List[Product]]

  def deleteItemForVendor(vendorId: String, productId: String): ServiceCall[NotUsed, Boolean]

  def addItemForVendor(vendorId: String): ServiceCall[NotUsed, UUID]

  // Composite Methods
  def getProductsForVendor(vendorId: String, sortType: Option[String], pageSize: Option[Int], pageNumber: Option[Int], filterString: Option[String]): ServiceCall[NotUsed, List[Product]] = {
    getProductsForVendorWithSortAndFilter(vendorId, new PageSort(sortType, pageSize, pageNumber), new PageFilter(filterString))
  }
  def getProductsInCategoryIdForResellerWithSort(reseller: String,  categoryId: String, sortType: String, pageSize: Int, pageNumber: Int): ServiceCall[NotUsed, List[Product]] = {
    getProductsInCategoryIdForResellerSorted(reseller, categoryId, new PageSort(Some(sortType), Some(pageSize), Some(pageNumber)))
  }
  def getProductsInCategoryNameForResellerWithSort(reseller: String,  categoryNumber: Int, sortType: String, pageSize: Int, pageNumber: Int): ServiceCall[NotUsed, List[Product]] = {
    getProductsInCategoryNameForResellerSorted(reseller, categoryNumber, new PageSort(Some(sortType), Some(pageSize), Some(pageNumber)))
  }
  def getProductsInCategoryIdForReseller(reseller: String,  categoryId: String): ServiceCall[NotUsed, List[Product]] = {
    getProductsInCategoryIdForResellerSorted(reseller, categoryId, new PageSort())
  }
  def getProductsInCategoryNameForReseller(reseller: String,  categoryNumber: Int): ServiceCall[NotUsed, List[Product]] = {
    getProductsInCategoryNameForResellerSorted(reseller, categoryNumber, new PageSort())
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
          pathCall("/api/products/vendor/vendorId:?sortType&pageNumber&pageSize&filterString", getProductsForVendor _),
          pathCall("/api/products/reseller/resllerId:/categoryId/:categoryId?sortType&pageNumber&pageSize", getProductsInCategoryIdForResellerWithSort _),
          pathCall("/api/products/reseller/resllerId:/category/:categoryName?sortType&pageNumber&pageSize", getProductsInCategoryNameForResellerWithSort _),
          pathCall("/api/products/reseller/resllerId:/categoryId/:categoryId", getProductsInCategoryIdForReseller _),
          pathCall("/api/products/reseller/resllerId:/category/:categoryName", getProductsInCategoryNameForReseller _),
          restCall(Method.DELETE, "/api/product/vendor/:vendorId/product/:id", deleteItemForVendor _),
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
