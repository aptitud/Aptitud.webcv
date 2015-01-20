package se.webcv.repository;

import se.webcv.model.Employee;
import se.webcv.model.EmployeeSearchResultDto;

import java.util.List;

public interface EmployeeRepository {

    List<EmployeeSearchResultDto> findActiveEmployeesNoImage(String searchText);

    Employee saveEmployee(Employee employee);

    Employee findActiveEmployee(String employeeId);
}
