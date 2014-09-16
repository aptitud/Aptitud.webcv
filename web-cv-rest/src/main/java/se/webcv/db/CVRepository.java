package se.webcv.db;

import java.util.List;

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
	  
	public CV getCV(String employeeID) {
		List<CV> cv = mongoTemplate.find(getQuery(employeeID) , CV.class);
		if(cv == null || cv.isEmpty()){
			return new CV();
		}
    	return cv.get(0);
	}

	private Query getQuery(String employeeID) {
		if(employeeID == null || employeeID.isEmpty()){
			throw new IllegalArgumentException("Employee id can nto be null");
		}
		Query query = new Query();
		query.addCriteria(Criteria.where("employeeId").is(employeeID));
		return query;
	}
	
	public void saveCV(CV cv){
		CV found = mongoTemplate.findOne(getQuery(cv.getEmployeeId()) , CV.class);
		if(found != null){
			found.setEmployeeId(cv.getEmployeeId());
			found.setFramework(cv.getFramework());;
			found.setIntroduction(cv.getIntroduction());
			found.setLanguage(cv.getLanguage());;
			found.setMethod(cv.getMethod());
			found.setAssignments(cv.getAssignments());
			mongoTemplate.save(found);
		}else{
			mongoTemplate.insert(cv);
		}
	}

	public List<CV> ListCVs() {
		return mongoTemplate.findAll(CV.class);
	}

}
