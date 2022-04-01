package com.l2jserver.api;

import org.apache.http.HttpEntity;

/**
 * @author Melua
 *
 */
public class VoidResponse implements ApiResponse<Void>
{
	private int _status;

	@Override
	public void setStatus(int status)
	{
		this._status = status;
	}

	@Override
	public int getStatus()
	{
		return _status;
	}

	@Override
	public void processEntity(HttpEntity entity)
	{
		// Nothing to do
	}

	@Override
	public Void getEntity()
	{
		return null;
	}

}
