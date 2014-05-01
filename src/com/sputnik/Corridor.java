package com.sputnik;

public class Corridor {
	private Node node1;
	private int weight = 1;

	public Corridor() {

	}

	public Corridor(Node node1) {
		this.node1 = node1;
	}

	public void setNode(Node node1) {
		this.node1 = node1;
	}

	public Node getNode() {
		return this.node1;
	}

	public int getWeight() {
		if (node1 == null) {
			return 9999;
		}
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
