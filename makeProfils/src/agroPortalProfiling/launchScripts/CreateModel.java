package agroPortalProfiling.launchScripts;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import agroPortalProfiling.util.*;


public class CreateModel { 

	public static Model createModel(String idDataset, ArrayList<String> listDatasets) throws Exception {

		Instant start0 = Instant.now();
		Model modelTemp = ModelFactory.createDefaultModel();
		// Récupération du nom du répertoire des datasets à traiter dans la configuration
		String mainDirectoryForDatasets = AgroPortalProfilingConf.folderForDatasets;
		for(String nameDataset: listDatasets){
			Path pathFileDataset = Paths.get(mainDirectoryForDatasets, idDataset, nameDataset);
			System.out.println("The dataset " + pathFileDataset + " is being loaded");
			String typeOfSerialization = getFileSerializationType(nameDataset);
			String baseXml = getBaseXml(pathFileDataset.toString(), typeOfSerialization);
			System.out.println("baseXml: " + baseXml);
			// Lecture du fichier en fonction du type de sérialisation
			try (InputStream is = new FileInputStream(pathFileDataset.toString())) {
				modelTemp.read(is, baseXml, typeOfSerialization);
			}
		}
		
		Instant end0 = Instant.now();
		System.out.println("Runtime for loading models into memory: " + AgroPortalProfilingUtil.getDurationAsString(Duration.between(start0, end0).toMillis()));

		return modelTemp;
	}

	/**
	 * Détection du type de sérialisation
	 */
	private static String getFileSerializationType(String fileName) {
		if (fileName.matches("^.*json$")) {
			return "JSONLD";
		} else if (fileName.matches("^.*ttl$")) {
			return "TTL";
		} else {
			return "RDF/XML";  // Default to RDF/XML if no specific type is detected
		}
	}

	/**
	 * Détection du xml:base
	 */
	private static String getBaseXml(String pathDataset, String typeOfSerialization) {
		String base = ""; // Variable pour stocker la valeur de xml:base
	
		if (typeOfSerialization.equals("RDF/XML")) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathDataset), StandardCharsets.UTF_8))) {
				String line;
				// Lire chaque ligne et chercher l'attribut xml:base
				while ((line = reader.readLine()) != null) {
					// Vérifier si la ligne contient l'attribut xml:base
					if (line.contains("xml:base")) {
						// Extraire la valeur de l'attribut xml:base
						int start = line.indexOf("xml:base=\"") + 10; // 10 est la longueur de "xml:base=\""
						int end = line.indexOf("\"", start);
						if (start > -1 && end > -1) {
							base = line.substring(start, end); // Récupérer la valeur entre les guillemets
						}
						break; // Sortir après avoir trouvé l'attribut
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace(); // Gérer l'exception si le fichier n'est pas trouvé
			} catch (IOException e) {
				e.printStackTrace(); // Gérer les erreurs de lecture du fichier
			}
		}
		return base; // Retourner la valeur de xml:base (ou une chaîne vide si elle n'est pas trouvée)
	}
}
