package com.kevel.apso.elasticsearch.config

import scala.concurrent.duration._

import com.kevel.apso.elasticsearch.config.Elasticsearch.BulkInserter

case class Elasticsearch(
    host: String,
    port: Int,
    useHttps: Boolean,
    username: Option[String] = None,
    password: Option[String] = None,
    bulkInserter: Option[BulkInserter] = None
) {
  require(username.isDefined == password.isDefined, "Both username and password must be provided!")
}

object Elasticsearch {
  case class BulkInserter(
      flushFrequency: FiniteDuration,
      esDownCheckFrequency: FiniteDuration,
      maxBufferSize: Int,
      maxTryCount: Int
  )
}
