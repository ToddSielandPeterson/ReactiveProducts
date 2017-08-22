package com.cognitivecreations.product.data

import java.util.UUID

import org.joda.money.Money

object VendorProfile{
  val GROUND_SHIPPING: Int = 0

  val TWO_DAY_SHIPPING: Int = 1
  val ONE_DAY_SHIPPING: Int = 2

  val USE_UPS: Int = 1
  val USE_FEDEX: Int = 2
  val USE_USPS: Int = 3

}

case class Address(firstName: Option[String] = None,
                  lastName: Option[String] = None,
                  companyName: Option[String] = None,
                  address1: Option[String] = None,
                  address2: Option[String] = None,
                  city: Option[String] = None,
                  stateCode: Option[String] = None,
                  postalCode: Option[String] = None,
                  countryCode: Option[String] = None)

case class Contacts(firstName: String,
                    lastName: String,
                    emailAddress: String,
                    phone: Option[String],
                    fax: Option[String],
                    contactType: String
                   )

object Contacts {
  val CONTACT_TYPE = ""
}


case class Vendor(vendorNr: UUID = UUID.randomUUID(),
                  ediPartnerid: Option[String] = None,
                  // company Information
                  companyName: Option[String] = None,
                  federalId: Option[String] = None,
                  companyAddress: Address,
                  shippingAddress: Address,

                  notes: List[String] = List.empty,

                  contacts: List[Contacts],

                  newOrderNotification: List[String] = List.empty, // emails
                  notificationSubject: Option[String] = None,
                  orderCharge: Option[Money] = None,

                  // account Types
                  shippingAccountType: Integer = null, // what type of ups account above is. (1-ups, 2-fedex etc.)
                  upsAccount: Option[String] = None, // really the code for the type from above

                  // shipping lookup table info
                  shippingType: Integer = null, // use stdshipping zone. special loaded zone

                  shippingZone: Integer = null,
                  statusId: Integer = null,
                  resellerId: Integer = null,
                  attachedResellersFlag: Integer = null,
                  platformResellerGroupVendorXrefs: Nothing = new Nothing,
                  products: Nothing = new Nothing,
                  orderVendor: Nothing = new Nothing,
                  vendorStates: Nothing = new Nothing,
                  daysToAutoClose: Integer = null,

                  daysExpectShip: Integer = null,
                  daysTilCancel: Integer = null,
                  ediVendorNumber: Option[String] = None,

                  wsKey: Option[String] = None,
                  alternateId: Option[String] = None,
                  requiredShippingMethod: Integer = null,
                  accountNr: Option[String] = None,
                  accountAbaNr: Option[String] = None,
                  // id that the external system may use
                  bankToken: Option[String] = None,

                  shippingAccountId: Option[String] = None,
                  shippingApi: Option[String] = None,
                  shippingShipper: Option[String] = None,
                  avgHeight: Integer = null,

                  avgWidth: Integer = null,

                  avgDepth: Integer = null)

            )

