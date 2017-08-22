package com.cognitivecreations.impl

import java.time.LocalDateTime
import java.util.UUID

import akka.Done
import com.cognitivecreations.product.data.{PriceLevel, Product, ProductDescription}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

class ProductEntity extends PersistentEntity {

  override type Command = ProductCommand[_]
  override type Event = ProductEvent
  override type State = ProductState

  override def initialState: ProductState = new ProductState(Product(), LocalDateTime.now.toString)

  override def behavior: Behavior = {
    case ProductState(message, _) => Actions().onCommand[Command, Done] {

      case (AddProduct(product), ctx, state) =>
        ctx.thenPersist( ProductAdded(product) ) { _ => ctx.reply(Done) }

      case (UpdateQuantity(id, quantity), ctx, state) =>
        ctx.thenPersist( QuantityUpdated(id, quantity) ) { _ => ctx.reply(Done) }

    }.onReadOnlyCommand[FetchOneProductCommand, State] {

      case (FetchOneProductCommand(id), ctx, state) =>
        ctx.reply(state)

    }.onEvent {

      case (QuantityUpdated(id, newQuantity), state) =>
        ProductState(product = state.product.copy(quantityOnHand = newQuantity), LocalDateTime.now().toString)

    }
  }
}

/**
  * Main product state
  */
case class ProductState(product: Product, timestamp: String)
object ProductState {
  implicit val format: Format[ProductState] = Json.format
}

/**
  * This interface defines all the events that the ProductEntity supports.
  */
sealed trait ProductEvent extends AggregateEvent[ProductEvent] {
  def aggregateTag = ProductEvent.Tag
}

object ProductEvent {
  val Tag = AggregateEventTag[ProductEvent]
}

sealed trait ProductCommand[R] extends ReplyType[R]


/**
  * This interface defines all the commands that the HelloWorld entity supports.
  */
case class FetchOneProductCommand(id: Int) extends ProductCommand[ProductEntity]
object FetchOneProductCommand {
  implicit val formatFetchOneProductCommand: Format[FetchOneProductCommand] = Json.format
}

/**
  * Commands
  */
case class AddProduct(product: Product) extends ProductCommand[ProductEntity]
object AddProduct {
  implicit val formatNewProduct: Format[AddProduct] = Json.format
}

case class UpdateQuantity(id: String, newQuantity: Int) extends ProductCommand[ProductEntity]
object UpdateQuantity {
  implicit val formatUpdateQuantity: Format[UpdateQuantity] = Json.format
}

/**
  * Events
  */
case class QuantityUpdated(id: String, newQuantity: Int) extends ProductEvent
object QuantityUpdated {
  implicit val formatQuantityUpdated: Format[QuantityUpdated] = Json.format
}

case class ProductAdded(product: Product) extends ProductEvent
object ProductAdded {
  implicit val formatQuantityUpdated: Format[ProductAdded] = Json.format
}

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized. While it's
  * possible to use any serializer you want for Akka messages, out of the box
  * Lagom provides support for JSON, via this registry abstraction.
  *
  * The serializers are registered here, and then provided to Lagom in the
  * application loader.
  */
object ProductSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[AddProduct],
    JsonSerializer[UpdateQuantity],
    JsonSerializer[ProductAdded],
    JsonSerializer[FetchOneProductCommand],
    JsonSerializer[UpdateQuantity],
    JsonSerializer[ProductState]
  )
}
