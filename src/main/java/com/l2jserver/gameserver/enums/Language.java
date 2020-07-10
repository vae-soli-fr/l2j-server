package com.l2jserver.gameserver.enums;

public enum Language {

	COMMON(""),
	ELVEN("*elfique* "),
	DARKELF("*sombre* "),
	ORCISH("*orc* "),
	DWARVEN("*nain* "),
	KAMAEL("*kamael* ");

	private final String _didascalie;

	private Language(String didascalie) {
		_didascalie = didascalie;
	}

	@Override
	public String toString() {
		return _didascalie;
	}

}
