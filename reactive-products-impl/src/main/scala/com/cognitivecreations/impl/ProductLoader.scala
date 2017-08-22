package com.cognitivecreations.impl

import com.cognitivecreations.product.api.ProductService
import com.example.hello.api.HellolagomService
import com.example.hello.impl.{HellolagomApplication, HellolagomEntity, HellolagomSerializerRegistry, HellolagomServiceImpl}
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader}
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents

class ProductLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new ProductApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ProductApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[ProductService])
}

abstract class ProductApplication(context: LagomApplicationContext)
    extends LagomApplication(context)
        with CassandraPersistenceComponents
        with LagomKafkaComponents
        with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[ProductService](wire[ProductServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = ProductSerializerRegistry

  // Register the hello-lagom persistent entity
  persistentEntityRegistry.register(wire[ProductEntity])

}
