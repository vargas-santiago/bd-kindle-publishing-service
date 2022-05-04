package com.amazon.ata.kindlepublishingservice.mastery;

import com.amazon.ata.kindlepublishingservice.helpers.IntegrationTestBase;
import com.amazon.ata.kindlepublishingservice.helpers.KindlePublishingServiceTctTestDao;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils extends IntegrationTestBase {

    public TestUtils() {}

    public void cleanTables()  {

        List<String> statuses = new ArrayList<>();
        statuses.add("QUEUED");
        statuses.add("IN_PROGRESS");
        statuses.add("FAILED");
        statuses.add("SUCCESSFUL");

        for (String status : statuses) {
            SUPER_CLEAN(status);
        }


    }

    private void SUPER_CLEAN(String status) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_WEST_2).build();

        DynamoDBMapper mapper = new DynamoDBMapper(client);

        Map<String, AttributeValue> test = new HashMap<>();

        test.put(":statuss", new AttributeValue().withS(status));

        DynamoDBQueryExpression<KindlePublishingServiceTctTestDao.PublishingStatusItem> query = new DynamoDBQueryExpression<KindlePublishingServiceTctTestDao.PublishingStatusItem>()
                .withConsistentRead(false)
                .withIndexName("statuss-index")
                .withKeyConditionExpression("statuss = :statuss")
                .withExpressionAttributeValues(test)
                .withScanIndexForward(false);

        List<KindlePublishingServiceTctTestDao.PublishingStatusItem> items = mapper.query(KindlePublishingServiceTctTestDao.PublishingStatusItem.class, query);

        for (KindlePublishingServiceTctTestDao.PublishingStatusItem item : items) {
            System.out.println(item);
            super.getTestDao().delete(item);

        }
    }
}
