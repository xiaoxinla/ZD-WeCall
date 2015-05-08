package org.lucene.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

/**
 * 分词器实现
 * @author XF
 * 2014-5-3
 */
public class WeCallAnalyzer {
	
	public WeCallAnalyzer(Analyzer a)
	{
		analyzer=a;
	}
	/*
	 * getAnalyzeString方法用于输入一个字符串，分词器按照一定方法分好词，然后在字符串之间插空格，然后返回一个插好空格的字符串
	 * 例如str="广州市中山大学至善园2号612"
	 * 分词后：str="中山市   中山大学   至善    园   2   号    612"
	 * 这个方法用于INSERT、UPDATE时候的分词
	 */
	public String getAnalyzedString(String str)
	{
		String result="";
		try 
		{
			TokenStream stream=analyzer.tokenStream("content", new StringReader(str));
			CharTermAttribute cta=stream.addAttribute(CharTermAttribute.class);
			while(stream.incrementToken())
			{
				result+=cta+" ";
			}
			
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/*
	 * getTokenList方法会返回一个Token类的列表，其中Token用于记录每个分词的value以及它在原字符串中的偏移量
	 * */
	@SuppressWarnings("unused")
	public List<String> getTokenList(String str)
	{
		List<String> list=new ArrayList<String>();
		try 
		{
			TokenStream stream=analyzer.tokenStream("content", new StringReader(str));
			//CharTermAttribute 字符值
			CharTermAttribute cta=stream.addAttribute(CharTermAttribute.class);
			//OffsetAttribute用于记录字符值的起始位置和终止位置
			OffsetAttribute oa=stream.addAttribute(OffsetAttribute.class);
			while(stream.incrementToken())
			{
				//Token token=new Token(cta+"",oa.startOffset(),oa.endOffset());
				list.add(new String(cta + ""));
			}
			
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	private Analyzer analyzer;
}
