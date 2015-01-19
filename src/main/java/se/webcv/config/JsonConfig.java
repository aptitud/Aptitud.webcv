package se.webcv.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;

/**
 * Created by marcus on 19/01/15.
 */
@Configuration
public class JsonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperFactoryBean factoryBean = new Jackson2ObjectMapperFactoryBean();
        factoryBean.setModulesToInstall(JodaModule.class);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

}
