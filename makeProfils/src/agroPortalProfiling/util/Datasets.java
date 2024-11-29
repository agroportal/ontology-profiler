package agroPortalProfiling.util;

import java.util.ArrayList;

public class Datasets {
	
	private String idDataset;
	private String activeDatasets;
	private ArrayList<FileName> filesSource;

	public Datasets() {
	}

	public Datasets(String idDataset, String activeDatasets, ArrayList<FileName> filesSource) {
		this.idDataset = idDataset;
		this.activeDatasets = activeDatasets;
		this.filesSource = filesSource;
	}

	public String getidDataset() {
		return idDataset;
	}

	public void setidDataset(String idDataset) {
		this.idDataset = idDataset;
	}

	public String getactiveDatasets() {
		return activeDatasets;
	}

	public void setactiveDatasets(String activeDatasets) {
		this.activeDatasets = activeDatasets;
	}

	public ArrayList<FileName> getFilesSource() {
		return filesSource;
	}

	public void setFilesSource(ArrayList<FileName> filesSource) {
		this.filesSource = filesSource;
	}
}
	