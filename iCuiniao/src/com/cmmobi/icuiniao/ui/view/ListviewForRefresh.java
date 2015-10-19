package com.cmmobi.icuiniao.ui.view;



import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.LogPrint;

public class ListviewForRefresh extends ListView implements OnScrollListener {

	private static final String TAG = "listview";
	
	private final static int HEAD_RELEASE_To_REFRESH = 0; //松开刷新
	private final static int HEAD_PULL_To_REFRESH = 1;	//向下拉
	private final static int HEAD_REFRESHING = 2;	//刷新中
	private final static int HEAD_DONE = 3;
	private final static int HEAD_LOADING = 4;
	
	private final static int FOOT_RELEASE_To_REFRESH = 10; //松开刷新
	private final static int FOOT_PULL_To_REFRESH = 11;	//向下拉
	private final static int FOOT_REFRESHING = 12;	//刷新中
	private final static int FOOT_DONE = 13;
	private final static int FOOT_LOADING = 14;

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;
	
	private LayoutInflater inflater;

	private LinearLayout headView;
	private LinearLayout footView;

	private TextView headTipsTextview;
	private ImageView headArrowImageView;
	private RelativeLayout headProgressLayout;
	private RelativeLayout headContentLayout;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isHeadRecored;
	private boolean isFootRecored;
	private RotateAnimation headAnimation;
	private RotateAnimation headReverseAnimation;
	private int headContentWidth;
	private int headContentHeight;
	
	private int footContentWidth;
	private int footContentHeight;
	private RotateAnimation footAnimation;
	private RotateAnimation footReverseAnimation;
	
	private TextView footTipsTextview;
	private ImageView footArrowImageView;
	private RelativeLayout footProgressLayout;
	private RelativeLayout footContentLayout;
	private int footStartY;
	private int headStartY;
	private int state;
	private boolean isBack;
	private OnRefreshListener refreshListener;
	private OnContinusLoadListener nextLoadListener;

	//新添加消息对聊界面的下拉刷新
	private OnRefreshLoadListener refreshLoadListener;
	private boolean isHeadRefreshableNext;
	
	private boolean isHeadRefreshable;
	public void setHeadRefreshable(boolean isHeadRefreshable) {
		this.isHeadRefreshable = isHeadRefreshable;
	}

	private boolean isFootRefreshable;	
	
	public int currPage; //加载后当前的页面索引
	private int saveListPos; //保存刷新前的位置
	

	public ListviewForRefresh(Context context) {
		super(context);
		if (Integer.parseInt(Build.VERSION.SDK) >= 9) {
            this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
		init(context);
	}

	public ListviewForRefresh(Context context, AttributeSet attrs) {
		super(context, attrs);
		//2.3及以上顶部和底部滚（拖）动出现渐变阴影解决方案
		//(2.3以下android:fadingEdge="none")
		if (Integer.parseInt(Build.VERSION.SDK) >= 9) {
            this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
		init(context);		
	}

	private void init(Context context) {
		setCacheColorHint(context.getResources().getColor(R.color.transparent));
		inflater = LayoutInflater.from(context);
		// headview
		headView = (LinearLayout) inflater.inflate(R.layout.head_comment, null);
		headArrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
//		headArrowImageView.setMinimumWidth(70);
//		headArrowImageView.setMinimumHeight(50);
		headProgressLayout = (RelativeLayout) headView
				.findViewById(R.id.head_load);
		headContentLayout = (RelativeLayout)headView.findViewById(R.id.head_contentLayout);
		headTipsTextview = (TextView) headView
				.findViewById(R.id.head_tipsTextView);		
		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();
		LogPrint.Print("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);
		addHeaderView(headView, null, false);
		// footview
		footView = (LinearLayout) inflater.inflate(R.layout.foot_comment, null);
		footArrowImageView = (ImageView) footView
				.findViewById(R.id.foot_arrowImageView);
//		footArrowImageView.setMinimumWidth(70);
//		footArrowImageView.setMinimumHeight(50);
		footProgressLayout = (RelativeLayout) footView
				.findViewById(R.id.foot_load);
		footContentLayout = (RelativeLayout)footView.findViewById(R.id.foot_contentLayout);
		footTipsTextview = (TextView) footView
				.findViewById(R.id.foot_tipsTextView);
		measureView(footView);
		footContentHeight = footView.getMeasuredHeight();
		footContentWidth = footView.getMeasuredWidth();
//		footView.setPadding(0, -1 * headContentHeight, 0, 0);
		footView.setPadding(0, 0, 0, -1 * headContentHeight);
		footView.invalidate();
		addFooterView(footView, null, false);

		setOnScrollListener(this);

		headAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		headAnimation.setInterpolator(new LinearInterpolator());
		headAnimation.setDuration(250);
		headAnimation.setFillAfter(true);

		headReverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		headReverseAnimation.setInterpolator(new LinearInterpolator());
		headReverseAnimation.setDuration(200);
		headReverseAnimation.setFillAfter(true);

//		state = HEAD_DONE;
		isHeadRefreshable = false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisiableItem, int visibleItemCount,
			int totalItemCount) {
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 当不滚动时
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			//保存位置
			saveListPos = view.getFirstVisiblePosition();
		}

	}
	
	/**
	 * 是否在顶部
	 * @return
	 */
	private boolean isGoHead(){
		if(getFirstVisiblePosition() == 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否在底部
	 * @return
	 */
	private boolean isGoFoot(){
		if (getLastVisiblePosition() == getCount() - 1) {
			return true;
		}
		return false;
	}
	
	private boolean onHeadTouchEvent(MotionEvent event){
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//触发判断移至MOVE事件中（down事件会被子控件click事件消费掉）
			break;
		case MotionEvent.ACTION_UP:
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
					onRefresh();
					LogPrint.Print(TAG, "由松开刷新状态，到done状态");
				}
			}
			isHeadRecored = false;			
			isBack = false;
			break;
		case MotionEvent.ACTION_MOVE:
			int tempY = (int) event.getY();
			if (isGoHead() && !isHeadRecored) {
				isHeadRecored = true;
				headStartY = (int) event.getY();
				state = HEAD_PULL_To_REFRESH;
				LogPrint.Print(TAG, "在down时候记录当前位置‘");
			}
			if (state != HEAD_REFRESHING && isHeadRecored && state != HEAD_LOADING) {

				// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

				// 可以松手去刷新了
				if (state == HEAD_RELEASE_To_REFRESH) {

					// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
					if (((tempY - headStartY) / RATIO < headContentHeight)
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
					}
					// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
					else {
						// 不用进行特别的操作，只用更新paddingTop的值就行了
					}
				}
				// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
				if (state == HEAD_PULL_To_REFRESH) {

					// 下拉到可以进入RELEASE_TO_REFRESH的状态
					if ((tempY - headStartY) / RATIO >= headContentHeight*1.7) {
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
					headView.setPadding(0, -1 * headContentHeight
							+ (tempY - headStartY) / RATIO, 0, 0);

				}

				// 更新headView的paddingTop
				if (state == HEAD_RELEASE_To_REFRESH) {
					headView.setPadding(0, (tempY - headStartY) / RATIO
							- headContentHeight, 0, 0);
				}

			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
	private boolean onFootTouchEvent(MotionEvent event){
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//触发判断移至MOVE事件中（down事件会被子控件click事件消费掉）
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
					nextPage();
				}
			}				
			isFootRecored = false;
			isBack = false;
			break;
		case MotionEvent.ACTION_MOVE:
			int tempY = (int) event.getY();
			if(isGoFoot() &&  !isFootRecored){	
				isFootRecored = true;
				footStartY = (int) event.getY();
				state = FOOT_PULL_To_REFRESH;
			}
			//*****************footer

			if (state != FOOT_REFRESHING && isFootRecored && state != FOOT_LOADING) {

				// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

				// 可以松手去刷新了
				if (state == FOOT_RELEASE_To_REFRESH) {

					// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
					if (((footStartY - tempY) / RATIO < footContentHeight)
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
					if ((footStartY - tempY) / RATIO >= footContentHeight*1.7) {
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
					footView.setPadding(0, 0, 0, -1 * footContentHeight
							+(footStartY - tempY) / RATIO);

				}

				// 更新headView的paddingTop
				if (state == FOOT_RELEASE_To_REFRESH) {
					footView.setPadding(0, 0, 0, (footStartY - tempY) / RATIO
							- footContentHeight);
				}

			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
	//新添加
	private boolean onHeadTouchNextEvent(MotionEvent event){
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//触发判断移至MOVE事件中（down事件会被子控件click事件消费掉）
			break;
		case MotionEvent.ACTION_UP:
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
					onRefreshNextPage();
					LogPrint.Print(TAG, "由松开刷新状态，到done状态");
				}
			}
			isHeadRecored = false;			
			isBack = false;
			break;
		case MotionEvent.ACTION_MOVE:
			int tempY = (int) event.getY();
			if (isGoHead() && !isHeadRecored) {
				isHeadRecored = true;
				headStartY = (int) event.getY();
				state = HEAD_PULL_To_REFRESH;
				LogPrint.Print(TAG, "在down时候记录当前位置‘");
			}
			if (state != HEAD_REFRESHING && isHeadRecored && state != HEAD_LOADING) {

				// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

				// 可以松手去刷新了
				if (state == HEAD_RELEASE_To_REFRESH) {

					// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
					if (((tempY - headStartY) / RATIO < headContentHeight)
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
					}
					// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
					else {
						// 不用进行特别的操作，只用更新paddingTop的值就行了
					}
				}
				// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
				if (state == HEAD_PULL_To_REFRESH) {

					// 下拉到可以进入RELEASE_TO_REFRESH的状态
					if ((tempY - headStartY) / RATIO >= headContentHeight*1.7) {
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
					headView.setPadding(0, -1 * headContentHeight
							+ (tempY - headStartY) / RATIO, 0, 0);

				}

				// 更新headView的paddingTop
				if (state == HEAD_RELEASE_To_REFRESH) {
					headView.setPadding(0, (tempY - headStartY) / RATIO
							- headContentHeight, 0, 0);
				}

			}
			break;
		}
		return super.onTouchEvent(event);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (isHeadRefreshable) {
			onHeadTouchEvent(event);
		}
		if(isFootRefreshable){
			onFootTouchEvent(event);
		}
		if(isHeadRefreshableNext){
			
			onHeadTouchNextEvent(event);
			
		}
		return super.onTouchEvent(event);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case HEAD_RELEASE_To_REFRESH:
			headProgressLayout.setVisibility(View.GONE);						
			headContentLayout.setVisibility(View.VISIBLE);
			headArrowImageView.clearAnimation();
			headArrowImageView.startAnimation(headAnimation);
			headTipsTextview.setText(head_release);

			LogPrint.Print(TAG, "当前状态，松开刷新");
			break;
		case HEAD_PULL_To_REFRESH:
			headProgressLayout.setVisibility(View.GONE);
			headContentLayout.setVisibility(View.VISIBLE);
			headArrowImageView.clearAnimation();
			headArrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				headArrowImageView.clearAnimation();
				headArrowImageView.startAnimation(headReverseAnimation);

				headTipsTextview.setText(pull);
			} else {
				headTipsTextview.setText(pull);
			}
			LogPrint.Print(TAG, "当前状态，下拉刷新");
			break;

		case HEAD_REFRESHING:
			headView.setPadding(0, 0, 0, 0);
			headArrowImageView.clearAnimation();
			headContentLayout.setVisibility(View.GONE);
			headProgressLayout.setVisibility(View.VISIBLE);
			LogPrint.Print(TAG, "当前状态,正在刷新...");
			break;
		case HEAD_DONE:
			headProgressLayout.setVisibility(View.GONE);
			headContentLayout.setVisibility(View.VISIBLE);
			headView.setPadding(0, -1 * headContentHeight, 0, 0);						
			headArrowImageView.clearAnimation();
			headArrowImageView.setImageResource(R.drawable.arrow_down);
			headTipsTextview.setText(pull);
			LogPrint.Print(TAG, "当前状态，done");
			break;
		}
	}
	
	// 当状态改变时候，调用该方法，以更新界面
	private void changeFooterViewByState() {
		switch (state) {
		case FOOT_RELEASE_To_REFRESH:
			footProgressLayout.setVisibility(View.GONE);						
			footContentLayout.setVisibility(View.VISIBLE);
			footArrowImageView.clearAnimation();
			footArrowImageView.startAnimation(headAnimation);
			footTipsTextview.setText(foot_release);

			LogPrint.Print(TAG, "当前状态，松开刷新");
			break;
		case FOOT_PULL_To_REFRESH:
			footProgressLayout.setVisibility(View.GONE);
			footContentLayout.setVisibility(View.VISIBLE);;
			footArrowImageView.clearAnimation();
			footArrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				footArrowImageView.clearAnimation();
				footArrowImageView.startAnimation(headReverseAnimation);
				footTipsTextview.setText(push);
			} else {
				footTipsTextview.setText(push);
			}
			LogPrint.Print(TAG, "当前状态，下拉刷新");
			break;

		case FOOT_REFRESHING:
			footView.setPadding(0, 0, 0, 0);
			footContentLayout.setVisibility(View.GONE);
			footProgressLayout.setVisibility(View.VISIBLE);
			footArrowImageView.clearAnimation();			
			LogPrint.Print(TAG, "当前状态,正在刷新...");
			break;
		case FOOT_DONE:			
//			footView.setPadding(0, -1 * headContentHeight, 0, 0);
			footView.setPadding(0, 0, 0, -1 * headContentHeight);
			footProgressLayout.setVisibility(View.GONE);
			footContentLayout.setVisibility(View.VISIBLE);			
			footArrowImageView.clearAnimation();
			footArrowImageView.setImageResource(R.drawable.arrow_up);
			footTipsTextview.setText(push);
			footView.invalidate();
			LogPrint.Print(TAG, "当前状态，done");
			break;
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isHeadRefreshable = true;
	}
	
	public void setFootLoadListener(OnContinusLoadListener nextLoadListener){
		this.nextLoadListener = nextLoadListener;
		isFootRefreshable = true;
	}

	public void setFootRefreshable(boolean isFootRefreshable) {
		this.isFootRefreshable = isFootRefreshable;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}
	//新添加
	public interface OnRefreshLoadListener{
		
		public void onRefreshNextPage(int page);
	}
	//新添加
	private void onRefreshNextPage(){
		
		if (refreshLoadListener != null) {
			refreshLoadListener.onRefreshNextPage(++currPage);
			
		}
	}
	//新添加消息对聊界面的下拉刷新
	public void setonRefreshNextListener(OnRefreshLoadListener refreshLoadListener) {
		this.refreshLoadListener = refreshLoadListener;
		isHeadRefreshableNext = true;
	}
	//新添加
	public void onHeadRefreshNextComplete() {
		state = HEAD_DONE;
		changeHeaderViewByState();
	}
	public void setHeadNextRefreshable(boolean isHeadRefreshable) {
		this.isHeadRefreshableNext = isHeadRefreshable;
	}
	
	public interface OnContinusLoadListener{
		public void nextLoad(int page);
	}

	public void onHeadRefreshComplete() {
		state = HEAD_DONE;
		changeHeaderViewByState();
	}
	
	public void onFootRefreshComplete() {
		state = FOOT_DONE;
		changeFooterViewByState();
		setSelection(saveListPos);
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
			currPage = 0;
		}
	}
	
	public void nextPage(){
		if(nextLoadListener != null){
			nextLoadListener.nextLoad(++currPage);
		}
	}

	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
	private void measureView(View child) {
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

	public void setAdapter(BaseAdapter adapter) {
//		lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
		super.setAdapter(adapter);
	}
	
	private  String pull = "下拉可以刷新评论";
	private  String head_release = "松开刷新评论";
	
	private  String foot_release = "松开获取更多";
	private  String push = "上推可以获取更多";
	
	/**
	 * 设置下拉提示文字
	 * @param pull
	 * @param head_release
	 */
	public void setPullHeadTips(String pull, String head_release){
		this.pull = pull;
		this.head_release = head_release;
	}
	
	/**
	 * 设置上推提示文字
	 * @param push
	 * @param foot_release
	 */
	public void setPushFootTips(String push, String foot_release){
		this.push = push;
		this.foot_release = foot_release;
	}
}
