package com.serverless

import scala.beans.BeanProperty

class Response(@BeanProperty var iotEndpoint: String,
               @BeanProperty var region: String,
               @BeanProperty var accessKey: String,
               @BeanProperty var secretKey: String,
               @BeanProperty var sessionToken: String,
               @BeanProperty var sessionName: String) {
  def this() = this(
    iotEndpoint = null,
    region = null,
    accessKey = null,
    secretKey = null,
    sessionToken = null,
    sessionName = null
  )
}
