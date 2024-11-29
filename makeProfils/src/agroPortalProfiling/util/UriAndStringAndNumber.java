package agroPortalProfiling.util;

public class UriAndStringAndNumber {
	
	private String uri;
	private String str;
	private Integer number;
	
	
	public UriAndStringAndNumber() {
	}
	public UriAndStringAndNumber(String uri, String str, Integer number) {
		this.uri = uri;
		this.str = str;
		this.number = number;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
}
