package se.webcv.db;

import java.util.List;

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
	  
	public CV findCV(String employeeID, String lang) {
		List<CV> cv = mongoTemplate.find(getQuery(employeeID, lang) , CV.class);
		if(cv == null || cv.isEmpty()){
			CV newcv = new CV();
			newcv.getDynamicSections().add(new DynamicSection(HeadLines.EDUCATION));
			newcv.getDynamicSections().add(new DynamicSection(HeadLines.COURSES));
			newcv.getDynamicSections().add(new DynamicSection(HeadLines.LECTIURES));
			newcv.getDynamicSections().add(new DynamicSection(HeadLines.OTHER));
			return newcv;
		}
    	return cv.get(0);
	}

	private Query getQuery(String employeeID, String lang) {
		if(employeeID == null || employeeID.isEmpty()){
			throw new IllegalArgumentException("Employee id can nto be null");
		}
		Query query = new Query();
		if(lang == null || lang.isEmpty()){
			query.addCriteria(Criteria.where("employeeId").is(employeeID));
		}else{
			query.addCriteria(Criteria.where("employeeId").is(employeeID).and("lang").is(lang));
		}
		return query;
	}
	
	public void saveCV(CV cv){
		CV found = mongoTemplate.findOne(getQuery(cv.getEmployeeId(), cv.getLang()) , CV.class);
		if(found != null){
			found.setEmployeeId(cv.getEmployeeId());
			found.setFramework(cv.getFramework());;
			found.setIntroduction(cv.getIntroduction());
			found.setLanguage(cv.getLanguage());;
			found.setMethod(cv.getMethod());
			found.setAssignments(cv.getAssignments());
			found.setDynamicSections(getSections(cv));
			found.setModified(LocalDate.now());
			mongoTemplate.save(found);
		}else{
			mongoTemplate.insert(cv);
		}
	}

	private List<DynamicSection> getSections(CV cv) {
		if(cv.getDynamicSections().isEmpty()){
			cv.getDynamicSections().add(new DynamicSection(HeadLines.EDUCATION));
			cv.getDynamicSections().add(new DynamicSection(HeadLines.COURSES));
			cv.getDynamicSections().add(new DynamicSection(HeadLines.LECTIURES));
			cv.getDynamicSections().add(new DynamicSection(HeadLines.OTHER));
		}
		return cv.getDynamicSections();
	}

	public List<CV> listCVs() {
		return mongoTemplate.findAll(CV.class, "cV");
	}
	
	public void backupCVList(){
		if(isCreateBackup()){
			mongoTemplate.dropCollection("cvbackup");
			mongoTemplate.createCollection("cvbackup");;
			for(CV cv : listCVs()){
				cv.setModified(LocalDate.now());
				mongoTemplate.save(cv, "cvbackup");
			}
		}
	}

	private boolean isCreateBackup() {
		List<CV> findAll = mongoTemplate.findAll(CV.class, "cvbackup");
		if(!findAll.isEmpty()){
			LocalDate modified = findAll.get(0).getModified();
			if(modified != null && !modified.isBefore(getLastSunday())){
				return false;
			}
		}
		return true;
	}

	private LocalDate getLastSunday() {
		return  LocalDate.now().withDayOfWeek(7).plusWeeks(-1);
	}

}
