package com.cmmobi.icuiniao.ui.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.ViewID;

public class ScrollviewForRefresh extends ScrollView {
	private static final String TAG = "ScrollView";
	private final static int HEAD_RELEASE_To_REFRESH = 0;
	private final static int HEAD_PULL_To_REFRESH = 1;
	private final static int HEAD_REFRESHING = 2;
	private final static int HEAD_DONE = 3;
	private final static int HEAD_LOADING = 4;
	// 实际的padding的距离与界面上偏移距离的比例
	private final static int HEAD_RATIO = 3;
	private int headContentWidth;
	private int headContentHeight;
	private OnHeadRefreshListener head_refreshListener;
	private LinearLayout head_titleback;
	private TextView tipText;
	
	private final static int FOOT_RELEASE_To_REFRESH = 10;
	private final static int FOOT_PULL_To_REFRESH = 11;
	private final static int FOOT_REFRESHING = 12;
	private final static int FOOT_DONE = 13;
	private final static int FOOT_LOADING = 14;
	// 实际的padding的距离与界面上偏移距离的比例
	private final static int FOOT_RATIO =3;
	private int footContentWidth;
	private int footContentHeight;
	private LinearLayout footView;
	private ImageView foot_arrowImageView;
	private ProgressBar foot_progressBar;
	private TextView foot_tipsTextview;
	private OnFootRefreshListener foot_refreshListener;
	
	private boolean isHeadRefreshable;
	public void setHeadRefreshable(boolean isHeadRefreshable) {
		this.isHeadRefreshable = isHeadRefreshable;
	}

	private boolean isFootRefreshable;
	
	private int state;
	private boolean isBack;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	private boolean canReturn;
	private boolean isHeadRecored;
	private boolean isFootRecored;
	private int headStartY;
	private int footStartY;


	public ScrollviewForRefresh(Context context) {
		super(context);
		init(context);
	}

	public ScrollviewForRefresh(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public void setHeadView(){
		head_titleback = (LinearLayout)findViewById(ViewID.ID_PAGE_TITLE_BACK);	
		tipText = (TextView)findViewById(ViewID.ID_PAGE_TITLE_TEXT);
		measureView(head_titleback);
		headContentHeight = head_titleback.getMeasuredHeight();
	}
	
	public void setFootView(LinearLayout footView){
		this.footView = footView;
		measureView(footView);
		footContentHeight = footView.getMeasuredHeight();
		footContentWidth = footView.getMeasuredWidth();
		foot_arrowImageView = (ImageView) footView
				.findViewById(R.id.foot_arrowImageView);
		foot_progressBar = (ProgressBar) footView
				.findViewById(R.id.footloading);
		foot_tipsTextview = (TextView) footView.findViewById(R.id.foot_tipsTextView);		
	}

	private void init(Context context) {		
		LogPrint.Print("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = HEAD_DONE;
		isHeadRefreshable = false;
		isFootRefreshable = false;
		canReturn = false;
	}
	
	private boolean isBottom(){
		if(getScrollY() + getHeight() >=  computeVerticalScrollRange()){
			return true;
		}
		return false;
	}
	
//	@Override
//	protected void onScrollChanged(int l, int t, int oldl, int oldt){
//		if(!isFootRefreshable){
//			return;
//		}
//		if(getScrollY() + getHeight() >=  computeVerticalScrollRange()){
//			MyPageActivityA.mPopupMenu.closeMenu();
//		}
//	}
	


	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		LogPrint.Print(TAG, "action =" + event.getAction());
		if(isHeadRefreshable){
			return onTouch_head(event);
		}else if(isFootRefreshable){
			return onTouch_Foot(event);
		}
		return super.onTouchEvent(event);
		
	}	

	
	private boolean onTouch_Foot(MotionEvent event){
//		LogPrint.Print(TAG, "foot action =" + event.getAction());
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:			
//			if(isBottom() && !isFootRecored){
//				isFootRecored = true;
//				footStartY = (int)event.getY();
//				state = FOOT_PULL_To_REFRESH;
//				LogPrint.Print(TAG, "foot 在down时候记录当前位置‘");
//			}
			break;
		case MotionEvent.ACTION_UP:			
			//footer			
			if (state != FOOT_REFRESHING && state != FOOT_LOADING) {
				if (state == FOOT_DONE) {
					// 什么都不做
				}
				if (state == FOOT_PULL_To_REFRESH) {
					state = FOOT_DONE;
					changeFooterViewByState();						
				}
				if (state == FOOT_RELEASE_To_REFRESH) {
					state = FOOT_REFRESHING;
					changeFooterViewByState();
					onFootRefresh();
				}
			}
			isFootRecored = false;
			isBack = false;
			break;
		case MotionEvent.ACTION_MOVE:
			int tempY = (int) event.getY();
			//*****************footer
			if (!isFootRecored && isBottom()) {
				LogPrint.Print(TAG, "在move时候记录下位置");
				isFootRecored = true;
				footStartY = tempY;	
//				state = FOOT_PULL_To_REFRESH;
			}
			if (state != FOOT_REFRESHING && isFootRecored && state != FOOT_LOADING) {

				// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

				// 可以松手去刷新了
				if (state == FOOT_RELEASE_To_REFRESH) {

					// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
					if (((footStartY - tempY) / FOOT_RATIO < footContentHeight)
							&& (footStartY - tempY) > 0) {
						state = FOOT_PULL_To_REFRESH;
						changeFooterViewByState();
					}
					// 一下子推到顶了
					else if (footStartY - tempY <= 0) {
						state = FOOT_DONE;
						changeFooterViewByState();
					}
					// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
					else {
						// 不用进行特别的操作，只用更新paddingTop的值就行了
					}
				}
				// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
				if (state == FOOT_PULL_To_REFRESH) {

					// 下拉到可以进入RELEASE_TO_REFRESH的状态
					if ((footStartY - tempY) / FOOT_RATIO >= footContentHeight) {
						state = FOOT_RELEASE_To_REFRESH;
						isBack = true;
						changeFooterViewByState();
					}
					// 上推到顶了
					else if (footStartY - tempY <= 0) {
						state = FOOT_DONE;
						changeFooterViewByState();
					}
				}

				// done状态下
				if (state == FOOT_DONE) {
					if (footStartY - tempY > 0) {
						state = FOOT_PULL_To_REFRESH;
						changeFooterViewByState();
					}
				}

				// 更新footView的size
				if (state == FOOT_PULL_To_REFRESH) {
//					footView.setPadding(0, 0, 0, -1 * footContentHeight
//							+(footStartY - tempY + footContentHeight) / FOOT_RATIO);
					footView.setPadding(0, 0, 0, footContentHeight * 2 +
							(footStartY - tempY) / FOOT_RATIO);
				}

				// 更新footView的paddingTop
				if (state == FOOT_RELEASE_To_REFRESH) {
//					footView.setPadding(0, 0, 0, (footStartY - tempY + footContentHeight) / FOOT_RATIO
//							- footContentHeight);
					footView.setPadding(0, 0, 0, footContentHeight * 2 + (footStartY - tempY) / FOOT_RATIO
							);
				}

			}
			break;
		}
		return super.onTouchEvent(event);		
	}	

	
	private boolean onTouch_head(MotionEvent event){	
//		LogPrint.Print(TAG, "head action =" + event.getAction());
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (getScrollX() == 0 && !isHeadRecored) {
					isHeadRecored = true;
					headStartY = (int) event.getY();
					state = HEAD_PULL_To_REFRESH;	
					changeHeaderViewByState();
					LogPrint.Print(TAG, "在down时候记录当前位置‘");
				}							
				break;
			case MotionEvent.ACTION_UP:
				LogPrint.Print(TAG, "UP ");
				if (state != HEAD_REFRESHING && state != HEAD_LOADING) {
					if (state == HEAD_DONE) {
						// 什么都不做
					}
					if (state == HEAD_PULL_To_REFRESH) {
						state = HEAD_DONE;
						changeHeaderViewByState();
						LogPrint.Print(TAG, "由下拉刷新状态，到done状态");
					}
					if (state == HEAD_RELEASE_To_REFRESH) {
						state = HEAD_REFRESHING;
						changeHeaderViewByState();
						onHeadRefresh();
						LogPrint.Print(TAG, "由松开刷新状态，到done状态");
					}
				}				
				isHeadRecored = false;				
				isBack = false;
				break;
			case MotionEvent.ACTION_MOVE:				
				int tempY = (int) event.getY();
				if (!isHeadRecored && getScrollY() == 0) {
					LogPrint.Print(TAG, "在move时候记录下位置");
					isHeadRecored = true;
					headStartY = tempY;
				}

				if (state != HEAD_REFRESHING && isHeadRecored && state != HEAD_LOADING) {
					// 可以松手去刷新了
					if (state == HEAD_RELEASE_To_REFRESH) {
						canReturn = true;

						if (((tempY - headStartY) / HEAD_RATIO < headContentHeight)
								&& (tempY - headStartY) > 0) {
							state = HEAD_PULL_To_REFRESH;
							changeHeaderViewByState();
							LogPrint.Print(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶了
						else if (tempY - headStartY <= 0) {
							state = HEAD_DONE;
							changeHeaderViewByState();
							LogPrint.Print(TAG, "由松开刷新状态转变到done状态");
						} else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == HEAD_PULL_To_REFRESH) {
						canReturn = true;

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - headStartY) / HEAD_RATIO >= headContentHeight/2) {
							state = HEAD_RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
							LogPrint.Print(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (tempY - headStartY <= 0) {
							state = HEAD_DONE;
							changeHeaderViewByState();
							LogPrint.Print(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					// done状态下
					if (state == HEAD_DONE) {
						if (tempY - headStartY > 0) {
							state = HEAD_PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}

					// 更新headView的size
					if (state == HEAD_PULL_To_REFRESH) {
						head_titleback.setPadding(0,  headContentHeight + (tempY - headStartY ) / HEAD_RATIO, 0, 0);

					}

					// 更新headView的paddingTop
					if (state == HEAD_RELEASE_To_REFRESH) {
						head_titleback.setPadding(0,  headContentHeight + (tempY - headStartY) / HEAD_RATIO , 0, 0);
					}
					if (canReturn) {
						canReturn = false;
						return true;
					}
				}			

				break;
			}		
		return super.onTouchEvent(event);
	}


	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case HEAD_RELEASE_To_REFRESH:
//			head_titleback.setPadding(0, headContentHeight, 0, 0);
			tipText.setVisibility(View.VISIBLE);
			tipText.setText(head_release);
			LogPrint.Print(TAG, "当前状态，松开刷新");
			break;
		case HEAD_PULL_To_REFRESH:
//			head_progressBar.setVisibility(View.GONE);
//			head_tipsTextview.setVisibility(View.VISIBLE);		
//			head_arrowImageView.clearAnimation();
//			head_arrowImageView.setVisibility(View.VISIBLE);
//			// 是由RELEASE_To_REFRESH状态转变来的
//			if (isBack) {
//				isBack = false;
//				head_arrowImageView.clearAnimation();
//				head_arrowImageView.startAnimation(reverseAnimation);
//
//				head_tipsTextview.setText(head_pull);
//			} else {
//				head_tipsTextview.setText(head_pull);
//			}
			if(isBack){
				isBack = false;
				tipText.setVisibility(View.VISIBLE);
				tipText.setText(head_pull);
			}else{
				tipText.setVisibility(View.VISIBLE);
				tipText.setText(head_pull);
			}			
			LogPrint.Print(TAG, "当前状态，下拉刷新");
			break;

		case HEAD_REFRESHING:
//			headView.setPadding(0, 0, 0, 0);
//			head_progressBar.setVisibility(View.VISIBLE);
//			head_arrowImageView.clearAnimation();
//			head_arrowImageView.setVisibility(View.GONE);
//			head_tipsTextview.setText(head_refreshing);	
//			head_titleback.setPadding(0, headContentHeight, 0, 0);
			tipText.setVisibility(View.VISIBLE);
			tipText.setText(head_refreshing);
			LogPrint.Print(TAG, "当前状态,正在刷新...");
			break;
		case HEAD_DONE:
//			headView.setPadding(0, -1 * headContentHeight, 0, 0);
//			head_progressBar.setVisibility(View.GONE);
//			head_arrowImageView.clearAnimation();
//			head_arrowImageView.setImageResource(R.drawable.arrow_down);
//			head_tipsTextview.setText(head_pull);
			head_titleback.setPadding(0,0, 0, 0);
			tipText.setVisibility(View.INVISIBLE);
			tipText.setText(head_pull);
			isHeadRecored = false;
			LogPrint.Print(TAG, "当前状态，done");
			break;
		}
	}
	
	// 当状态改变时候，调用该方法，以更新界面
	private void changeFooterViewByState() {
		switch (state) {
		case FOOT_RELEASE_To_REFRESH:			
			foot_arrowImageView.setVisibility(View.VISIBLE);
			foot_progressBar.setVisibility(View.GONE);
			foot_tipsTextview.setVisibility(View.VISIBLE);			
			foot_arrowImageView.clearAnimation();
			foot_arrowImageView.startAnimation(animation);
			foot_tipsTextview.setText(foot_release);
			LogPrint.Print(TAG, "当前状态，松开刷新");
			break;
		case FOOT_PULL_To_REFRESH:
			foot_progressBar.setVisibility(View.GONE);
			foot_tipsTextview.setVisibility(View.VISIBLE);		
			foot_arrowImageView.clearAnimation();
			foot_arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				foot_arrowImageView.clearAnimation();
				foot_arrowImageView.startAnimation(reverseAnimation);
				foot_tipsTextview.setText(foot_push);
			} else {
				foot_tipsTextview.setText(foot_push);
			}
			LogPrint.Print(TAG, "当前状态，上推刷新");
			break;

		case FOOT_REFRESHING:
			footView.setPadding(0, 0, 0, footContentHeight*3);
			foot_progressBar.setVisibility(View.VISIBLE);
			foot_arrowImageView.clearAnimation();
			foot_arrowImageView.setVisibility(View.GONE);
			foot_tipsTextview.setText(foot_refreshing);
			LogPrint.Print(TAG, "当前状态,正在刷新评论...");
			break;
		case FOOT_DONE:
			footView.setPadding(0, 0, 0, -1 * footContentHeight);
			foot_progressBar.setVisibility(View.GONE);
			foot_arrowImageView.clearAnimation();
			foot_arrowImageView.setImageResource(R.drawable.arrow_down);
			foot_tipsTextview.setText(foot_push);
			isFootRecored = false;
			LogPrint.Print(TAG, "当前状态，done");
			break;
		}
	}

	public void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setonHeadRefreshListener(OnHeadRefreshListener refreshListener) {
		this.head_refreshListener = refreshListener;
		isHeadRefreshable = true;
	}

	public interface OnHeadRefreshListener {
		public void onRefresh();
	}

	public void onHeadRefreshComplete() {
		state = HEAD_DONE;	
		changeHeaderViewByState();
		postInvalidate();
		scrollTo(0, 0);
	}
	
	public  void resetHead(){
		state = HEAD_DONE;	
		changeHeaderViewByState();
	}

	private void onHeadRefresh() {
		if (head_refreshListener != null) {
			head_refreshListener.onRefresh();
		}
	}
	
	//foot
	public void setonFootRefreshListener(OnFootRefreshListener refreshListener) {
		this.foot_refreshListener = refreshListener;
		isFootRefreshable = true;
	}

	public interface OnFootRefreshListener {
		public void onRefresh();
	}

	public void onFootRefreshComplete() {
		state = FOOT_DONE;	
		changeFooterViewByState();
		invalidate();		
	}

	private void onFootRefresh() {
		if (foot_refreshListener != null) {
			foot_refreshListener.onRefresh();
		}
	}
	
	private final String head_pull = "下拉可以刷新页面";
	private final String head_release = "松开刷新页面";
	private final String head_refreshing = "加载中";
	
	private final String foot_release = "松开加载新评论";
	private final String foot_push = "上推可以刷新评论";
	private final String foot_refreshing = "评论加载中";	


}
