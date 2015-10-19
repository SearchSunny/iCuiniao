/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * @author hw
 *
 *View的父类,定义所有的相应API
 */
public class View_AbsPage extends LinearLayout{

	public View_AbsPage(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	/**titlebar/titlebarfloat的左按钮的响应*/
	public void onTitleBarLeftButtonClick(String url, int action){}
	/**titlebar/titlebarfloat的中间按钮的响应*/
	public void onTitleBarMiddleButtonClick(String url, int action){}
	/**titlebar/titlebarfloat的右侧按钮的响应*/
	public void onTitleBarRightButtonClick(String url, int action){}
	/**titlebar/titlebarfloat的中间菜单的响应*/
	public void onTitleBarMenuClick(int pageid){}
	/**mainpage的图片响应*/
	public void onMainPageItemClick(String url, int type, int chickPos){}
	/**单品页中晒单按钮的响应*/
	public void onCommodityInfoFormButtonClick(String url,int action){}
	/**单品页中喜欢按钮的响应*/
	public void onCommodityInfoLikeButtonClick(String url, int action){}
	/**单品页中商品简介区域的响应*/
	public void onCommodityInfoBoxClick(){}
	/**单品页中卖家图标的响应*/
	public void onCommodityInfoIconClick(String url){}
	/**单品页中评论列表的响应*/
	public void onListItemCommentC_Click(String url, int userid,int action,int subjectid){}
	/**喜欢窗口中图标的响应*/
	public void onPopLikeGridItemClick(String url){}
	/**用户主页中喜欢按钮的响应*/
	public void onUserInfoLikeButtonClick(String url, int userid, int action){}
	/**用户主页中晒单按钮的响应*/
	public void onUserInfoFormButtonClick(String url, int userid, int action){}
	/**用户主页中userbar的加关注按钮的响应*/
	public void onUserBarAddButtonClick(String url, int userid, int type){}
	/**用户主页中userbar的发消息按钮的响应*/
	public void onUserBarMessageButtonClick(int userid){}
	/**用户主页中userbar的推荐好友按钮的响应*/
	public void onUserBarRecommendButtonClick(int userid){}
	/**三级评论中的列表的响应*/
	public void onListItemCommentU_Click(String url, int userid,int action,boolean isMoreItem,int curIndex,int totalpage,int subjectid,int commentid){}
	/**单品页中评论列表查看更多的响应*/
	public void onListItemCommentC_Click(int curIndex,int totalpage){}
	/**评论对话的相应*/
	public void onCommentInputBoxClick(){}
	/**我的好友中tabbutton的响应*/
	public void onFriendTabbuttonClick(String url,int curIndex){}
	/**好友列表项的响应*/
	public void onFriendListItemClick(String url, int userid,int action,boolean isMoreItem,int curIndex,int totalpage){}
	/**好友列表(onlytext)的子项的响应*/
	public void onFriendListItemChildClick(int userid,String flush,boolean isselected,int type,String name){}
	/**搜索的响应*/
	public void onInputlineClick(String url,String str){}
	/**添加好友列表项的响应*/
	public void onAddFriendsListItemClick(String url, int userid,int action,boolean isMoreItem,int curIndex,int totalpage){}
	/**我的撒娇中更多的响应*/
	public void onCoquetryItemClick(int curIndex,int totalpage,boolean ismore,int selectIndex,String href){}
	/**瀑布流中子项的响应*/
	public void onStreamPageItemClick(String url,int chickpos){}
	/**瀑布流中更多的响应*/
	public void onStreamPageMoreClick(String nexturl){}
	/**单品页中大图的响应*/
	public void onMyPagePlayClick(String url){}
}
