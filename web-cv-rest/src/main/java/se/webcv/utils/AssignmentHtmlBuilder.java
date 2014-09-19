package se.webcv.utils;

import java.util.Collection;

import se.webcv.model.Assignment;

public class AssignmentHtmlBuilder {

	private String html = "";
	private static final String start = "<html><head><title></title></head><body>";
	private static final String end = "</body></html>";
	
	
	public void add(Assignment assignment){
		if(assignment.isInclude()){	
			html = html + assignemntHeader(assignment) + assignemntBody(assignment);
		}
	}
	
	public void addAll(Collection<Assignment> assignments){
		 for(Assignment assignment : assignments){
			 add(assignment);
		 }
	}
	
	public String build(){
		return start + html + end;
	}
	
	
	private static String assignemntBody(Assignment assignment){
		StringBuilder sb = new StringBuilder();
		sb.append("<table style=\"width: 100%; font-size: 11pt; font-family: calibri;\">");
		sb.append("<tr><td>");
		sb.append(getDescription(assignment.getDescription()));
		sb.append("<p><span style=\"font-weight: bold;\">Roller: </span>");
		sb.append(assignment.getRole());
		sb.append("</p>");
		sb.append("<p><span style=\"font-weight: bold;\">Tekniker: </span>");
		sb.append(assignment.getTechniques());
		sb.append("</p>");
		sb.append("</td></tr></table>");
		return sb.toString();
	}
	
	private static String getDescription(String description) {
		String[] split = description.split("\n");
		StringBuilder sb = new StringBuilder();
		for(String s : split){
			sb.append("<p>");
			sb.append(s);
			sb.append("</p>");
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
