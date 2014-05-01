package com.sputnik;

import lejos.nxt.ColorSensor.Color;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.robotics.subsumption.Behavior;

public class BallDetecting implements Behavior {

	int count_red = 0;
	public static int detectedBalls = 0;

	@Override
	public boolean takeControl() {
		// TODO Auto-generated method stub
		Color colorSensed = Sputnik.colorSensor.getColor();
		int blue = colorSensed.getBlue();
		int red = colorSensed.getRed();
		// RConsole.println("Blue: " + String.valueOf(blue) + " Red: "
		// + String.valueOf(red));
		if (red > 80) {
			return true;
		} else if (blue > 80) {
			return true;
		}
		return false;
	}

	@Override
	public void action() {
		RConsole.println("Ball Has been detected");
		// TODO Auto-generated method stub
		int increased = 0;
		Color colorSensed = Sputnik.colorSensor.getColor();
		int blue = colorSensed.getBlue();
		int red = colorSensed.getRed();
		while (blue > 80 || red > 80) {
			PathFollowing.ball = true;
			Sputnik.pilot.stop();
			detectedBalls++;
			if (blue > 80) {
				Sound.buzz();
				Sputnik.robotMap.setBall();

			} else if (red > 80) {
				Sound.beepSequenceUp();
				Sputnik.robotMap.setBall();
				if (increased == 0) {
					count_red++;
					if (count_red == 1) {
						System.exit(0);
					}
					increased = 1;
				}
			}
			Thread.yield();
			colorSensed = Sputnik.colorSensor.getColor();
			blue = colorSensed.getBlue();
			red = colorSensed.getRed();
		}
	}

	@Override
	public void suppress() {
		// TODO Auto-generated method stub

	}

}
