package se.webcv.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.model.fields.merge.MailMerger;
import org.docx4j.model.fields.merge.MailMerger.OutputField;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import se.webcv.model.Assignment;
import se.webcv.model.CV;
import se.webcv.model.Employee;

public class DocumentGenerator {
		
	private static final String MSMALL_DOCX = "msmall.docx";
	private Employee employee;
	private CV cv;
	
	public DocumentGenerator(Employee employee, CV cv) {
		this.employee = employee;
		this.cv = cv;
	}

	
	public byte[] generateMSdoc(){
		try {
			WordprocessingMLPackage template = getTemplate();
			merge(template);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			template.save(out);
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] generatePDF(){
		try {
			WordprocessingMLPackage template = getTemplate();
			merge(template);		
			FOSettings fos = Docx4J.createFOSettings();
			fos.setWmlPackage(template);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Docx4J.toFO(fos, out, Docx4J.FLAG_NONE);
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private WordprocessingMLPackage getTemplate() throws Docx4JException, FileNotFoundException {
		URL resource = getClass().getClassLoader().getResource(MSMALL_DOCX);
		WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(resource.getFile()));
		return template;
	}

	
	private void merge(WordprocessingMLPackage wordMLPackage) throws Docx4JException{
	    Map<DataFieldName, String> docMapping = new HashMap<DataFieldName, String>();     
	    docMapping.put(new DataFieldName(Tag.employeename.name()), employee.getName());
		docMapping.put(new DataFieldName(Tag.employeerole.name()), employee.getRole());
		docMapping.put(new DataFieldName(Tag.frameworks.name()), cv.getFramework());
		docMapping.put(new DataFieldName(Tag.intortext.name()), cv.getIntroduction());
		docMapping.put(new DataFieldName(Tag.languages.name()), cv.getLanguage());
		docMapping.put(new DataFieldName(Tag.methods.name()), cv.getMethod());
	    int count = 1;
	    for(Assignment assignment : cv.getAssignments()){
	    	docMapping.put(new DataFieldName(Tag.assignemntdescription.name()+count), assignment.getDescription());
	    	docMapping.put(new DataFieldName(Tag.assignemntrole.name()+count), assignment.getRole());
	    	docMapping.put(new DataFieldName(Tag.assignmentcustomer.name()+count), assignment.getCustomer());
	    	docMapping.put(new DataFieldName(Tag.assignmenttechniques.name()+count), assignment.getTechniques());
	    	docMapping.put(new DataFieldName(Tag.assignemntdate.name()+count), assignment.getDate());
	    	docMapping.put(new DataFieldName(Tag.assignmentheader.name()+count), "Uppdragshistorik");
	    	docMapping.put(new DataFieldName(Tag.assignmenttechniquesheader.name()+count), "Tekniker: ");
	    	docMapping.put(new DataFieldName(Tag.assignmentroleheader.name()+count), "Roller: ");
	    	count++;
	    } 
	    
	    MailMerger.setMERGEFIELDInOutput(OutputField.REMOVED);
	    MailMerger.performMerge(wordMLPackage, docMapping, true);   
	}
		
	private enum Tag {
		employeename,
		employeerole,
		intortext,
		languages,
		frameworks,
		methods,
		assignmentcustomer,
		assignemntdate,
		assignemntdescription,
		assignemntrole,
		assignmenttechniques,
		assignmenttechniquesheader,
		assignmentroleheader,
		assignmentheader;
	}
	
}
