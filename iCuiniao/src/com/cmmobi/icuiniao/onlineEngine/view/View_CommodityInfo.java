/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_CommodityInfo;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Image;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author hw
 *
 */
public class View_CommodityInfo extends XmlViewLayout{

	private Parser_CommodityInfo parserCommodityInfo;
	private String likeBtnUrl;
	private int likeBtnAction;
	private LinearLayout lcommoditybox;
	private ImageView likebutton;
	public ImageView getLikebutton() {
		return likebutton;
	}

	private TextView likeTextView;
	private Handler mHandler;
	public View_CommodityInfo(Context context, Parser_CommodityInfo parserCommodityInfo, Handler handler) {
		super(context, handler,null);
		// TODO Auto-generated constructor stub
		mHandler = handler;
		this.parserCommodityInfo = parserCommodityInfo;
		LinearLayout l = null;
		l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.commodityinfo, null);
		LinearLayout llikebutton = (LinearLayout)l.findViewById(R.id.llikebutton);
		likebutton = (ImageView)l.findViewById(R.id.likebutton);
		TextView formTextView = (TextView)l.findViewById(R.id.formbuttontext);
		likeTextView = (TextView)l.findViewById(R.id.likebuttontext);
		lcommoditybox = (LinearLayout)l.findViewById(R.id.lcommoditybox);
		TextView commodityinfoboxTextView = (TextView)l.findViewById(R.id.commodityinfoboxtext);
		TextView commodityinfoboxtimeTextView = (TextView)l.findViewById(R.id.commodityinfoboxtime);
		
		setLikeButtonBgImage(getLikeState());
		if(parserCommodityInfo.getFormButton()!=null){
			formTextView.setText(parserCommodityInfo.getFormButton().getStr());
		}
		if(parserCommodityInfo.getLikeButton()!=null){
			likeTextView.setText(parserCommodityInfo.getLikeButton().getStr());
		}
		if(parserCommodityInfo.getCommodityInfo()!=null){
			String tempstr = parserCommodityInfo.getCommodityInfo().getStr();
			tempstr = tempstr.replaceAll("#", "\n");
			commodityinfoboxTextView.setText(tempstr);
		}
		if(parserCommodityInfo.getCommodityTime()!=null){
//			commodityinfoboxtimeTextView.setText(parserCommodityInfo.getCommodityTime().getStr());
			//取消时间显示
			commodityinfoboxtimeTextView.setText("");
		}
		
		if(parserCommodityInfo.getFormButton()!=null){
			if(parserCommodityInfo.getFormButton().getStr().length() > 0){
				if(Integer.parseInt(parserCommodityInfo.getFormButton().getStr()) >= 0){
					formTextView.setOnClickListener(formClickListener);
				}
			}
		}
		if(parserCommodityInfo.getLikeButton()!=null){
			if(parserCommodityInfo.getLikeButton().getStr().length() > 0){
				if(Integer.parseInt(parserCommodityInfo.getLikeButton().getStr()) >= 0){
					llikebutton.setOnClickListener(likeClickListener);
				}
			}
		}
		lcommoditybox.setOnClickListener(commodityboxClickListener);
		
		if(parserCommodityInfo.getLikeButton()!=null){
			likeBtnUrl = parserCommodityInfo.getLikeButton().getHref();
		}
		if(parserCommodityInfo.getLikeButton()!=null){
			likeBtnAction = parserCommodityInfo.getLikeButton().getAction();
		}
		
		addView(l);
	}

	int flingX,flingY;
	boolean isFling;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			flingX = (int)ev.getRawX();
			flingY = (int)ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			int tmp = (int)ev.getRawX();
			int tmpy = (int)ev.getRawY();
			if(Math.abs(tmpy-flingY) <= 100){
				if(flingX - tmp > 100){
					isFling = true;
					mHandler.sendEmptyMessage(MessageID.MESSAGE_FLING_NEXT);
				}else if(tmp - flingX > 100){
					isFling = true;
					mHandler.sendEmptyMessage(MessageID.MESSAGE_FLING_PRE);
				}else{
					isFling = false;
				}
			}else{
				isFling = false;
			}
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	private OnClickListener formClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onCommodityInfoFormButtonClick(parserCommodityInfo.getFormButton().getHref(), parserCommodityInfo.getFormButton().getAction());
		}
	};
	
	private OnClickListener likeClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onCommodityInfoLikeButtonClick(parserCommodityInfo.getLikeButton().getHref(), parserCommodityInfo.getLikeButton().getAction());
		}
	};
	
	private OnClickListener commodityboxClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(!isFling){
				onCommodityInfoBoxClick();
			}
			isFling = false;
		}
	};
	
	public Parser_Image getParserImage(){
		return parserCommodityInfo.getImage();
	}
	
	public String getLikeBtnUrl(){
		return likeBtnUrl;
	}
	
	public int getLikeBtnAction(){
		return likeBtnAction;
	}
	
	public String getCommodityInfoString(){
		if(parserCommodityInfo.getCommodityInfo()!=null){
			return parserCommodityInfo.getCommodityInfo().getStr();
		}
		return null;
	}
	
	public void changeBackGround(){
		post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				lcommoditybox.setBackgroundResource(R.drawable.commodityinfoboxbg);
			}
		});
	}
	
	public boolean getLikeState(){
		if(parserCommodityInfo.getLikeButton()!=null){
			if(parserCommodityInfo.getLikeButton().getStr().length() > 0){
				return parserCommodityInfo.getLikeButton().getLike();
			}
		}
		return false;
	}
	
	public int getDeleteId(){
		if(parserCommodityInfo.getLikeButton()!=null){
			if(parserCommodityInfo.getLikeButton().getStr().length() > 0){
				return parserCommodityInfo.getLikeButton().getLikeId();
			}
		}
		return -1;
	}
	
	public void setLikeButtonBgImage(boolean isLike){
		if(likebutton != null){
			if(isLike){
				parserCommodityInfo.getLikeButton().setLike(Parser_Layout_AbsLayout.TRUE);
				likebutton.setBackgroundResource(R.drawable.likebutton_f);
			}else{
				parserCommodityInfo.getLikeButton().setLike(Parser_Layout_AbsLayout.FALSE);
				likebutton.setBackgroundResource(R.drawable.likebutton);
			}
		}
	}
	
	public void setLikeNum(String num){
		if(likeTextView != null){
			if(num != null&&num.length() > 0){
				LogPrint.Print("num = "+num);
				likeTextView.setText(num);
			}
		}
	}
	
}
