package agroPortalProfiling.launchScripts;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import agroPortalProfiling.util.Uri;
import agroPortalProfiling.util.UriAndString;
import agroPortalProfiling.util.Datasets;
import agroPortalProfiling.util.AgroPortalProfilingConf;
import agroPortalProfiling.util.AgroPortalProfilingUtil;

public class AcronymTreatement {
	
	public static void ModelAndTreatment() throws Exception {
		
		ArrayList<UriAndString> listUriAndId = new ArrayList<UriAndString>();
		Integer n = 0;
		Integer n2 = 0;
		// Récupération du nom du fichier contenant la liste des ontologies à traiter.
		Path pathOfTheListDatasets = Paths.get(AgroPortalProfilingConf.mainFolderAgroPortalProfiling, AgroPortalProfilingConf.fileNameListDatasets);					
		// Récupération du nom des fichiers d'ontologies dans listDatasetsFileName.
		ArrayList<Datasets> listDatasetsFileName = new ArrayList<Datasets>();	
		listDatasetsFileName = AgroPortalProfilingUtil.makeListPairFileName(pathOfTheListDatasets.toString()); 
		
		for(Datasets datasets: listDatasetsFileName){
		
			// Récupération de l'identifiant du dataset.
			String idDataset = datasets.getidDataset();

			// Récupération du tag de la paire de datasets.
			String activeDatasets = datasets.getactiveDatasets();

			if (!activeDatasets.equals("toto") ) {
				ArrayList<Uri> listUriOntology = new ArrayList<Uri>();
				Path pathForFile = Paths.get(AgroPortalProfilingConf.folderForResults, idDataset, "listUriOntology.json");
				listUriOntology=AgroPortalProfilingUtil.makeArrayListUri(pathForFile.toString());
				// System.out.println("listUriOntology : " + idDataset + " " + listUriOntology.toString());
				// System.out.println("listUriOntology size : " + listUriOntology.size());	
				if (listUriOntology.size() > 0 ) {
					listUriAndId.add(new UriAndString(listUriOntology.get(0).getUri(), idDataset.substring(5).toUpperCase())); 
				} else {
					n++;
				}
			} else {
				n2++;
			}
		}
		// System.out.println("listUriAndId size : " + listUriAndId.size());	
		// System.out.println("Not treated ! : " + n2);
		// System.out.println("No uri ! : " + n);
		// System.out.println("listUriAndId : " + listUriAndId.toString());
		try {
			AgroPortalProfilingUtil.makeJsonUriAndStringFile(listUriAndId, "listUriAndId.json");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Path pathForResults = Paths.get(AgroPortalProfilingConf.folderForResults);
				AgroPortalProfilingUtil.ChangeDirectoryFiles(AgroPortalProfilingConf.folderForTmp, pathForResults.toString());

	}
}