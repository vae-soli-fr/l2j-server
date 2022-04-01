package com.l2jserver.api;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

/**
 * @author Melua
 *
 */
public class BytesResponse implements ApiResponse<byte[]>
{
	private int _status;
	private byte[] _entity;

	@Override
	public void setStatus(int status)
	{
		this._status = status;
	}

	@Override
	public void processEntity(HttpEntity entity) throws IOException
	{
		this._entity = EntityUtils.toByteArray(entity);
	}

	@Override
	public int getStatus()
	{
		return _status;
	}

	@Override
	public byte[] getEntity()
	{
		return _entity;
	}

}
