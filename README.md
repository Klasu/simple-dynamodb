SimpleDynamoDB
--------------

Very simple Scala library for Amazon Web Services DynamoDB.

Currently supports only tables with both hash and range keys, and only store and query operations.

Installation
------------

Add to your project's Build.scala
    
    resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    libraryDependencies ++= Seq(
        "com.github.klasu" %% "simple-dynamodb" % "0.1-SNAPSHOT"
    )

Usage
-----

    import simpledynamodb._

    object A extends DynamoDb {
      def table = db.table("TableName", "Credentials", "SecretKey")
      
      def query = {
        val resultFuture: Future[Iterable[Map[String, DynamoDbValue]]] = table.query("searchKey").perform
      }
      
      def store(values: Map[String, DynamoDbValue]) = {
        table.store("Key", "Range", values)
      }
    }

