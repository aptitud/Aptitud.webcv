package se.webcv.db;

import java.util.List;
import java.util.Optional;

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

    public List<Employee> getEmployees(Optional<String> searchText) {
        return searchText
                .map(s -> mongoTemplate.find(Query
                        .query(Criteria.where("name")
                                .regex(toRegex(s), "i")), Employee.class))
                .orElseGet(() -> mongoTemplate.findAll(Employee.class));
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
