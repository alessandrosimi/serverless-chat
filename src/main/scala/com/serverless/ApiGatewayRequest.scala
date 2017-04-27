package com.serverless

import java.util

import scala.beans.BeanProperty

class ApiGatewayRequest(@BeanProperty var resource: String,
                        @BeanProperty var path: String,
                        @BeanProperty var httpMethod: String,
                        @BeanProperty var headers: util.Map[String, Object],
                        @BeanProperty var queryStringParameters: util.Map[String, Object],
                        @BeanProperty var pathParameters: util.Map[String, Object],
                        @BeanProperty var stageVariables: util.Map[String, Object],
                        @BeanProperty var requestContext: util.Map[String, Object],
                        @BeanProperty var body: String,
                        private var isBase64Encoded: Boolean) {

  def this() = this(
    resource = null,
    path = null,
    httpMethod = null,
    headers = null,
    queryStringParameters = null,
    pathParameters = null,
    stageVariables = null,
    requestContext = null,
    body = null,
    isBase64Encoded = false
  )

  def isIsBase64Encoded = isBase64Encoded

  def setIsBase64Encoded(isBase64Encoded: Boolean) = this.isBase64Encoded = isBase64Encoded

  override def toString = s"ApiGatewayRequest($resource, $path, $httpMethod, $headers, $queryStringParameters, $pathParameters, $stageVariables, $requestContext, $body, $isBase64Encoded)"

}
