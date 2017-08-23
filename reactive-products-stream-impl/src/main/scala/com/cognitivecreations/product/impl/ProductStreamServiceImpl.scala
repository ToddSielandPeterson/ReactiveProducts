package com.cognitivecreations.product.impl

import com.cognitivecreations.product.api.{ProductService, ProductStreamService}
import com.lightbend.lagom.scaladsl.api.ServiceCall

import scala.concurrent.Future

/**
  * Implementation of the ProductStreamService.
  */
class ProductStreamServiceImpl(productService: ProductService) extends ProductStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(v => ProductService.addItemForVendor(v).invoke()))
  }
}
