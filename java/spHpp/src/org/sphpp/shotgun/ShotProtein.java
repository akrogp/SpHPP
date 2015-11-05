package org.sphpp.shotgun;

import java.util.HashMap;
import java.util.Map;

public class ShotProtein {
	public boolean isHpa() {
		return hpa;
	}
	public void setHpa(boolean hpa) {
		this.hpa = hpa;
	}
	public boolean isProteomicsDb() {
		return proteomicsDb;
	}
	public void setProteomicsDb(boolean proteomicsDb) {
		this.proteomicsDb = proteomicsDb;
	}
	public boolean isNappa() {
		return nappa;
	}
	public void setNappa(boolean nappa) {
		this.nappa = nappa;
	}
	public boolean isJpr2Missing() {
		return jpr2Missing;
	}
	public void setJpr2Missing(boolean jpr2Missing) {
		this.jpr2Missing = jpr2Missing;
	}
	public boolean isMrmValidated() {
		return mrmValidated;
	}
	public void setMrmValidated(boolean mrmValidated) {
		this.mrmValidated = mrmValidated;
	}
	public Map<String, ShotLine> getLineCounts() {
		return lineCounts;
	}
	public void setLineCounts(Map<String, ShotLine> lineCounts) {
		this.lineCounts = lineCounts;
		updateCounts();
	}
	public void addLineCount( String name, int count, int countSPG ) {
		ShotLine shotLine = new ShotLine();
		shotLine.setName(name);
		shotLine.setCount(count);
		shotLine.setCountSPG(countSPG);
		lineCounts.put(name, shotLine);
		updateCounts();
	}
	public int getShotgunDetected() {
		return shotgunDetected;
	}
	public int getShotgunObserved() {
		return shotgunObserved;
	}
	public String getUniprotAccession() {
		return uniprotAccession;
	}
	public void setUniprotAccession(String uniprotAccession) {
		this.uniprotAccession = uniprotAccession;
	}
	private void updateCounts() {
		shotgunObserved = 0;
		for( ShotLine line : lineCounts.values() ) {
			if( line.getCount() > 0 )
				shotgunObserved++;
			else if( line.getCount() == -1 )
				shotgunDetected++;
			if( line.getCountSPG() > 0 )
				shotgunObserved++;
			else if( line.getCountSPG() == -1 )
				shotgunDetected++;
		}
	}
	private String uniprotAccession;	
	private boolean hpa;
	private boolean proteomicsDb;
	private boolean nappa;
	private boolean jpr2Missing;
	private boolean mrmValidated;
	private Map<String,ShotLine> lineCounts = new HashMap<>();
	private int shotgunDetected;
	private int shotgunObserved;	
}
