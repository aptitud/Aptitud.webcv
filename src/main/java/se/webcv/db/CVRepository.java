package se.webcv.db;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import se.webcv.model.CV;

@Repository
public class CVRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    private Query withActive(Query query) {
        return query.addCriteria(Criteria.where("archivedAt").exists(false));
    }

    public List<CV> findActiveCVs(String employeeID) {
        return mongoTemplate.find(withActive(queryFor(employeeID, null)), CV.class);
    }

    public CV findActiveCV(String employeeID, String lang) {
        Validate.notEmpty(lang, "lang must be set");
        return mongoTemplate.findOne(withActive(queryFor(employeeID, lang)), CV.class);
    }

    public CV findCV(String employeeID, String lang) {
        Validate.notEmpty(lang, "lang must be set");
        return mongoTemplate.findOne(queryFor(employeeID, lang), CV.class);
    }

    private Query queryFor(String employeeId, String lang) {
        Validate.notEmpty(employeeId, "Employee id can not be null");
        Query query = new Query();
        if (lang == null || lang.isEmpty()) {
            query.addCriteria(Criteria.where("employeeId").is(employeeId));
        } else {
            query.addCriteria(Criteria.where("employeeId").is(employeeId).and("lang").is(lang));
        }
        return query;
    }

    public CV saveCV(CV cv) {
        CV found = findExisting(cv);
        if (found != null) {
            found.setLanguage(cv.getLanguage());
            found.setFramework(cv.getFramework());
            found.setIntroduction(cv.getIntroduction());
            found.setMethod(cv.getMethod());
            found.setAssignments(cv.getAssignments());
            found.setDynamicSections(cv.getDynamicSections());
            found.setModified(DateTime.now());
            found.setArchivedAt(cv.getArchivedAt());
            mongoTemplate.save(found);
            return found;
        }
        mongoTemplate.save(cv);
        return cv;
    }

    private CV findExisting(CV cv) {
        CV found = null;
        if (cv.getId() != null) {
            found = mongoTemplate.findById(cv.getId(), CV.class);
        }
        if (found == null) {
            found = mongoTemplate.findOne(queryFor(cv.getEmployeeId(), cv.getLang()), CV.class);
        }
        return found;
    }
}
