package se.webcv.config;


import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import se.webcv.utils.ConfigUtils;


@Configuration
@EnableMongoRepositories
public class DatabaseConfig extends AbstractMongoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);

    @Override
    protected String getDatabaseName() {
        return "web_cv";
    }

    private String url() {
        return ConfigUtils.systemOrEnv("mongodb.url", () -> "mongodb://admin:Passw0rd@kahana.mongohq.com:10092/web_cv");
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        MongoClientURI uri = new MongoClientURI(url());
        LOGGER.info("mongodb db {}", uri.getDatabase());
        LOGGER.info("mongodb hosts {}", uri.getHosts().toString());
        return new MongoClient(uri);
    }
}
