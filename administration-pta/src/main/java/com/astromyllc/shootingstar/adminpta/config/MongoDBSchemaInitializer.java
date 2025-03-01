package com.astromyllc.shootingstar.adminpta.config;

import com.astromyllc.shootingstar.adminpta.model.Parents;
import com.astromyllc.shootingstar.adminpta.model.Portfolio;
import com.astromyllc.shootingstar.adminpta.model.Students;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MongoDBSchemaInitializer implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final MongoMappingContext mongoMappingContext;

    public MongoDBSchemaInitializer(MongoTemplate mongoTemplate, MongoMappingContext mongoMappingContext) {
        this.mongoTemplate = mongoTemplate;
        this.mongoMappingContext = mongoMappingContext;
    }

    @Override
    public void run(String... args) {
        ensureCollectionExists(Students.class);
        ensureCollectionExists(Portfolio.class);
        ensureCollectionExists(Parents.class);
    }

    private void ensureCollectionExists(Class<?> entityClass) {
        if (!mongoTemplate.collectionExists(entityClass)) {
            mongoTemplate.createCollection(entityClass);
        }

        // Ensure indexes are created
        IndexOperations indexOps = mongoTemplate.indexOps(entityClass);
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);

        resolver.resolveIndexFor(entityClass).forEach(indexOps::ensureIndex);
    }
}
