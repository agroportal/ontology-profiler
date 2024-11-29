package agroPortalProfiling.launchScripts;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import agroPortalProfiling.util.FileName;
import agroPortalProfiling.util.Datasets;
import agroPortalProfiling.util.AgroPortalProfilingConf;
import agroPortalProfiling.util.AgroPortalProfilingUtil;

public class CreateModelAndRunTreatement {
	
	public static void ModelAndTreatment() throws Exception {
		
		// Récupération du nom du fichier contenant la liste des ontologies à traiter.
		Path pathOfTheListDatasets = Paths.get(AgroPortalProfilingConf.mainFolderAgroPortalProfiling, AgroPortalProfilingConf.fileNameListDatasets);					
		// Récupération du nom des fichiers d'ontologies dans listDatasetsFileName.
		ArrayList<Datasets> listDatasetsFileName = new ArrayList<Datasets>();	
		listDatasetsFileName = AgroPortalProfilingUtil.makeListPairFileName(pathOfTheListDatasets.toString()); 
		
		for(Datasets datasets: listDatasetsFileName){
			// Il peut y avoir plusieurs ontologies pour le jeux de données source. 

			// Récupération de l'identifiant de la paire de datasets.
			String idDataset = datasets.getidDataset();

			// Récupération du tag de la paire de datasets.
			String activeDatasets = datasets.getactiveDatasets();

			if (activeDatasets.equals("yes") ) {

				System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
				System.out.println("Processing " + idDataset + " dataset");
				System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	
				// Récupération du nom des ontologies à traiter pour le jeux de données source.
				ArrayList<String> listSourceDatasetsFileName = new ArrayList<String>();
				for(FileName filename: datasets.getFilesSource()){
					listSourceDatasetsFileName.add(filename.getName());
				}
				//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				// Création du model
				Model model = CreateModel.createModel(idDataset, listSourceDatasetsFileName);
				// traitement du model
				//RunTreatement.treatements(model);
				RunTreatement.treatements(model);
				model.close();
				// Sauvegarde des résultats dans fichier JSON	
				Path pathForResults = Paths.get(AgroPortalProfilingConf.folderForResults, idDataset);
				AgroPortalProfilingUtil.ChangeDirectoryFiles(AgroPortalProfilingConf.folderForTmp, pathForResults.toString());
			}
		}
	}
}