package com.sputnik;

import lejos.nxt.comm.RConsole;
import lejos.robotics.navigation.Pose;
import lejos.robotics.subsumption.Behavior;

public class PathSweeping implements Behavior {

	private static boolean suppressed = false;
	public static boolean intersection = true;
	public static boolean ball = false;

	@Override
	public boolean takeControl() {
		// TODO Auto-generated method stub
		if (intersection == false) {
			return true;
		}
		return false;
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		while (!suppressed) {
			Sputnik.opp.setPose(new Pose(Sputnik.opp.getPose().getX(),
					Sputnik.opp.getPose().getY(), 0));
			align();
			Sputnik.opp.setPose(new Pose(Sputnik.opp.getPose().getX(),
					Sputnik.opp.getPose().getY(), 0));
			sweep();
			Sputnik.pilot.stop();
			Sputnik.opp.setPose(new Pose(Sputnik.opp.getPose().getX(),
					Sputnik.opp.getPose().getY(), 0));
			move();
			Sputnik.pilot.stop();
		}
		suppressed = false;
	}

	public void sweep() {
		// sweep left
		Sputnik.pilot.rotate(90, true);
		while (Sputnik.pilot.isMoving()) {
			if (suppressed) {
				return;
			}
			if (Sputnik.lightSensor.getLightValue() > Sputnik.lightThreshold) {
				Sputnik.pilot.stop();
			}
		}
		float leftAngle = Sputnik.opp.getPose().getHeading();
		Sputnik.pilot.rotate(-leftAngle);
		// sweep right
		Sputnik.pilot.rotate(-90, true);
		while (Sputnik.pilot.isMoving()) {
			if (suppressed) {
				return;
			}
			if (Sputnik.lightSensor.getLightValue() > Sputnik.lightThreshold) {
				Sputnik.pilot.stop();
			}
		}
		float rightAngle = Sputnik.opp.getPose().getHeading();
		Sputnik.pilot.rotate(-rightAngle);
		// align
		if (rightAngle > -30 && leftAngle < 30) {
			float alignAngle = (rightAngle + leftAngle) / 2;
			Sputnik.pilot.rotate(alignAngle);
		} else {
			System.out.println("Angle");
			intersection = true;
			Sputnik.pilot.travel(73, true);
			while (Sputnik.pilot.isMoving()) {
				Thread.yield();
			}
		}
	}

	private void move() {
		Sputnik.pilot.travel(30, true);
		while (Sputnik.pilot.isMoving()) {
			if (suppressed) {
				return;
			}
			Thread.yield();
		}
	}

	public void align() {
		if (Sputnik.lightSensor.getLightValue() < Sputnik.lightThreshold) {
			return;
		}
		float firstAngle = 0;
		float secondAngle = 0;
		Sputnik.pilot.rotate(70, true);
		while (Sputnik.pilot.isMoving()) {
			if (firstAngle == 0
					&& Sputnik.lightSensor.getLightValue() < Sputnik.lightThreshold) {
				firstAngle = Sputnik.opp.getPose().getHeading();
			}
			if (firstAngle != 0
					&& Sputnik.lightSensor.getLightValue() > Sputnik.lightThreshold) {
				Sputnik.pilot.stop();
				secondAngle = Sputnik.opp.getPose().getHeading();
			}
		}
		RConsole.println("First: " + firstAngle + " Second: " + secondAngle);
		if (firstAngle != 0 && secondAngle != 0) {
			float averageAngle = (firstAngle + secondAngle) / 2;
			float currentAngle = Sputnik.opp.getPose().getHeading();
			Sputnik.pilot.rotate(averageAngle - currentAngle);
			RConsole.println("current: " + currentAngle);
			return;
		}
		firstAngle = 0;
		secondAngle = 0;
		Sputnik.pilot.rotate(-70);
		Sputnik.pilot.rotate(-70, true);
		while (Sputnik.pilot.isMoving()) {
			if (firstAngle == 0
					&& Sputnik.lightSensor.getLightValue() < Sputnik.lightThreshold) {
				firstAngle = Sputnik.opp.getPose().getHeading();
			}
			if (firstAngle != 0
					&& Sputnik.lightSensor.getLightValue() > Sputnik.lightThreshold) {
				Sputnik.pilot.stop();
				secondAngle = Sputnik.opp.getPose().getHeading();
			}
		}
		RConsole.println("First: " + firstAngle + " Second: " + secondAngle);
		if (firstAngle != 0 && secondAngle != 0) {
			float averageAngle = (firstAngle + secondAngle) / 2;
			float currentAngle = Sputnik.opp.getPose().getHeading();
			Sputnik.pilot.rotate(averageAngle - currentAngle);
			RConsole.println("current: " + currentAngle);
			return;
		}
	}

	@Override
	public void suppress() {
		// TODO Auto-generated method stub
		suppressed = true;
	}

}
