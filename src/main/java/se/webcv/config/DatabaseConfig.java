package se.webcv.config;


import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@Configuration
@EnableMongoRepositories
public class DatabaseConfig extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "web_cv";
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
    	MongoClientURI uri = new MongoClientURI("mongodb://admin:Passw0rd@kahana.mongohq.com:10092/web_cv");
		return new MongoClient(uri);
    }
}
