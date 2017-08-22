package com.cognitivecreations.impl

import java.util.UUID

import akka.NotUsed
import com.cognitivecreations.product.api.ProductService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

class ProductServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends ProductService {

  override def getSingleProductById(id: String): ServiceCall[NotUsed, Product] = ServiceCall { _ =>
    val ref = persistentEntityRegistry.refFor[ProductEntity](id)
    ref.ask(FetchOneProductCommand(id))
  }

  override def getProductsForVendor(vendorId: String, pageSize: Option[Int], pageNumber: Option[Int]): ServiceCall[NotUsed, List[Product]] = {

  }

  override def getProductsInCategoryIdForResellerWithSort(reseller: Int, categoryId: Int, sortType: Option[String], pageSize: Option[Int], pageNumber: Option[Int]): ServiceCall[NotUsed, List[Product]] = {

  }

  override def getProductsInCategoryNameForResellerWithSort(reseller: Int, categoryNumber: String, sortType: Option[String], pageSize: Option[Int], pageNumber: Option[Int]): ServiceCall[NotUsed, List[Product]] = {

  }

  override def deleteItemForVendor(vendorId: String, productId: String): ServiceCall[NotUsed, Boolean] = {

  }

  override def addItemForVendor(vendorId: String): ServiceCall[NotUsed, UUID] = {
    UUID.randomUUID()
  }

}