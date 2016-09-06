package trueffect.truconnect.api.publik.client.base;

public enum ContentType {

    XML ("text/xml"),
    JSON ("application/json");

    private final String type;

    ContentType(String type) {
	this.type = type;
    }

    public String getType() {
	return this.type;
    }
}
