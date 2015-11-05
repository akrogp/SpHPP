package org.sphpp.shotgun;

public class ShotLine {
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCountSPG() {
		return countSPG;
	}
	public void setCountSPG(int countSPG) {
		this.countSPG = countSPG;
	}
	public int getCountMax() {
		return count > countSPG ? count : countSPG;
	}
	private String name;
	private int count;
	private int countSPG;
}
