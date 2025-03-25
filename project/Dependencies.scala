import sbt._

object Dependencies {
  object Versions {
    val Pekko                   = "1.1.3"
    val PekkoHttp               = "1.1.0"
    val Aws                     = "1.12.782"
    val BouncyCastle            = "1.80"
    val Cats                    = "2.13.0"
    val Circe                   = "0.14.12"
    val CommonsCodec            = "1.18.0"
    val ConcurrentLinkedHashMap = "1.4.2"
    val FastMd5                 = "2.7.1"
    val JUnit                   = "4.13.2"
    val JodaTime                = "2.13.1"
    val NscalaTime              = "3.0.0"
    val ScalaCheck              = "1.18.1"
    val ScalaLogging            = "3.9.5"
    val ScalaPool               = "0.4.3"
    val ScalaTest               = "3.2.19"
    val SimpleJmx               = "2.2"
    val Specs2                  = "4.21.0"
    val Squants                 = "1.8.3"
    val SshJ                    = "0.39.0"
    val TypesafeConfig          = "1.4.3"
    val UnirestJava             = "4.4.5"
  }

  // scalafmt: { maxColumn = 200 }
  val AwsJavaSdkCore             = "com.amazonaws"                          % "aws-java-sdk-core"            % Versions.Aws
  val AwsJavaSdkS3               = "com.amazonaws"                          % "aws-java-sdk-s3"              % Versions.Aws
  val BouncyCastlePkix           = "org.bouncycastle"                       % "bcpkix-jdk18on"               % Versions.BouncyCastle
  val BouncyCastleProvider       = "org.bouncycastle"                       % "bcprov-jdk18on"               % Versions.BouncyCastle
  val CatsCore                   = "org.typelevel"                         %% "cats-core"                    % Versions.Cats
  val CirceCore                  = "io.circe"                              %% "circe-core"                   % Versions.Circe
  val CirceGeneric               = "io.circe"                              %% "circe-generic"                % Versions.Circe
  val CirceLiteral               = "io.circe"                              %% "circe-literal"                % Versions.Circe
  val CirceParser                = "io.circe"                              %% "circe-parser"                 % Versions.Circe
  val CommonsCodec               = "commons-codec"                          % "commons-codec"                % Versions.CommonsCodec
  val ConcurrentLinkedHashMapLru = "com.googlecode.concurrentlinkedhashmap" % "concurrentlinkedhashmap-lru"  % Versions.ConcurrentLinkedHashMap
  val FastMd5                    = "com.joyent.util"                        % "fast-md5"                     % Versions.FastMd5
  val JUnit                      = "junit"                                  % "junit"                        % Versions.JUnit
  val JodaTime                   = "joda-time"                              % "joda-time"                    % Versions.JodaTime
  val NscalaTime                 = "com.github.nscala-time"                %% "nscala-time"                  % Versions.NscalaTime
  val PekkoActor                 = "org.apache.pekko"                      %% "pekko-actor"                  % Versions.Pekko
  val PekkoActorTestkitTyped     = "org.apache.pekko"                      %% "pekko-actor-testkit-typed"    % Versions.Pekko
  val PekkoActorTyped            = "org.apache.pekko"                      %% "pekko-actor-typed"            % Versions.Pekko
  val PekkoHttp                  = "org.apache.pekko"                      %% "pekko-http"                   % Versions.PekkoHttp
  val PekkoHttpCore              = "org.apache.pekko"                      %% "pekko-http-core"              % Versions.PekkoHttp
  val PekkoHttpTestkit           = "org.apache.pekko"                      %% "pekko-http-testkit"           % Versions.PekkoHttp
  val PekkoStream                = "org.apache.pekko"                      %% "pekko-stream"                 % Versions.Pekko
  val PekkoStreamTestkit         = "org.apache.pekko"                      %% "pekko-stream-testkit"         % Versions.Pekko
  val ScalaCheck                 = "org.scalacheck"                        %% "scalacheck"                   % Versions.ScalaCheck
  val ScalaCollectionCompat      = "org.scala-lang.modules"                %% "scala-collection-compat"      % "2.13.0"
  val ScalaLogging               = "com.typesafe.scala-logging"            %% "scala-logging"                % Versions.ScalaLogging
  val ScalaPool                  = "io.github.andrebeat"                   %% "scala-pool"                   % Versions.ScalaPool
  val ScalaTestCore              = "org.scalatest"                         %% "scalatest-core"               % Versions.ScalaTest
  val SimpleJmx                  = "com.j256.simplejmx"                     % "simplejmx"                    % Versions.SimpleJmx
  val Specs2Common               = "org.specs2"                            %% "specs2-common"                % Versions.Specs2
  val Specs2Core                 = "org.specs2"                            %% "specs2-core"                  % Versions.Specs2
  val Specs2JUnit                = "org.specs2"                            %% "specs2-junit"                 % Versions.Specs2
  val Specs2Matcher              = "org.specs2"                            %% "specs2-matcher"               % Versions.Specs2
  val Specs2ScalaCheck           = "org.specs2"                            %% "specs2-scalacheck"            % Versions.Specs2
  val Squants                    = "org.typelevel"                         %% "squants"                      % Versions.Squants
  val SshJ                       = "com.hierynomus"                         % "sshj"                         % Versions.SshJ
  val TypesafeConfig             = "com.typesafe"                           % "config"                       % Versions.TypesafeConfig
  val UnirestJava                = "com.konghq"                             % "unirest-java-core"            % Versions.UnirestJava
  // scalafmt: { maxColumn = 120 }
}
