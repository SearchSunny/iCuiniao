package com.cmmobi.icuiniao.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.entity.DateObj;
import com.cmmobi.icuiniao.entity.ProductItem;
import com.cmmobi.icuiniao.entity.Products;
import com.cmmobi.icuiniao.onlineEngine.activity.MyPageActivityA;
import com.cmmobi.icuiniao.util.AsyncImageLoader;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.AsyncImageLoader.ImageCallback;

public class UserPageNewAdapter  extends BaseAdapter{

	private Context context;
	private Handler handler;
	private LayoutInflater mInflater;
	private ProductItem productItem;
	private AsyncImageLoader asyncImageLoader;
	private int uid;
	
	public void setProductItem(ProductItem productItem) {
		this.productItem = productItem;
	}
	public UserPageNewAdapter(Context context, int uid, Handler handler){
		this.context = context;
		this.handler = handler;
		productItem = new ProductItem();
		mInflater = LayoutInflater.from(context);
		asyncImageLoader = new AsyncImageLoader(context);
		this.uid = uid;
	}
	@Override
	public int getCount() {
		return productItem.arrProducts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LogPrint.Print("list", "user pos=" + position);
		Holder holder = null;
		if(convertView == null){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.listitem_userproducts, null);
			holder.showtime = (TextView)convertView.findViewById(R.id.showtime);
			holder.procount = (TextView)convertView.findViewById(R.id.procount);
			holder.proimg1 = (ImageView)convertView.findViewById(R.id.proimg1);
			holder.proimg2  = (ImageView)convertView.findViewById(R.id.proimg2);
			holder.proimg3  = (ImageView)convertView.findViewById(R.id.proimg3);		
			holder.relaArrimg = (RelativeLayout)convertView.findViewById(R.id.relaArrimg);
			holder.yearBtn = (Button)convertView.findViewById(R.id.yearBtn);
//			holder.yearLinear = (FrameLayout)convertView.findViewById(R.id.yearLinear);
			convertView.setTag(holder);
		}else{
			holder = (Holder)convertView.getTag();
		}
		final Products products = productItem.arrProducts.get(position);		
		if(products.showTime.indexOf("-")== -1){
			holder.showtime.setText(products.showTime);	
  		}else{
  			DateObj dateObj = getDayMonth(products.showTime); 
  			String date = dateObj.day + dateObj.month;
  			SpannableString msp = new SpannableString(date);
  			msp.setSpan(new AbsoluteSizeSpan(24, true), 0, dateObj.day.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
  			msp.setSpan(new StyleSpan(Typeface.BOLD),  0, dateObj.day.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
  			msp.setSpan(new AbsoluteSizeSpan(16, true), dateObj.day.length(), date.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
  			holder.showtime.setText(msp);
  		}
			
		holder.showtime.setTag(products.findTime);
		holder.procount.setText("共" + products.count + "个");
		final int imgSize = products.arrProImg.size();
		//少于三个的置空
		for(int i= imgSize; i< 3; i++){
			LogPrint.Print("lybjson", "img gone i =" + i);
			ImageView imgView = getImageView(holder, i);
			imgView.setVisibility(View.GONE);
			imgView.setImageDrawable(null);
			imgView.setTag(R.id.tag_first, "");
		}
		for(int i=0; i<products.arrProImg.size(); i++){
			String proImage = products.arrProImg.get(i);
//			ImageView imgView = holder.arrProImage.get(i);
			ImageView imgView = getImageView(holder, i);
			imgView.setTag(R.id.tag_first, proImage);			
			imgView.setTag(R.id.tag_second, products.arrcid.get(i));
			loadImage(proImage, imgView, products.arrPlayable.get(i));
			//点击事件
			imgView.setTag(i);
			imgView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					imageClick(products, (Integer)v.getTag());
					
				}
			});
		}
		final int nextPos = position + 1;
		if(nextPos <= productItem.arrProducts.size()-1){
			final Products productsNext = productItem.arrProducts.get(nextPos);
			int nextYear = Integer.parseInt(productsNext.findTime.split("-")[0]);
			int currYear = Integer.parseInt(products.findTime.split("-")[0]);
			if(nextYear != currYear){
				holder.yearBtn.setVisibility(View.VISIBLE);
				holder.yearBtn.setText(nextYear + "");
//				holder.yearLinear.setVisibility(View.VISIBLE);
			}else{
				holder.yearBtn.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
	
	private void imageClick(Products products, int index){
		final String pageUrl = productItem.propage;
		String listUrl = pageUrl  + "&uid=" + uid + "&dpi="+URLUtil.dpi()+"&imsi="+CommonUtil.getIMSI(context)+
			"&imei="+CommonUtil.getIMEI(context) +"&pla=" + CommonUtil.toUrlEncode(CommonUtil.getDeviceName()) + "&plaid=" + URLUtil.plaid;
		LogPrint.Print("lybconnect", "listUrl = " + listUrl);
		Intent intent = new Intent(context, MyPageActivityA.class);
		bulidUrl(products, listUrl);
		intent.putExtra("url", MyPageActivityA.urls[index]);
		intent.putExtra("chickPos", index);
		intent.putExtra("type", PageID.PAGEID_USERPAGE);				
		context.startActivity(intent);
		asyncImageLoader.free();
	}
	
	private ImageView getImageView(Holder holder, int idx){
		ImageView image = null;
		if(idx == 0){
			image = holder.proimg1;
		}else if(idx == 1){
			image = holder.proimg2;
		}else if(idx == 2){
			image = holder.proimg3;
		}
		return image;
	}
	
  	public void bulidUrl(Products products , String url){
  		final int length = products.arrcid.size();
  		MyPageActivityA.urls = null;
		MyPageActivityA.urls = new String[length];  		
  		for(int i=0; i< length; i++){
  			MyPageActivityA.urls[i] = url +"&cid="+ products.arrcid.get(i) + "&findtime=" + products.findTime;
  		}
  		MyPageActivityA.saveUrlsFromUserPage = MyPageActivityA.urls;
  		
  	}
  	
  	/**
  	 * 获取日期月份（月份前段去零）
  	 * @param showTime
  	 * @return
  	 */
  	private DateObj getDayMonth(String showTime){  		
  		String[] md = showTime.split("-");
  		String month = md[0];
  		if(month.charAt(0) == '0'){
  			month = month.substring(1);
  		}
  		DateObj dateObj = new DateObj();
  		dateObj.day = md[1];
  		dateObj.month = month + "月";
  		return dateObj;
  	}
	
	public final class Holder{
		public TextView showtime;
		public TextView procount;
		public ImageView proimg1;
		public ImageView proimg2;
		public ImageView proimg3;
		public Button yearBtn;
//		public FrameLayout yearLinear;
//		public ArrayList<ImageView> arrProImage;
		public RelativeLayout relaArrimg;
		public Holder(){
//			arrProImage = new ArrayList<ImageView>();
			proimg1 = null;
			proimg2 = null;
			proimg3 = null;
		}
		
	}
	
	//引入线程池，并引入内存缓存功能,并对外部调用封装了接口，简化调用过程   
    private void loadImage(final String url, final ImageView imageView, final int isPlayable) {  
          //如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行   
         Drawable cacheImage = asyncImageLoader.loadDrawable(url,new ImageCallback() {  
             //请参见实现：如果第一次加载url时下面方法会执行   
             public void imageLoaded(Drawable imageDrawable) {            	 
            	 if(imageView.getTag(R.id.tag_first).equals(url)){
            		 if(isPlayable == 1){
            			 Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.playicon_s);
                		 temp = CommonUtil.mergerBitmapForUser(context,CommonUtil.drawable2Bitmap(context, imageDrawable), 0, temp);
                		 imageView.setImageBitmap(temp);
            		 }else{
            			 imageView.setImageDrawable(imageDrawable);
            		 }
            		 imageView.setVisibility(View.VISIBLE);     		
            	 }
             }  
         });  
        if(cacheImage!=null){
        	 if(isPlayable == 1){
        		 Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.playicon_s);
        		 temp = CommonUtil.mergerBitmapForUser(context,CommonUtil.drawable2Bitmap(context, cacheImage), 0, temp);
        		 imageView.setImageBitmap(temp);
        	 }else{
        		 imageView.setImageDrawable(cacheImage);
        	 }        	
   		 	imageView.setVisibility(View.VISIBLE);  
        }  
    }
    

}
