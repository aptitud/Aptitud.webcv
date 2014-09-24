package se.webcv.db;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import se.webcv.model.Employee;

@Repository
public class EmployeeRepository {

	  @Autowired
	  private MongoTemplate mongoTemplate;
	  
	  public List<Employee> getEmployees() {
        return mongoTemplate.findAll(Employee.class);
    }

    public void saveEmployee(Employee employee) {
    	if(StringUtils.isEmpty(employee.getName())){
    		throw new IllegalArgumentException("Employee name can not be null");
    	}
		Query query = new Query();
		query.addCriteria(Criteria.where("name").is(employee.getName()));
		Employee found = mongoTemplate.findOne(query , Employee.class);
		if(found != null){
			found.setMail(employee.getMail());
			found.setName(employee.getName());
			found.setPhonenr(employee.getPhonenr());
			found.setRole(employee.getRole());
			found.setImg(employee.getImg());
			mongoTemplate.save(found);
		}else{
			mongoTemplate.insert(employee);
		}
       
    }

	public Employee getEmployee(String employeeid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(employeeid));
		return mongoTemplate.findOne(query , Employee.class);
	}
}
