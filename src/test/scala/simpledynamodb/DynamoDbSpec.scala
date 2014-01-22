package simpledynamodb

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, QueryRequest, QueryResult}
import org.specs2.mutable._
import scala.concurrent.duration._
import scala.concurrent.Await

class DynamoDbSpec extends Specification {

  "DynamoDB Query" should {
    "parse string results correctly" in {
      val results = new DynamoDbTable("name", "key", "range", new MockDynamoDbClient()).query("query").perform
      Await.result(results, new DurationInt(100).milliseconds).toList(0)("string").value mustEqual "a string"
    }
    "parse list results correctly" in {
      val results = new DynamoDbTable("name", "key", "range", new MockDynamoDbClient()).query("query").perform
      Await.result(results, new DurationInt(100).milliseconds).toList(0)("list").value mustEqual List("a string", "another string")
    }
  }
}

class MockDynamoDbClient extends AmazonDynamoDBClient {

  import collection.JavaConverters._

  override def query(request: QueryRequest): QueryResult = {
    val result = new QueryResult()
    val items = List(Map("string" -> stringValue("a string"), "list" -> listValue(List("a string", "another string"))).asJava).asJavaCollection
    result.setItems(items)
    result
  }

  def stringValue(x: String) = {
    val value = new AttributeValue()
    value.setS(x)
    value
  }

  def listValue(x: List[String]) = {
    val value = new AttributeValue()
    value.setSS(x.asJava)
    value
  }
}
