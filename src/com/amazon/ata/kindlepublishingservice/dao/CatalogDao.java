package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.Book;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormatConverter;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;
import javax.inject.Inject;

public class CatalogDao {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a new CatalogDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        return book;
    }

    public CatalogItemVersion getBookFromCatalog2(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        return book;
    }

    // Returns null if no version exists for the provided bookId
    private CatalogItemVersion getLatestVersionOfBook(String bookId) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);

        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression()
            .withHashKeyValues(book)
            .withScanIndexForward(false)
            .withLimit(1);

        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class, queryExpression);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public CatalogItemVersion removeBookFromCatalog(String bookId) {
        CatalogItemVersion book = getBookFromCatalog2(bookId);

        if (book == null) {
            throw new BookNotFoundException(bookId);
        }

        if (book.isInactive()) {
            throw new BookNotFoundException(bookId);
        }

        book.setInactive(true);

        dynamoDbMapper.save(book);

        return book;
    }

    public boolean validateBookExists(String bookId) {

        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);


        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression()
                .withHashKeyValues(book)
                .withScanIndexForward(false)
                .withLimit(1);

        CatalogItemVersion results = dynamoDbMapper.load(CatalogItemVersion.class, bookId);


        if (results == null) {
            return false;
        }

        return true;
    }


    public boolean createOrUpdateBook(KindleFormattedBook formattedBook) {
        if (formattedBook != null) {
            CatalogItemVersion book = new CatalogItemVersion();
            CatalogItemVersion lastestBook = null;

            if (getLatestVersionOfBook(formattedBook.getBookId()) != null) {

                CatalogItemVersion results = getLatestVersionOfBook(formattedBook.getBookId());

                book.setBookId(results.getBookId());
                book.setAuthor(formattedBook.getAuthor());
                book.setGenre(formattedBook.getGenre());
                book.setText(formattedBook.getText());
                book.setTitle(formattedBook.getTitle());
                book.setVersion(results.getVersion() + 1);

                lastestBook = this.getLatestVersionOfBook(formattedBook.getBookId());
                lastestBook.setInactive(true);
            } else {
                book.setBookId(formattedBook.getBookId());
                book.setAuthor(formattedBook.getAuthor());
                book.setGenre(formattedBook.getGenre());
                book.setText(formattedBook.getText());
                book.setTitle(formattedBook.getTitle());
                book.setVersion(1);
            }

            dynamoDbMapper.save(book);

            book = this.getLatestVersionOfBook(book.getBookId());

            if (book == null) {
                throw new BookNotFoundException(formattedBook.getBookId());
            }

            if (lastestBook != null) {
                dynamoDbMapper.save(lastestBook);
            }
            return true;
        }
        return false;
    }

}
