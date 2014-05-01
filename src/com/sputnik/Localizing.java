package com.sputnik;

import lejos.geom.Point;
import lejos.nxt.Button;
import lejos.robotics.navigation.Pose;
import lejos.robotics.subsumption.Behavior;

public class Localizing implements Behavior {

	public static int cornerCount = 0;

	private double edgeA = 690;
	private double edgeB = 1040;
	private double edgeC = 460;
	private double edgeD = 780;
	private int moe = 40;

	private Node corner1;
	private Node corner2;
	private int heading = 0;

	// private static double edgeA = 628;
	// private static double edgeB = 952;
	// private static double edgeC = 356;
	// private static double edgeD = 260;
	// private static double edgeE = 230;
	// private static double edgeF = 699;

	@Override
	public boolean takeControl() {
		if (PathSweeping.intersection == true && cornerCount != 2) {
			return true;
		}
		return false;
	}

	@Override
	public void action() {
		Node current = do360();
		if (cornerCount == 1) {
			if (isThePoint(corner1, current)) {
				int x = 1;
				int y = 3;
				int edgeNumber = -1;
				int head = -1;
				if (corner1.getCorridor(3).getWeight() == 1) {
					edgeNumber = 4;
					head = 0;
				} else if (corner1.getCorridor(1).getWeight() == 1) {
					edgeNumber = 5;
					head = 3;
				}

				System.out.println("X: " + x + " Y: " + y);
				System.out.println("H: " + head);
				System.out.println("C: " + cornerCount);
				System.out.println("E: " + edgeNumber);
				Button.waitForAnyPress();
				Sputnik.robotMap = new Map(4, 5, x, y, head);
				Sputnik.robotMap.makeMapClass();
				Button.waitForAnyPress();
				cornerCount++;
				PathSweeping.intersection = true;
				return;
			}
		}
		if (current.isLine()) {
			if (current.getCorridor(heading).getWeight() != 1) {
				Sputnik.pilot.rotate(-90);
			}
		} else if (current.isT()) {
			if (current.getCorridor(heading).getWeight() == 1) {
				if (current.isOnT(heading)) {
					Sputnik.pilot.rotate(-90);
				}
			} else {
				Sputnik.pilot.rotate(-90);
			}
		} else if (current.isCorner()) {
			if (cornerCount == 0) {
				if (current.getCorridor(heading).getWeight() != 1) {
					Sputnik.pilot.rotate(-180);
					heading = (heading + 2) % 4;
				}
				corner1 = current;
				Sputnik.opp.setPose(new Pose(0, 0, 0));
				cornerCount++;
			} else if (cornerCount == 1) {
				this.corner2 = current;
				int edgeNumber = getEdgeNumber(Sputnik.opp.getPose()
						.distanceTo(new Point(0, 0)));
				int x = getCurrentX(this.corner2, edgeNumber);
				int y = getCurrentY(this.corner2, edgeNumber);
				int head = getHeading(x, y, edgeNumber);
				System.out.println("E: " + edgeNumber);
				System.out.println("X: " + x + " Y: " + y);
				System.out.println("H: " + head);
				System.out.println("C: " + cornerCount);
				Button.waitForAnyPress();
				Sputnik.robotMap = new Map(4, 5, x, y, head);
				Sputnik.robotMap.makeMapClass();
				Button.waitForAnyPress();
				cornerCount++;
				PathSweeping.intersection = true;
				return;
			}
		}
		PathSweeping.intersection = false;
	}

	@Override
	public void suppress() {
		// TODO Auto-generated method stub

	}

	private Node do360() {
		System.out.println("H1: " + heading);
		Node current = new Node(0, 0);
		Sputnik.pilot.rotate(-360, true);
		while (Sputnik.pilot.isMoving()) {
			double angle = Sputnik.opp.getPose().getHeading();
			int temp_heading = heading;
			if (angle <= -45 && angle > -135) {
				temp_heading = (heading + 1) % 4;;
			} else if (angle <= -135 || angle > 135) {
				temp_heading = (heading + 2) % 4;;
			} else if (angle <= 135 && angle > 45) {
				temp_heading = (heading + 3) % 4;;
			} else if (angle <= 45 || angle > -45) {
				temp_heading = (heading + 0) % 4;;
			}
			if (Sputnik.lightSensor.getLightValue() < Sputnik.lightThreshold) {
				current.getCorridor(temp_heading).setNode(new Node(0, 0));
				current.getCorridor(temp_heading).setWeight(1);
			}
		}
		return current;
	}

	private int getEdgeNumber(double edgeLenght) {
		System.out.println("D: " + edgeLenght);
		int edgeNumber = -1;

		if (edgeLenght < (edgeA + moe) && edgeLenght > (edgeA - moe))
			return 0;

		else if (edgeLenght < (edgeB + moe) && edgeLenght > (edgeB - moe))
			return 1;

		else if (edgeLenght < (edgeC + moe) && edgeLenght > (edgeC - moe))
			return 2;

		else if (edgeLenght < (edgeD + moe) && edgeLenght > (edgeD - moe))
			return 3;
		return edgeNumber;
	}

	private int getCurrentX(Node node2, int edgeNumber) {
		if (edgeNumber == 0) {
			if (node2.getCorridor((heading + 1) % 4).getWeight() == 1) {
				return 0;
			} else {
				return 3;
			}
		} else if (edgeNumber == 1) {
			return 3;
		} else if (edgeNumber == 2) {
			if (node2.getCorridor((heading + 1) % 4).getWeight() == 1) {
				return 3;
			} else {
				return 1;
			}
		} else if (edgeNumber == 3) {
			return 0;
		}

		return -1;
	}

	private int getCurrentY(Node node2, int edgeNumber) {
		if (edgeNumber == 0) {
			return 0;
		} else if (edgeNumber == 1) {
			if (node2.getCorridor((heading + 1) % 4).getWeight() == 1) {
				return 0;
			} else {
				return 4;
			}
		} else if (edgeNumber == 2) {
			return 4;
		} else if (edgeNumber == 3) {

			if (node2.getCorridor((heading + 1) % 4).getWeight() == 1) {
				return 3;
			} else {
				return 0;
			}
		}

		return -1;
	}

	private int getHeading(int x, int y, int edgeNumber) {
		int heading = -1;
		if (edgeNumber == 0) {
			if (x == 0) {
				return 1;
			} else {
				return 3;
			}
		} else if (edgeNumber == 1) {
			if (y == 0) {
				return 0;
			} else {
				return 2;
			}
		} else if (edgeNumber == 2) {
			if (x == 1) {
				return 1;
			} else {
				return 3;
			}
		} else if (edgeNumber == 3) {
			if (y == 0) {
				return 0;
			} else {
				return 2;
			}
		}
		return heading;
	}

	private boolean isThePoint(Node node1, Node current) {
		if (node1.getCorridor(1).getWeight() == 1) {
			if (current.getCorridor(3).getWeight() == 1) {
				return true;
			}
		} else if (node1.getCorridor(3).getWeight() == 1) {
			if (current.getCorridor(1).getWeight() == 1) {
				return true;
			}
		}
		return false;
	}

}
