package com.cognitivecreations.product.impl

import com.cognitivecreations.product.api.{ProductService, ProductStreamService}
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents

class HellolagomStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new ProductStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ProductStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[ProductStreamService])
}

abstract class ProductStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[ProductStreamService](wire[ProductStreamServiceImpl])

  // Bind the HellolagomService client
  lazy val productService = serviceClient.implement[ProductService]
}
