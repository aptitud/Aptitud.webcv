package se.webcv.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import se.webcv.db.EmployeeRepository;
import se.webcv.model.Employee;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@RequestMapping(produces = "application/json", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<Employee> getEmployees() {
        return employeeRepository.getEmployees();
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public void creatEmployee(@RequestBody Employee employee) {
    	employeeRepository.saveEmployee(employee);
    }
	
}
