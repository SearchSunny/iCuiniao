/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Image;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_LikeItem;
import com.cmmobi.icuiniao.ui.adapter.GridViewAdapter;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;

/**
 * @author hw
 *
 */
public class View_LikeDialog extends Dialog implements OnGestureListener{

	private final static int LIKE_MAX_NUM = 1;//like组件的最大数量
	private final static int LIKEITEM_MAX_NUM = 15;//like的子组件的最大数量
	
	private int startIndex;//下载队列开始的索引号
	private int viewIndex = 0;
	
	private LinearLayout l;
	private ViewFlipper poplike_viewflipper;
	private GridView poplike_grid1;
//	private GridView poplike_grid2;
//	private GridView poplike_grid3;
//	private GridView poplike_grid4;
//	private GridView poplike_grid5;
//	private Button poplike_input;
	
	private Bitmap[] bitmaps0;
//	private Bitmap[] bitmaps1;
//	private Bitmap[] bitmaps2;
//	private Bitmap[] bitmaps3;
//	private Bitmap[] bitmaps4;
	private GridViewAdapter[] adapters;
	
	private GestureDetector detector;
	private ArrayList<Parser_LikeItem> likeItems;
	private XmlViewLayout xmlViewLayout;
	
	protected View_LikeDialog(Context context,int theme, XmlViewLayout xmlViewLayout) {
		super(context,theme);
		// TODO Auto-generated constructor stub
		this.xmlViewLayout = xmlViewLayout;
		bitmaps0 = new Bitmap[LIKEITEM_MAX_NUM];
//		bitmaps1 = new Bitmap[LIKEITEM_MAX_NUM];
//		bitmaps2 = new Bitmap[LIKEITEM_MAX_NUM];
//		bitmaps3 = new Bitmap[LIKEITEM_MAX_NUM];
//		bitmaps4 = new Bitmap[LIKEITEM_MAX_NUM];
		adapters = new GridViewAdapter[LIKE_MAX_NUM];
		likeItems = new ArrayList<Parser_LikeItem>();
		startIndex = DownImageManager.Size();
		viewIndex = 0;
		
		l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.poplike, null);
		poplike_viewflipper = (ViewFlipper)l.findViewById(R.id.poplike_viewflipper);
		poplike_grid1 = (GridView)l.findViewById(R.id.poplike_grid1);
//		poplike_grid2 = (GridView)l.findViewById(R.id.poplike_grid2);
//		poplike_grid3 = (GridView)l.findViewById(R.id.poplike_grid3);
//		poplike_grid4 = (GridView)l.findViewById(R.id.poplike_grid4);
//		poplike_grid5 = (GridView)l.findViewById(R.id.poplike_grid5);
//		poplike_input = (Button)l.findViewById(R.id.poplike_input);
//		poplike_input.setOnClickListener(inputClickListener);
		
		adapters[0] = new GridViewAdapter(bitmaps0,getContext());
		poplike_grid1.setAdapter(adapters[0]);
		
		detector = new GestureDetector(this);
		poplike_grid1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return detector.onTouchEvent(event);
			}
		});
		poplike_grid1.setOnItemClickListener(grid1ItemClickListener);
//		poplike_grid2.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				return detector.onTouchEvent(event);
//			}
//		});
//		poplike_grid2.setOnItemClickListener(grid2ItemClickListener);
//		poplike_grid3.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				return detector.onTouchEvent(event);
//			}
//		});
//		poplike_grid3.setOnItemClickListener(grid3ItemClickListener);
//		poplike_grid4.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				return detector.onTouchEvent(event);
//			}
//		});
//		poplike_grid4.setOnItemClickListener(grid4ItemClickListener);
//		poplike_grid5.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				return detector.onTouchEvent(event);
//			}
//		});
//		poplike_grid5.setOnItemClickListener(grid5ItemClickListener);
		
		addContentView(l, new LayoutParams(CommonUtil.screen_width-40, LayoutParams.WRAP_CONTENT));
	}
	
	public void add(Parser_LikeItem parserLikeItem){
		likeItems.add(parserLikeItem);
		ArrayList<Parser_Image> tmpImages = parserLikeItem.getImages();
		for(int i = 0;i < tmpImages.size();i ++){
			String url = tmpImages.get(i).getSrc();
			if(tmpImages.get(i).getSrc() == null||tmpImages.get(i).getSrc().equalsIgnoreCase("null")||tmpImages.get(i).getSrc().equalsIgnoreCase("")){
				url = null;
			}
			DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_LIKE, url, tmpImages.get(i).getPageId(),0);
			DownImageManager.add(downImageItem);
		}
	}
	
	public void setImageView(final byte[] data, int index,final String cacheUrl){
		try {
			//保存图片
			new Thread(){
				public void run(){
					CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(cacheUrl), data);
				}
			}.start();
		} catch (Exception e) {
		}
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp == null)return;
		Bitmap background = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.usericonbg);
		temp = CommonUtil.resizeImage(temp, background.getWidth(), background.getHeight());
		index = index-startIndex;
		
		adapters[0] = new GridViewAdapter(bitmaps0,getContext());
		poplike_grid1.setAdapter(adapters[0]);
		adapters[0].notifyDataSetChanged();
		
		switch (index/LIKEITEM_MAX_NUM) {
		case 0:
			bitmaps0[index%LIKEITEM_MAX_NUM] = CommonUtil.mergerIcon(background, temp,7);
			break;
//		case 1:
//			bitmaps1[index%LIKEITEM_MAX_NUM] = CommonUtil.mergerIcon(background, temp,10);
//			break;
//		case 2:
//			bitmaps2[index%LIKEITEM_MAX_NUM] = CommonUtil.mergerIcon(background, temp,10);
//			break;
//		case 3:
//			bitmaps3[index%LIKEITEM_MAX_NUM] = CommonUtil.mergerIcon(background, temp,10);
//			break;
//		case 4:
//			bitmaps4[index%LIKEITEM_MAX_NUM] = CommonUtil.mergerIcon(background, temp,10);
//			break;
		}
	}
	
	private OnItemClickListener grid1ItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if(likeItems.size() > 0){
				if(arg2 < likeItems.get(0).getImages().size()){
					if(likeItems.get(0).getImages().get(arg2).getHref()!=null&&likeItems.get(0).getImages().get(arg2).getHref().length() > 0){
						xmlViewLayout.onPopLikeGridItemClick(likeItems.get(0).getImages().get(arg2).getHref());
					}
					cancel();
				}
			}
		}
	};
	
//	private OnItemClickListener grid2ItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			// TODO Auto-generated method stub
//			if(likeItems.size() > 1){
//				if(arg2 < likeItems.get(1).getImages().size()){
//					xmlViewLayout.onPopLikeGridItemClick(likeItems.get(1).getImages().get(arg2).getHref());
//					cancel();
//				}
//			}
//		}
//	};
//	
//	private OnItemClickListener grid3ItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			// TODO Auto-generated method stub
//			if(likeItems.size() > 2){
//				if(arg2 < likeItems.get(2).getImages().size()){
//					xmlViewLayout.onPopLikeGridItemClick(likeItems.get(2).getImages().get(arg2).getHref());
//					cancel();
//				}
//			}
//		}
//	};
//	
//	private OnItemClickListener grid4ItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			// TODO Auto-generated method stub
//			if(likeItems.size() > 3){
//				if(arg2 < likeItems.get(3).getImages().size()){
//					xmlViewLayout.onPopLikeGridItemClick(likeItems.get(3).getImages().get(arg2).getHref());
//					cancel();
//				}
//			}
//		}
//	};
//	
//	private OnItemClickListener grid5ItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			// TODO Auto-generated method stub
//			if(likeItems.size() > 4){
//				if(arg2 < likeItems.get(4).getImages().size()){
//					xmlViewLayout.onPopLikeGridItemClick(likeItems.get(4).getImages().get(arg2).getHref());
//					cancel();
//				}
//			}
//		}
//	};
	
//	private View.OnClickListener inputClickListener = new View.OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			xmlViewLayout.onCommentInputBoxClick();
//		}
//	};

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
//		if(e1.getX() - e2.getX() > 50) {
//			poplike_viewflipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_right_in)); 
//			poplike_viewflipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_right_out)); 
//			poplike_viewflipper.showNext();
//			switch (viewIndex) {
//			case 0:
//				adapters[1] = new GridViewAdapter(bitmaps1,getContext());
//				poplike_grid2.setAdapter(adapters[1]);
//				viewIndex++;
//				break;
//			case 1:
//				adapters[2] = new GridViewAdapter(bitmaps2,getContext());
//				poplike_grid3.setAdapter(adapters[2]);
//				viewIndex++;
//				break;
//			case 2:
//				adapters[3] = new GridViewAdapter(bitmaps3,getContext());
//				poplike_grid4.setAdapter(adapters[3]);
//				viewIndex++;
//				break;
//			case 3:
//				adapters[4] = new GridViewAdapter(bitmaps4,getContext());
//				poplike_grid5.setAdapter(adapters[4]);
//				viewIndex++;
//				break;
//			case 4:
//				adapters[0] = new GridViewAdapter(bitmaps0,getContext());
//				poplike_grid1.setAdapter(adapters[0]);
//				viewIndex = 0;
//				break;
//			}
//		}else if(e2.getX() - e1.getX() > 50) {
//			poplike_viewflipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in)); 
//			poplike_viewflipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_left_out)); 
//			poplike_viewflipper.showPrevious();
//			switch (viewIndex) {
//			case 0:
//				adapters[4] = new GridViewAdapter(bitmaps4,getContext());
//				poplike_grid5.setAdapter(adapters[4]);
//				viewIndex = 4;;
//				break;
//			case 1:
//				adapters[0] = new GridViewAdapter(bitmaps0,getContext());
//				poplike_grid1.setAdapter(adapters[0]);
//				viewIndex--;
//				break;
//			case 2:
//				adapters[1] = new GridViewAdapter(bitmaps1,getContext());
//				poplike_grid2.setAdapter(adapters[1]);
//				viewIndex--;
//				break;
//			case 3:
//				adapters[2] = new GridViewAdapter(bitmaps2,getContext());
//				poplike_grid3.setAdapter(adapters[2]);
//				viewIndex--;
//				break;
//			case 4:
//				adapters[3] = new GridViewAdapter(bitmaps3,getContext());
//				poplike_grid4.setAdapter(adapters[3]);
//				viewIndex--;
//				break;
//			}
//		}else{
//			return false;
//		}
		return true;
	}
	
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
