import trueffect.truconnect.api.tpasapi.client.factory.TpasapiServiceFactory;
import trueffect.truconnect.api.tpasapi.client.proxy.CreativeGroupTpasapiServiceProxy;
import trueffect.truconnect.api.tpasapi.model.CreativeGroup;

public class SampleGetCreativeGroup {

	protected static final String URL = "https://truapi-alpha.trueffect.com/3pasapi/v1.0.1";
	protected static final String AUTH_URL = "https://truapi-alpha.trueffect.com/oauth/v1.0.1";
	protected static final String CONTENT_TYPE = "text/xml";
	protected static final String USER_ID = "coredigital@trueffect.com";
	protected static final String PASSWORD = "trueffect";

	public static void main(String[] args) throws Exception {

		getCreativeGroup();
	}

	protected static void getCreativeGroup() throws Exception {
		TpasapiServiceFactory factory = new TpasapiServiceFactory(URL, AUTH_URL, CONTENT_TYPE, USER_ID, PASSWORD);

		CreativeGroupTpasapiServiceProxy proxy = factory.getCreativeGroupProxy();

		CreativeGroup group = proxy.getById(12798513);

		System.out.println("done: " + group);
	}

}
