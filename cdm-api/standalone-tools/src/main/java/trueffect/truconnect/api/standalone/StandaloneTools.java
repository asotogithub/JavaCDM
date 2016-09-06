package trueffect.truconnect.api.standalone;

import java.util.Scanner;

import trueffect.truconnect.api.standalone.tools.Exporter;
import trueffect.truconnect.api.standalone.tools.Importer;

public class StandaloneTools {

    private Scanner scanner;

    public StandaloneTools() {
        this.scanner = new Scanner(System.in);
    }

    public void run(){
        System.out.println("Welcome to Trueffect Standalone Tools v1.0");
        System.out.println("Select one option:");
        System.out.println("1. Export Campaign");
        System.out.println("2. Import Campaign");
        int option = Integer.valueOf(scanner.nextLine());
        switch (option) {
        case 1:
            callExporter();
            break;
        case 2:
            callImporter();
            break;
        }
    }

    private void callExporter() {
        System.out.println("Introduce the baseURL of the services:");
        String baseUrl = scanner.nextLine();
        System.out.println("Introduce the version of the services:");
        String version = scanner.nextLine();
        System.out.println("Introduce your credentials");
        System.out.println("Username:");
        String username = scanner.nextLine();
        System.out.println("Password:");
        String password = scanner.nextLine();

        System.out.println("Introduce the path to Output Folder:");
        String pathToOuputFolder = scanner.nextLine();

        Long campaignId = null;
        do {
            System.out.println("Introduce Campaign's Id to Export:");
            campaignId = readId("Wrong Campaign Id value.");
        } while(campaignId == null);

        Exporter exporter = new Exporter(baseUrl, version, username, password, pathToOuputFolder, campaignId);
        try {
            exporter.export();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Long readId(String errorMessage) {
        Long res = null;
        try {
            res = Long.valueOf(scanner.nextLine());
        } catch (Exception e) {
            System.out.println(errorMessage);
        }
        return res;
    }

    private void callImporter() {
        System.out.println("Introduce the baseURL of the services:");
        String baseUrl = scanner.nextLine();
        System.out.println("Introduce the version of the services:");
        String version = scanner.nextLine();
        System.out.println("Introduce your credentials");
        System.out.println("Username:");
        String username = scanner.nextLine();
        System.out.println("Password:");
        String password = scanner.nextLine();

        Long agencyId = null;
        do {
            System.out.println("Introduce Agency's Id tied to the username:");
            agencyId = readId("Wrong Agency Id value.");
        } while(agencyId == null);

        System.out.println("Introduce the base path to the ZIP file:");
        String basePathToZipFile = scanner.nextLine();

        System.out.println("Introduce the name of the ZIP file:");
        String zipFileName = scanner.nextLine();

        Importer importer = new Importer(baseUrl, version, agencyId, username, password, basePathToZipFile, zipFileName);
        importer.importCampaign();
    }
}
