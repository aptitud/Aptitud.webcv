package se.webcv.repository.mongo;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import se.webcv.model.Employee;
import se.webcv.model.EmployeeSearchResultDto;
import se.webcv.repository.EmployeeRepository;
import se.webcv.utils.ConfigUtils;

import java.util.List;

@Repository
@Profile("!in-mem")
public class MongoEmployeeRepository extends AbstractMongoRepository implements EmployeeRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    final int maxResults = Integer.parseInt(ConfigUtils.systemOrEnv("defaultMaxResult", "30"));

    public List<Employee> findActiveEmployees(String searchText) {
        Query query = withNameSortAndLimit(withActive(queryFor(searchText)));
        return mongoTemplate.find(query, Employee.class);
    }

    private Query withActive(Query query) {
        return query.addCriteria(Criteria.where("archivedAt").exists(false));
    }

    private Query withNameSortAndLimit(Query query) {
        return query.with(new Sort(Sort.Direction.ASC, "name"))
                .limit(maxResults);
    }

    private Query queryFor(String searchText) {
        return searchText != null ?
                Query
                        .query(Criteria.where("name")
                                .regex(toRegex(searchText), "i"))
                : new Query();
    }

    public List<EmployeeSearchResultDto> findActiveEmployeesNoImage(String searchText) {
        Query query = withNameSortAndLimit(withActive(queryFor(searchText)));
        query.fields().exclude("img");
        return mongoTemplate.find(query, EmployeeSearchResultDto.class);
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
        if (!StringUtils.isEmpty(employee.getId())) {
            Employee found = mongoTemplate.findById(employee.getId(), Employee.class);
            if (found != null) {
                found.setMail(employee.getMail());
                found.setName(employee.getName());
                found.setPhonenr(employee.getPhonenr());
                found.setRole(employee.getRole());
                found.setImg(employee.getImg());
                found.setArchivedAt(employee.getArchivedAt());
                mongoTemplate.save(found);
                return found;
            }
        }
        Employee newEmployee = new Employee(employee.getName());
        newEmployee.setImg(employee.getImg());
        newEmployee.setRole(employee.getRole());
        mongoTemplate.insert(newEmployee);
        return newEmployee;
    }

    public Employee findActiveEmployee(String employeeid) {
        return mongoTemplate.findOne(withActive(
                Query.query(Criteria.where("id").is(employeeid))), Employee.class);
    }
}
