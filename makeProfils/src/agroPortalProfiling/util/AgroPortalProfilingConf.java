package agroPortalProfiling.util;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.jena.iri.impl.Main;
import org.apache.log4j.PropertyConfigurator;

public final class AgroPortalProfilingConf { 
	
	private static Properties prop = null;
	private static String filename = "C:\\Users\\conde\\Documents\\GitHub\\Profiling for AgroPortal\\config\\agroPortalProfiling.properties";
	
	public static String preferredLanguage = null;
	public static String preferredLanguages = null;
	public static String labelProperties = null;
	public static String[] listPreferredLanguages = null;
	public static String[] listLabelProperties = null;
	
	public static String dskForWindows = null;
	public static String mainfolder = null;
	public static String mainFolderAgroPortalProfiling = null;
	
	public static String fileNameListDatasetsForCuration = null;
	public static String fileNameListDatasets = null;
	public static String fileNameParameters = null;

	
	public static String folderForDatasets = null;
	public static String folderForResults = null;
	public static String folderForTmp = null;
	
	
	//query prefix
	public static String dsp = null;
	public static String skos = null;
	public static String skosXL = null;
	public static String vs = null;
	public static String dc = null;
	public static String foaf = null;
	public static String xsd = null;
	public static String owl = null;
	public static String dcterms = null;
	public static String rdfs = null;
	public static String rdf = null;
	public static String geo = null;
	public static String geof = null;
	public static String sf = null;
	public static String sosa = null;
	public static String ssn = null;
	public static String bfo = null;
	public static String om = null;
	public static String wpo = null;
	public static String ncbi = null;
	public static String time = null;
	public static String spatialF = null;
	public static String spatial = null;
	public static String wgs = null;
	public static String sio = null;
	public static String uom = null;
	public static String vid = null;
	
	public static String queryPrefix = null;
	
	/**
	 * Constructor
	*/
	public AgroPortalProfilingConf() {
		
		// Charger la configuration Log4j
        ClassLoader classLoader = Main.class.getClassLoader();
        if (classLoader.getResource("properties") != null) {
            PropertyConfigurator.configure(classLoader.getResource("properties"));
        } else {
            System.err.println("properties file not found in classpath.");
        }
		Properties prop = new Properties();
		InputStream input = null;
		// InputStream in = null;
		try {
			//input = getClass().getClassLoader().getResourceAsStream(AgroPortalProfilingConf.filename);
			input = new FileInputStream(AgroPortalProfilingConf.filename);
			//System.out.println("input : " + input);
			
			prop.load(input);
			
			preferredLanguage = prop.getProperty("preferredLanguage");
			preferredLanguages = prop.getProperty("preferredLanguages");
			labelProperties = prop.getProperty("labelProperties");
			listPreferredLanguages =  preferredLanguages.split(",", 0);
			listLabelProperties = labelProperties.split(",", 0);
			
			Path pathRoot = Paths.get("/");
			Path pathC = Paths.get("C:/");
			if (pathRoot.toAbsolutePath().startsWith(pathC)) {
				mainfolder = prop.getProperty("dskForWindows") + prop.getProperty("mainfolder");
				Path pathmainfolder = Paths.get(mainfolder).toAbsolutePath().normalize();
				mainfolder = pathmainfolder.toString();
			}
			else {
				mainfolder = prop.getProperty("mainfolder");
				Path pathmainfolder = Paths.get(mainfolder).toAbsolutePath().normalize();
				mainfolder = pathmainfolder.toString();
			}
				
			if (pathRoot.toAbsolutePath().startsWith(pathC)) {
				mainFolderAgroPortalProfiling = prop.getProperty("dskForWindows") + prop.getProperty("mainFolderAgroPortalProfiling");
				Path pathmainfolder = Paths.get(mainFolderAgroPortalProfiling).toAbsolutePath().normalize();
				mainFolderAgroPortalProfiling = pathmainfolder.toString();
			}
			else {
				mainFolderAgroPortalProfiling = prop.getProperty("mainFolderAgroPortalProfiling");
				Path pathmainfolder = Paths.get(mainFolderAgroPortalProfiling).toAbsolutePath().normalize();
				mainFolderAgroPortalProfiling = pathmainfolder.toString();
			}
			
			fileNameListDatasetsForCuration = prop.getProperty("fileNameListDatasetsForCuration");	
			fileNameListDatasets = prop.getProperty("fileNameListDatasets");
			fileNameParameters = prop.getProperty("fileNameParameters");
			
			
			folderForDatasets = prop.getProperty("folderForDatasets");
			Path pathfolderForDatasets = Paths.get(mainFolderAgroPortalProfiling + folderForDatasets);
			folderForDatasets = pathfolderForDatasets.toString();
			
			folderForResults = prop.getProperty("folderForResults");
			Path pathfolderForResults = Paths.get(mainFolderAgroPortalProfiling + folderForResults);
			folderForResults = pathfolderForResults.toString();

			folderForTmp = prop.getProperty("folderForTmp");
			Path pathfolderForTmp = Paths.get(mainFolderAgroPortalProfiling + folderForTmp);
			folderForTmp = pathfolderForTmp.toString();
			
			dsp = prop.getProperty("dsp");
			skos = prop.getProperty("skos");
			skosXL = prop.getProperty("skosXL");
			vs = prop.getProperty("vs");
			dc = prop.getProperty("dc");
			foaf = prop.getProperty("foaf");
			xsd = prop.getProperty("xsd");
			owl = prop.getProperty("owl");
			dcterms = prop.getProperty("dcterms");
			rdfs = prop.getProperty("rdfs");
			rdf = prop.getProperty("rdf");
			geo = prop.getProperty("geo");
			geof = prop.getProperty("geof");
			sf = prop.getProperty("sf");
			sosa = prop.getProperty("sosa");
			ssn = prop.getProperty("ssn");
			bfo = prop.getProperty("bfo");
			om = prop.getProperty("om");
			wpo = prop.getProperty("wpo");
			ncbi = prop.getProperty("ncbi");
			time = prop.getProperty("time");
			spatialF = prop.getProperty("spatialF");
			spatial = prop.getProperty("spatial");
			wgs = prop.getProperty("wgs");
			sio = prop.getProperty("sio");
			uom = prop.getProperty("uom");
			vid = prop.getProperty("void");
			
			// Prefix pour les query
			
			queryPrefix =  	
					"prefix dsp: <" + dsp + ">\n" +
					"prefix skos: <" + skos + ">\n" +
					"prefix skosXL: <" + skosXL + ">\n" +
					"prefix vs: <" + vs + ">\n" +
					"prefix dc: <" + dc + ">\n" +
					"prefix foaf: <" + foaf + ">\n" +
					"prefix xsd: <" + xsd + ">\n" +
					"prefix owl: <" + owl + ">\n" +
					"prefix dcterms: <" + dcterms + ">\n" +
					"prefix rdfs: <" + rdfs + ">\n" +
					"prefix rdf: <" + rdf + ">\n" +
					"prefix geo: <" + geo + ">\n" +
					"prefix geof: <" + geof + ">\n" +
					"prefix sf: <" + sf + ">\n" +
					"prefix sosa: <" + sosa + ">\n" +
					"prefix ssn: <" + ssn + ">\n" +
					"prefix om: <" + om + ">\n" +
					"prefix time: <" + time + ">\n" +
					"prefix spatialF: <" + spatialF + ">\n" +
					"prefix spatial: <" + spatial + ">\n" +
					"prefix wgs: <" + wgs + ">\n" +
					"prefix sio: <" + sio + ">\n" +
					"prefix uom: <" + uom + ">\n" +
					"prefix bfo: <" + bfo + ">\n" +
					"prefix void: <" + vid + ">\n" ;
							
			AgroPortalProfilingConf.prop = prop;
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMyProperty(String key) {
		return (AgroPortalProfilingConf.prop.getProperty(key));
	}
	
	/**
	 * Update property
	 */
	public boolean setMyProperty(String key, String value) {
		boolean success = false;
		try {
			AgroPortalProfilingConf.prop.setProperty(key, value);
			URL resource = getClass().getClassLoader().getResource(AgroPortalProfilingConf.filename);
			BufferedWriter out = new BufferedWriter(new FileWriter(Paths.get(resource.toURI()).toFile()));
			AgroPortalProfilingConf.prop.store(out, null);
			out.close();
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}
	
}
