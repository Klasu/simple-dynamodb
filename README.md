Installation
------------
Clone
    
    git clone git@github.com:Klasu/simple-dynamodb.git
    
Run

    sbt publish-local
    
Add to your project's Build.scala
    
    resolvers ++= Seq(
      Resolver.file("Local Ivy", file(Path.userHome +
        File.separator + ".ivy2" + File.separator +
        "local"))(Resolver.ivyStylePatterns),
      Resolver.mavenLocal
    ),
    libraryDependencies ++= Seq(
        "com.github.klasu" %% "simple-dynamodb" % "0.1-SNAPSHOT"
    )

Usage
-----

    import simpledynamodb._

    object A extends DynamoDb {
      def table = db.table("TableName", "Credentials", "SecretKey", "AVAILABILITY-ZONE")
      
      def query = {
        val resultFuture: Future[List[Map[String, String]]] = table.query("searchKey").perform
      }
      
      def store(values: Map[String, String]) = {
        table.store("Key", "Range", values)
      }
    }

