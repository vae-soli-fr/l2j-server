package com.l2jserver.api;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

/**
 * @author Melua
 *
 */
public class StringResponse implements ApiResponse<String>
{
	private int _status;
	private String _entity;

	@Override
	public void setStatus(int status)
	{
		this._status = status;
	}

	@Override
	public void processEntity(HttpEntity entity) throws IOException
	{
		this._entity = EntityUtils.toString(entity);
	}

	@Override
	public int getStatus()
	{
		return _status;
	}

	@Override
	public String getEntity()
	{
		return _entity;
	}

}
