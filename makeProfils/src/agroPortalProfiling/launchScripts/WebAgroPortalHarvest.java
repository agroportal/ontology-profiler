package agroPortalProfiling.launchScripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import agroPortalProfiling.util.AgroPortalProfilingConf;
import agroPortalProfiling.util.Datasets;
import agroPortalProfiling.util.FileName;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.channels.FileChannel;


public class WebAgroPortalHarvest {
	
	static final String REST_URL = "https://data.agroportal.lirmm.fr/";
    static final String API_KEY = "5592185f-9951-49c4-903a-135e68aaa6f3";
	static final String PUBLIC_API_KEY = "1de0a270-29c5-4dda-b043-7c3580628cd5";
	
    static final ObjectMapper mapper = new ObjectMapper();
    static final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();


	public static void Treatment() throws Exception {
		
		// Première recherche pour les différentes ontologies d'AgroPortal
        JsonNode searchResult = jsonToNode(get(REST_URL + "ontologies"));
		if (searchResult == null) {
			System.out.println("Impossible d'acceder au site AgroPortal !");
		
		}
		for (JsonNode node : searchResult) {
			TreatmentNode(node);
		}
	}

	public static void Treatment(String acronym) throws Exception {
		
		// Première recherche pour les différentes ontologies d'AgroPortal
        JsonNode node = jsonToNode(get(REST_URL + "ontologies/" + acronym));
		if (node == null) {
			System.out.println("Impossible d'acceder au site AgroPortal !");
		}
		
		TreatmentNode(node);
	}

	private static void TreatmentNode(JsonNode node) {
		String year = Year.now().toString();
		String hasOntologyLanguage = "";
		String id = node.path("acronym").asText();
		// Recherche date de version
		JsonNode linksNode = node.path("links");			
		if (linksNode.isMissingNode()) {
			//si "links" node manquant
			System.out.println("No links node !");
		} else {
			String url_latest_submission = linksNode.path("latest_submission").asText();
			//missing node, just return empty string
			//System.out.println("url_latest_submission : "  + url_latest_submission);
			JsonNode latest_submission_Node = jsonToNode(get(url_latest_submission));

			if (latest_submission_Node == null) {
				System.out.println("Impossible de récupérer la derniére soumission pour : " + id);
			
			} else {
				String latest_submission_date = latest_submission_Node.path("released").asText();
				Pattern pattern = Pattern.compile("(\\d{4})"); // Recherche 4 chiffres consécutifs
				Matcher matcher = pattern.matcher(latest_submission_date);
				if (matcher.find()) {
					year = matcher.group(1); // Récupère le premier groupe (l'année)
				}
				hasOntologyLanguage = latest_submission_Node.path("hasOntologyLanguage").asText();
			} 
		};
		String folderName = year + "-" + id.toLowerCase();
		String fileName = id.toLowerCase();
		//System.out.println("Acronym: " + id);
		// On cherche maintenant à récupérer le fichier RDF/XML de l'ontologie
		// Si l'ontologie a pour langage OWL ou SKOS on prend le fichier natif de l'ontologie
		if (hasOntologyLanguage.equals("OWL") || hasOntologyLanguage.equals("SKOS")) {
			String rdfUrl = REST_URL + "ontologies/" + id + "/download";
			//System.out.println("outputFilePath : " + outputFilePath.toString());
			downloadRDF(rdfUrl, folderName, fileName);
		} else {
			// Sinon on rajoute l'option "?download_format=rdf" pour obtenir le fichier local d'AgroPortal qui est toujours en rdf/xml
			// Exemple un fichier natif OBO (XEO_v1_6_v1.6_no_Instance.obo) sera transformé en fichier xeo.xrdf par OWLAPI
			System.out.println(id + " hasOntologyLanguage : " + hasOntologyLanguage);
			String rdfUrl = REST_URL + "ontologies/" + id + "/download?download_format=rdf";
			downloadRDF(rdfUrl, folderName, fileName);	
		}
	}

	private static JsonNode jsonToNode(String json) {
		if (json == null) {
			return null; // Retourne null si l'entrée est null
		}
		try {
			return mapper.readTree(json);
		} catch (JsonProcessingException e) {
			System.err.println("Erreur de traitement JSON : " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	private static String get(String urlToGet) {
		StringBuilder result = new StringBuilder();
		try {
			URL url = new URL(urlToGet);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
			conn.setRequestProperty("Accept", "application/json");
	
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
					String line;
					while ((line = rd.readLine()) != null) {
						result.append(line);
					}
				}
			} else {
				System.err.println("HTTP Error (" + responseCode + "): " + urlToGet);
				return null; // Retourne null en cas d'erreur HTTP
			}
		} catch (Exception e) {
			System.err.println("Erreur de connexion : " + e.getMessage());
			e.printStackTrace();
			return null; // Retourne null en cas d'exception
		}
		return result.toString();
	}

	public static void downloadRDF(String urlToGet, String folderName, String fileName) {
		// Récupération du nom du fichier contenant la liste des ontologies à traiter.
		Path pathOfTheListDatasets = Paths.get(AgroPortalProfilingConf.mainFolderAgroPortalProfiling, AgroPortalProfilingConf.fileNameListDatasets);					
		try {
			URL url = new URL(urlToGet);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
			conn.setRequestProperty("Accept", "application/rdf+xml");
	
			// Vérification du code de réponse HTTP
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// Déterminer le type de contenu via l'en-tête
				String contentType = conn.getContentType();
				//System.out.println("contentType : " + contentType);
				boolean oboAlert = false;
				boolean isGzip = false;
				isGzip = contentType != null && contentType.contains("gzip");
				isGzip = contentType != null && contentType.contains("zip");
				String  fileExtention = ".xml";
				// Déterminer le type de contenu via Content-Disposition
				String contentDisposition = conn.getHeaderField("Content-Disposition");
				//System.out.println("contentDisposition : " + contentDisposition);
				if (contentDisposition != null) {
					if (contentDisposition.contains(".xrdf")) {
						fileExtention = ".xrdf";
					}
					if (contentDisposition.contains(".rdf")) {
						fileExtention = ".rdf";
					}	
					if (contentDisposition.contains(".owl")) {
						fileExtention = ".owl";
					}			
					if (contentDisposition.contains(".ttl")) {
						fileExtention = ".ttl";
					}			
					if (contentDisposition.contains(".gz")) {
						fileExtention = ".rdf.gz";
						isGzip = true;
					}
					if (contentDisposition.contains(".nt.zip")) {
						fileExtention = ".nt.zip";
						isGzip = true;
					}	
					// Vérification OBO non déclaré		
					if (contentDisposition.contains(".obo") && !urlToGet.contains("?download_format=rdf")) {
						System.out.println("Le fichier téléchargé est sous format obo ! : " + urlToGet);
						oboAlert = true;
					}				
				}

	
				Path outputFilePath = Paths.get(AgroPortalProfilingConf.folderForDatasets, folderName, fileName + fileExtention);

				// Créer les répertoires manquants
				File outputFile = new File(outputFilePath.toString());
				File parentDir = outputFile.getParentFile();
				if (parentDir != null && !parentDir.exists()) {
					parentDir.mkdirs(); // Crée les répertoires parents si nécessaire
				}
	
				// Lire le contenu et écrire dans un fichier
				try (InputStream inputStream = conn.getInputStream();
					 FileOutputStream outputStream = new FileOutputStream(outputFilePath.toString())) {
					
					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}
	
					// Traitement spécifique si le fichier est une archive
					if (isGzip && fileExtention.contains(".gz")) {
						System.out.println("Le fichier téléchargé est une archive .gz : " + outputFilePath.toString());
						// Appeler une méthode pour décompresser l'archive, si nécessaire
						decompressGzip(outputFilePath.toString());
						fileExtention = fileExtention.replace(".gz", "");
					} 

					// Traitement spécifique si le fichier est une archive .zip
					if (isGzip && fileExtention.contains(".zip")) {
						System.out.println("Le fichier téléchargé est une archive .zip : " + outputFilePath.toString());
						// Appeler une méthode pour décompresser l'archive, si nécessaire
						decompressSingleFileZip(outputFilePath.toString());
						fileExtention = fileExtention.replace(".zip", "");
					}
					
					if (isGzip) {
						// Supprimer le fichier archive après extraction
						outputStream.close();
						File zipFile = new File(outputFilePath.toString());
						if (zipFile.delete()) {
							//System.out.println("Archive supprimée avec succès : " + outputFilePath.toString());
						} else {
							System.err.println("Impossible de supprimer l'archive : " + outputFilePath.toString());
						}
					}
	
					String activeDatasets = "yes";
					if (oboAlert) {
						activeDatasets = "no!obo";
					}

					// Mettre à jour le fichier JSON
					updateDatasetJson(pathOfTheListDatasets.toString(), folderName, activeDatasets, fileName + fileExtention);
				}
			} else {
				if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
					System.err.println("Fichier introuvable : " + urlToGet);
				} else {
					System.err.println("Erreur HTTP (" + responseCode + ") : " + urlToGet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void updateDatasetJson(String filePath, String idDataset, String activeDatasets, String fileName) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			// Lire le fichier JSON existant
			File jsonFile = new File(filePath);
			ArrayList<Datasets> datasetsList = new ArrayList<>();
	
			if (jsonFile.exists()) {
				datasetsList = new ArrayList<>(List.of(mapper.readValue(jsonFile, Datasets[].class)));
			}
	
			// Vérifier si le dataset existe déjà
			Datasets existingDataset = datasetsList.stream()
				.filter(dataset -> dataset.getidDataset().equals(idDataset))
				.findFirst()
				.orElse(null);
	
			if (existingDataset == null) {
				// Créer un nouveau dataset si inexistant
				ArrayList<FileName> filesSource = new ArrayList<>();
				filesSource.add(new FileName(fileName));
				datasetsList.add(new Datasets(idDataset, activeDatasets, filesSource));
			} else {
				// Vérifier si le fichier existe déjà dans le dataset
				boolean fileExists = existingDataset.getFilesSource().stream()
					.anyMatch(file -> file.getName().equals(fileName));
				if (!fileExists) {
					existingDataset.getFilesSource().add(new FileName(fileName));
				}
			}
	
			// Réécrire le fichier JSON
			mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, datasetsList);
	
		} catch (Exception e) {
			System.err.println("Erreur lors de la mise à jour du fichier JSON : " + filePath);
			e.printStackTrace();
		}
	}
	
	public static void decompressGzip(String gzipFilePath) {
		if (!isGzipFile(gzipFilePath)) {
			System.err.println("Le fichier n'est pas un fichier GZIP valide : " + gzipFilePath);
			return;
		}	
		String outputFilePath = gzipFilePath.replace(".gz", "");
		try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(gzipFilePath));
			FileOutputStream fos = new FileOutputStream(outputFilePath)) {
			
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = gis.read(buffer)) != -1) {
				fos.write(buffer, 0, bytesRead);
			}
			System.out.println("Fichier décompressé : " + outputFilePath);
		} catch (IOException e) {
			System.err.println("Erreur lors de la décompression de l'archive : " + gzipFilePath);
			e.printStackTrace();
		}
	}

	public static boolean isGzipFile(String filePath) {
		try (FileInputStream fis = new FileInputStream(filePath)) {
			byte[] signature = new byte[2];
			if (fis.read(signature) == 2) {
				// GZIP signature is 0x1F8B
				return signature[0] == (byte) 0x1F && signature[1] == (byte) 0x8B;
			}
		} catch (IOException e) {
			System.err.println("Erreur lors de la vérification du fichier GZIP : " + e.getMessage());
		}
		return false;
	}

	public static void decompressSingleFileZip(String zipFilePath) {
		if (!isZipFile(zipFilePath)) {
			System.err.println("Le fichier n'est pas un fichier ZIP valide : " + zipFilePath);
			return;
		}

		String outputFilePath = zipFilePath.replace(".zip", "");

		// Flux à manipuler
		FileInputStream fis = null;
		FileChannel fileChannel = null;
		ZipInputStream zis = null;

		try {
			fis = new FileInputStream(zipFilePath);
			fileChannel = fis.getChannel();
			zis = new ZipInputStream(fis);

			ZipEntry zipEntry = zis.getNextEntry();
			if (zipEntry == null) {
				System.err.println("L'archive ZIP est vide : " + zipFilePath);
				return;
			}

			if (zipEntry.isDirectory()) {
				System.err.println("L'archive contient un répertoire au lieu d'un fichier : " + zipEntry.getName());
			} else {
				try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = zis.read(buffer)) != -1) {
						fos.write(buffer, 0, bytesRead);
					}
				}
				System.out.println("Fichier décompressé : " + outputFilePath);
			}
			zis.closeEntry();

		} catch (IOException e) {
			System.err.println("Erreur lors de la décompression de l'archive ZIP : " + zipFilePath);
			e.printStackTrace();
		} finally {
			// Fermeture explicite des flux
			try {
				if (zis != null) zis.close();
				if (fileChannel != null) fileChannel.close();
				if (fis != null) fis.close();
			} catch (IOException e) {
				System.err.println("Erreur lors de la fermeture des flux.");
				e.printStackTrace();
			}
		}
	}

	
	

	public static boolean isZipFile(String filePath) {
		try (FileInputStream fis = new FileInputStream(filePath)) {
			byte[] signature = new byte[4];
			if (fis.read(signature) == 4) {
				// ZIP signature is 0x504B0304
				return signature[0] == (byte) 0x50 && signature[1] == (byte) 0x4B &&
					   signature[2] == (byte) 0x03 && signature[3] == (byte) 0x04;
			}
		} catch (IOException e) {
			System.err.println("Erreur lors de la vérification du fichier ZIP : " + e.getMessage());
		}
		return false;
	}
	
}