package com.sputnik;

import lejos.geom.Point;
import lejos.nxt.comm.RConsole;
import lejos.robotics.navigation.Pose;
import lejos.robotics.subsumption.Behavior;

public class PathPlanning implements Behavior {
	private boolean suppressed = false;
	private float previousRotate = 0;

	@Override
	public boolean takeControl() {

		double distance = Sputnik.opp.getPose().distanceTo(new Point(0, 0));
		Thread.yield();
		//System.out.println("distance: " + distance);
		if (Sputnik.robotMap.getHeading() == 0
				|| Sputnik.robotMap.getHeading() == 2) {
			if (distance > Sputnik.intersectionDistance_height) {
				return true;
			}
		}
		if (Sputnik.robotMap.getHeading() == 1
				|| Sputnik.robotMap.getHeading() == 3) {
			if (distance > Sputnik.intersectionDistance_width) {
				return true;
			}
		}
		if (PathSweeping.intersection) {
			return true;
		}
		return false;
	}

	@Override
	public void action() {
		Sputnik.pilot.stop();
		RConsole.println("Intersection Has been detected");
		if (suppressed) {
			suppressed = false;
			return;
		}
		// TODO Auto-generated method stub
		Sputnik.pilot.stop();
		Sputnik.opp.setPose(new Pose(0, 0, 0));
		int heading = Sputnik.robotMap.getHeading();
		int status = Sputnik.robotMap.getPosition().getStatus();
		if (status != 1 && status != 4) {
			checkSurrondings(heading);
		}

		checkTSection();
		int cross = checkCrossSection();
		int nextHeading = 0;
		if (cross == 0) {
			nextHeading = (heading + 1) % 4;
			RConsole.println("Cross From left");
		} else if (cross == 1) {

			RConsole.println("Cross From down");
			nextHeading = heading;

		} else if (cross == -1) {
			nextHeading = Sputnik.robotMap.getNextHeading();
		}
		//RConsole.print(Sputnik.robotMap.drawMap());
		System.out.println("H:" + heading + " N:" + nextHeading);
		if (heading == 0) {
			if (nextHeading == 3) {
				Sputnik.pilot.rotate(90);
			}
			if (nextHeading == 1) {
				Sputnik.pilot.rotate(-90);
			}
			if (nextHeading == 2) {
				Sputnik.pilot.rotate(-180);
			}
		}
		if (heading == 1) {
			if (nextHeading == 0) {
				Sputnik.pilot.rotate(90);
			}
			if (nextHeading == 2) {
				Sputnik.pilot.rotate(-90);
			}
			if (nextHeading == 3) {
				Sputnik.pilot.rotate(-180);
			}
		}
		if (heading == 2) {
			if (nextHeading == 1) {
				Sputnik.pilot.rotate(90);
			}
			if (nextHeading == 3) {
				Sputnik.pilot.rotate(-90);
			}
			if (nextHeading == 0) {
				Sputnik.pilot.rotate(-180);
			}
		}
		if (heading == 3) {
			if (nextHeading == 2) {
				Sputnik.pilot.rotate(90);
			}
			if (nextHeading == 0) {
				Sputnik.pilot.rotate(-90);
			}
			if (nextHeading == 1) {
				Sputnik.pilot.rotate(-180);
			}
		}
		if (suppressed) {
			suppressed = false;
			return;
		}
		Sputnik.robotMap.setPosition(Sputnik.robotMap.getPosition()
				.getCorridor(nextHeading).getNode());
		Sputnik.robotMap.setHeading(nextHeading);
		// Sputnik.pilot.rotate(360);
		previousRotate = Sputnik.opp.getPose().getHeading();
		Sputnik.opp.setPose(new Pose(0, 0, 0));
		PathSweeping.intersection = false;
		Thread.yield();
		suppressed = false;
	}

	public void checkSurrondings(int heading) {
		if (Sputnik.robotMap.getPosition().getCorridor(0).getNode() != null) {
			Sputnik.robotMap.getPosition().getCorridor(0).setWeight(9999);
			Sputnik.robotMap.getPosition().getCorridor(0).getNode()
					.getCorridor(2).setWeight(9999);
		}
		if (Sputnik.robotMap.getPosition().getCorridor(1).getNode() != null) {
			Sputnik.robotMap.getPosition().getCorridor(1).setWeight(9999);
			Sputnik.robotMap.getPosition().getCorridor(1).getNode()
					.getCorridor(3).setWeight(9999);
		}
		if (Sputnik.robotMap.getPosition().getCorridor(2).getNode() != null) {
			Sputnik.robotMap.getPosition().getCorridor(2).setWeight(9999);
			Sputnik.robotMap.getPosition().getCorridor(2).getNode()
					.getCorridor(0).setWeight(9999);
		}
		if (Sputnik.robotMap.getPosition().getCorridor(3).getNode() != null) {
			Sputnik.robotMap.getPosition().getCorridor(3).setWeight(9999);
			Sputnik.robotMap.getPosition().getCorridor(3).getNode()
					.getCorridor(1).setWeight(9999);
		}
		RConsole.println("check sourendings");
		Sputnik.pilot.rotate(-360, true);
		while (Sputnik.pilot.isMoving()) {
			double angle = Sputnik.opp.getPose().getHeading();
			// RConsole.println("Angle:" + String.valueOf(angle));
			int temp_heading = heading;
			if (angle <= -45 && angle > -135) {
				temp_heading = (heading + 1) % 4;
			} else if (angle <= -135 || angle > 135) {
				temp_heading = (heading + 2) % 4;
			} else if (angle <= 135 && angle > 45) {
				temp_heading = (heading + 3) % 4;
			} else if (angle <= 45 || angle > -45) {
				temp_heading = (heading + 4) % 4;
			}
			// RConsole.println("TH: " + String.valueOf(temp_heading));
			if (Sputnik.lightSensor.getLightValue() < Sputnik.lightThreshold) {
				if (Sputnik.robotMap.getPosition().getCorridor(temp_heading)
						.getNode() != null) {
					Sputnik.robotMap.getPosition().getCorridor(temp_heading)
							.setWeight(1);
					Sputnik.robotMap.getPosition().getCorridor(temp_heading)
							.getNode().getCorridor((temp_heading + 2) % 4)
							.setWeight(1);
				}
				if (Sputnik.sonarSensor.getDistance() < 30) {
					if (Sputnik.robotMap.getPosition()
							.getCorridor(temp_heading).getNode() != null) {
						Sputnik.robotMap.getPosition()
								.getCorridor(temp_heading).getNode()
								.setStatus(5);
					}
				} /*
				 * else if (Sputnik.sonarSensor.getDistance() < 50) { if
				 * (Sputnik.robotMap.getPosition()
				 * .getCorridor(temp_heading).getNode() != null) { if
				 * (Sputnik.robotMap.getPosition()
				 * .getCorridor(temp_heading).getNode()
				 * .getCorridor(temp_heading).getNode() != null) {
				 * 
				 * Sputnik.robotMap.getPosition()
				 * .getCorridor(temp_heading).getNode()
				 * .getCorridor(temp_heading).getNode() .setStatus(5); } } }
				 */
			}
			Thread.yield();
		}
		suppressed = false;
	}

	private int checkCrossSection() {
		Node current = Sputnik.robotMap.getPosition();
		int corridors = 0;
		for (int i = 0; i < 4; i++) {
			Corridor c = current.getCorridor(i);
			if (c.getWeight() == 1) {
				corridors++;
			}
		}
		if (corridors == 4) {
			if (previousRotate < 0) {
				return 0;
			} else if( previousRotate >0 ){
				return 1;
			}
		}
		return -1;
	}

	private void checkTSection() {
		Node current = Sputnik.robotMap.getPosition();
		int corridors = 0;
		for (int i = 0; i < 4; i++) {
			Corridor c = current.getCorridor(i);
			if (c.getWeight() == 1) {
				corridors++;
			}
		}
		if (corridors == 3 && BallDetecting.detectedBalls == 0) {
			RConsole.println("T section");
			for (int i = 0; i < 4; i++) {
				Corridor c = current.getCorridor(i);
				Corridor oposit = current.getCorridor((i + 2) % 4);
				if (c.getWeight() == 1 && oposit.getWeight() > 1) {
					c.getNode().getCorridor(0).setWeight(9999);
					c.getNode().getCorridor(0).getNode().getCorridor(2)
							.setWeight(9999);
					c.getNode().getCorridor(1).setWeight(9999);
					c.getNode().getCorridor(1).getNode().getCorridor(3)
							.setWeight(9999);
					c.getNode().getCorridor(2).setWeight(9999);
					c.getNode().getCorridor(2).getNode().getCorridor(0)
							.setWeight(9999);
					c.getNode().getCorridor(3).setWeight(9999);
					c.getNode().getCorridor(3).getNode().getCorridor(1)
							.setWeight(9999);
				}
			}
		}
	}

	@Override
	public void suppress() {
		// TODO Auto-generated method stub
		suppressed = true;

	}

}
