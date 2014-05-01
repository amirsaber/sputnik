package com.sputnik;

import lejos.geom.Point;
import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;
import lejos.robotics.navigation.Pose;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.util.Delay;

public class Localizing1 implements Behavior {
	// double distance = Sputnik.opp.getPose().distanceTo(new Point(0, 0));

	private static boolean suppressed = false;
	private static boolean interFound = false;
	private static boolean Left_Right = false;
	private static boolean start = true;

	private static int moe = 40;
	private static int heading = 0; // if starting on corner, then must be 3
									// (Line 345)
	private static int sideCorner = 0;

	// number side 215.9 mm
	// letter side 177.8 mm
	private static float distance;
	private static double edgeA = 690;
	private static double edgeB = 1040;
	private static double edgeC = 460;
	private static double edgeD = 260;
	private static double edgeE = 230;
	private static double edgeF = 780;
	// private static double edgeA = 628;
	// private static double edgeB = 952;
	// private static double edgeC = 356;
	// private static double edgeD = 260;
	// private static double edgeE = 230;
	// private static double edgeF = 699;

	private static Pose pose1;
	private static Pose pose2;
	private static Pose pose3;

	private static boolean corner = false;
	private static boolean localComplete = false;

	private static Map localMap = new Map(10, 10, 5, 5, 0);

	private static Delay d = new Delay();

	@Override
	public void action() {
		pose1 = Sputnik.opp.getPose();
		checkSurrondings(heading);
		orientToLine();
		start = false;

		if (!corner) {
			moveRobot();
			Sputnik.pilot.rotate(180);
		}

		pose1 = new Pose(0, 0, 0);
		Sputnik.opp.setPose(pose1);

		corner = false;
		//PathSweeping.align();
		Sputnik.pilot.travel(50);
		Sputnik.opp.setPose(new Pose(Sputnik.opp.getPose().getX(), Sputnik.opp
				.getPose().getY(), 0));

		moveRobot();
		pose2 = Sputnik.opp.getPose();
		Sputnik.opp.setPose(pose2);
		// System.out.println("\nP2: " + pose1.getX() + ", " + pose1.getY());
		System.out.println("\nP2: " + pose2.getX() + ", " + pose2.getY());
		d.msDelay(2000);

		// compareDistances(SPLCurrentEdge(pose2.distanceTo(pose1.getLocation())));
		compareDistances(SPLCurrentEdge(Sputnik.opp.getPose().distanceTo(
				pose1.getLocation())));
		localComplete = true;
		System.out.println("END: " + localComplete);
		suppressed = true;
	}

	// WORK ON EDGES D AND E 4 AND 5
	private void compareDistances(int edge) {
		System.out.println("edge: " + edge);
		d.msDelay(3000);

		if (edge == 1) {
			if (turnLeft()) {
				Sputnik.robotMap.setPosition(new Node(0, 4));
				Sputnik.robotMap.setHeading(1);
			}

			else {
				Sputnik.robotMap.setPosition(new Node(3, 4));
				Sputnik.robotMap.setHeading(3);
			}
		}

		else if (edge == 2) {
			if (turnLeft()) {
				Sputnik.robotMap.setPosition(new Node(3, 4));
				Sputnik.robotMap.setHeading(0);
			}

			else {
				Sputnik.robotMap.setPosition(new Node(3, 0));
				Sputnik.robotMap.setHeading(2);
			}
		}

		else if (edge == 3) {
			if (turnLeft()) {
				Sputnik.robotMap.setPosition(new Node(3, 0));
				Sputnik.robotMap.setHeading(3);
			}

			else {
				Sputnik.robotMap.setPosition(new Node(1, 0));
				Sputnik.robotMap.setHeading(1);
			}
		}

		else if (edge == 6) {
			if (turnLeft()) {
				Sputnik.robotMap.setPosition(new Node(0, 1));
				Sputnik.robotMap.setHeading(2);
			}

			else {
				Sputnik.robotMap.setPosition(new Node(0, 4));
				Sputnik.robotMap.setHeading(0);
			}
		}

		// System.out.println("\nPoint: " + Sputnik.opp.getPose().getX() + ", "
		// + Sputnik.opp.getPose().getY());
		System.out.println("\nHead: " + heading);
		d.msDelay(3000);
	}

	private void checkSurrondings(int heading) {
		if (localMap.getPosition().getCorridor(0).getNode() != null) {
			localMap.getPosition().getCorridor(0).setWeight(9999);
		}

		if (localMap.getPosition().getCorridor(1).getNode() != null) {
			localMap.getPosition().getCorridor(1).setWeight(9999);
		}

		if (localMap.getPosition().getCorridor(2).getNode() != null) {
			localMap.getPosition().getCorridor(2).setWeight(9999);
		}

		if (localMap.getPosition().getCorridor(3).getNode() != null) {
			localMap.getPosition().getCorridor(3).setWeight(9999);
		}

		Sputnik.pilot.rotate(-360, true);

		while (Sputnik.pilot.isMoving()) {
			double angle = Sputnik.opp.getPose().getHeading();
			int temp_heading = heading;

			if (angle <= -45 && angle > -135) {
				temp_heading = (heading + 1) % 4;
			}

			else if (angle <= -135 || angle > 135) {
				temp_heading = (heading + 2) % 4;
			}

			else if (angle <= 135 && angle > 45) {
				temp_heading = (heading + 3) % 4;
			}

			else if (angle <= 45 || angle > -45) {
				temp_heading = (heading + 4) % 4;
			}

			if (Sputnik.lightSensor.getLightValue() < Sputnik.lightThreshold) {
				if (localMap.getPosition().getCorridor(temp_heading).getNode() != null) {
					localMap.getPosition().getCorridor(temp_heading)
							.setWeight(1);
					// Sputnik.robotMap.getPosition().getCorridor(temp_heading).getNode().getCorridor((temp_heading
					// + 2) % 4).setWeight(1);
				}
			}
			Thread.yield();
		}
		checkIntersection();
	}

	private void checkIntersection() {
		// System.out.println("\nIntersect\nC0: " +
		// localMap.getPosition().getCorridor(0).getWeight() + "C1: " +
		// localMap.getPosition().getCorridor(1).getWeight() + "C2: " +
		// localMap.getPosition().getCorridor(2).getWeight() + "C3: " +
		// localMap.getPosition().getCorridor(3).getWeight());
		// d.msDelay(5000);

		pose3 = new Pose(Sputnik.opp.getPose().getX(), Sputnik.opp.getPose()
				.getY(), 0);

		checkCrossSection();

		if (!checkTSection())
			isCorner();
		/*
		 * if(localMap.getPosition().getCorridor((heading + 1) % 4).getWeight()
		 * == 1 && !start) { distance = pose3.distanceTo(pose1.getLocation());
		 * 
		 * if(distance < 250) { Left_Right = true; Sputnik.pilot.rotate(90);
		 * heading = 3; }
		 * 
		 * else if(corner) heading = 2; }
		 * 
		 * else if(localMap.getPosition().getCorridor((heading + 1) %
		 * 4).getWeight() == 1) { Pose pose4 = new
		 * Pose(Sputnik.opp.getPose().getX(),Sputnik.opp.getPose().getY(),0);
		 * distance = pose4.distanceTo(pose3.getLocation());
		 * 
		 * if(distance < 250) { localComplete = true;
		 * Sputnik.robotMap.setPosition(new Node(0, 1));
		 * Sputnik.robotMap.setHeading(3); suppressed = true; }
		 * 
		 * else if(corner) heading = 0; }
		 */
	} // end of checkIntersection()

	private void checkCrossSection() {
		Node current = localMap.getPosition();
		int corridors = 0;

		for (int i = 0; i < 4; i++) {
			Corridor c = current.getCorridor(i);
			if (c.getWeight() == 1)
				corridors++;
		}

		if (corridors == 4) {
			localComplete = true;
			Sputnik.robotMap.setPosition(new Node(1, 1));
			Sputnik.robotMap.setHeading(0);
			suppressed = true;
		}

		else if (localMap.getPosition()
				.getCorridor(Math.abs((sideCorner + 2) % 4)).getWeight() == 1) {
			localComplete = true;
			Sputnik.robotMap.setPosition(new Node(1, 1));

			if (sideCorner == heading + 1)
				heading = 3;
			else
				heading = 0;

			// if(heading == 1 || heading == 3)
			// heading = Math.abs((heading + 2) % 4);

			Sputnik.robotMap.setHeading(heading);
			suppressed = true;
			System.out.println("Cross Complete\nH: " + heading);
			d.msDelay(5000);
		}
	}

	private Boolean checkTSection() {
		Node current = localMap.getPosition();
		int corridors = 0;

		for (int i = 0; i < 4; i++) {
			Corridor c = current.getCorridor(i);
			if (c.getWeight() == 1)
				corridors++;
		}

		if (corridors == 3)
			return true;

		return false;
	}

	private void isCorner() {
		if (localMap.getPosition().getCorridor(0).getWeight() == 1
				&& (localMap.getPosition().getCorridor(1).getWeight() == 1 || localMap
						.getPosition().getCorridor(3).getWeight() == 1)) {
			corner = true;
		}

		else if (localMap.getPosition().getCorridor(2).getWeight() == 1
				&& (localMap.getPosition().getCorridor(1).getWeight() == 1 || localMap
						.getPosition().getCorridor(3).getWeight() == 1))
			corner = true;
	}

	private void moveRobot() {
		while (corner == false) {
			while (!PathSweeping.intersection) {
				Sputnik.pilot.travel(30);
				Sputnik.pilot.stop();
				Sputnik.opp.setPose(new Pose(Sputnik.opp.getPose().getX(),
						Sputnik.opp.getPose().getY(), 0));
				//PathSweeping.align();
				Sputnik.opp.setPose(new Pose(Sputnik.opp.getPose().getX(),
						Sputnik.opp.getPose().getY(), 0));
				//PathSweeping.sweep();
				Sputnik.pilot.stop();
				Sputnik.opp.setPose(new Pose(Sputnik.opp.getPose().getX(),
						Sputnik.opp.getPose().getY(), 0));
			} // end of while(!PathSweeping.intersection)

			// if(PathSweeping.intersection == true &&
			// (Sputnik.lightSensor.getLightValue() > Sputnik.lightThreshold) )
			checkSurrondings(heading);

			PathSweeping.intersection = false;
			if (!corner)
				Sputnik.pilot.travel(150);

		} // end of while(!isCorner())
	} // end of moveRobot()

	private void orientToLine() {
		// d.msDelay(10000);

		if (corner) {
			while (localMap.getPosition().getCorridor(heading).getWeight() == 9999) {
				Sputnik.pilot.rotate(90);
				Sputnik.pilot.stop();
				heading = (heading - 1) % 4;
				if (heading < 0)
					heading = 3;
			}
		}

		else if (localMap.getPosition().getCorridor(heading).getWeight() == 9999) {
			Sputnik.pilot.rotate(90);
			Sputnik.pilot.stop();
			heading = (heading + 1) % 4;
		}

		// on T intersection, rotates to straight line
		else if (checkTSection()) {
			// if opposite side line is also weighted as 1, then robot is
			// already on the straight line.
			if (localMap.getPosition().getCorridor(((heading + 2) % 4))
					.getWeight() != 1) {
				Sputnik.pilot.rotate(90);
				heading = (heading + 1) % 4;
			}
		}

		if (localMap.getPosition().getCorridor((heading + 1) % 4).getWeight() == 1)
			sideCorner = (heading + 2) % 4;

		else
			sideCorner = Math.abs((heading + 2) % 4);
	} // end of orientToLine()

	private int SPLCurrentEdge(double distance) {
		// double distance = Sputnik.opp.getPose().distanceTo(new Point(0, 0))

		System.out.println("DIS: " + distance);
		d.msDelay(5000);

		if (distance < (edgeA + moe) && distance > (edgeA - moe))
			return 1;

		else if (distance < (edgeB + moe) && distance > (edgeB - moe))
			return 2;

		else if (distance < (edgeC + moe) && distance > (edgeC - moe))
			return 3;

		else if (distance < (edgeD + moe) && distance > (edgeD - moe))
			return 4;

		else if (distance < (edgeE + moe) && distance > (edgeE - moe))
			return 5;

		else if (distance < (edgeF + moe) && distance > (edgeF - moe))
			return 6;

		return 0;
	}

	@Override
	public void suppress() {
		suppressed = true;
	} // end of suppress()

	@Override
	public boolean takeControl() {
		// if corner is detected, take control
		if (!localComplete) {
			return true;
		}
		return false;
	} // end of takeControl()

	private boolean turnLeft() {
		if (localMap.getPosition().getCorridor(1).getWeight() == 1
				&& localMap.getPosition().getCorridor(0).getWeight() == 1)
			return true;

		else if (localMap.getPosition().getCorridor(3).getWeight() == 1
				&& localMap.getPosition().getCorridor(2).getWeight() == 1)
			return true;

		return false;
	}
}
