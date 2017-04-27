package com.serverless

import java.util.UUID

import com.amazonaws.services.iot.AWSIotClientBuilder
import com.amazonaws.services.iot.model.DescribeEndpointRequest
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder
import com.amazonaws.services.securitytoken.model.{AssumeRoleRequest, GetCallerIdentityRequest}
import org.apache.log4j.Logger

class Handler extends RequestHandler[ApiGatewayRequest, ApiGatewayResponse] {

  private val RoleName = "serverless-chat"

  private val log = Logger.getLogger(classOf[Handler])
  private val iot = AWSIotClientBuilder.defaultClient()
  private val sts = AWSSecurityTokenServiceClientBuilder.defaultClient()

  def handleRequest(input: ApiGatewayRequest, context: Context): ApiGatewayResponse = {

    log.info("describe IOT endpoint")
    val result = iot.describeEndpoint(new DescribeEndpointRequest())
    val endpointAddress = result.getEndpointAddress
    val region = getRegion(endpointAddress)

    log.info("get caller identity account")
    val identity = sts.getCallerIdentity(new GetCallerIdentityRequest())
    val account = identity.getAccount

    log.info("assume role")
    val sessionName = generateRandomSessionName
    val assumeRoleRequest = new AssumeRoleRequest()
      .withRoleArn(s"arn:aws:iam::$account:role/$RoleName")
      .withRoleSessionName(sessionName)
    val role = sts.assumeRole(assumeRoleRequest)

    log.info("build response")
    val responseBody = new Response(
      iotEndpoint = endpointAddress,
      region = region,
      accessKey = role.getCredentials.getAccessKeyId,
      secretKey = role.getCredentials.getSecretAccessKey,
      sessionToken = role.getCredentials.getSessionToken,
      sessionName = sessionName
    )
    val headers = Map(
      "Access-Control-Allow-Origin" -> "*",
      "Content-Type" -> "application/json"
    )
    ApiGatewayResponse.builder
      .setStatusCode(200)
      .setObjectBody(responseBody)
      .setHeaders(headers)
      .build
  }

  private def getRegion(endpoint: String) = {
    val partial = endpoint.replace(".amazonaws.com", "")
    val iotIndex = endpoint.indexOf("iot")
    partial.substring(iotIndex + 4)
  }

  private def generateRandomSessionName = UUID.randomUUID().toString

}
