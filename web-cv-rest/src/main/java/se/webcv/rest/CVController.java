package se.webcv.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import se.webcv.db.CVRepository;
import se.webcv.model.CV;

@Controller
@RequestMapping("/cv")
public class CVController {

	@Autowired
	private CVRepository cvRepository;
	
	@RequestMapping(produces = "application/json", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public CV getCV(@RequestParam(required = true) String employeeID, @RequestParam(required = false) String lang) {
        return cvRepository.getCV(employeeID, lang);
    }
	
	@RequestMapping(produces = "application/json", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public void saveCV(@RequestBody CV cv) {
         cvRepository.saveCV(cv);
    }
	
	@RequestMapping(value = "/list", produces = "application/json", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<CV> getCVList() {
		//cvRepository.backupCVList();
        return cvRepository.listCVs();
    }

}
