package simpledynamodb

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.dynamodbv2.model._
import scala.concurrent.Future

trait DynamoDb {
  def db = DynamoDb
}

object DynamoDb {
  import scala.collection.JavaConversions._

  def table(name: String, accessKey: String, secretKey: String, region: String): DynamoDbTable = {
    val credentials = new AWSCredentials() {
      override def getAWSAccessKeyId(): String = accessKey
      override def getAWSSecretKey(): String = secretKey
    }

    val client = new AmazonDynamoDBClient(credentials)
    client.setRegion(Region.getRegion(Regions.valueOf(region)))

    val table = client.describeTable(name).getTable()
    val primaryKey = table.getKeySchema().toList
      .find(elem => elem.getKeyType() == "HASH").get.getAttributeName
    val range = table.getKeySchema().toList
      .find(elem => elem.getKeyType() == "RANGE").get.getAttributeName
    new DynamoDbTable(name, primaryKey, range, client)
  }
}

class DynamoDbTable(name: String, key: String, range: String, client: AmazonDynamoDBClient) {
  import collection.JavaConverters._

  import concurrent.ExecutionContext.Implicits.global

  def store(keyValue: String, rangeValue: String, values: Map[String, String]) = {
    Future{
      val valuesWithKeys = values ++ Map(key -> keyValue, range -> rangeValue)
      val itemRequest =
        new PutItemRequest().withTableName(name)
          .withItem(valuesWithKeys.mapValues(x => new AttributeValue().withS(x)).asJava)
      client.putItem(itemRequest)
    }
  }

  def query(searchKey: String): DynamoDbQuery =
    new DynamoDbQuery(Map(key -> new Condition().withComparisonOperator(ComparisonOperator.EQ)
      .withAttributeValueList(new AttributeValue().withS(searchKey))), name, client)
}

class DynamoDbQuery(keyCondition: Map[String, Condition],
                    tableName: String,
                    client: AmazonDynamoDBClient,
                    ascending: Option[Boolean] = None,
                    valueLimit: Option[Int] = None) {

  import scala.collection.JavaConversions._
  import collection.JavaConverters._

  def asc = new DynamoDbQuery(keyCondition, tableName, client, Some(true), valueLimit)
  def desc = new DynamoDbQuery(keyCondition, tableName, client, Some(false), valueLimit)
  def limit(x: Int) = new DynamoDbQuery(keyCondition, tableName, client, Some(false), Some(x))

  import concurrent.ExecutionContext.Implicits.global

  def perform: Future[List[Map[String, String]]] = {
    Future {
      val queryRequest =
        new QueryRequest().withTableName(tableName).withKeyConditions(keyCondition.asJava)
      valueLimit.map(queryRequest.setLimit(_))
      ascending.map(queryRequest.setScanIndexForward(_))
      val items = client.query(queryRequest).getItems.toList
      items.map(x => x.toMap.mapValues(x => x.getS))
    }
  }
}

