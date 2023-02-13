package com.l2jserver.gameserver;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E>
{
	private final NavigableMap<Double, E> _map = new TreeMap<>();
	private final Random _random;
	private double _total = 0;
	
	public RandomCollection()
	{
		this(new Random());
	}
	
	public RandomCollection(Random random)
	{
		_random = random;
	}
	
	public RandomCollection<E> add(double weight, E result)
	{
		if (weight <= 0) {
			return this;
		}
		_total += weight;
		_map.put(_total, result);
		return this;
	}
	
	public E next()
	{
		double value = _random.nextDouble() * _total;
		return _map.higherEntry(value).getValue();
	}
}
