package agroPortalProfiling.util;

public class UriAndString {
	
	private String uri;
	private String str;
	
	public UriAndString() {
	}
	public UriAndString(String uri, String str) {
		this.uri = uri;
		this.str = str;
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

	// Méthode toString()
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(uri);
		sb.append(",");
		sb.append(str);
        sb.append("}");
        return sb.toString();
    }
}
