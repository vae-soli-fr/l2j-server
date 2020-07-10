package com.l2jserver.gameserver.enums;

public enum Volume {

	WHISPER(100, "*murmure* "),
	DEFAULT(1250, ""),
	SHOUT(2900, "*crie* ");

	private final int _radius;
	private final String _didascalie;

	private Volume(int radius, String didascalie) {
		_radius = radius;
		_didascalie = didascalie;
	}

	public int getRadius() {
		return _radius;
	}

	@Override
	public String toString() {
		return _didascalie;
	}

}
