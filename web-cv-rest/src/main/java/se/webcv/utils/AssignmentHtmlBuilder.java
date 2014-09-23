package se.webcv.utils;

import java.util.Collection;

import se.webcv.model.Assignment;
import se.webcv.model.DynamicSection;

public class AssignmentHtmlBuilder {

	private String html = "";
	private static final String start = "<html><head><title></title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" /></head><body>";
	private static final String end = "</body></html>";
	
	
	public void add(Assignment assignment){
		if(assignment.isInclude()){	
			html = html + assignemntHeader(assignment) + assignemntBody(assignment);
		}
	}
	
	public void add(DynamicSection sections){
		if(sections.isInclude()){
			html = html + dynamicSecionHeader(sections) + dynamicSecionBody(sections);
		}
	}
	
	private static String dynamicSecionHeader(DynamicSection section){
		StringBuilder sb = new StringBuilder();
		sb.append("<table style=\"width: 100%; font-size: 14pt; font-weight: bold; font-family: calibri;\">");
		sb.append("<tr><td>");
		sb.append(section.getHeadlineen());
		sb.append("</td></tr></table>");
		return sb.toString();
	}
	
	private String dynamicSecionBody(DynamicSection sections){
		StringBuilder sb = new StringBuilder();
		sb.append("<table style=\"width: 100%; font-size: 11pt; font-family: calibri; \">");
		sb.append("<tr><td>");
		sb.append(getDescription(sections.getContent()));
		sb.append("</td></tr></table><br/>");
		return sb.toString();
	}
	
	public void addAllAssignments(Collection<Assignment> assignments){
		 for(Assignment assignment : assignments){
			 add(assignment);
		 }
	}
	
	public void addAllSections(Collection<DynamicSection> sections){
		 for(DynamicSection section : sections){
			 add(section);
		 }
	}
	
	public String build(){
		return start + html + end;
	}
	
	
	private static String assignemntBody(Assignment assignment){
		StringBuilder sb = new StringBuilder();
		sb.append("<table style=\"width: 100%; font-size: 11pt; font-family: calibri; \">");
		sb.append("<tr><td>");
		sb.append(getDescription(assignment.getDescription()));
		sb.append("<p><span style=\"font-weight: bold;\">Roller: </span>");
		sb.append(assignment.getRole());
		sb.append("</p>");
		sb.append("<p><span style=\"font-weight: bold;\">Tekniker: </span>");
		sb.append(assignment.getTechniques());
		sb.append("</p>");
		sb.append("</td></tr></table><br/>");
		return sb.toString();
	}
	
	private static String getDescription(String description) {
		StringBuilder sb = new StringBuilder();
		if(description != null){
			String[] split = description.split("\n");
			for(String s : split){
				sb.append("<p>");
				sb.append(s);
				sb.append("</p>");
			}
		}
		return sb.toString();
	}

	private static String assignemntHeader(Assignment assignment){
		StringBuilder sb = new StringBuilder();
		sb.append("<table style=\"width: 100%; font-size: 14pt; font-weight: bold; font-family: calibri;\">");
		sb.append("<tr><td>");
		sb.append(assignment.getCustomer());
		sb.append("</td><td></td><td style=\"text-align: right;\">");
		sb.append(assignment.getDate());
		sb.append("</td></tr></table>");
		return sb.toString();
	}
}
