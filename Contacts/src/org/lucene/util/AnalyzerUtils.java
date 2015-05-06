package org.lucene.util;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class AnalyzerUtils 
{
	public static void displayToken(String str,Analyzer a)
	{
		try 
		{
			TokenStream stream=a.tokenStream("content", new StringReader(str));
			CharTermAttribute cta=stream.addAttribute(CharTermAttribute.class);
			while(stream.incrementToken())
			{
				System.out.print("["+cta+"]");
			}
			System.out.println();
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
