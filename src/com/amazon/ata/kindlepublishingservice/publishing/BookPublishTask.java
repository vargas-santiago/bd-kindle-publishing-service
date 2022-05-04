package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.checkerframework.checker.units.qual.C;

import javax.inject.Inject;
import java.util.List;

public class BookPublishTask implements Runnable {

    private BookPublishRequestManager manager;
    private PublishingStatusDao publishingStatusDao;
    private CatalogDao catalogDao;

    AmazonDynamoDB amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
            .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
            .withRegion(Regions.US_WEST_2)
            .build();

    @Inject
    public BookPublishTask(BookPublishRequestManager manager) {
        this.manager = manager;
        this.publishingStatusDao = new PublishingStatusDao(new DynamoDBMapper(amazonDynamoDBClient));
        this.catalogDao = new CatalogDao(new DynamoDBMapper(amazonDynamoDBClient));
    }

    @Override
    public void run() {
        System.out.println("RUNNING TASK TEST");
        try {
            publishBook();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void publishBook() throws InterruptedException {
        System.out.println("FINDING REQUEST");

        BookPublishRequestManager manager1 = new BookPublishRequestManager();
        //Thread.sleep(5000);
        manager1.populateRequest();

        BookPublishRequest requests = manager1.getBookPublishRequestsToProcess();

        //System.out.println("FINISHED FINDING REQUEST: " + requests.size());

        //BookPublishRequest request = requests.get(requests.size());


        if (requests == null) {
            System.out.println("NO REQUEST");
            return;
        }

        System.out.println("FOUND REQUEST" + requests.getPublishingRecordId());

        Thread.sleep(600);

        publishingStatusDao.setPublishingStatus(requests.getPublishingRecordId(), PublishingRecordStatus.IN_PROGRESS, requests.getBookId());

        KindleFormattedBook kindleFormattedBook = KindleFormatConverter.format(requests);


        try {
            while (catalogDao.createOrUpdateBook(kindleFormattedBook) == false) {
                catalogDao.createOrUpdateBook(kindleFormattedBook);
            }
            System.out.println("added Item");
            publishingStatusDao.setPublishingStatus(requests.getPublishingRecordId(), PublishingRecordStatus.SUCCESSFUL, requests.getBookId(), "Success");
            System.out.println("set status");
            System.out.println("Success request" + requests.getPublishingRecordId());
        } catch (Exception e) {
            publishingStatusDao.setPublishingStatus(requests.getPublishingRecordId(), PublishingRecordStatus.FAILED, requests.getBookId(), "Failed");
            System.out.println("Failed request" + requests.getPublishingRecordId());
            e.printStackTrace();
        }
    }
}
