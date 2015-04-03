import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "tz_chatroom"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "mysql" % "mysql-connector-java" % "5.1.25",
    "org.apache.commons" % "commons-lang3" % "3.1",
    "commons-validator" % "commons-validator" % "1.4.0",
    "com.amazonaws" % "aws-java-sdk" % "1.5.1",
    "com.github.mumoshu" %% "play2-memcached" % "0.3.0.2",
  	"redis.clients" % "jedis" % "2.1.0",
  	"com.typesafe" %% "play-plugins-mailer" % "2.1-RC2",
		"org.hibernate" % "hibernate-entitymanager" % "3.6.9.Final",
		"org.hibernate.javax.persistence" % "hibernate-jpa-2.0-api" % "1.0.1.Final",
		"org.mybatis" % "mybatis" % "3.1.1",
		"com.google.code.gson" % "gson" % "2.2.4",
		"javax.websocket" % "javax.websocket-api" % "1.1"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
	  resolvers += "Spy Repository" at "http://files.couchbase.com/maven2" // required to resolve `spymemcached`, the plugin's dependency.
  )

}
