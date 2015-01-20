package se.webcv.repository.inmem;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import se.webcv.model.CV;
import se.webcv.model.Employee;
import se.webcv.model.EmployeeSearchResultDto;
import se.webcv.repository.EmployeeRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by marcus on 20/01/15.
 */
@Repository
@Profile("in-mem")
public class InMemoryEmployeeRepository extends AbstractInMemoryRepository<String, Employee> implements EmployeeRepository {

    @Override
    public List<EmployeeSearchResultDto> findActiveEmployeesNoImage(final String searchText) {
        final String s = searchText != null ? searchText.toUpperCase() : null;
        return Lists.newArrayList(Iterables.transform(Iterables.filter(findAll(), new Predicate<Employee>() {
            @Override
            public boolean apply(Employee input) {
                return s == null || input.getName().toUpperCase().contains(s)
                        && input.getArchivedAt() == null;
            }
        }), new Function<Employee, EmployeeSearchResultDto>() {
            @Override
            public EmployeeSearchResultDto apply(Employee input) {
                EmployeeSearchResultDto dto = new EmployeeSearchResultDto();
                dto.setId(input.getId());
                dto.setName(input.getName());
                dto.setRole(input.getRole());
                dto.setPhonenr(input.getPhonenr());
                dto.setMail(input.getMail());
                return dto;
            }
        }));
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        if (employee.getId() == null) {
            employee.setId(UUID.randomUUID().toString());
        }
        return save(employee.getId(), employee);
    }

    @Override
    public Employee findActiveEmployee(final String employeeId) {
        return Iterables.find(findAll(), new Predicate<Employee>() {
            @Override
            public boolean apply(Employee input) {
                return input.getId().equals(employeeId)
                        && input.getArchivedAt() == null;
            }
        }, null);
    }
}
