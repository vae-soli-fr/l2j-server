package com.l2jserver.loginserver.model.data;

/**
 * @author Melua
 */
public final class ForumInfo
{
	private final String _username;
	private final String _fakepass;
	private final int _loginstatus;

	public ForumInfo(final String username, final String fakepass, final int loginstatus)
	{
		_username = username;
		_fakepass = fakepass;
		_loginstatus = loginstatus;
	}

	public String getUserName()
	{
		return _username;
	}

	public String getFakePass()
	{
		return _fakepass;
	}

	public int getLoginStatus()
	{
		return _loginstatus;
	}

}