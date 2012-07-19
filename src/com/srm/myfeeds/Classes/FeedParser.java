package com.srm.myfeeds.Classes;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class FeedParser {
		
	private URL rssUrl;
	
	//if true, this stops reading and returns null
	private boolean stop = false;
	
	//How many items load by page
	private int itemsPerPage;
	
	//NodeList
	NodeList content = null;
	
	
	/**
	 * Start RSS url
	 * @param String RSS url
	 */
	public FeedParser(String url){			
		
		try{
			this.rssUrl = new URL(url);
		}catch(MalformedURLException e){
			
		}
	}
	
	
	/**
	 * Gets Feed title
	 * @return  String  title
	 */
	public String getTitle(){
	
		String title = null;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try{			
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(this.getInputStream());
			Element root = dom.getDocumentElement();			
			NodeList titleNode = root.getElementsByTagName("title");
			
			title = titleNode.item(0).getTextContent();
			
		}catch(Exception e){}		
		
		return title;
	}	
	
	
	/**
	 * Get feed items
	 * @param int startItem
	 * @return  ArrayList<Entry>
	 */
	public ArrayList<Entry> read(int startItem){
		
		ArrayList<Entry> entries = new ArrayList<Entry>();
		
		getRemoteContents();		
		
		if (content != null){
			int endItem = startItem + itemsPerPage;		
			if (endItem >= content.getLength()) endItem = content.getLength();				
			
			for (int i=startItem;i<endItem;i++){				
				
				//Force task stop				
				if (stop) return null;				
				
				Node item = content.item(i);					
				
				entries.add(this.parseItem(item));
			}
		}else{
			entries = null;
		}
		
		return entries;
	}
	
	/**
	 * Connect & get feed contents 
	 */
	private void getRemoteContents(){		
		
		//if content has been downloaded, avoid downloading it again
		if (content != null) return;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try{			
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(this.getInputStream());
			Element root = dom.getDocumentElement();			
			content = root.getElementsByTagName("item");
			
		}catch(Exception e){}
	}
	
	/**
	 * Get info of item
	 * @param item
	 * @return entry
	 */
	private Entry parseItem(Node item){
		
		Entry entry = new Entry();		
		
		NodeList dato = item.getChildNodes();				
		
		//Each item's tag
		for (int j=0;j<dato.getLength();j++){				
			
			Node tag = dato.item(j);
			String label = tag.getNodeName();					
			
			//Force task stop				
			if (stop) return null;			
			
			if (label.equals("title")){		
				
				entry.setTitle(getValue(tag));
				
			}else if (label.equals("description")){						
				
				//Get the first description image and set as thumbnail
				String html = getValue(tag);
				String images[] = Utils.extractImgFromHTML(html);
				
				if (images!=null && images[0]!=""){					
					entry.setImageUrl(images[0]);
				}							
			    						
			}else if(label.equalsIgnoreCase("link")){	
				
				entry.setUrl(tag.getFirstChild().getNodeValue());
				
			}else if(label.equalsIgnoreCase("pubDate")){		
				
				entry.setDate(tag.getFirstChild().getNodeValue());
				
			}			
		}
		
		return entry;
	}
	
	
	/**
	 * Read string values that can contain HTML and cause bad reading (this function prevent this)
	 * @param Node dato
	 * @return String
	 */
	
	private String getValue(Node dato){
		
		StringBuilder text = new StringBuilder();
		NodeList pieces = dato.getChildNodes();
		
		for (int i=0;i<pieces.getLength();i++){
			text.append(pieces.item(i).getNodeValue());
		}
		
		return text.toString();
	}
	
	
	/**
	 * Get all RSS content
	 * @return Inputstream
	 */
	private InputStream getInputStream(){
		
		try{
			
			HttpURLConnection conn = (HttpURLConnection) rssUrl.openConnection();
			conn.setConnectTimeout(10000);	
			conn.setReadTimeout(30000);	
			return conn.getInputStream();
		
		}catch(IOException e){}
		
		return null;
		
	}
	
	/**
	 * Get how many items has the feed
	 * @return
	 */
	public int count(){
		getRemoteContents();	
		
		if (content == null) return 0;
		else return content.getLength();
	}
	
	/**
	 * Stops reading feed
	 */
	public void stop(){
		this.stop = true;
	}
	
	
	/**
	 * Set How many entries will be loaded by page
	 * @param itemsPerPage
	 */
	public void setItemsPerPage(int itemsPerPage){
		this.itemsPerPage = itemsPerPage;
	}
	
	
}
