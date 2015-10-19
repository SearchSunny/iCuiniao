/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Commodity;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;

/**
 * @author lenovo
 *
 */
public class View_Commodity extends XmlViewLayout{

	private Parser_Commodity parserCommodity;
	private ImageView commodityicon;
	
	public View_Commodity(Context context, Parser_Commodity parserCommodity, Handler handler) {
		super(context, handler,null);
		// TODO Auto-generated constructor stub
		this.parserCommodity = parserCommodity;
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.commodity,null);
		commodityicon = (ImageView)l.findViewById(R.id.commodityicon);
		TextView commodityname = (TextView)l.findViewById(R.id.commodityname);
		TextView commoditytext = (TextView)l.findViewById(R.id.commoditytext);
		
		String commoditynameString = parserCommodity.getCommodityName().getStr();
		commoditynameString = commoditynameString.replaceAll("#", "\n");
		String commoditytextString = parserCommodity.getCommodityInfo().getStr();
		commoditytextString = commoditytextString.replaceAll("#", "\n");
		commodityname.setText(commoditynameString);
		commoditytext.setText(commoditytextString);
		
		String url = parserCommodity.getImage().getSrc();
		if(parserCommodity.getImage().getSrc() == null||parserCommodity.getImage().getSrc().equalsIgnoreCase("null")||parserCommodity.getImage().getSrc().equalsIgnoreCase("")){
			url = null;
		}
		//压入下载队列
		DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_COMMODITY, 0, url, parserCommodity.getPageId(),0);
		DownImageManager.add(downImageItem);
		addView(l);
	}
	
	public void setImageView(byte[] data){
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp == null)return;
		if(commodityIcon_width == 147){
			temp = CommonUtil.resizeImage(temp, commodityIcon_width+4, commodityIcon_height+4);
		}else{
			temp = CommonUtil.resizeImage(temp, commodityIcon_width+3, commodityIcon_height+3);
		}
		Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.commodityiconbg);
		commodityicon.setImageBitmap(CommonUtil.mergerIcon(background, temp,15,5,4));
	}
	
	private int commodityIcon_width,commodityIcon_height;
	public void initImageSize(int dpi){
		if(dpi <= 120){//qvga
			commodityIcon_width = 46;
			commodityIcon_height = 46;
		}else if(dpi <= 160){//hvga
			commodityIcon_width = 62;
			commodityIcon_height = 62;
		}else if(dpi <= 320){//wvga
			commodityIcon_width = 92;
			commodityIcon_height = 92;
			if(CommonUtil.screen_height >= 960){
				commodityIcon_width = 147;
				commodityIcon_height = 147;
			}
		}else{//更大屏幕分辨率
			commodityIcon_width = 92;
			commodityIcon_height = 92;
		}
	}

}
