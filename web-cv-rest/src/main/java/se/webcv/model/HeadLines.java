package se.webcv.model;

public enum HeadLines {

	EDUCATION("Utbildning","Education"),
	COURSES("Kurser", "Courses"),
	LECTIURES("Föreläsningar","Lectures"),
	OTHER("Övrigt","Other");
	
	private String se;
	private String en;
	
	HeadLines(String se, String en){
		this.se = se;
		this.en = en;
	}

	public String getSe() {
		return se;
	}

	public String getEn() {
		return en;
	}
	
}
