package com.cmmobi.icuiniao.sql.helper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class RegionalsDomXml {
	
	private Regional regional = null;
	private List<Regional> regionals = null;
	private final String TAG = "SettingDatabase";
	
	public List<Regional> getRegionals(InputStream is){
		regionals = new ArrayList<Regional>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder(); 
			Document dom = builder.parse(is); 
			Element root = dom.getDocumentElement(); 
			NodeList items = root.getElementsByTagName("regional");
			for (int i = 0; i < items.getLength(); i++) { 
				regional= new Regional(); 
				Element personNode = (Element) items.item(i); 
				NodeList childsNodes = personNode.getChildNodes(); 
				for (int j = 0; j < childsNodes.getLength(); j++) { 
					Node node = (Node) childsNodes.item(j);
					if(node.getNodeType() == Node.ELEMENT_NODE){    
						Element childNode = (Element) node; 
						if("id".equals(childNode.getNodeName())) { 
							regional.setId(Integer.parseInt(childNode.getFirstChild().getNodeValue()));                               
						}
						else if("regionalnum".equals(childNode.getNodeName())) { 
							regional.setRegionalnum(childNode.getFirstChild().getNodeValue()); 
						}
						else if("regionalname".equals(childNode.getNodeName())){
							regional.setRegionalname(childNode.getFirstChild().getNodeValue());
						}
						else if("fid".equals(childNode.getNodeName())){
							regional.setFid(Integer.parseInt(childNode.getFirstChild().getNodeValue()));
						}
						else if("regionallevel".equals(childNode.getNodeName())){
							regional.setRegionallevel(Integer.parseInt(childNode.getFirstChild().getNodeValue()));
						}
					} 
				}
				regionals.add(regional); 
			} 
			is.close(); 
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 


		return regionals;
	}
}
