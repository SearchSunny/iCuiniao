/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;

import com.cmmobi.icuiniao.onlineEngine.activity.MyPageActivityA;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownLoadPool;
import com.cmmobi.icuiniao.util.DownLoadPoolItem;
import com.cmmobi.icuiniao.util.LogPrint;

/**
 * @author hw
 *
 *xml解析及分发
 */
public class Parser_ParserEngine extends Parser_Layout_AbsLayout{

	//tag
	protected final static String TAG_PAGE = "page";
	protected final static String TAG_TITLEBAR = "titlebar";
	protected final static String TAG_TITLEBARFLOAT = "titlebarfloat";
	protected final static String TAG_MAINPAGE = "mainpage";
	protected final static String TAG_IMAGE = "image";
	protected final static String TAG_TEXT = "text";
	protected final static String TAG_BUTTON = "button";
	protected final static String TAG_INPUTLINE = "inputline";
	protected final static String TAG_USERINFO = "userinfo";
	protected final static String TAG_TABBUTTON = "tabbutton";
	protected final static String TAG_USERBAR = "userbar";
	protected final static String TAG_LIST = "list";
	protected final static String TAG_LISTITEM = "listitem";
	protected final static String TAG_LIKEBUTTON = "likebutton";
	protected final static String TAG_FORMBUTTON = "formbutton";
	protected final static String TAG_LIKE = "like";
	protected final static String TAG_COMMODITYBUTTON = "commoditybutton";
	protected final static String TAG_ACTIVITYBUTTON = "activitybutton";
	protected final static String TAG_COMMODITY = "commodity";
	protected final static String TAG_COMMODITYINFO = "commodityinfo";
	protected final static String TAG_COMMODITYBAR = "commoditybar";
	protected final static String TAG_COQUETRY = "coquetry";
	protected final static String TAG_COQUETRYITEM = "coquetryitem";
	protected final static String TAG_STREAMPAGE = "streampage";
	//lyb for userInfo_new
	protected final static String TAG_INFOS = "infos";
	//自定义列表
	protected final static String TAG_STYLEPAGE = "stylepage";
	protected final static String TAG_STYLEITEM = "styleitem";
	
	//param
	protected final static String PARAM_VERSION = "version";
	protected final static String PARAM_PAGEID = "pageid";
	protected final static String PARAM_BACKGROUND = "background";
	protected final static String PARAM_MODELTYPE = "modeltype";
	protected final static String PARAM_MODELINDEX = "modelindex";
	protected final static String PARAM_HREF = "href";
	protected final static String PARAM_ACTION = "action";
	protected final static String PARAM_SRC = "src";
	protected final static String PARAM_CLICKABLE = "clickable";
	protected final static String PARAM_MENUABLE = "menuable";
	protected final static String PARAM_TYPE = "type";
	protected final static String PARAM_WAPURL = "wapurl";
	protected final static String PARAM_STR = "str";
	protected final static String PARAM_PLAYABLE = "playable";
	protected final static String PARAM_USERID = "userid";
	protected final static String PARAM_PARTRENDER = "partrender";
	protected final static String PARAM_PAGEINDEX = "pageindex";
	protected final static String PARAM_COMMODITYID = "commodityid";
	protected final static String PARAM_FLUSH = "flush";
	protected final static String PARAM_TOTALPAGE = "totalpage";
	protected final static String PARAM_MERCHANTID = "merchantid";
	protected final static String PARAM_SUBJECTID = "subjectid";
	protected final static String PARAM_COMMENTID = "commentid";
	protected final static String PARAM_PREURL = "preurl";
	protected final static String PARAM_NEXTURL = "nexturl";
	protected final static String PARAM_CURPAGEID = "curpageid";
	protected final static String PARAM_UID = "uid";
	protected final static String PARAM_ID = "id";
	protected final static String PARAM_EMPTY = "empty";
	protected final static String PARAM_LIKE = "like";
	//商品权重
	protected final static String PARAM_WEIGHT_TYPE = "weighttype";
	protected final static String PARAM_PROPRICE = "proprice";
	protected final static String PARAM_PROTAG = "protag";
	//自定义列表
	protected final static String PARAM_SELECTED = "selected";
	protected final static String PARAM_VALIDNUM = "validnum";
	protected final static String PARAM_VALIDABLE = "validable";
	
	//value
	protected final static String VALUE_DEFAULT = "default";
	protected final static String VALUE_MODELTYPE_1 = "modeltype_1";
	protected final static String VALUE_MODELTYPE_2 = "modeltype_2";
	protected final static String VALUE_MODELTYPE_3 = "modeltype_3";
	protected final static String VALUE_MODELTYPE_4 = "modeltype_4";
	protected final static String VALUE_MODELTYPE_5 = "modeltype_5";
	protected final static String VALUE_MODELTYPE_6 = "modeltype_6";
	protected final static String VALUE_TRUE = "true";
	protected final static String VALUE_FALSE = "false";
	protected final static String VALUE_ADD = "add";
	protected final static String VALUE_ADDEACHOTHER = "addeachother";
	protected final static String VALUE_ADDOVER = "addover";
	protected final static String VALUE_BACK = "back";
	protected final static String VALUE_MAINMENU = "mainmenu";
	protected final static String VALUE_PALY = "play";
	protected final static String VALUE_OK = "ok";
	protected final static String VALUE_DISCOUNT ="discount";
	protected final static String VALUE_RECOMMEND = "recommend";
	protected final static String VALUE_FORM = "form";
	protected final static String VALUE_ACTIVITIES = "activities";
	protected final static String VALUE_ONLYLEFT = "onlyleft";
	protected final static String VALUE_ONLYRIGHT = "onlyright";
	protected final static String VALUE_ITEM_ONLYTEXT = "item_onlytext";
	protected final static String VALUE_ITEM_FRIENDS = "item_friends";
	protected final static String VALUE_ITEM_COMMENT_C = "item_comment_c";
	protected final static String VALUE_ITEM_COMMENT_U = "item_comment_u";
	protected final static String VALUE_DIALOG = "dialog";
	protected final static String VALUE_SEND = "send";
	protected final static String VALUE_MULTI = "multi";
	protected final static String VALUE_CANCEL = "cancel";
	protected final static String VALUE_COMMENT = "comment";	
	//lyb for userInfo
	protected final static String VALUE_TO_DELETE = "todelete";
	protected final static String VALUE_DELETED = "deleted";
	protected final static String VALUE_FRIEND = "friend";
	protected final static String VALUE_SET = "set";
	protected final static String VALUE_DEL_BLACK = "delblack";
	protected final static String VALUE_ADD_FRIEND = "addfriend";
	
	//版本支持标签集
	private static final String[] versionTags = {
			TAG_PAGE,
			TAG_TITLEBAR,
			TAG_TITLEBARFLOAT,
			TAG_MAINPAGE,
			TAG_IMAGE,
			TAG_TEXT,
			TAG_BUTTON,
			TAG_INPUTLINE,
			TAG_USERINFO,
			TAG_TABBUTTON,
			TAG_USERBAR,
			TAG_LIST,
			TAG_LISTITEM,
			TAG_LIKEBUTTON,
			TAG_FORMBUTTON,
			TAG_LIKE,
			TAG_COMMODITYBUTTON,
			TAG_ACTIVITYBUTTON,
			TAG_COMMODITY,
			TAG_COMMODITYINFO,
			TAG_COMMODITYBAR,
			TAG_COQUETRY,
			TAG_COQUETRYITEM,
			TAG_STREAMPAGE
	};
	
	//分发数据集合
	private Parser_Layout_AbsLayout[] mAbsLayouts;
	//当前页对象
	private Parser_Page parserPage;
	private Context context;
	
	public Parser_ParserEngine(){
		super();
	}
	
	public Parser_ParserEngine(Context context){
		super();
		this.context = context;
	}
	
	/**返回页面的全部数据对象*/
	public Parser_Layout_AbsLayout[] getLayouts(){
		return mAbsLayouts;
	}
	
	/**返回页对象*/
	public Parser_Page getPageObject(){
		return parserPage;
	}
	
	
	/**判断当前版本是否支持此标签*/
	private static boolean isSupport(String tag){
		for(int i = 0; i < versionTags.length;i ++){
			if(tag.equalsIgnoreCase(versionTags[i])){
				return true;
			}
		}
		return false;
	}
	
	/**将文本值转化为索引值*/
	private int valueOf(String str){
		if(str.equalsIgnoreCase(VALUE_DEFAULT)){
			return DEFAULT;
		}else if(str.equalsIgnoreCase(VALUE_MODELTYPE_1)){
			return MODELTYPE_1;
		}else if(str.equalsIgnoreCase(VALUE_MODELTYPE_2)){
			return MODELTYPE_2;
		}else if(str.equalsIgnoreCase(VALUE_MODELTYPE_3)){
			return MODELTYPE_3;
		}else if(str.equalsIgnoreCase(VALUE_MODELTYPE_4)){
			return MODELTYPE_4;
		}else if(str.equalsIgnoreCase(VALUE_MODELTYPE_5)){
			return MODELTYPE_5;
		}else if(str.equalsIgnoreCase(VALUE_MODELTYPE_6)){
			return MODELTYPE_6;
		}else if(str.equalsIgnoreCase(VALUE_TRUE)){
			return TRUE;
		}else if(str.equalsIgnoreCase(VALUE_FALSE)){
			return FALSE;
		}else if(str.equalsIgnoreCase(VALUE_ADD)){
			return TYPE_ADD;
		}else if(str.equalsIgnoreCase(VALUE_ADDEACHOTHER)){
			return TYPE_ADDEACHOTHER;
		}else if(str.equalsIgnoreCase(VALUE_ADDOVER)){
			return TYPE_ADDOVER;
		}else if(str.equalsIgnoreCase(VALUE_BACK)){
			return ACTION_BACK;
		}else if(str.equalsIgnoreCase(VALUE_MAINMENU)){
			return ACTION_MAINMENU;
		}else if(str.equalsIgnoreCase(VALUE_PALY)){
			return ACTION_PLAY;
		}else if(str.equalsIgnoreCase(VALUE_OK)){
			return ACTION_OK;
		}else if(str.equalsIgnoreCase(VALUE_DISCOUNT)){
			return TYPE_DISCOUNT;
		}else if(str.equalsIgnoreCase(VALUE_RECOMMEND)){
			return TYPE_RECOMMEND;
		}else if(str.equalsIgnoreCase(VALUE_FORM)){
			return TYPE_FORM;
		}else if(str.equalsIgnoreCase(VALUE_ACTIVITIES)){
			return TYPE_ACTIVITIES;
		}else if(str.equalsIgnoreCase(VALUE_ONLYLEFT)){
			return TYPE_ONLYLEFT;
		}else if(str.equalsIgnoreCase(VALUE_ONLYRIGHT)){
			return TYPE_ONLYRIGHT;
		}else if(str.equalsIgnoreCase(VALUE_ITEM_ONLYTEXT)){
			return TYPE_ITEM_ONLYTEXT;
		}else if(str.equalsIgnoreCase(VALUE_ITEM_FRIENDS)){
			return TYPE_ITEM_FRIENDS;
		}else if(str.equalsIgnoreCase(VALUE_ITEM_COMMENT_C)){
			return TYPE_ITEM_COMMENT_C;
		}else if(str.equalsIgnoreCase(VALUE_ITEM_COMMENT_U)){
			return TYPE_ITEM_COMMENT_U;
		}else if(str.equalsIgnoreCase(VALUE_DIALOG)){
			return TYPE_DIALOG;
		}else if(str.equalsIgnoreCase(VALUE_SEND)){
			return ACTION_SEND;
		}else if(str.equalsIgnoreCase(VALUE_MULTI)){
			return TYPE_MULTI;
		}else if(str.equalsIgnoreCase(VALUE_CANCEL)){
			return TYPE_CANCEL;
		}else if(str.equalsIgnoreCase(VALUE_COMMENT)){
			return ACTION_COMMENT;
		}else if(str.equalsIgnoreCase(VALUE_TO_DELETE)){
			return ACTION_TO_DELETE;
		}else if(str.equalsIgnoreCase(VALUE_DELETED)){
			return ACTION_DELETED;
		}else if(str.equalsIgnoreCase(VALUE_FRIEND)){
			return ACTION_FRIEND;
		}else if(str.equalsIgnoreCase(VALUE_SET)){
			return ACTION_SET;
		}else if(str.equalsIgnoreCase(VALUE_DEL_BLACK)){
			return ACTION_DEL_BLACK;
		}else if(str.equalsIgnoreCase(VALUE_ADD_FRIEND)){
			return ACTION_ADD_FRIEND;
		}
		return -1;
	}
	
	/**
	 * DOM方式解析xml数据
	 * */
	public void parser(byte[] data){
		LogPrint.Print("parser");
		ByteArrayInputStream stream = null;
		if(data == null)return;
		if(data.length <= 0)return;
		
		stream = new ByteArrayInputStream(data);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(stream);
			Element root = dom.getDocumentElement();//获得根节点
			String rootname = root.getTagName();
			
			if(!rootname.toLowerCase().equals(TAG_PAGE)){
				LogPrint.Print("Error: root tag is not page");
				return;
			}
			//获得page相关参数
			parserPage = buildPage(root);
			
			int layoutNum = 0;
			NodeList rootNodeList = root.getChildNodes();//根
			if(rootNodeList != null&&rootNodeList.getLength() > 0){
				if(parserPage.getVersion() == VERSION){//版本匹配
					for(int i = 0;i < rootNodeList.getLength();i ++){
						if(rootNodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
							layoutNum ++;//计算共有多少个布局
						}
					}
				}else{//版本不匹配
					for(int i = 0;i < rootNodeList.getLength();i ++){
						if(rootNodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
							if(isSupport(rootNodeList.item(i).getNodeName())){
								layoutNum ++;//计算共有多少个布局
							}
						}
					}
				}
				
				mAbsLayouts = new Parser_Layout_AbsLayout[layoutNum];
				int index = 0;
				
				for(int i = 0;i < rootNodeList.getLength();i ++){
					Node rootNode = rootNodeList.item(i);//获得page下的所有节点
					if(rootNode.getNodeType() == Node.ELEMENT_NODE){
						if(rootNode.getNodeName().equalsIgnoreCase(TAG_TITLEBAR)){
							mAbsLayouts[index] = buildTitleBar(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_TITLEBARFLOAT)){
							mAbsLayouts[index] = buildTitleBarFloat(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_MAINPAGE)){
							mAbsLayouts[index] = buildMainPage(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
							mAbsLayouts[index] = buildImage(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_TEXT)){
							mAbsLayouts[index] = buildText(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_BUTTON)){
							mAbsLayouts[index] = buildButton(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_INPUTLINE)){
							mAbsLayouts[index] = buildInputLine(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_USERINFO)){
							mAbsLayouts[index] = buildUserInfo(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_TABBUTTON)){
							mAbsLayouts[index] = buildTabButton(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_USERBAR)){
							mAbsLayouts[index] = buildUserBar(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_LIST)){
							mAbsLayouts[index] = buildList(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_LIKE)){
							mAbsLayouts[index] = buildLike(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_COMMODITY)){
							mAbsLayouts[index] = buildCommodity(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_COMMODITYINFO)){
							mAbsLayouts[index] = buildCommodityInfo(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_COMMODITYBAR)){
							mAbsLayouts[index] = buildCommodityBar(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_COQUETRY)){
							mAbsLayouts[index] = buildCoquetry(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_STREAMPAGE)){
							mAbsLayouts[index] = buildStreamPage(rootNode);
							index ++;
							//lyb for userInfo_new
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_INFOS)){
							mAbsLayouts[index] = buildUserInfoNew(rootNode);
							index ++;
						}else if(rootNode.getNodeName().equalsIgnoreCase(TAG_STYLEPAGE)){
							mAbsLayouts[index] = buildStylePage(rootNode);
							index ++;
						}
					}
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private Parser_Page buildPage(Element element){
		Parser_Page page = new Parser_Page();
		//version
		Node versionNode = element.getAttributes().getNamedItem(PARAM_VERSION);
		if(versionNode != null&&versionNode.getNodeName().equalsIgnoreCase(PARAM_VERSION)){
			page.setVersion(versionNode.getNodeValue());
		}
		//pageid
		Node pageIdNode = element.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			page.setPageId(pageIdNode.getNodeValue());
		}
		//userid
		Node userIdNode = element.getAttributes().getNamedItem(PARAM_USERID);
		if(userIdNode != null&&userIdNode.getNodeName().equalsIgnoreCase(PARAM_USERID)){
			page.setUserId(userIdNode.getNodeValue());
		}
		//commodityid
		Node commodityIdNode = element.getAttributes().getNamedItem(PARAM_COMMODITYID);
		if(commodityIdNode != null&&commodityIdNode.getNodeName().equalsIgnoreCase(PARAM_COMMODITYID)){
			page.setCommodityId(commodityIdNode.getNodeValue());
		}
		//totalpage
		Node totalpageNode = element.getAttributes().getNamedItem(PARAM_TOTALPAGE);
		if(totalpageNode != null&&totalpageNode.getNodeName().equalsIgnoreCase(PARAM_TOTALPAGE)){
			page.setTotalpage(totalpageNode.getNodeValue());
		}
		//type
		Node pageRenderTypeNode = element.getAttributes().getNamedItem(PARAM_TYPE);
		if(pageRenderTypeNode != null&&pageRenderTypeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			page.setType(pageRenderTypeNode.getNodeValue());
		}
		//wapurl
		Node wapUrlNode = element.getAttributes().getNamedItem(PARAM_WAPURL);
		if(wapUrlNode != null&&wapUrlNode.getNodeName().equalsIgnoreCase(PARAM_WAPURL)){
			page.setWapUrl(wapUrlNode.getNodeValue());
		}
		//merchantid
		Node merchantIdNode = element.getAttributes().getNamedItem(PARAM_MERCHANTID);
		if(merchantIdNode != null&&merchantIdNode.getNodeName().equalsIgnoreCase(PARAM_MERCHANTID)){
			page.setMerchantId(merchantIdNode.getNodeValue());
		}
		//partrender
		Node partrenderNode = element.getAttributes().getNamedItem(PARAM_PARTRENDER);
		if(partrenderNode!=null&&partrenderNode.getNodeName().equalsIgnoreCase(PARAM_PARTRENDER)){
			page.setPartRender(valueOf(partrenderNode.getNodeValue()));
		}
		//nexturl
		Node nexturlNode = element.getAttributes().getNamedItem(PARAM_NEXTURL);
		if(nexturlNode != null&&nexturlNode.getNodeName().equalsIgnoreCase(PARAM_NEXTURL)){
			page.setNextUrl(nexturlNode.getNodeValue());
			//wifi下的预加载逻辑
			if(CommonUtil.isNetWorkOpen(context)&&CommonUtil.getApnType(context).toLowerCase().indexOf("wifi") >= 0){
				if(!DownLoadPool.isClear){
					if(nexturlNode.getNodeValue()!=null&&nexturlNode.getNodeValue().length() > 0){
						DownLoadPoolItem item = new DownLoadPoolItem(nexturlNode.getNodeValue(), 0, false);
						DownLoadPool.getInstance(context).add(item, -1);
					}
				}
			}
		}
		//preurl
		Node preurlNode = element.getAttributes().getNamedItem(PARAM_PREURL);
		if(preurlNode != null&&preurlNode.getNodeName().equalsIgnoreCase(PARAM_PREURL)){
			page.setPreUrl(preurlNode.getNodeValue());
			//wifi下的预加载逻辑
			if(CommonUtil.isNetWorkOpen(context)&&CommonUtil.getApnType(context).toLowerCase().indexOf("wifi") >= 0){
				if(!DownLoadPool.isClear){
					if(preurlNode.getNodeValue()!=null&&preurlNode.getNodeValue().length() > 0){
						DownLoadPoolItem item = new DownLoadPoolItem(preurlNode.getNodeValue(), 0, false);
						DownLoadPool.getInstance(context).add(item, -1);
					}
				}
			}
		}
		//curpageid
		Node curpageidNode = element.getAttributes().getNamedItem(PARAM_CURPAGEID);
		if(curpageidNode != null&&curpageidNode.getNodeName().equalsIgnoreCase(PARAM_CURPAGEID)){
			page.setCurPageId(curpageidNode.getNodeValue());
		}
		
		//uid
		Node uIdNode = element.getAttributes().getNamedItem(PARAM_UID);
		if(uIdNode != null&&uIdNode.getNodeName().equalsIgnoreCase(PARAM_UID)){
			page.setUid(uIdNode.getNodeValue());
		}
		
		//empty
		Node emptyNode = element.getAttributes().getNamedItem(PARAM_EMPTY);
		if(emptyNode!=null&&emptyNode.getNodeName().equalsIgnoreCase(PARAM_EMPTY)){
			page.setEmpty(valueOf(emptyNode.getNodeValue()));
		}
		
		return page;
	}
	
	private Parser_Text buildText(Node node){
		Parser_Text parserText = new Parser_Text();
		parserText.setModelType(MODELTYPE_TEXT);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserText.setPageId(pageIdNode.getNodeValue());
		}
		//str
		Node strNode = node.getAttributes().getNamedItem(PARAM_STR);
		if(strNode!=null&&strNode.getNodeName().equalsIgnoreCase(PARAM_STR)){
			parserText.setStr(strNode.getNodeValue());
		}
		//href
		Node hrefNode = node.getAttributes().getNamedItem(PARAM_HREF);
		if(hrefNode!=null&&hrefNode.getNodeName().equalsIgnoreCase(PARAM_HREF)){
			parserText.setHref(hrefNode.getNodeValue());
		}
		//action
		Node actionNode = node.getAttributes().getNamedItem(PARAM_ACTION);
		if(actionNode!=null&&actionNode.getNodeName().equalsIgnoreCase(PARAM_ACTION)){
			parserText.setAction(valueOf(actionNode.getNodeValue()));
		}
		//clickable
		Node clickableNode = node.getAttributes().getNamedItem(PARAM_CLICKABLE);
		if(clickableNode!=null&&clickableNode.getNodeName().equalsIgnoreCase(PARAM_CLICKABLE)){
			parserText.setClickable(valueOf(clickableNode.getNodeValue()));
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserText.setType(valueOf(typeNode.getNodeValue()));
		}
		
		return parserText;
	}
	
	private Parser_Image buildImage(Node node){
		Parser_Image parserImage = new Parser_Image();
		parserImage.setModelType(MODELTYPE_IMAGE);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserImage.setPageId(pageIdNode.getNodeValue());
		}
		//validable
		Node validableNode = node.getAttributes().getNamedItem(PARAM_VALIDABLE);
		if(validableNode!=null&&validableNode.getNodeName().equalsIgnoreCase(PARAM_VALIDABLE)){
			parserImage.setValidable(valueOf(validableNode.getNodeValue()));
		}
		//src
		Node srcNode = node.getAttributes().getNamedItem(PARAM_SRC);
		if(srcNode!=null&&srcNode.getNodeName().equalsIgnoreCase(PARAM_SRC)){
			parserImage.setSrc(srcNode.getNodeValue());
			if(CommonUtil.isNetWorkOpen(context)){
				if(parserImage.getValidable()){//有效
					if(!DownLoadPool.isClear){
						if(srcNode.getNodeValue()!=null&&srcNode.getNodeValue().length() > 0){
							DownLoadPoolItem item = new DownLoadPoolItem(srcNode.getNodeValue(), 1, false);
							DownLoadPool.getInstance(context).add(item, -1);
						}
					}
				}
			}
		}
		//href
		Node hrefNode = node.getAttributes().getNamedItem(PARAM_HREF);
		if(hrefNode!=null&&hrefNode.getNodeName().equalsIgnoreCase(PARAM_HREF)){
			parserImage.setHref(hrefNode.getNodeValue());
		}
		//action
		Node actionNode = node.getAttributes().getNamedItem(PARAM_ACTION);
		if(actionNode!=null&&actionNode.getNodeName().equalsIgnoreCase(PARAM_ACTION)){
			parserImage.setAction(valueOf(actionNode.getNodeValue()));
		}
		//clickable
		Node clickableNode = node.getAttributes().getNamedItem(PARAM_CLICKABLE);
		if(clickableNode!=null&&clickableNode.getNodeName().equalsIgnoreCase(PARAM_CLICKABLE)){
			parserImage.setClickable(valueOf(clickableNode.getNodeValue()));
		}
		//playable
		Node playableNode = node.getAttributes().getNamedItem(PARAM_PLAYABLE);
		if(playableNode!=null&&playableNode.getNodeName().equalsIgnoreCase(PARAM_PLAYABLE)){
			parserImage.setPlayable(valueOf(playableNode.getNodeValue()));
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserImage.setType(valueOf(typeNode.getNodeValue()));
		}
		//id
		Node idNode = node.getAttributes().getNamedItem(PARAM_ID);
		if(idNode!=null&&idNode.getNodeName().equalsIgnoreCase(PARAM_ID)){
			parserImage.setId(idNode.getNodeValue());
		}
		//WEIGHT_TYPE
		Node weightNode = node.getAttributes().getNamedItem(PARAM_WEIGHT_TYPE);
		if(weightNode!=null&&weightNode.getNodeName().equalsIgnoreCase(PARAM_WEIGHT_TYPE)){
			parserImage.setWeight(weightNode.getNodeValue());
		}
		//price
		Node priceNode = node.getAttributes().getNamedItem(PARAM_PROPRICE);
		if(priceNode!=null&&priceNode.getNodeName().equalsIgnoreCase(PARAM_PROPRICE)){
			parserImage.setPrice(priceNode.getNodeValue());
		}
		//feature
		Node featureNode = node.getAttributes().getNamedItem(PARAM_PROTAG);
		if(featureNode!=null&&featureNode.getNodeName().equalsIgnoreCase(PARAM_PROTAG)){
			parserImage.setFeature(featureNode.getNodeValue());
		}
		
		return parserImage;
	}
	
	private Parser_Button buildButton(Node node){
		Parser_Button parserButton = new Parser_Button();
		parserButton.setModelType(MODELTYPE_BUTTON);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserButton.setPageId(pageIdNode.getNodeValue());
		}
		//str
		Node strNode = node.getAttributes().getNamedItem(PARAM_STR);
		if(strNode!=null&&strNode.getNodeName().equalsIgnoreCase(PARAM_STR)){
			parserButton.setStr(strNode.getNodeValue());
		}
		//href
		Node hrefNode = node.getAttributes().getNamedItem(PARAM_HREF);
		if(hrefNode!=null&&hrefNode.getNodeName().equalsIgnoreCase(PARAM_HREF)){
			parserButton.setHref(hrefNode.getNodeValue());
		}
		//action
		Node actionNode = node.getAttributes().getNamedItem(PARAM_ACTION);
		if(actionNode!=null&&actionNode.getNodeName().equalsIgnoreCase(PARAM_ACTION)){
			parserButton.setAction(valueOf(actionNode.getNodeValue()));
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserButton.setType(valueOf(typeNode.getNodeValue()));
		}
		//like
		Node likeNode = node.getAttributes().getNamedItem(PARAM_LIKE);
		if(likeNode!=null&&likeNode.getNodeName().equalsIgnoreCase(PARAM_LIKE)){
			parserButton.setLike(valueOf(likeNode.getNodeValue()));
		}
		//likeId
		Node likeIdNode = node.getAttributes().getNamedItem(PARAM_ID);
		if(likeIdNode!=null&&likeIdNode.getNodeName().equalsIgnoreCase(PARAM_ID)){
			parserButton.setLikeId(likeIdNode.getNodeValue());
		}
		
		return parserButton;
	}
	
	private Parser_LikeButton buildLikeButton(Node node){
		Parser_LikeButton parserLikeButton = new Parser_LikeButton();
		parserLikeButton.setModelType(MODELTYPE_LIKEBUTTON);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserLikeButton.setPageId(pageIdNode.getNodeValue());
		}
		//str
		Node strNode = node.getAttributes().getNamedItem(PARAM_STR);
		if(strNode!=null&&strNode.getNodeName().equalsIgnoreCase(PARAM_STR)){
			parserLikeButton.setStr(strNode.getNodeValue());
		}
		//href
		Node hrefNode = node.getAttributes().getNamedItem(PARAM_HREF);
		if(hrefNode!=null&&hrefNode.getNodeName().equalsIgnoreCase(PARAM_HREF)){
			parserLikeButton.setHref(hrefNode.getNodeValue());
		}
		//action
		Node actionNode = node.getAttributes().getNamedItem(PARAM_ACTION);
		if(actionNode!=null&&actionNode.getNodeName().equalsIgnoreCase(PARAM_ACTION)){
			parserLikeButton.setAction(valueOf(actionNode.getNodeValue()));
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserLikeButton.setType(valueOf(typeNode.getNodeValue()));
		}
		
		return parserLikeButton;
	}
	
	private Parser_FormButton buildFormButton(Node node){
		Parser_FormButton parserFormButton = new Parser_FormButton();
		parserFormButton.setModelType(MODELTYPE_FORMBUTTON);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserFormButton.setPageId(pageIdNode.getNodeValue());
		}
		//str
		Node strNode = node.getAttributes().getNamedItem(PARAM_STR);
		if(strNode!=null&&strNode.getNodeName().equalsIgnoreCase(PARAM_STR)){
			parserFormButton.setStr(strNode.getNodeValue());
		}
		//href
		Node hrefNode = node.getAttributes().getNamedItem(PARAM_HREF);
		if(hrefNode!=null&&hrefNode.getNodeName().equalsIgnoreCase(PARAM_HREF)){
			parserFormButton.setHref(hrefNode.getNodeValue());
		}
		//action
		Node actionNode = node.getAttributes().getNamedItem(PARAM_ACTION);
		if(actionNode!=null&&actionNode.getNodeName().equalsIgnoreCase(PARAM_ACTION)){
			parserFormButton.setAction(valueOf(actionNode.getNodeValue()));
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserFormButton.setType(valueOf(typeNode.getNodeValue()));
		}
		
		return parserFormButton;
	}
	
	private Parser_CommodityButton buildCommodityButton(Node node){
		Parser_CommodityButton parserCommodityButton = new Parser_CommodityButton();
		parserCommodityButton.setModelType(MODELTYPE_COMMODITYBUTTON);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserCommodityButton.setPageId(pageIdNode.getNodeValue());
		}
		//str
		Node strNode = node.getAttributes().getNamedItem(PARAM_STR);
		if(strNode!=null&&strNode.getNodeName().equalsIgnoreCase(PARAM_STR)){
			parserCommodityButton.setStr(strNode.getNodeValue());
		}
		//href
		Node hrefNode = node.getAttributes().getNamedItem(PARAM_HREF);
		if(hrefNode!=null&&hrefNode.getNodeName().equalsIgnoreCase(PARAM_HREF)){
			parserCommodityButton.setHref(hrefNode.getNodeValue());
		}
		//action
		Node actionNode = node.getAttributes().getNamedItem(PARAM_ACTION);
		if(actionNode!=null&&actionNode.getNodeName().equalsIgnoreCase(PARAM_ACTION)){
			parserCommodityButton.setAction(valueOf(actionNode.getNodeValue()));
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserCommodityButton.setType(valueOf(typeNode.getNodeValue()));
		}
		
		return parserCommodityButton;
	}
	
	private Parser_ActivityButton buildActivityButton(Node node){
		Parser_ActivityButton parserActivityButton = new Parser_ActivityButton();
		parserActivityButton.setModelType(MODELTYPE_ACTIVITYBUTTON);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserActivityButton.setPageId(pageIdNode.getNodeValue());
		}
		//str
		Node strNode = node.getAttributes().getNamedItem(PARAM_STR);
		if(strNode!=null&&strNode.getNodeName().equalsIgnoreCase(PARAM_STR)){
			parserActivityButton.setStr(strNode.getNodeValue());
		}
		//href
		Node hrefNode = node.getAttributes().getNamedItem(PARAM_HREF);
		if(hrefNode!=null&&hrefNode.getNodeName().equalsIgnoreCase(PARAM_HREF)){
			parserActivityButton.setHref(hrefNode.getNodeValue());
		}
		//action
		Node actionNode = node.getAttributes().getNamedItem(PARAM_ACTION);
		if(actionNode!=null&&actionNode.getNodeName().equalsIgnoreCase(PARAM_ACTION)){
			parserActivityButton.setAction(valueOf(actionNode.getNodeValue()));
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserActivityButton.setType(valueOf(typeNode.getNodeValue()));
		}
		
		return parserActivityButton;
	}
	
	private Parser_TitleBar buildTitleBar(Node node){
		Parser_TitleBar parserTitleBar = new Parser_TitleBar();
		parserTitleBar.setModelType(MODELTYPE_TITLEBAR);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserTitleBar.setPageId(pageIdNode.getNodeValue());
		}
		//str
		Node strNode = node.getAttributes().getNamedItem(PARAM_STR);
		if(strNode!=null&&strNode.getNodeName().equalsIgnoreCase(PARAM_STR)){
			parserTitleBar.setStr(strNode.getNodeValue());
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserTitleBar.setType(valueOf(typeNode.getNodeValue()));
		}
		//menuable
		Node menuableNode = node.getAttributes().getNamedItem(PARAM_MENUABLE);
		if(menuableNode!=null&&menuableNode.getNodeName().equalsIgnoreCase(PARAM_MENUABLE)){
			parserTitleBar.setMenuable(valueOf(menuableNode.getNodeValue()));
		}
		
		//解析子标签
		NodeList titlebarNodeList = node.getChildNodes();
		if(titlebarNodeList != null){
			int size = titlebarNodeList.getLength();
			if(size > 0){
				//按钮解析
				int index = 0;
				for(int i = 0; i < size;i ++){
					Node buttonNode = titlebarNodeList.item(i);
					if(buttonNode.getNodeType() == Node.ELEMENT_NODE){
						if(buttonNode.getNodeName().equalsIgnoreCase(TAG_BUTTON)){
							switch (index) {
							case 0:
								if(parserTitleBar.getType() == TYPE_ONLYRIGHT){//只有右边有按钮
									parserTitleBar.setRightButton(buildButton(buttonNode));
								}else{
									parserTitleBar.setLeftButton(buildButton(buttonNode));
								}
								break;
							case 1:
								parserTitleBar.setRightButton(buildButton(buttonNode));
								break;
							case 2:
								parserTitleBar.setMiddleButton(buildButton(buttonNode));
								break;
							}
							index ++;
						}
					}
				}
			}
		}
		
		return parserTitleBar;
	}
	
	private Parser_TitleBarFloat buildTitleBarFloat(Node node){
		Parser_TitleBarFloat parserTitleBarFloat = new Parser_TitleBarFloat();
		parserTitleBarFloat.setModelType(MODELTYPE_TITLEBARFLOAT);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserTitleBarFloat.setPageId(pageIdNode.getNodeValue());
		}
		//str
		Node strNode = node.getAttributes().getNamedItem(PARAM_STR);
		if(strNode!=null&&strNode.getNodeName().equalsIgnoreCase(PARAM_STR)){
			parserTitleBarFloat.setStr(strNode.getNodeValue());
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserTitleBarFloat.setType(valueOf(typeNode.getNodeValue()));
		}
		//menuable
		Node menuableNode = node.getAttributes().getNamedItem(PARAM_MENUABLE);
		if(menuableNode!=null&&menuableNode.getNodeName().equalsIgnoreCase(PARAM_MENUABLE)){
			parserTitleBarFloat.setMenuable(valueOf(menuableNode.getNodeValue()));
		}
		
		//解析子标签
		NodeList titlebarFloatNodeList = node.getChildNodes();
		if(titlebarFloatNodeList != null){
			int size = titlebarFloatNodeList.getLength();
			if(size > 0){
				//按钮解析
				int index = 0;
				for(int i = 0; i < size;i ++){
					Node buttonNode = titlebarFloatNodeList.item(i);
					if(buttonNode.getNodeType() == Node.ELEMENT_NODE){
						if(buttonNode.getNodeName().equalsIgnoreCase(TAG_BUTTON)){
							switch (index) {
							case 0:
								if(parserTitleBarFloat.getType() == TYPE_ONLYRIGHT){//只有右边有按钮
									parserTitleBarFloat.setRightButton(buildButton(buttonNode));
								}else{
									parserTitleBarFloat.setLeftButton(buildButton(buttonNode));
								}
								break;
							case 1:
								parserTitleBarFloat.setRightButton(buildButton(buttonNode));
								break;
							case 2:
								parserTitleBarFloat.setMiddleButton(buildButton(buttonNode));
								break;
							}
							index ++;
						}
					}
				}
			}
		}
		
		return parserTitleBarFloat;
	}
	
	private Parser_StreamPage buildStreamPage(Node node){
		Parser_StreamPage parserStreamPage = new Parser_StreamPage();
		parserStreamPage.setModelType(MODELTYPE_STREAMPAGE);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserStreamPage.setPageId(pageIdNode.getNodeValue());
		}
		//nexturl
		Node nexturlNode = node.getAttributes().getNamedItem(PARAM_NEXTURL);
		if(nexturlNode != null&&nexturlNode.getNodeName().equalsIgnoreCase(PARAM_NEXTURL)){
			parserStreamPage.setNextUrl(nexturlNode.getNodeValue());
		}
		
		//empty
		Node emptyNode = node.getAttributes().getNamedItem(PARAM_EMPTY);
		if(emptyNode!=null&&emptyNode.getNodeName().equalsIgnoreCase(PARAM_EMPTY)){
			parserStreamPage.setEmpty(valueOf(emptyNode.getNodeValue()));
		}
		
		//解析子标签
		NodeList mainpageNodeList = node.getChildNodes();
		if(mainpageNodeList != null){
			int size = mainpageNodeList.getLength();
			for(int i = 0;i < size;i ++){
				Node imageNode = mainpageNodeList.item(i);
				if(imageNode.getNodeType() == Node.ELEMENT_NODE){
					if(imageNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
						Parser_Image parserImage = buildImage(imageNode);
						parserStreamPage.addImage(parserImage);
						parserStreamPage.addUrl(parserImage.getHref());
					}
				}
			}
		}
		
		return parserStreamPage;
	}
	
	private Parser_MainPage buildMainPage(Node node){
		Parser_MainPage parserMainPage = new Parser_MainPage();
		parserMainPage.setModelType(MODELTYPE_MAINPAGE);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserMainPage.setPageId(pageIdNode.getNodeValue());
		}
		//modeltype
		Node modelTypeNode = node.getAttributes().getNamedItem(PARAM_MODELTYPE);
		if(modelTypeNode!=null&&modelTypeNode.getNodeName().equalsIgnoreCase(PARAM_MODELTYPE)){
			parserMainPage.setType(valueOf(modelTypeNode.getNodeValue()));
		}
		//modelindex
		Node modelIndexNode = node.getAttributes().getNamedItem(PARAM_MODELINDEX);
		if(modelIndexNode!=null&&modelIndexNode.getNodeName().equalsIgnoreCase(PARAM_MODELINDEX)){
			parserMainPage.setModelIndex(modelIndexNode.getNodeValue());
		}
		//validnum
		Node validNumNode = node.getAttributes().getNamedItem(PARAM_VALIDNUM);
		if(validNumNode != null&&validNumNode.getNodeName().equalsIgnoreCase(PARAM_VALIDNUM)){
			try {
				parserMainPage.setValidNum(Integer.parseInt(validNumNode.getNodeValue()));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		//解析子标签
		NodeList mainpageNodeList = node.getChildNodes();
		if(mainpageNodeList != null){
			int size = mainpageNodeList.getLength();
			int index = 0;
			if(MyPageActivityA.urls == null){
				MyPageActivityA.urls = new String[54];
			}
			for(int i = 0;i < size;i ++){
				Node imageNode = mainpageNodeList.item(i);
				if(imageNode.getNodeType() == Node.ELEMENT_NODE){
					if(imageNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
						Parser_Image parserImage = buildImage(imageNode);
						parserMainPage.addImage(parserImage);
						if(MyPageActivityA.urls.length == 54){
							if(MyPageActivityA.urls[(parserMainPage.getType()-MODELTYPE_1)*9+index] == null){
								MyPageActivityA.urls[(parserMainPage.getType()-MODELTYPE_1)*9+index] = parserImage.getHref();
								if(MyPageActivityA.saveUrlsFromMain == null){
									MyPageActivityA.saveUrlsFromMain = new String[54];
								}
								MyPageActivityA.saveUrlsFromMain[(parserMainPage.getType()-MODELTYPE_1)*9+index] = parserImage.getHref();
							}
							index ++;
						}
					}
				}
			}
		}
		
		return parserMainPage;
	}
	
	private Parser_InputLine buildInputLine(Node node){
		Parser_InputLine parserInputLine = new Parser_InputLine();
		parserInputLine.setModelType(MODELTYPE_INPUTLINE);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserInputLine.setPageId(pageIdNode.getNodeValue());
		}
		//href
		Node hrefNode = node.getAttributes().getNamedItem(PARAM_HREF);
		if(hrefNode!=null&&hrefNode.getNodeName().equalsIgnoreCase(PARAM_HREF)){
			parserInputLine.setHref(hrefNode.getNodeValue());
		}
		//action
		Node actionNode = node.getAttributes().getNamedItem(PARAM_ACTION);
		if(actionNode!=null&&actionNode.getNodeName().equalsIgnoreCase(PARAM_ACTION)){
			parserInputLine.setAction(valueOf(actionNode.getNodeValue()));
		}
		
		return parserInputLine;
	}
	
	private Parser_UserInfo buildUserInfo(Node node){
		Parser_UserInfo parserUserInfo = new Parser_UserInfo();
		parserUserInfo.setModelType(MODELTYPE_USERINFO);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserUserInfo.setPageId(pageIdNode.getNodeValue());
		}
		
		//解析子标签
		NodeList userinfoNodeList = node.getChildNodes();
		if(userinfoNodeList != null){
			int size = userinfoNodeList.getLength();
			int index = 0;
			for(int i = 0;i < size;i ++){
				Node itemNode = userinfoNodeList.item(i);
				if(itemNode.getNodeType() == Node.ELEMENT_NODE){
					if(itemNode.getNodeName().equalsIgnoreCase(TAG_TEXT)){
						switch (index) {
						case 0:
							parserUserInfo.setName(buildText(itemNode));
							break;
						case 1:
							parserUserInfo.setGender(buildText(itemNode));
							break;
						case 2:
							parserUserInfo.setAddr(buildText(itemNode));
							break;
						}
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_LIKEBUTTON)){
						parserUserInfo.setLikeButton(buildLikeButton(itemNode));
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_FORMBUTTON)){
						parserUserInfo.setFormButton(buildFormButton(itemNode));
					}
					
					index ++;
				}
			}
		}
		return parserUserInfo;
	}
	
	//lyb for userInfo_new
	private Parser_UserInfo_new buildUserInfoNew(Node node){
		Parser_UserInfo_new parser_UserInfo_new = new Parser_UserInfo_new();
		parser_UserInfo_new.setModelType(MODELTYPE_USERINFO_new);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parser_UserInfo_new.setPageId(pageIdNode.getNodeValue());
		}
		
		//解析子标签
		NodeList userinfoNodeList = node.getChildNodes();
		if(userinfoNodeList != null){
			int size = userinfoNodeList.getLength();
			int index = 0;
			int buttonIdx = 0;
			for(int i = 0;i < size;i ++){
				Node itemNode = userinfoNodeList.item(i);
				if(itemNode.getNodeType() == Node.ELEMENT_NODE){
					if(itemNode.getNodeName().equalsIgnoreCase(TAG_TEXT)){
						switch (index) {
						case 0:
							parser_UserInfo_new.setName(buildText(itemNode));
							break;
						case 1:
							parser_UserInfo_new.setGender(buildText(itemNode));
							break;
						case 2:
							parser_UserInfo_new.setAddr(buildText(itemNode));
							break;
						}
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_BUTTON)){						
						switch(buttonIdx){						
						case 0:		
							parser_UserInfo_new.setRightButton(buildButton(itemNode));
//							parser_UserInfo_new.setLeftButton(buildButton(itemNode));
							break;
						case 1:
//							parser_UserInfo_new.setRightButton(buildButton(itemNode));
							parser_UserInfo_new.setLeftButton(buildButton(itemNode));
							break;
						}
						buttonIdx ++;
						
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
						parser_UserInfo_new.setImage(buildImage(itemNode));
					}
					
					index ++;
				}
			}
		}
		return parser_UserInfo_new;
	}
	
	private Parser_TabButton buildTabButton(Node node){
		Parser_TabButton parserTabButton = new Parser_TabButton();
		parserTabButton.setModelType(MODELTYPE_TABBUTTON);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserTabButton.setPageId(pageIdNode.getNodeValue());
		}
		
		//解析子标签
		NodeList tabbuttonNodeList = node.getChildNodes();
		if(tabbuttonNodeList != null){
			int size = tabbuttonNodeList.getLength();
			for(int i = 0;i < size;i ++){
				Node buttonNode = tabbuttonNodeList.item(i);
				if(buttonNode.getNodeType() == Node.ELEMENT_NODE){
					if(buttonNode.getNodeName().equalsIgnoreCase(TAG_BUTTON)){
						parserTabButton.addButton(buildButton(buttonNode));
					}
				}
			}
		}
		
		return parserTabButton;
	}
	
	private Parser_UserBar buildUserBar(Node node){
		Parser_UserBar parserUserBar = new Parser_UserBar();
		parserUserBar.setModelType(MODELTYPE_USERBAR);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserUserBar.setPageId(pageIdNode.getNodeValue());
		}
		
		//解析子标签
		NodeList userbarNodeList = node.getChildNodes();
		if(userbarNodeList != null){
			int size = userbarNodeList.getLength();
			for(int i = 0;i < size;i ++){
				Node imageNode = userbarNodeList.item(i);
				if(imageNode.getNodeType() == Node.ELEMENT_NODE){
					if(imageNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
						parserUserBar.setLeftImage(buildImage(imageNode));
					}
				}
			}
		}
		
		return parserUserBar;
	}
	
	private Parser_List buildList(Node node){
		Parser_List parserList = new Parser_List();
		parserList.setModelType(MODELTYPE_LIST);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserList.setPageId(pageIdNode.getNodeValue());
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserList.setType(valueOf(typeNode.getNodeValue()));
		}
		//partrender
		Node partrenderNode = node.getAttributes().getNamedItem(PARAM_PARTRENDER);
		if(partrenderNode!=null&&partrenderNode.getNodeName().equalsIgnoreCase(PARAM_PARTRENDER)){
			parserList.setPartRender(valueOf(partrenderNode.getNodeValue()));
		}
		
		//解析子标签
		NodeList nodeList = node.getChildNodes();
		if(nodeList != null){
			int size = nodeList.getLength();
			for(int i = 0;i < size;i ++){
				Node itemNode = nodeList.item(i);
				if(itemNode.getNodeType() == Node.ELEMENT_NODE){
					switch (parserList.getType()) {
					case TYPE_ITEM_ONLYTEXT:
						parserList.addListItem(buildListItemOnlyText(itemNode));
						break;
					case TYPE_ITEM_FRIENDS:
						parserList.addListItem(buildListItemFriends(itemNode));
						break;
					case TYPE_ITEM_COMMENT_C:
						parserList.addListItem(buildListItemCommentC(itemNode));
						break;
					case TYPE_ITEM_COMMENT_U:
						parserList.addListItem(buildListItemCommentU(itemNode));
						break;
					}
				}
			}
		}
		
		return parserList;
	}
	
	private Parser_ListItem_OnlyText buildListItemOnlyText(Node node){
		Parser_ListItem_OnlyText parserListItemOnlyText = new Parser_ListItem_OnlyText();
		parserListItemOnlyText.setModelType(MODELTYPE_LISTITEM);
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserListItemOnlyText.setPageId(pageIdNode.getNodeValue());
		}
		//userid
		Node userIdNode = node.getAttributes().getNamedItem(PARAM_USERID);
		if(userIdNode != null&&userIdNode.getNodeName().equalsIgnoreCase(PARAM_USERID)){
			parserListItemOnlyText.setUserId(Integer.parseInt(userIdNode.getNodeValue()));
		}
		//href
		Node hrefNode = node.getAttributes().getNamedItem(PARAM_HREF);
		if(hrefNode!=null&&hrefNode.getNodeName().equalsIgnoreCase(PARAM_HREF)){
			parserListItemOnlyText.setHref(hrefNode.getNodeValue());
		}
		//action
		Node actionNode = node.getAttributes().getNamedItem(PARAM_ACTION);
		if(actionNode!=null&&actionNode.getNodeName().equalsIgnoreCase(PARAM_ACTION)){
			parserListItemOnlyText.setAction(valueOf(actionNode.getNodeValue()));
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserListItemOnlyText.setType(valueOf(typeNode.getNodeValue()));
		}
		//flush
		Node flushNode = node.getAttributes().getNamedItem(PARAM_FLUSH);
		if(flushNode!=null&&flushNode.getNodeName().equalsIgnoreCase(PARAM_FLUSH)){
			parserListItemOnlyText.setFlush(flushNode.getNodeValue());
		}
		//subjectid
		Node subjectIdNode = node.getAttributes().getNamedItem(PARAM_SUBJECTID);
		if(subjectIdNode != null&&subjectIdNode.getNodeName().equalsIgnoreCase(PARAM_SUBJECTID)){
			parserListItemOnlyText.setSubjectId(Integer.parseInt(subjectIdNode.getNodeValue()));
		}
		//commentid
		Node commentIdNode = node.getAttributes().getNamedItem(PARAM_COMMENTID);
		if(commentIdNode != null&&commentIdNode.getNodeName().equalsIgnoreCase(PARAM_COMMENTID)){
			parserListItemOnlyText.setCommentId(Integer.parseInt(commentIdNode.getNodeValue()));
		}
		
		//解析子标签
		NodeList onlytextNodeList = node.getChildNodes();
		if(onlytextNodeList != null){
			int size = onlytextNodeList.getLength();
			for(int i = 0;i < size;i ++){
				Node itemNode = onlytextNodeList.item(i);
				if(itemNode.getNodeType() == Node.ELEMENT_NODE){
					if(itemNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
						parserListItemOnlyText.setImage(buildImage(itemNode));
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_TEXT)){
						parserListItemOnlyText.setText(buildText(itemNode));
					}
				}
			}
		}
		
		return parserListItemOnlyText;
	}
	
	private Parser_ListItem_Friends buildListItemFriends(Node node){
		Parser_ListItem_Friends parserListItemFriends = new Parser_ListItem_Friends();
		parserListItemFriends.setModelType(MODELTYPE_LISTITEM);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserListItemFriends.setPageId(pageIdNode.getNodeValue());
		}
		//userid
		Node userIdNode = node.getAttributes().getNamedItem(PARAM_USERID);
		if(userIdNode != null&&userIdNode.getNodeName().equalsIgnoreCase(PARAM_USERID)){
			parserListItemFriends.setUserId(Integer.parseInt(userIdNode.getNodeValue()));
		}
		//href
		Node hrefNode = node.getAttributes().getNamedItem(PARAM_HREF);
		if(hrefNode!=null&&hrefNode.getNodeName().equalsIgnoreCase(PARAM_HREF)){
			parserListItemFriends.setHref(hrefNode.getNodeValue());
		}
		//action
		Node actionNode = node.getAttributes().getNamedItem(PARAM_ACTION);
		if(actionNode!=null&&actionNode.getNodeName().equalsIgnoreCase(PARAM_ACTION)){
			parserListItemFriends.setAction(valueOf(actionNode.getNodeValue()));
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserListItemFriends.setType(valueOf(typeNode.getNodeValue()));
		}
		//flush
		Node flushNode = node.getAttributes().getNamedItem(PARAM_FLUSH);
		if(flushNode!=null&&flushNode.getNodeName().equalsIgnoreCase(PARAM_FLUSH)){
			parserListItemFriends.setFlush(flushNode.getNodeValue());
		}
		//subjectid
		Node subjectIdNode = node.getAttributes().getNamedItem(PARAM_SUBJECTID);
		if(subjectIdNode != null&&subjectIdNode.getNodeName().equalsIgnoreCase(PARAM_SUBJECTID)){
			parserListItemFriends.setSubjectId(Integer.parseInt(subjectIdNode.getNodeValue()));
		}
		//commentid
		Node commentIdNode = node.getAttributes().getNamedItem(PARAM_COMMENTID);
		if(commentIdNode != null&&commentIdNode.getNodeName().equalsIgnoreCase(PARAM_COMMENTID)){
			parserListItemFriends.setCommentId(Integer.parseInt(commentIdNode.getNodeValue()));
		}
		
		//解析子标签
		NodeList friendsNodeList = node.getChildNodes();
		if(friendsNodeList != null){
			int size = friendsNodeList.getLength();
			int index = 0;
			for(int i = 0;i < size;i ++){
				Node itemNode = friendsNodeList.item(i);
				if(itemNode.getNodeType() == Node.ELEMENT_NODE){
					if(itemNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
						parserListItemFriends.setImage(buildImage(itemNode));
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_TEXT)){
						switch (index) {
						case 0:
							parserListItemFriends.setName(buildText(itemNode));
							break;
						case 1:
							parserListItemFriends.setGender(buildText(itemNode));
							break;
						case 2:
							parserListItemFriends.setAddr(buildText(itemNode));
							break;
						}
						
						index ++;
					}
				}
			}
		}
		
		return parserListItemFriends;
	}
	
	private Parser_ListItem_Comment_C buildListItemCommentC(Node node){
		Parser_ListItem_Comment_C parserListItemCommentC = new Parser_ListItem_Comment_C();
		parserListItemCommentC.setModelType(MODELTYPE_LISTITEM);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserListItemCommentC.setPageId(pageIdNode.getNodeValue());
		}
		//userid
		Node userIdNode = node.getAttributes().getNamedItem(PARAM_USERID);
		if(userIdNode != null&&userIdNode.getNodeName().equalsIgnoreCase(PARAM_USERID)){
			parserListItemCommentC.setUserId(Integer.parseInt(userIdNode.getNodeValue()));
		}
		//href
		Node hrefNode = node.getAttributes().getNamedItem(PARAM_HREF);
		if(hrefNode!=null&&hrefNode.getNodeName().equalsIgnoreCase(PARAM_HREF)){
			parserListItemCommentC.setHref(hrefNode.getNodeValue());
		}
		//action
		Node actionNode = node.getAttributes().getNamedItem(PARAM_ACTION);
		if(actionNode!=null&&actionNode.getNodeName().equalsIgnoreCase(PARAM_ACTION)){
			parserListItemCommentC.setAction(valueOf(actionNode.getNodeValue()));
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserListItemCommentC.setType(valueOf(typeNode.getNodeValue()));
		}
		//flush
		Node flushNode = node.getAttributes().getNamedItem(PARAM_FLUSH);
		if(flushNode!=null&&flushNode.getNodeName().equalsIgnoreCase(PARAM_FLUSH)){
			parserListItemCommentC.setFlush(flushNode.getNodeValue());
		}
		//subjectid
		Node subjectIdNode = node.getAttributes().getNamedItem(PARAM_SUBJECTID);
		if(subjectIdNode != null&&subjectIdNode.getNodeName().equalsIgnoreCase(PARAM_SUBJECTID)){
			parserListItemCommentC.setSubjectId(Integer.parseInt(subjectIdNode.getNodeValue()));
		}
		//commentid
		Node commentIdNode = node.getAttributes().getNamedItem(PARAM_COMMENTID);
		if(commentIdNode != null&&commentIdNode.getNodeName().equalsIgnoreCase(PARAM_COMMENTID)){
			parserListItemCommentC.setCommentId(Integer.parseInt(commentIdNode.getNodeValue()));
		}
		
		//解析子标签
		NodeList friendsNodeList = node.getChildNodes();
		if(friendsNodeList != null){
			int size = friendsNodeList.getLength();
			int index = 0;
			for(int i = 0;i < size;i ++){
				Node itemNode = friendsNodeList.item(i);
				if(itemNode.getNodeType() == Node.ELEMENT_NODE){
					if(itemNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
						parserListItemCommentC.setImage(buildImage(itemNode));
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_TEXT)){
						switch (index) {
						case 0:
							parserListItemCommentC.setName(buildText(itemNode));
							break;
						case 1:
							parserListItemCommentC.setCommentTime(buildText(itemNode));
							break;
						case 2:
							parserListItemCommentC.setCommentMsg(buildText(itemNode));
							break;
						}
						
						index ++;
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_BUTTON)){
						parserListItemCommentC.setCommentButton(buildButton(itemNode));
					}
				}
			}
		}
		
		return parserListItemCommentC;
	}

	private Parser_ListItem_Comment_U buildListItemCommentU(Node node){
		Parser_ListItem_Comment_U parserListItemCommentU = new Parser_ListItem_Comment_U();
		parserListItemCommentU.setModelType(MODELTYPE_LISTITEM);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserListItemCommentU.setPageId(pageIdNode.getNodeValue());
		}
		//userid
		Node userIdNode = node.getAttributes().getNamedItem(PARAM_USERID);
		if(userIdNode != null&&userIdNode.getNodeName().equalsIgnoreCase(PARAM_USERID)){
			parserListItemCommentU.setUserId(Integer.parseInt(userIdNode.getNodeValue()));
		}
		//href
		Node hrefNode = node.getAttributes().getNamedItem(PARAM_HREF);
		if(hrefNode!=null&&hrefNode.getNodeName().equalsIgnoreCase(PARAM_HREF)){
			parserListItemCommentU.setHref(hrefNode.getNodeValue());
		}
		//action
		Node actionNode = node.getAttributes().getNamedItem(PARAM_ACTION);
		if(actionNode!=null&&actionNode.getNodeName().equalsIgnoreCase(PARAM_ACTION)){
			parserListItemCommentU.setAction(valueOf(actionNode.getNodeValue()));
		}
		//type
		Node typeNode = node.getAttributes().getNamedItem(PARAM_TYPE);
		if(typeNode!=null&&typeNode.getNodeName().equalsIgnoreCase(PARAM_TYPE)){
			parserListItemCommentU.setType(valueOf(typeNode.getNodeValue()));
		}
		//flush
		Node flushNode = node.getAttributes().getNamedItem(PARAM_FLUSH);
		if(flushNode!=null&&flushNode.getNodeName().equalsIgnoreCase(PARAM_FLUSH)){
			parserListItemCommentU.setFlush(flushNode.getNodeValue());
		}
		//subjectid
		Node subjectIdNode = node.getAttributes().getNamedItem(PARAM_SUBJECTID);
		if(subjectIdNode != null&&subjectIdNode.getNodeName().equalsIgnoreCase(PARAM_SUBJECTID)){
			parserListItemCommentU.setSubjectId(Integer.parseInt(subjectIdNode.getNodeValue()));
		}
		//commentid
		Node commentIdNode = node.getAttributes().getNamedItem(PARAM_COMMENTID);
		if(commentIdNode != null&&commentIdNode.getNodeName().equalsIgnoreCase(PARAM_COMMENTID)){
			parserListItemCommentU.setCommentId(Integer.parseInt(commentIdNode.getNodeValue()));
		}
		
		//解析子标签
		NodeList friendsNodeList = node.getChildNodes();
		if(friendsNodeList != null){
			int size = friendsNodeList.getLength();
			int index = 0;
			for(int i = 0;i < size;i ++){
				Node itemNode = friendsNodeList.item(i);
				if(itemNode.getNodeType() == Node.ELEMENT_NODE){
					if(itemNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
						parserListItemCommentU.setImage(buildImage(itemNode));
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_TEXT)){
						switch (index) {
						case 0:
							parserListItemCommentU.setName(buildText(itemNode));
							break;
						case 1:
							parserListItemCommentU.setCommentTime(buildText(itemNode));
							break;
						case 2:
							parserListItemCommentU.setCommentMsg(buildText(itemNode));
							break;
						}
						
						index ++;
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_BUTTON)){
						parserListItemCommentU.setCommentButton(buildButton(itemNode));
					}
				}
			}
		}
		
		return parserListItemCommentU;
	}
	
	private Parser_Like buildLike(Node node){
		Parser_Like parserLike = new Parser_Like();
		Parser_LikeItem parserLikeItem = new Parser_LikeItem();
		parserLike.setModelType(MODELTYPE_LIKE);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserLike.setPageId(pageIdNode.getNodeValue());
		}
		
		//解析子标签
		NodeList likeNodeList = node.getChildNodes();
		if(likeNodeList != null){
			int size = likeNodeList.getLength();
			for(int i = 0;i < size;i ++){
				Node likeNode = likeNodeList.item(i);
				if(likeNode.getNodeType() == Node.ELEMENT_NODE){
					if(likeNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
						parserLikeItem.addImage(buildImage(likeNode));
					}
				}
			}
			parserLike.setLikeItem(parserLikeItem);
		}
		
		return parserLike;
	}
	
//	private Parser_LikeItem buildLikeItem(Node node){
//		Parser_LikeItem parserLikeItem = new Parser_LikeItem();
//		//pageid
//		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
//		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
//			parserLikeItem.setPageId(pageIdNode.getNodeValue());
//		}
//		
//		//解析子标签
//		NodeList likeItemNodeList = node.getChildNodes();
//		if(likeItemNodeList != null){
//			int size = likeItemNodeList.getLength();
//			for(int i = 0;i < size;i ++){
//				Node imageNode = likeItemNodeList.item(i);
//				if(imageNode.getNodeType() == Node.ELEMENT_NODE){
//					if(imageNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
//						parserLikeItem.addImage(buildImage(imageNode));
//					}
//				}
//			}
//		}
//		
//		return parserLikeItem;
//	}
	
	private Parser_Commodity buildCommodity(Node node){
		Parser_Commodity parserCommodity = new Parser_Commodity();
		parserCommodity.setModelType(MODELTYPE_COMMODITY);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserCommodity.setPageId(pageIdNode.getNodeValue());
		}
		
		//解析子标签
		NodeList commodityNodeList = node.getChildNodes();
		if(commodityNodeList != null){
			int size = commodityNodeList.getLength();
			int index = 0;
			for(int i = 0;i < size;i ++){
				Node itemNode = commodityNodeList.item(i);
				if(itemNode.getNodeType() == Node.ELEMENT_NODE){
					if(itemNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
						parserCommodity.setImage(buildImage(itemNode));
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_TEXT)){
						switch (index) {
						case 0:
							parserCommodity.setCommodityName(buildText(itemNode));
							break;
						case 1:
							parserCommodity.setCommodityInfo(buildText(itemNode));
							break;
						}
						
						index ++;
					}
				}
			}
		}
		
		return parserCommodity;
	}
	
	private Parser_CommodityInfo buildCommodityInfo(Node node){
		Parser_CommodityInfo parserCommodityInfo = new Parser_CommodityInfo();
		parserCommodityInfo.setModelType(MODELTYPE_COMMODITYINFO);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserCommodityInfo.setPageId(pageIdNode.getNodeValue());
		}
		
		//解析子标签
		NodeList commodityInfoNodeList = node.getChildNodes();
		if(commodityInfoNodeList != null){
			int size = commodityInfoNodeList.getLength();
			int index = 0;
			for(int i = 0;i < size;i ++){
				Node itemNode = commodityInfoNodeList.item(i);
				if(itemNode.getNodeType() == Node.ELEMENT_NODE){
					if(itemNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
						parserCommodityInfo.setImage(buildImage(itemNode));
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_BUTTON)){
						switch (index) {
						case 1:
							parserCommodityInfo.setFormButton(buildButton(itemNode));
							break;
						case 2:
							parserCommodityInfo.setLikeButton(buildButton(itemNode));
							break;
						}
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_TEXT)){
						switch (index) {
						case 3:
							parserCommodityInfo.setCommodityInfo(buildText(itemNode));
							break;
						case 4:
							parserCommodityInfo.setCommodityTime(buildText(itemNode));
							break;
						}
					}
					
					index ++;
				}
			}
		}
		
		return parserCommodityInfo;
	}
	
	private Parser_CommodityBar buildCommodityBar(Node node){
		Parser_CommodityBar parserCommodityBar = new Parser_CommodityBar();
		parserCommodityBar.setModelType(MODELTYPE_COMMODITYBAR);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserCommodityBar.setPageId(pageIdNode.getNodeValue());
		}
		
		//解析子标签
		NodeList commodityBarNodeList = node.getChildNodes();
		if(commodityBarNodeList != null){
			int size = commodityBarNodeList.getLength();
			for(int i = 0;i < size;i ++){
				Node itemNode = commodityBarNodeList.item(i);
				if(itemNode.getNodeType() == Node.ELEMENT_NODE){
					if(itemNode.getNodeName().equalsIgnoreCase(TAG_COMMODITYBUTTON)){
						parserCommodityBar.setCommodityButton(buildCommodityButton(itemNode));
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_ACTIVITYBUTTON)){
						parserCommodityBar.setActivityButton(buildActivityButton(itemNode));
					}
				}
			}
		}
		
		return parserCommodityBar;
	}
	
	private Parser_Coquetry buildCoquetry(Node node){
		Parser_Coquetry parserCoquetry = new Parser_Coquetry();
		parserCoquetry.setModelType(MODELTYPE_COQUETRY);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserCoquetry.setPageId(pageIdNode.getNodeValue());
		}
		//partrender
		Node partrenderNode = node.getAttributes().getNamedItem(PARAM_PARTRENDER);
		if(partrenderNode!=null&&partrenderNode.getNodeName().equalsIgnoreCase(PARAM_PARTRENDER)){
			parserCoquetry.setPartRender(valueOf(partrenderNode.getNodeValue()));
		}
		//empty
		Node emptyNode = node.getAttributes().getNamedItem(PARAM_EMPTY);
		if(emptyNode!=null&&emptyNode.getNodeName().equalsIgnoreCase(PARAM_EMPTY)){
			parserCoquetry.setEmpty(valueOf(emptyNode.getNodeValue()));
		}
		
		//解析子标签
		NodeList nodeList = node.getChildNodes();
		if(nodeList != null){
			int size = nodeList.getLength();
			for(int i = 0;i < size;i ++){
				Node itemNode = nodeList.item(i);
				if(itemNode.getNodeType() == Node.ELEMENT_NODE){
					if(itemNode.getNodeName().equalsIgnoreCase(TAG_COQUETRYITEM)){
						Parser_Coquetryitem item = buildCoquetryItem(itemNode);
						parserCoquetry.addCoquetryList(item);
						parserCoquetry.addUrls(item.getHref());
					}
				}
			}
		}
		
		return parserCoquetry;
	}
	
	private Parser_Coquetryitem buildCoquetryItem(Node node){
		Parser_Coquetryitem parserCoquetryitem = new Parser_Coquetryitem();
		parserCoquetryitem.setModelType(MODELTYPE_COQUETRYITEM);
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserCoquetryitem.setPageId(pageIdNode.getNodeValue());
		}
		
		//href
		Node hrefNode = node.getAttributes().getNamedItem(PARAM_HREF);
		if(hrefNode != null&&hrefNode.getNodeName().equalsIgnoreCase(PARAM_HREF)){
			parserCoquetryitem.setHref(hrefNode.getNodeValue());
		}
		
		//解析子标签
		NodeList nodeList = node.getChildNodes();
		if(nodeList != null){
			int size = nodeList.getLength();
			int index = 0;
			for(int i = 0;i < size;i ++){
				Node itemNode = nodeList.item(i);
				if(itemNode.getNodeType() == Node.ELEMENT_NODE){
					if(itemNode.getNodeName().equalsIgnoreCase(TAG_IMAGE)){
						parserCoquetryitem.setImage(buildImage(itemNode));
					}else if(itemNode.getNodeName().equalsIgnoreCase(TAG_TEXT)){
						switch (index) {
						case 0:
							parserCoquetryitem.setTime(buildText(itemNode));
							break;
						case 1:
							parserCoquetryitem.setTo(buildText(itemNode));
							break;
						}
						index ++;
					}
				}
			}
		}
		
		return parserCoquetryitem;
	}
	
	private Parser_StylePage buildStylePage(Node node){
		Parser_StylePage parserStylePage = new Parser_StylePage();
		parserStylePage.setModelType(MODELTYPE_STYLEPAGE);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserStylePage.setPageId(pageIdNode.getNodeValue());
		}
		//解析子标签
		NodeList nodeList = node.getChildNodes();
		if(nodeList != null){
			int size = nodeList.getLength();
			for(int i = 0;i < size;i ++){
				Node itemNode = nodeList.item(i);
				if(itemNode.getNodeType() == Node.ELEMENT_NODE){
					if(itemNode.getNodeName().equalsIgnoreCase(TAG_STYLEITEM)){
						parserStylePage.setItems(buildStyleItem(itemNode));
					}
				}
			}
		}
		
		return parserStylePage;
	}
	
	private Parser_StyleItem buildStyleItem(Node node){
		Parser_StyleItem parserStyleItem = new Parser_StyleItem();
		parserStyleItem.setModelType(MODELTYPE_STYLEITEM);
		
		//pageid
		Node pageIdNode = node.getAttributes().getNamedItem(PARAM_PAGEID);
		if(pageIdNode != null&&pageIdNode.getNodeName().equalsIgnoreCase(PARAM_PAGEID)){
			parserStyleItem.setPageId(pageIdNode.getNodeValue());
		}
		//src
		Node srcNode = node.getAttributes().getNamedItem(PARAM_SRC);
		if(srcNode != null&&srcNode.getNodeName().equalsIgnoreCase(PARAM_SRC)){
			parserStyleItem.setSrc(srcNode.getNodeValue());
		}
		//str
		Node strNode = node.getAttributes().getNamedItem(PARAM_STR);
		if(strNode != null&&strNode.getNodeName().equalsIgnoreCase(PARAM_STR)){
			parserStyleItem.setStr(strNode.getNodeValue());
		}
		//id
		Node idNode = node.getAttributes().getNamedItem(PARAM_ID);
		if(idNode != null&&idNode.getNodeName().equalsIgnoreCase(PARAM_ID)){
			parserStyleItem.setId(idNode.getNodeValue());
		}
		//selected
		Node selectedNode = node.getAttributes().getNamedItem(PARAM_SELECTED);
		if(selectedNode!=null&&selectedNode.getNodeName().equalsIgnoreCase(PARAM_SELECTED)){
			parserStyleItem.setSelected(valueOf(selectedNode.getNodeValue()));
		}
		
		return parserStyleItem;
	}
}
