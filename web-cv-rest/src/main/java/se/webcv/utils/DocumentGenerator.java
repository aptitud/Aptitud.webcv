package se.webcv.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.model.fields.merge.MailMerger;
import org.docx4j.model.fields.merge.MailMerger.OutputField;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;

import se.webcv.model.Assignment;
import se.webcv.model.CV;
import se.webcv.model.DynamicSection;
import se.webcv.model.Employee;

public class DocumentGenerator {
		
	private static final String PARAGRAPH_XPATH = "//w:p";
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
			populatePlaceholders(template);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			template.save(out);
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void populatePlaceholders(WordprocessingMLPackage template)throws Exception{
		populateIntroText(template);
		MainDocumentPart mainDocument = template.getMainDocumentPart();
		P emptyParagraph = getLastParagraph(template);
		Tbl tableFromTemplate = getAssignemnTable(template);
		Object tableTemplate = XmlUtils.deepCopy(tableFromTemplate);
		populateAssignments(mainDocument, emptyParagraph, tableFromTemplate, tableTemplate);	
		populateDynamicSections(mainDocument, emptyParagraph, tableTemplate);
	}


	private void populateDynamicSections(MainDocumentPart mainDocument, P emptyParagraph, Object tableTemplate) {
		// The base of the dynamic sections are the same as assignments so we will reuse the same template
		Tbl dynamicSectionTable = modifyTableForDynamicSection(tableTemplate);
		
		for(DynamicSection ds : cv.getDynamicSections()){
			if(ds.isInclude()){
				Object tableToUse = XmlUtils.deepCopy(dynamicSectionTable);
				List<Object> paragraphs = getAllElementFromObject(tableToUse, P.class);
				replacePlaceHolder("customer", paragraphs, ds.getHeadline().getSe());
				replacePlaceHolder("date", paragraphs, "");
				replacePlaceHolder("assignemnttext", paragraphs, StringUtils.splitPreserveAllTokens(ds.getContent(), '\n'));
				mainDocument.getContent().add(tableToUse);
				Object space = XmlUtils.deepCopy(emptyParagraph);
				mainDocument.getContent().add(space);
			}
		}
	}


	private void populateAssignments(MainDocumentPart mainDocument, P emptyParagraph, Tbl tableFromTemplate, Object tableTemplate) {
		List<Assignment> assignments = getAssignemntsToInclude(cv.getAssignments());	
		//Using iterator to populate the existing table in the template with the first assignment
		Iterator<Assignment> itr = assignments.iterator();
		if(itr.hasNext()){
			populateAssignmentTable(tableFromTemplate, itr.next());
		}
		//Use the copy of the existing table in the template to add more assignments
		while(itr.hasNext()){
			Object tableToUse = XmlUtils.deepCopy(tableTemplate);
			Assignment a = itr.next();
			modifyTableLayout(tableToUse, a);
			tableToUse  = populateAssignmentTable(tableToUse, a);
			mainDocument.getContent().add(tableToUse);
			Object space = XmlUtils.deepCopy(emptyParagraph);
			mainDocument.getContent().add(space);
		}
	}

	private void modifyTableLayout(Object tableCopy, Assignment a){
		List<Object> rows = getAllElementFromObject(tableCopy, Tr.class);
		List<Tr> rowsToRemove = new ArrayList<Tr>();
		if(StringUtils.isEmpty(a.getDescription())){
			rowsToRemove.add((Tr)rows.get(1));
		}
		if(StringUtils.isEmpty(a.getRole())){
			rowsToRemove.add((Tr)rows.get(2));
		}
		if(StringUtils.isEmpty(a.getTechniques())){
			rowsToRemove.add((Tr)rows.get(3));
		}
		for(Tr row : rowsToRemove){
			((ContentAccessor) row.getParent()).getContent().remove(row); 
		}
	}

	private void populateIntroText(WordprocessingMLPackage template) throws JAXBException,
			XPathBinderAssociationIsPartialException {
		//We will have to fetch all paragraphs with xpath due to the introduction text is within an image. 
		List<Object> allParagraphsInDocument = template.getMainDocumentPart().getJAXBNodesViaXPath(PARAGRAPH_XPATH, true);
		replacePlaceHolder("infotext", allParagraphsInDocument, StringUtils.splitPreserveAllTokens(cv.getIntroduction(), '\n'));
	}
	
	private Tbl modifyTableForDynamicSection(Object table){
		Tbl copy = (Tbl) XmlUtils.deepCopy(table);
		List<Object> allElementFromObject = getAllElementFromObject(copy, Tr.class);
		int lastIndex = allElementFromObject.size() -1;
		int seconedLastIndex = lastIndex -1;
		//Remove the two last rows
		if(seconedLastIndex > 0){
			Tr lastrow = (Tr)allElementFromObject.get(lastIndex);
			((ContentAccessor) lastrow.getParent()).getContent().remove(lastrow); 
			Tr seconedLastRow = (Tr)allElementFromObject.get(seconedLastIndex);
			((ContentAccessor) seconedLastRow.getParent()).getContent().remove(seconedLastRow);
		}
		return copy;
	}
	
	private List<Assignment> getAssignemntsToInclude(List<Assignment> assignments) {
		List<Assignment> result = new ArrayList<Assignment>();
		for(Assignment a : assignments){
			if(a.isInclude()){
				result.add(a);
			}
		}
		return result;
	}


	private P getLastParagraph(WordprocessingMLPackage template){
		List<Object> paragraphs = getAllElementFromObject(template.getMainDocumentPart(), P.class);
		if(paragraphs.isEmpty()){
			throw new IllegalStateException("Template can not be empty");
		}
		P p = (P)paragraphs.get(paragraphs.size()-1);
		return p;
	}
	
	private Object populateAssignmentTable(Object table, Assignment a){
		List<Object> paragraphs = getAllElementFromObject(table, P.class);
		replacePlaceHolder("customer", paragraphs,a.getCustomer());
		replacePlaceHolder("date", paragraphs, a.getDate());
		replacePlaceHolder("assignemnttext", paragraphs, StringUtils.splitPreserveAllTokens(a.getDescription(), '\n'));
		replacePlaceHolder("assignmentrole", paragraphs, a.getRole());
		replacePlaceHolder("assignmenttek", paragraphs, a.getTechniques());
		return table;
	}

	private void replacePlaceHolder(String placeholder, List<Object> paragraphs, String... insert) {
		P toReplace = getPlaceHolder(paragraphs, placeholder);
		if(toReplace != null && insert != null && insert.length != 0){
			for (String ptext : insert) {
				P copy = (P) XmlUtils.deepCopy(toReplace);
				List<?> texts = getAllElementFromObject(copy, Text.class);
				if (texts.size() > 0) {
					Text textToReplace = (Text) texts.get(0);
					textToReplace.setValue(ptext);
				}
				((ContentAccessor) toReplace.getParent()).getContent().add(copy);
			}
			((ContentAccessor)toReplace.getParent()).getContent().remove(toReplace);
		}
	}
	
	private Tbl getAssignemnTable( WordprocessingMLPackage template)throws Exception{
		List<Object> allElementFromObject = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);
		if(!allElementFromObject.isEmpty()){
			//Should only be one table in the document
			return (Tbl)allElementFromObject.get(0);
		}
		return null;
	}

	private P getPlaceHolder(final List<Object> paragraphs, String placeholder) {
		P toReplace = null;
		for (Object p : paragraphs) {
			List<Object> texts = getAllElementFromObject(p, Text.class);
			for (Object t : texts) {
				Text content = (Text) t;
				if (content.getValue().equals(placeholder)) {
					toReplace = (P) p;
					break;
				}
			}
		}
		return toReplace;
	}
	
	private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
		List<Object> result = new ArrayList<Object>();
		if (obj instanceof JAXBElement) {
			obj = ((JAXBElement<?>) obj).getValue();
		}	
		if (obj.getClass().equals(toSearch)){
			result.add(obj);
		}
		else if (obj instanceof ContentAccessor) {
			List<?> children = ((ContentAccessor) obj).getContent();
			for (Object child : children) {
				result.addAll(getAllElementFromObject(child, toSearch));
			}
 
		}
		return result;
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
		docMapping.put(new DataFieldName(Tag.assignmentheader.name()), "Uppdragshistorik");
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
