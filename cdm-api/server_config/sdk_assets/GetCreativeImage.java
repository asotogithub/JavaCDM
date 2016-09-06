import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import trueffect.truconnect.api.tpasapi.client.factory.TpasapiServiceFactory;
import trueffect.truconnect.api.tpasapi.client.proxy.CreativeTpasapiServiceProxy;
import trueffect.truconnect.api.tpasapi.model.Creative;

public class GetCreativeImage {

	protected static final String URL = "https://truapi-alpha.trueffect.com/3pasapi/v1.0.1";
	protected static final String AUTH_URL = "https://truapi-alpha.trueffect.com/oauth/v1.0.1";
	protected static final String CONTENT_TYPE = "text/xml";

	private String password;
	private String username;
	private Long creativeId;

	public static void main(String[] args) throws Exception {

		GetCreativeImage app = new GetCreativeImage(args);

		app.go();

	}

	public GetCreativeImage(String[] args) {
		parseArgs(args);
	}

	public void go() throws Exception {

		uploadCreative();

	}

	private void parseArgs(String[] args) {

		for (int index = 0; index < args.length; index++) {
			if (args[index].equals("-u")) {
				this.username = args[++index];
			} else if (args[index].equals("-p")) {
				this.password = args[++index];
			} else if (args[index].equals("-id")) {
				this.creativeId = Long.parseLong(args[++index]);
			}
		}
	}

	private void uploadCreative() throws Exception {

		TpasapiServiceFactory factory = new TpasapiServiceFactory(URL,
				AUTH_URL, CONTENT_TYPE, this.username, this.password);

		CreativeTpasapiServiceProxy proxy = factory.getCreativeProxy();

		File image = proxy.getImage(creativeId);
		
		Creative creative = proxy.getById(creativeId.intValue());
		
		String name = creative.getFilename();

		Files.copy(image, "./"+name);

		System.out.println("done");
	}

}
