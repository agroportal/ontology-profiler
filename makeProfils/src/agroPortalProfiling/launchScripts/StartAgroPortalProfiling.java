package agroPortalProfiling.launchScripts;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import agroPortalProfiling.util.AgroPortalProfilingConf;
import agroPortalProfiling.util.AgroPortalProfilingUtil;

public class StartAgroPortalProfiling {

	// Lancement initial pour le traitement des requêtes
	// Les fichiers déposés sur le serveur (\var\www\agroPortalProfiling) servent à paramétrer le modéle
	//  à traiter (choix des ontologies, ...) 

	public static void main(String[] args) throws Exception {
		new AgroPortalProfilingConf();
		
		// information sur mémoire disponible
		long heapsize = Runtime.getRuntime().totalMemory();
		long maxSize = Runtime.getRuntime().maxMemory();
		int availableProcessors = Runtime.getRuntime().availableProcessors();

        System.out.println("Total memory is: " + heapsize);
		System.out.println("Max memory is: " + maxSize);
		System.out.println("The number of available processors is: " + availableProcessors);
		
		Instant start0 = Instant.now();
		String formattedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
    	.withZone(ZoneId.systemDefault())
    	.format(Instant.now());
		System.out.println("Start time : " + formattedDate);

		CreateModelAndRunTreatement.ModelAndTreatment();
		
		Instant end0 = Instant.now();
		System.out.println("Total running time : " + AgroPortalProfilingUtil.getDurationAsString(Duration.between(start0, end0).toMillis()));
	}  
}