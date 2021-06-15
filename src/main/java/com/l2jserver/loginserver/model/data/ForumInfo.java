package com.l2jserver.loginserver.model.data;

import java.util.Base64;

/**
 * @author Melua
 */
public final class ForumInfo
{
	private final String _username;
	private final String _fakepass;
	private final int _loginstatus;

	public ForumInfo()
	{
		this(-1);
	}

	public ForumInfo(final int loginstatus)
	{
		this(null, loginstatus);
	}

	public ForumInfo(final String username, final int loginstatus)
	{
		_username = username;
		_fakepass = username != null ? Base64.getEncoder().encodeToString(username.getBytes()) : null;
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