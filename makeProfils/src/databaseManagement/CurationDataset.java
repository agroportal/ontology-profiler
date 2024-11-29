package databaseManagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.net.URLEncoder;

import agroPortalProfiling.util.FileName;
import agroPortalProfiling.util.Datasets;
import agroPortalProfiling.util.AgroPortalProfilingConf;
import agroPortalProfiling.util.AgroPortalProfilingUtil;

public class CurationDataset {

	public static void main(final String[] args) throws Exception {

		initialisation();

	}
	public static void initialisation() throws Exception {
		
		Instant start0 = Instant.now();
		// Initialisation de la configuration
		// Chemin d'accès, noms fichiers...
		new AgroPortalProfilingConf(); 

		// Récupération du nom du répertoire des datasets à traiter dans la configuration
		String mainDirectoryForDatasets = AgroPortalProfilingConf.folderForDatasets;
	
		// Récupération du nom du fichier contenant la liste des datasets à traiter.
		Path pathOfTheListDatasets = Paths.get(AgroPortalProfilingConf.mainFolderAgroPortalProfiling, AgroPortalProfilingConf.fileNameListDatasetsForCuration);
			
		// Récupération du nom des fichiers de dataset dans listDatasetsFileName pour les jeux de données source
		ArrayList<Datasets> listDatasetsFileName = new ArrayList<Datasets>();	
		listDatasetsFileName = AgroPortalProfilingUtil.makeListPairFileName(pathOfTheListDatasets.toString()); 
		
		long heapsize = Runtime.getRuntime().totalMemory();
		long maxSize = Runtime.getRuntime().maxMemory();
		int availableProcessors = Runtime.getRuntime().availableProcessors();

        System.out.println("Total memory is: " + heapsize);
		System.out.println("Max memory is: " + maxSize);
		System.out.println("The number of available processors is: " + availableProcessors);
			
		for(Datasets pairOfDatasets: listDatasetsFileName){
			// Il peut y avoir plusieurs ontologies pour le jeux de données source et pour le jeux de données target. 

			// Récupération de l'identifiant de la paire de datasets.
			String idDataset = pairOfDatasets.getidDataset();

			// Récupération du tag de la paire de datasets.
			String activeDatasets = pairOfDatasets.getactiveDatasets();
			

			if (activeDatasets.equals("yes") ) {

				System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
				System.out.println("Loading files from the directory " + idDataset);
				System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	
				// Récupération du nom des ontologies à traiter pour le jeux de données source.
				ArrayList<String> listSourceDatasetsFileName = new ArrayList<String>();
				for(FileName filename: pairOfDatasets.getFilesSource()){
					listSourceDatasetsFileName.add(filename.getName());
				}

				//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				
				treatment(mainDirectoryForDatasets, idDataset, listSourceDatasetsFileName);
			}
		}
		
		System.out.println("End of the curation of datasets");
		Instant end0 = Instant.now();
		System.out.println("Total running time : " + AgroPortalProfilingUtil.getDurationAsString(Duration.between(start0, end0).toMillis()));
	}


	public static void treatment(String mainDirectoryForDatasets, String idDataset, ArrayList<String> listDatasetsFileName) throws Exception {
		
		for (String fileName : listDatasetsFileName) {
			
			Path pathFileDataset = Paths.get(mainDirectoryForDatasets, idDataset, fileName);
			System.out.println("The dataset " + pathFileDataset + " is being loaded");
			// Correction des erreurs du fichier
			modifyFile(pathFileDataset.toString());
		}	
	}		
	
	/**
	 * Correction des problémes de typo du fichier
	 */
	public static void modifyFile(String filePath) {
		try {
			// Ouverture du fichier d'entrée et du fichier temporaire de sortie
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath + ".tmp"), StandardCharsets.UTF_8));
	
			// Lecture et modification ligne par ligne
			String line;
			while ((line = reader.readLine()) != null) {
				// Modification des erreurs typographiques
				String modifiedLine = line.replaceAll("http:///", "http://")
					.replaceAll("https:///", "https://")
					.replaceAll("%5D%5(?!D)", "%5D%5D")
					.replaceAll("xmlns:ns3=\"http://dbkwik.webdatacommons.org/marvel.wikia.com/property/%3\"", "")
					.replaceAll("ns3:CdivAlign", "ns1:divAlign")
					.replaceAll("http://www.wikipedia.com:secrets_of_spiderman_revealed", "http://www.wikipedia.com/secrets_of_spiderman_revealed")
					.replaceAll("OntologyID\\(Anonymous-2\\)module1", "http://OntologyID/Anonymous_2/module1")
					.replaceAll("ِAlRay_AlAam", "AlRay_AlAam")
					.replaceAll("<dcterms:created rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\"></dcterms:created>", "<dcterms:created rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">0001-01-01T00:00:00Z</dcterms:created>")
					.replaceAll("<dcterms:modified rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\"></dcterms:modified>", "<dcterms:modified rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">0001-01-01T00:00:00Z</dcterms:modified>")
					.replaceAll("http:/php.net", "http://php.net")
					.replaceAll("http:/www.apple.com/jp/iphone", "http://www.apple.com/jp/iphone")
					.replaceAll("http:/www.apple.com/safari", "http://www.apple.com/safari")
					.replaceAll("http:/www.senate.gov", "http://www.senate.gov");
	
				// Encodage des caractères illégaux dans les IRIs
				String encodedLine = encodeInvalidCharactersInIRI(modifiedLine);
	
				// Unicode Normalization Form C
				String normalizedLine = Normalizer.normalize(encodedLine, Normalizer.Form.NFC);
				writer.write(normalizedLine + "\n");
			}
	
			// Fermeture des flux
			reader.close();
			writer.close();
	
			// Suppression du fichier d'origine
			Files.deleteIfExists(Paths.get(filePath));
	
			// Renommer le fichier temporaire en fichier d'origine
			Files.move(Paths.get(filePath + ".tmp"), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
	
			System.out.println("Le fichier a été modifié avec succès.");
		} catch (IOException e) {
			System.out.println("Une erreur s'est produite lors de la modification du fichier : " + e.getMessage());
		}
	}
	
	private static String encodeInvalidCharactersInIRI(String iri) {
		StringBuilder encodedIRI = new StringBuilder();
		for (char c : iri.toCharArray()) {
			// Vérifier si le caractère est illégal pour un IRI et doit être encodé
			if (isIllegalIRICharacter(c)) {
				try {
					// Encoder le caractère illégal
					encodedIRI.append(URLEncoder.encode(String.valueOf(c), StandardCharsets.UTF_8.toString()));
				} catch (Exception e) {
					// En cas d'erreur d'encodage
					encodedIRI.append(c); // Garder le caractère original si l'encodage échoue
				}
			} else {
				encodedIRI.append(c); // Caractère valide, on le garde tel quel
			}
		}
		return encodedIRI.toString();
	}
	
	private static boolean isIllegalIRICharacter(char c) {
		// Définir les caractères illégaux à encoder
		return (c >= 0xD800 && c <= 0xDFFF) // Paires de substitution UTF-16
				//|| c == ' ' // Les espaces sont également illégaux dans un IRI (mais là nous lisons la ligne entière)
				|| !Character.isValidCodePoint(c); // Tout autre caractère non valide
	}
}




