package com.serverless

import java.nio.charset.StandardCharsets
import java.util.Base64

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.log4j.Logger
import java.util
import scala.collection.JavaConverters._

import scala.beans.BeanProperty

class ApiGatewayResponse(@BeanProperty var statusCode: Int,
                         @BeanProperty var body: String,
                         @BeanProperty var headers: util.Map[String, String],
                         isBase64Encoded: Boolean) {

  def this() = this(statusCode = 200, body = null, headers = null, isBase64Encoded = false)

  def isIsBase64Encoded = isBase64Encoded

}

object ApiGatewayResponse {

  def builder = new Builder

  object Builder {
    private val LOG: Logger = Logger.getLogger(classOf[Builder])
    private val objectMapper: ObjectMapper = new ObjectMapper
  }

  class Builder {
    private var statusCode: Int = 200
    private var headers: Map[String, String] = Map.empty
    private var rawBody: String = null
    private var objectBody: Any = null
    private var binaryBody: Array[Byte] = null
    private var base64Encoded: Boolean = false

    def setStatusCode(statusCode: Int): Builder = {
      this.statusCode = statusCode
      this
    }

    def setHeaders(headers: Map[String, String]): Builder = {
      this.headers = headers
      this
    }

    /**
      * Builds the [[ApiGatewayResponse]] using the passed raw body string.
      */
    def setRawBody(rawBody: String): Builder = {
      this.rawBody = rawBody
      this
    }

    /**
      * Builds the [[ApiGatewayResponse]] using the passed object body
      * converted to JSON.
      */
    def setObjectBody(objectBody: Any): Builder = {
      this.objectBody = objectBody
      this
    }

    /**
      * Builds the [[ApiGatewayResponse]] using the passed binary body
      * encoded as base64. [[ApiGatewayResponse.Builder.setBase64Encoded(Boolean)]]
		  * will be in invoked automatically.
      */
    def setBinaryBody(binaryBody: Array[Byte]): Builder = {
      this.binaryBody = binaryBody
      setBase64Encoded(true)
      this
    }

    /**
      * A binary or rather a base64encoded responses requires
      * <ol>
      * <li>"Binary Media Types" to be configured in API Gateway
      * <li>a request with an "Accept" header set to one of the "Binary Media
      * Types"
      * </ol>
      */
    def setBase64Encoded(base64Encoded: Boolean): Builder = {
      this.base64Encoded = base64Encoded
      this
    }

    def build: ApiGatewayResponse = {
      val body: String = if (rawBody != null) rawBody
      else if (objectBody != null) {
        try {
          Builder.objectMapper.writeValueAsString(objectBody)
        } catch {
          case e: JsonProcessingException =>
            Builder.LOG.error("failed to serialize object", e)
            throw new RuntimeException(e)
        }
      } else if (binaryBody != null) new String(Base64.getEncoder.encode(binaryBody), StandardCharsets.UTF_8)
      else null
      new ApiGatewayResponse(statusCode, body, headers.asJava, base64Encoded)
    }
  }

}
