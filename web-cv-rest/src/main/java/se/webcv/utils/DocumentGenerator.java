package se.webcv.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.jaxb.Context;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.model.fields.merge.MailMerger;
import org.docx4j.model.fields.merge.MailMerger.OutputField;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;
import org.docx4j.wml.Text;

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
			replaceParagraph("infotext", cv.getIntroduction(), template, template.getMainDocumentPart());
			merge(template);
			addhtml(template);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			template.save(out);
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void replaceParagraph(String placeholder, String textToAdd, WordprocessingMLPackage template, ContentAccessor addTo) throws Exception, JAXBException {
		// 1. get the paragraph
		final String XPATH_TO_SELECT_TEXT_NODES = "//w:p";
		final List<Object> paragraphs = template.getMainDocumentPart().getJAXBNodesViaXPath(XPATH_TO_SELECT_TEXT_NODES, true);
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
		// we now have the paragraph that contains our placeholder: toReplace
		// 2. split into seperate lines
		String as[] = StringUtils.splitPreserveAllTokens(textToAdd, '\n');
		for (String ptext : as) {
			// copy the found paragraph to keep styling correct
			P copy = (P) XmlUtils.deepCopy(toReplace);
			// replace the text elements from the copy
			List<?> texts = getAllElementFromObject(copy, Text.class);
			if (texts.size() > 0) {
				Text textToReplace = (Text) texts.get(0);
				textToReplace.setValue(ptext);
			}
			// add the paragraph to the parent
			((ContentAccessor) toReplace.getParent()).getContent().add(copy);
		}
		// remove the original one
		((ContentAccessor)toReplace.getParent()).getContent().remove(toReplace);
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


	private void addhtml(WordprocessingMLPackage template) throws InvalidFormatException {
		AssignmentHtmlBuilder ahb = new AssignmentHtmlBuilder();
		ahb.addAllAssignments(cv.getAssignments());
		ahb.addAllSections(cv.getDynamicSections());
		AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/hw.html"));
		afiPart.setBinaryData(ahb.build().getBytes());
		afiPart.setContentType(new ContentType("text/html"));
		Relationship altChunkRel = template.getMainDocumentPart().addTargetPart(afiPart);
		CTAltChunk ac = Context.getWmlObjectFactory().createCTAltChunk();
		ac.setId(altChunkRel.getId() );
		template.getMainDocumentPart().addObject(ac);
		template.getContentTypeManager().addDefaultContentType("html", "text/html");
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
