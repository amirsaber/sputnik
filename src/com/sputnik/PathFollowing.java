package com.sputnik;

import lejos.nxt.comm.RConsole;
import lejos.robotics.subsumption.Behavior;

public class PathFollowing implements Behavior {
	private boolean suppressed = false;
	public static boolean intersection = false;
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
		RConsole.println("Go Forward");
		while (!suppressed) {
			// align into the path before moving
			alignPath();
			alignPath();
			Sputnik.pilot.stop();
			Sputnik.pilot.travel(999999, true);
			while (Sputnik.pilot.isMoving()) {
				if (suppressed) {
					Sputnik.pilot.stop();
					break;
				}
				Thread.yield();
			}
		}
		suppressed = false;
	}

	private void alignPath() {
		// RConsole.println("Align Path");
		// off the line
		if (Sputnik.lightSensor.getLightValue() > Sputnik.lightThreshold) {
			// RConsole.println("off the line");
			// RConsole.println("Try Left");
			// try to find the line on left
			boolean lineDetected = false;
			Sputnik.pilot.rotate(-45, true);
			while (Sputnik.pilot.isMoving()) {
				double first_angle = 0;
				double second_angle = 0;
				if (Sputnik.lightSensor.getLightValue() < Sputnik.lightThreshold) {
					first_angle = Sputnik.opp.getPose().getHeading();
					lineDetected = true;
				}
				if (lineDetected
						&& Sputnik.lightSensor.getLightValue() > Sputnik.lightThreshold) {
					Sputnik.pilot.stop();
					second_angle = Sputnik.opp.getPose().getHeading();
					// RConsole.println("first angle: "+first_angle+" second angle: "+second_angle);
					Sputnik.pilot.rotate((first_angle - second_angle) / 2);
				}
				Thread.yield();
			}
			// if line was on robot left
			if (lineDetected) {
				Sputnik.pilot.stop();
				Sputnik.pilot.travel(40, true);
				while (Sputnik.pilot.isMoving()) {
					if (suppressed) {
						Sputnik.pilot.stop();
						return;
					}
					Thread.yield();
				}
				return;
			}
			// now check the line on right
			// RConsole.println("Try Right");
			Sputnik.pilot.rotate(100, true);
			while (Sputnik.pilot.isMoving()) {
				double first_angle = 0;
				double second_angle = 0;
				if (Sputnik.lightSensor.getLightValue() < Sputnik.lightThreshold) {
					first_angle = Sputnik.opp.getPose().getHeading();
					lineDetected = true;
				}
				if (lineDetected
						&& Sputnik.lightSensor.getLightValue() > Sputnik.lightThreshold) {
					Sputnik.pilot.stop();
					second_angle = Sputnik.opp.getPose().getHeading();
					// RConsole.println("first angle: "+first_angle+" second angle: "+second_angle);
					Sputnik.pilot.rotate(-(second_angle - first_angle) / 2);
				}
				Thread.yield();
			}
			Sputnik.pilot.stop();
			Sputnik.pilot.travel(40, true);
			while (Sputnik.pilot.isMoving()) {
				if (suppressed) {
					Sputnik.pilot.stop();
					return;
				}
				Thread.yield();
			}
		}
		// on the line
		else {
			// RConsole.println("on the line");
			double left_angle = 0;
			double right_angle = 0;
			// find angle of left
			Sputnik.pilot.rotate(-45, true);
			while (Sputnik.pilot.isMoving()) {
				if (Sputnik.lightSensor.getLightValue() > Sputnik.lightThreshold) {
					Sputnik.pilot.stop();
					left_angle = Sputnik.opp.getPose().getHeading();
					Sputnik.pilot.rotate(-left_angle);
				}
				Thread.yield();
			}
			// now on the right
			Sputnik.pilot.rotate(90, true);
			while (Sputnik.pilot.isMoving()) {
				if (Sputnik.lightSensor.getLightValue() > Sputnik.lightThreshold) {
					Sputnik.pilot.stop();
					right_angle = Sputnik.opp.getPose().getHeading();
					Sputnik.pilot.rotate(-right_angle);
				}
				Thread.yield();
			}
			// RConsole.println("left angle: "+left_angle);
			// RConsole.println("right angle: "+right_angle);
			// go to center
			double angle = (left_angle + right_angle) / 2;
			Sputnik.pilot.rotate(angle);
			Sputnik.pilot.stop();
			Sputnik.pilot.travel(40, true);
			while (Sputnik.pilot.isMoving()) {
				if (suppressed) {
					Sputnik.pilot.stop();
					return;
				}
				Thread.yield();
			}
		}
	}

	@Override
	public void suppress() {
		// TODO Auto-generated method stub
		suppressed = true;
	}

}
