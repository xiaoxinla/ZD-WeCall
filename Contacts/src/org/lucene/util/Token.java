package org.lucene.util;

public class Token {
	public Token(String value,int startPos,int endPos)
	{
		this.value=value;
		this.startPos=startPos;
		this.endPos=endPos;
	}
	public String getValue()
	{
		return value;
	}
	public int getStartPos()
	{
		return startPos;
	}
	public int getEndPos()
	{
		return endPos;
	}
	private String value;
	private int startPos;
	private int endPos;
	
}
