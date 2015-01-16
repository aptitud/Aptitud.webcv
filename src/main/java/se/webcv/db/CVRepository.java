package se.webcv.db;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import se.webcv.model.CV;
import se.webcv.model.DynamicSection;
import se.webcv.model.HeadLines;

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

    private Query queryFor(String employeeID, String lang) {
        if (employeeID == null || employeeID.isEmpty()) {
            throw new IllegalArgumentException("Employee id can nto be null");
        }
        Query query = new Query();
        if (lang == null || lang.isEmpty()) {
            query.addCriteria(Criteria.where("employeeId").is(employeeID));
        } else {
            query.addCriteria(Criteria.where("employeeId").is(employeeID).and("lang").is(lang));
        }
        return query;
    }

    public CV saveCV(CV cv) {
        CV found = mongoTemplate.findOne(queryFor(cv.getEmployeeId(), cv.getLang()), CV.class);
        if (found != null) {
            found.setFramework(cv.getFramework());
            found.setIntroduction(cv.getIntroduction());
            found.setMethod(cv.getMethod());
            found.setAssignments(cv.getAssignments());
            found.setDynamicSections(cv.getDynamicSections());
            found.setModified(DateTime.now());
            found.setArchivedAt(cv.getArchivedAt());
            mongoTemplate.save(found);
            return found;
        } else {
            mongoTemplate.insert(cv);
            return cv;
        }
    }

    public List<CV> listCVs() {
        return mongoTemplate.findAll(CV.class, "cV");
    }

}
