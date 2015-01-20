package se.webcv.repository;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import se.webcv.model.CV;

public interface CVRepository {

    List<CV> findActiveCVs(String employeeId);

    CV findActiveCV(String employeeId, String lang);

    CV saveCV(CV cv);
}
