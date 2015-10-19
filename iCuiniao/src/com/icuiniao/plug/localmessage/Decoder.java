/**
 * 
 */
package com.icuiniao.plug.localmessage;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cmmobi.icuiniao.util.LogPrint;

/**
 * @author hw
 *解析器
 */
public class Decoder {

	private final static String TAG_LOCAL = "local";
	private final static String TAG_INFO = "info";
	private final static String TAG_MESSAGE = "message";
	private final static String TAG_CONTENT = "Content";
	private final static String TAG_USER = "user";
	private final static String TAG_LABEL = "label";
	private final static String TAG_KEYWORD = "keyword";
	
	private final static String PARAM_ID = "id";
	private final static String PARAM_ACTIVETIMESTART = "activeTimeStart";
	private final static String PARAM_ACTIVETIMEEND = "activeTimeEnd";
	private final static String PARAM_SHOWTIMESTART = "showTimeStart";
	private final static String PARAM_SHOWTIMEEND = "showTimeEnd";
	private final static String PARAM_SYSTEMPLATFORM = "systemPlatform";
	private final static String PARAM_SEX = "sex";
	private final static String PARAM_NOTSTART = "notStart";
	private final static String PARAM_TYPE = "type";
	private final static String PARAM_CITY = "city";
	private final static String PARAM_BIRTHDAYSTART = "birthdayStart";
	private final static String PARAM_BIRTHDAYEND = "birthdayEnd";
	private final static String PARAM_AGESTART = "ageStart";
	private final static String PARAM_AGEEND = "ageEnd";
	private final static String PARAM_SALARYSTART = "salaryStart";
	private final static String PARAM_SALARYEND = "salaryEnd";
	
	public Decoder(){
		
	}
	
	public TagLocal doDecode(byte[] data){
		LogPrint.Print("local","doDecode");
		TagLocal local = null;
		ByteArrayInputStream stream = null;
		if(data == null)return null;
		if(data.length <= 0)return null;
		
		stream = new ByteArrayInputStream(data);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(stream);
			Element root = dom.getDocumentElement();//获得根节点
			String rootname = root.getTagName();
			
			if(!rootname.toLowerCase().equals(TAG_LOCAL)){
				LogPrint.Print("local","Error: root tag is not local");
				return null;
			}
			
			NodeList rootNodeList = root.getChildNodes();//根
			if(rootNodeList != null&&rootNodeList.getLength() > 0){
				
				local = new TagLocal();
				
				for(int i = 0;i < rootNodeList.getLength();i ++){
					Node rootNode = rootNodeList.item(i);//获得page下的所有节点
					if(rootNode.getNodeType() == Node.ELEMENT_NODE){
						if(rootNode.getNodeName().equalsIgnoreCase(TAG_INFO)){
							local.setLocal(buildTagInfo(rootNode));
						}
					}
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return local;
	}
	
	private TagInfo buildTagInfo(Node node){
		TagInfo tagInfo = new TagInfo();
		NodeList infoNodeList = node.getChildNodes();
		int lenth;
		if(infoNodeList != null&&infoNodeList.getLength() > 0){
			lenth = infoNodeList.getLength();
			for(int i = 0;i < lenth;i ++){
				Node childNode = infoNodeList.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE){
					if(childNode.getNodeName().equalsIgnoreCase(TAG_MESSAGE)){
						tagInfo.setMessage(buildTagMessage(childNode));
					}else if(childNode.getNodeName().equalsIgnoreCase(TAG_USER)){
						tagInfo.setUser(buildTagUser(childNode));
					}
				}
			}
		}
		return tagInfo;
	}
	
	private TagMessage buildTagMessage(Node node){
		TagMessage tagMessage = new TagMessage();
		//id
		tagMessage.setId(getNodeValueInt(node, PARAM_ID));
		//activeTimeStart
		tagMessage.setActiveTimeStart(getNodeValueString(node, PARAM_ACTIVETIMESTART));
		//activeTimeEnd
		tagMessage.setActiveTimeEnd(getNodeValueString(node, PARAM_ACTIVETIMEEND));
		//showTimeStart
		tagMessage.setShowTimeStart(getNodeValueString(node, PARAM_SHOWTIMESTART));
		//showTimeEnd
		tagMessage.setShowTimeEnd(getNodeValueString(node, PARAM_SHOWTIMEEND));
		//获取子节点
		NodeList contentNodeList = node.getChildNodes();
		int lenth;
		if(contentNodeList != null&&contentNodeList.getLength() > 0){
			lenth = contentNodeList.getLength();
			for(int i = 0;i < lenth;i ++){
				Node childNode = contentNodeList.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE){
					if(childNode.getNodeName().equalsIgnoreCase(TAG_CONTENT)){
						if(childNode.getFirstChild() != null){
							TagContent content = new TagContent();
							content.setContent(childNode.getFirstChild().getNodeValue());
							tagMessage.setContent(content);
						}
						break;
					}
				}
			}
		}
		return tagMessage;
	}
	
	private TagUser buildTagUser(Node node){
		TagUser tagUser = new TagUser();
		//systemPlatform
		tagUser.setSystemPlatform(getNodeValueInt(node, PARAM_SYSTEMPLATFORM));
		//sex
		tagUser.setSex(getNodeValueInt(node, PARAM_SEX));
		//notStart
		tagUser.setNotStart(getNodeValueInt(node, PARAM_NOTSTART));
		//type
		tagUser.setType(getNodeValueInt(node, PARAM_TYPE));
		//city
		tagUser.setCity(getNodeValueString(node, PARAM_CITY));
		//birthdayStart
		tagUser.setBirthdayStart(getNodeValueString(node, PARAM_BIRTHDAYSTART));
		//birthdayEnd
		tagUser.setBirthdayEnd(getNodeValueString(node, PARAM_BIRTHDAYEND));
		//ageStart
		tagUser.setAgeStart(getNodeValueInt(node, PARAM_AGESTART));
		//ageEnd
		tagUser.setAgeEnd(getNodeValueInt(node, PARAM_AGEEND));
		//salaryStart
		tagUser.setSalaryStart(getNodeValueInt(node, PARAM_SALARYSTART));
		//salaryEnd
		tagUser.setSalaryEnd(getNodeValueInt(node, PARAM_SALARYEND));
		//获取子节点
		NodeList childnodeList = node.getChildNodes();
		int lenth;
		if(childnodeList != null&&childnodeList.getLength() > 0){
			lenth = childnodeList.getLength();
			for(int i = 0;i < lenth;i ++){
				Node childNode = childnodeList.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE){
					if(childNode.getNodeName().equalsIgnoreCase(TAG_LABEL)){
						if(childNode.getFirstChild() != null){
							tagUser.setLabel(childNode.getFirstChild().getNodeValue());
						}
					}else if(childNode.getNodeName().equalsIgnoreCase(TAG_KEYWORD)){
						if(childNode.getFirstChild() != null){
							tagUser.setKeyword(childNode.getFirstChild().getNodeValue());
						}
					}
				}
			}
		}
		return tagUser;
	}
	
	private String getNodeValueString(Node node,String param){
		String result = null;
		
		Node stringNode = node.getAttributes().getNamedItem(param);
		if(stringNode != null&&stringNode.getNodeName().equalsIgnoreCase(param)){
			String value = stringNode.getNodeValue();
			if(value != null&&value.trim().length() > 0){
				result = value;
			}
		}
		
		return result;
	}
	
	private int getNodeValueInt(Node node,String param){
		int result = -1;
		
		try {
			Node intNode = node.getAttributes().getNamedItem(param);
			if(intNode != null&&intNode.getNodeName().equalsIgnoreCase(param)){
				String value = intNode.getNodeValue();
				if(value != null&&value.trim().length() > 0){
					result = Integer.parseInt(value);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
}
