package com.cognitivecreations.product.data

import java.util.UUID

import org.joda.money.Money
import play.api.libs.json.{Format, Json}

case class Brand(id: UUID = UUID.randomUUID(),
                 group: String,
                 brandName: String)

object Brand {
  implicit val brandFormat: Format[Brand] = Json.format
}

case class ProductDescription(id: UUID = UUID.randomUUID(),
                             index: Int,
                             tabName: String,
                             text: String)

object ProductDescription {
  implicit val productDescriptionFormat: Format[ProductDescription] = Json.format
}

case class PriceLevel(level: Int, salePrice: Money, commission: Money, vendorCost: Money)

object PriceLevel {
  implicit val priceLevelFormat: Format[PriceLevel] = Json.format
}

case class Product(id: UUID = UUID.randomUUID(),
                   name: String,
                   brand: String,
                   msrp: Option[Money],
                   prices: List[PriceLevel],
                   shortDescription: List[String],
                   descriptions: List[ProductDescription],
                   quantityOnHand: Int,
                   quantityInCart: Int = 0,
                   noQuantity: Boolean,
                   images: List[String] // base image name.
)

object Product {
  def apply(id: UUID = UUID.randomUUID(),
            name: String = "",
            brand: String = "",
            msrp: Option[Money] = None,
            prices: List[PriceLevel] = List.empty[PriceLevel],
            shortDescription: List[String] = List.empty[String],
            descriptions: List[ProductDescription] = List.empty[ProductDescription],
            quantityOnHand: Int = 0,
            quantityInCart: Int = 0,
            noQuantity: Boolean = false,
            images: List[String] = List.empty[String] // base image name.
           ): Product = new Product(id, name, brand, msrp, prices, shortDescription, descriptions, quantityOnHand, quantityInCart, noQuantity, images)

  implicit val productFormat = Format[Product] = Json.format
}
