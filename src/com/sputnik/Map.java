package com.sputnik;

import java.util.ArrayList;

public class Map {
	private int width;
	private int height;
	private Node current;
	private Node[][] mapNode;
	private int heading;
	private Node destination;
	private Node[] path;

	public Map(int width, int height, int current_x, int current_y, int heading) {
		// System.out.println("Test1");
		this.width = width;
		this.heading = heading;
		this.height = height;
		mapNode = new Node[width][height];
		for (int i = 0; i < this.width; i++) {// initilize nodes
			for (int j = 0; j < this.height; j++) {
				mapNode[i][j] = new Node(i, j);
			}
		}
		for (int i = 0; i < this.width; i++) {// initilize node corridors
			for (int j = 0; j < this.height; j++) {
				int rightNode_i = i + 1;
				int leftNode_i = i - 1;
				int topNode_j = j - 1;
				int bottomNode_j = j + 1;
				if (rightNode_i < this.width) {// set node right
					mapNode[i][j].getCorridor(3).setNode(
							mapNode[rightNode_i][j]);
				} else {
					mapNode[i][j].getCorridor(3).setNode(null);
				}
				if (leftNode_i > -1) {// set node left
					mapNode[i][j].getCorridor(1)
							.setNode(mapNode[leftNode_i][j]);
				} else {
					mapNode[i][j].getCorridor(1).setNode(null);
				}
				if (topNode_j > -1) {
					mapNode[i][j].getCorridor(0).setNode(mapNode[i][topNode_j]);
				} else {
					mapNode[i][j].getCorridor(0).setNode(null);
				}
				if (bottomNode_j < this.height) {
					mapNode[i][j].getCorridor(2).setNode(
							mapNode[i][bottomNode_j]);
				} else {
					mapNode[i][j].getCorridor(2).setNode(null);
				}
			}
		}
		current = mapNode[current_x][current_y];
	}

	public String drawMap() {
		String result = "";
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (mapNode[i][j].getCorridor(1).getNode() != null) {
					if (mapNode[i][j].getCorridor(1).getWeight() != 1) {
						result += "\t";
					} else {
						result += "-\t";
					}
				}
				if (mapNode[i][j].equals(current)) {
					result += "R\t";
				} else if (mapNode[i][j].equals(destination)) {
					result += mapNode[i][j].getStatus() + "G\t";
				} else {
					int k = 0;
					for (k = 0; k < path.length; k++) {
						if (path[k].equals(mapNode[i][j])) {
							result += mapNode[i][j].getStatus() + "*\t";
							break;
						}
					}
					if (k >= path.length) {
						result = result + mapNode[i][j].getStatus() + "\t";
					}
				}
			}
			result += "\n";
			for (int i = 0; i < width; i++) {
				if (mapNode[i][j].getCorridor(2).getNode() != null
						&& mapNode[i][j].getCorridor(2).getWeight() == 1) {
					result += "|\t\t";
				} else {
					result += "\t\t";
				}
			}
			result += "\n";
		}
		return result;
	}

	public void makeMapClass() {
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				if (i == 0 && j == this.height-1) {
					for (int k = 0; k < 4; k++) {
						mapNode[i][j].getCorridor(k).setWeight(9999);
						if (mapNode[i][j].getCorridor(k).getNode() != null) {
							mapNode[i][j].getCorridor(k).getNode()
									.getCorridor((k + 2) % 4).setWeight(9999);
						}
						mapNode[i][j].setStatus(4);
					}
					System.out.println("BS: " + i + "," + j);
				}
				if(j == 2 && i == 0){
					mapNode[i][j].setStatus(0);
					continue;
				}
				if(j == 2 && i == 3){
					mapNode[i][j].setStatus(0);
					continue;
				}
				if (i == 0 || i == this.width - 1 || j == 0
						|| j == this.height - 1) {
					mapNode[i][j].setStatus(1);
					continue;
				}
				if (i == 1 && j == 2) {
					mapNode[i][j].setStatus(5);
					continue;
				}
				if (i == 2 && j == 2) {
					mapNode[i][j].setStatus(5);
					continue;
				}
				if (i == 1 && j == 3) {
					mapNode[i][j].setStatus(5);
					continue;
				}
				for (int k = 0; k < 4; k++) {
					mapNode[i][j].getCorridor(k).setWeight(9999);
					mapNode[i][j].getCorridor(k).getNode()
							.getCorridor((k + 2) % 4).setWeight(9999);
					mapNode[i][j].setStatus(1);
				}
				System.out.println("B: " + i + "," + j);
			}
		}
	}

	public void setBall() {
		int x = current.getCordinates()[0];
		int y = current.getCordinates()[1];
		mapNode[x][y].setStatus(2);
		/*
		 * for (int i = 0; i < width; i++) { for (int j = 0; j < height; j++) {
		 * if (mapNode[i][j].getStatus() == 2) { continue; } if (i == x || y ==
		 * j) { mapNode[i][j].setStatus(3); } if (Math.abs(i - x) == Math.abs(j
		 * - y)) { mapNode[i][j].setStatus(3); } } }
		 */
	}

	public int getNextHeading() {
		while (true) {
			Node nextNode = getNextNode();
			if (nextNode == null) {
				destination.setStatus(3);
				continue;
			}
			System.out.println("ND:" + nextNode.getCordinates()[0] + "," + nextNode.getCordinates()[1] + " > " + nextNode.getStatus());
			if (nextNode.getCordinates()[0] > current.getCordinates()[0]) {
				return 3;
			} else if (nextNode.getCordinates()[0] < current.getCordinates()[0]) {
				return 1;
			} else if (nextNode.getCordinates()[1] < current.getCordinates()[1]) {
				return 0;
			} else if (nextNode.getCordinates()[1] > current.getCordinates()[1]) {
				return 2;
			}
			return 0;
		}
	}

	private Node getNextNode() {
		Node[] possibleNode = getPossibleNode();
		Node bestNode = new Node(9999, 9999);
		for (int i = 0; i < possibleNode.length; i++) {
			if (possibleNode[i].getStatus() == 5) {
				if(bestNode.getStatus()==0){
					bestNode = possibleNode[i];
				}
				else if (distance(bestNode, current) > distance(possibleNode[i],
						current)) {
					bestNode = possibleNode[i];
				}
			} else if (distance(bestNode, current) > distance(possibleNode[i],
					current)) {
				if (bestNode.getStatus() != 5) {
					bestNode = possibleNode[i];
				}
			}
		}
		this.destination = bestNode;
		Node[] nextNodes = AStar(bestNode);
		path = nextNodes;
		if (path == null) {
			return null;
		}
		return nextNodes[nextNodes.length - 1];
	}

	private int distance(Node node1, Node node2) {
		int x = Math.abs(node1.getCordinates()[0] - node2.getCordinates()[0]);
		int y = Math.abs(node1.getCordinates()[1] - node2.getCordinates()[1]);
		return x + y;
	}

	private Node[] getPossibleNode() {
		ArrayList<Node> possibleNodes = new ArrayList<Node>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (i == current.getCordinates()[0]
						&& j == current.getCordinates()[1]) {
					continue;
				}
				if (mapNode[i][j].getStatus() == 0
						|| mapNode[i][j].getStatus() == 5) {
					possibleNodes.add(mapNode[i][j]);
				}
			}
		}

		return (Node[]) possibleNodes.toArray(new Node[possibleNodes.size()]);
	}

	public Node[] AStar(Node destination) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				mapNode[i][j].setFValue(9999);
				mapNode[i][j].setGValue(9999);
				mapNode[i][j].setCameFrom(null);
			}
		}
		ArrayList<Node> closedset = new ArrayList<Node>();
		ArrayList<Node> openset = new ArrayList<Node>();
		openset.add(current);
		current.setGValue(0);
		current.setFValue(current.getGValue() + distance(current, destination));
		while (openset.size() > 0) {
			Node temp_current = new Node(999, 999);
			temp_current.setFValue(999999);
			for (int i = 0; i < openset.size(); i++) {
				if (openset.get(i).getFValue() < temp_current.getFValue()) {
					temp_current = openset.get(i);
				}
			}
			if (temp_current.equals(destination)) {
				if (temp_current.getFValue() > 200) {
					return null;
				}
				return recunstructPath(destination);
			}
			openset.remove(temp_current);
			closedset.add(temp_current);
			for (int i = 0; i < 4; i++) {
				Node neighbour = temp_current.getCorridor(i).getNode();
				if (neighbour == null) {
					continue;
				}
				int j = 0;
				for (j = 0; j < closedset.size(); j++) {
					if (closedset.get(j).equals(neighbour)) {
						break;
					}
				}
				if (j < closedset.size()) {
					continue;
				}
				int tentative_g = temp_current.getGValue()
						+ temp_current.getCorridor(i).getWeight();
				for (j = 0; j < openset.size(); j++) {
					if (openset.get(j).equals(neighbour)) {
						break;
					}
				}
				if (j >= openset.size() || tentative_g < neighbour.getGValue()) {
					neighbour.setCameFrom(temp_current);
					neighbour.setGValue(tentative_g);
					neighbour.setFValue(neighbour.getGValue()
							+ distance(neighbour, destination));
					if (j >= openset.size()) {
						openset.add(neighbour);
					}
				}
			}
		}
		return null;
	}

	private Node[] recunstructPath(Node current) {
		ArrayList<Node> path = new ArrayList<Node>();
		while (current.getCameFrom() != null) {
			path.add(current);
			current = current.getCameFrom();
		}
		return (Node[]) path.toArray(new Node[path.size()]);
	}

	public Node getPosition() {
		return this.current;
	}

	public void setPosition(Node current) {
		this.current.setStatus(1);
		this.current = current;
	}

	public int getHeading() {
		return this.heading;
	}

	public void setHeading(int heading) {
		this.heading = heading;
	}

	public int[] getSize() {
		int[] size = new int[2];
		size[0] = this.width;
		size[1] = this.height;
		return size;
	}

	public Node getNode(int x, int y) {
		return mapNode[x][y];
	}
}
