package com.sputnik;

public class Node {
	private int status = 0;
	private String name;
	private int x;
	private int y;
	private Corridor[] corridor;
	private int f_value=0;
	private int g_value=0;
	private Node cameFrom;

	public Node(int x, int y) {
		this.x = x;
		this.y = y;
		corridor = new Corridor[4];
		for(int i=0;i<4;i++){
			corridor[i]=new Corridor();
		}
	}

	public Corridor getCorridor(int corridorNumber) {
		return corridor[corridorNumber];
	}

	public void setCorridor(Corridor corridor, int corridorNumber) {
		this.corridor[corridorNumber] = corridor;
	}

	public int getFValue() {
		return this.f_value;
	}
	
	public Node getCameFrom(){
		return this.cameFrom;
	}
	
	public void setCameFrom(Node cameFrom){
		this.cameFrom=cameFrom;
	}
	
	public void setFValue(int f_value) {
		this.f_value=f_value;
	}
	public int getGValue() {
		return this.g_value;
	}
	public void setGValue(int g_value) {
		this.g_value=g_value;
	}
	
	
	public int getStatus() {
		return this.status;
	}

	public String getName() {
		return this.name;
	}

	public int[] getCordinates() {
		int[] cordinates = new int[2];
		cordinates[0] = this.x;
		cordinates[1] = this.y;
		return cordinates;
	}

	public void setStatus(int status) {
		if(this.status==4){
			return;
		}
		if(this.status==3){
			if(status==1){
				this.status=4;
			}
			return;
		}
		if(status==0 && this.status==1){
			return;
		}
		if(status==0 && this.status==4){
			return;
		}
		if(status==0 && this.status==5){
			return;
		}
		if(status==3 && this.status==1){
			this.status=4;
			return;
		}
		if(this.status==1 && status==5) {
			return;
		}
		this.status = status;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCordinates(int[] cordinates) {
		this.x = cordinates[0];
		this.y = cordinates[1];
	}

}