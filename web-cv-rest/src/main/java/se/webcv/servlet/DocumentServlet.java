package se.webcv.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import se.webcv.db.CVRepository;
import se.webcv.db.EmployeeRepository;
import se.webcv.model.CV;
import se.webcv.model.Employee;
import se.webcv.utils.DocumentGenerator;


public class DocumentServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private CVRepository cvRepository; 
	
	@Autowired
	private EmployeeRepository employeeRepository;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	    SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
	      config.getServletContext());
	}

	@Override
    protected void doGet(final HttpServletRequest request,
        final HttpServletResponse response) throws ServletException,
        IOException {
  		String employeeid = request.getParameter("employeeid");
  		String gentype = request.getParameter("gentype");
  		String lang = request.getParameter("lang");
  		CV cv = cvRepository.getCV(employeeid, lang);
  		Employee employee = employeeRepository.getEmployee(employeeid);
  		if(generatePdf(gentype)){
  			processRequestPDF(employee, cv, request, response);
  		}else{
  			processRequestMS(employee, cv, request, response);
  		}
    }
	
	private boolean generatePdf(String gentype) {
		return "pdf".equalsIgnoreCase(gentype);
	}

	public void processRequestPDF(Employee employee, CV cv, final HttpServletRequest request,
        final HttpServletResponse response) throws ServletException, IOException {
//		TODO impl
//    	  byte[] pdf = generator.generatePDF();
//        if (pdf != null) {
//            response.setContentType("application/pdf");
//            response.addHeader("Content-Disposition", "attachment; filename=test.pdf");
//            response.setContentLength(pdf.length);
//            ServletOutputStream outputStream = response.getOutputStream();
//            outputStream.write(pdf);
//            
//        }
    }
	
	public void processRequestMS(Employee employee, CV cv,  final HttpServletRequest request,
		final HttpServletResponse response) throws ServletException, IOException {
    	 DocumentGenerator generator = new DocumentGenerator(employee, cv);
    	  byte[] doc = generator.generateMSdoc();
        if (doc != null) {
            response.setContentType("application/docx");
            response.addHeader("Content-Disposition", "attachment; filename=cv.docx");
            response.setContentLength(doc.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(doc);
        }
    }
}
