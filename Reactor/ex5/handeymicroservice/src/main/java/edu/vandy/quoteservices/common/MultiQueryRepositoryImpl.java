package edu.vandy.quoteservices.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * This implementation defines a method that returns a {@link List} of
 * {@link Quote} objects in the database containing at least one of
 * the {@code queries} (ignoring case).
 */
public class MultiQueryRepositoryImpl
      implements MultiQueryRepository {
    /**
     * This field represents a session with the database that provides
     * the main API for performing CRUD (Create, Read, Update, Delete)
     * operations and querying the database.
     */

    @Autowired
    private DatabaseClient databaseClient;

    /**
     * Find a {@link Flux} of {@link Quote} objects in the database
     * containing all of the {@code queries} (ignoring case).
     *
     * @param queries List of queries
     * @return A {@link List} of {@link Quote} objects in the database
     *         containing at all of the {@code queries}
     *         (ignoring case)
     */
    @Override
    public Flux<Quote> findAllByQuoteContainingAllIn(List<String> queries) {
        return databaseClient
                .sql("SELECT * FROM quotes WHERE LOWER(quote) LIKE CONCAT('%', $1, '%')")
                .bind("$1", String.join("%' OR LOWER(quote) LIKE '%", queries).toLowerCase())

                /*
            .sql("SELECT * FROM quotes WHERE LOWER(quote) LIKE $1")
            .bind("$1",
                  "%"
                  + String.join("%", queries).toLowerCase()
                  + "%") */
            .map(row -> new Quote(row.get("id", Integer.class),
                                  row.get("quote", String.class)))
            .all();
    }
}
