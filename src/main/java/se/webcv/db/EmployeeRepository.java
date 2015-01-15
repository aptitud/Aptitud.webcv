package se.webcv.db;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import se.webcv.model.Employee;
import se.webcv.utils.ConfigUtils;

import java.util.List;

@Repository
public class EmployeeRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    final int maxResults = Integer.parseInt(ConfigUtils.systemOrEnv("defaultMaxResult", "30"));

    public List<Employee> getEmployees(String searchText) {
        Query query = searchText != null ?
                Query
                        .query(Criteria.where("name")
                                .regex(toRegex(searchText), "i"))
                : new Query();
        return mongoTemplate.find(query
                .with(new Sort(Sort.Direction.ASC, "name"))
                .limit(maxResults), Employee.class);
    }

    private String toRegex(String searchText) {
        return ".*" + escape(searchText) + ".*";
    }

    private String escape(String searchText) {
        return searchText
                .replace(".", "\\.")
                .replace("*", "\\*")
                .replace("+", "\\+")
                .replace("?", "\\?")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("^", "\\^")
                .replace("$", "\\$")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("{", "\\{")
                .replace("}", "\\}");
    }

    public Employee saveEmployee(Employee employee) {
        if (StringUtils.isEmpty(employee.getName())) {
            throw new IllegalArgumentException("Employee name can not be null");
        }
        Employee found = null;
        if (!StringUtils.isEmpty(employee.getId())) {
            found = mongoTemplate.findById(employee.getId(), Employee.class);
        }
        if (found != null) {
            found.setMail(employee.getMail());
            found.setName(employee.getName());
            found.setPhonenr(employee.getPhonenr());
            found.setRole(employee.getRole());
            found.setImg(employee.getImg());
            mongoTemplate.save(found);
            return found;
        } else {
            Employee newEmployee = new Employee();
            newEmployee.setImg(employee.getImg());
            newEmployee.setName(employee.getName());
            newEmployee.setRole(employee.getRole());
            mongoTemplate.insert(newEmployee);
            return newEmployee;
        }

    }

    public Employee getEmployee(String employeeid) {
        return mongoTemplate.findById(employeeid, Employee.class);
    }
}
