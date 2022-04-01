package com.l2jserver.api;

import java.io.IOException;

import org.apache.http.HttpEntity;

/**
 * @author Melua
 *
 */
public interface ApiResponse<T>
{
	void setStatus(int status);
	int getStatus();

	void processEntity(HttpEntity entity) throws IOException;
	T getEntity();

}
