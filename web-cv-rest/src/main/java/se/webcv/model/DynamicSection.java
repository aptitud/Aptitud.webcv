package se.webcv.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamicSection {

	private HeadLines headline;
	private String content;
	private boolean include;
	
	public DynamicSection(){
		//default
	}
	
	public String getHeadlineen(){
		return headline != null ? headline.getEn() : "";
	}
	
	public DynamicSection(HeadLines headline){
		this.headline = headline;
	}
	
	public HeadLines getHeadline() {
		return headline;
	}
	
	public void setHeadline(HeadLines headline) {
		this.headline = headline;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isInclude() {
		return include;
	}

	public void setInclude(boolean include) {
		this.include = include;
	}
}
