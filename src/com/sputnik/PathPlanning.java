package com.sputnik;

import lejos.geom.Point;
import lejos.nxt.comm.RConsole;
import lejos.robotics.navigation.Pose;
import lejos.robotics.subsumption.Behavior;

public class PathPlanning implements Behavior {
	private boolean suppressed = false;

	@Override
	public boolean takeControl() {

		double distance = Sputnik.opp.getPose().distanceTo(new Point(0, 0));
		Thread.yield();
		// System.out.println("distance: " + distance);
		if (Localizing.cornerCount != 2) {
			return false;
		}
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
		if (PathSweeping.intersection && Localizing.cornerCount == 2) {
			return true;
		}
		return false;
	}

	@Override
	public void action() {
		PathSweeping.intersection = false;
		Sputnik.opp.setPose(new Pose(0, 0, 0));
		Sputnik.pilot.stop();
		System.out.println("Intersection Has been detected");
		// TODO Auto-generated method stub
		int heading = Sputnik.robotMap.getHeading();
		int status = Sputnik.robotMap.getPosition().getStatus();
		if (status != 1 && status != 4) {
			checkSurrondings(heading);
		}
		int nextHeading = Sputnik.robotMap.getNextHeading();
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
		} else if (heading == 1) {
			if (nextHeading == 0) {
				Sputnik.pilot.rotate(90);
			}
			if (nextHeading == 2) {
				Sputnik.pilot.rotate(-90);
			}
			if (nextHeading == 3) {
				Sputnik.pilot.rotate(-180);
			}
		} else if (heading == 2) {
			if (nextHeading == 1) {
				Sputnik.pilot.rotate(90);
			}
			if (nextHeading == 3) {
				Sputnik.pilot.rotate(-90);
			}
			if (nextHeading == 0) {
				Sputnik.pilot.rotate(-180);
			}
		} else if (heading == 3) {
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
		Sputnik.robotMap.getPosition().setStatus(1);
		Sputnik.robotMap.setPosition(Sputnik.robotMap.getPosition()
				.getCorridor(nextHeading).getNode());
		Sputnik.robotMap.setHeading(nextHeading);
		Sputnik.opp.setPose(new Pose(0, 0, 0));
		PathSweeping.intersection = false;
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
			if (Sputnik.lightSensor.getLightValue() < Sputnik.lightThreshold) {
				if (Sputnik.robotMap.getPosition().getCorridor(temp_heading)
						.getNode() != null) {
					Sputnik.robotMap.getPosition().getCorridor(temp_heading)
							.setWeight(1);
					Sputnik.robotMap.getPosition().getCorridor(temp_heading)
							.getNode().getCorridor((temp_heading + 2) % 4)
							.setWeight(1);
				}
			}
			Thread.yield();
		}
	}

	@Override
	public void suppress() {
		// TODO Auto-generated method stub
		PathSweeping.intersection = false;
		suppressed = true;

	}

}
