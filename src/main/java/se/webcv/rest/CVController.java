package se.webcv.rest;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public CV findCV(@RequestParam(required = true) String employeeId, @RequestParam(required = false) String lang) {
        CV cv = cvRepository.findActiveCV(employeeId, lang);
        if (cv == null) {
            throw new ResourceNotFoundException();
        }
        return cv;
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public CV createCVIfNotFound(@RequestBody CV cv) {
        return cvRepository.saveCV(cv.createDynamicSectionsIfNotFound());
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public CV saveCV(@RequestBody CV cv) {
        return cvRepository.saveCV(cv);
    }

    @RequestMapping(value = "/{employeeId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCVsForEmployee(@PathVariable String employeeId) {
        Collection<CV> cvs = cvRepository.findActiveCVs(employeeId);
        if (cvs.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        for (CV cv : cvs) {
            cvRepository.saveCV(cv.archive(DateTime.now()));
        }
    }

    @RequestMapping(value = "/list", produces = "application/json", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<CV> findCVList() {
        //cvRepository.backupCVList();
        return cvRepository.listCVs();
    }

}
