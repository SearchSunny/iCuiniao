/**
 * 
 */
package com.icuiniao.plug.im;

import java.util.ArrayList;

/**
 * @author hw
 *数据封装
 */
public class Entity {

	private short tag; // 包头
	private int userId; // 用户ID
	private String userName;//用户名
	private String deviceId; // 设备ID
	private int isRead;//已读未读
	private int size; // 消息体长度
	private String currentTime; // 当前时间
	private String[] heartStarttime;//心跳开始时间
	private String[] heartEndtime;//心跳结束时间
	private long heartInterval[];//心跳间隔
	private int revicerUserId;//接收用户id
	private long messageid;//消息id
	private String sendMessage;
	private String commenttime;//时间
	private int type;//0好友消息，1私信，2系统消息，3申请建立好友关系，4确认建立好友关系
	private String regInfo; // 注册验证信息
	private long tempMessageId;//消息流水号（使用系统时间毫秒级）
	private int cid;//商品id
	private int heartResponse;//心跳返回状态结果
	private int refuseTag;//拒绝或失败代码
	private String nickName;//昵称
	private String remarks;//备注
	private byte repetSend;//是否为重发，1：重发，0：首发
	private byte serviceState;//服务器状态
	private ArrayList<MessageBody> messageBodies;//保存消息内容
	private int sendstate;//发送状态（0默认，1发送中，2发送成功，3发送失败）
	private int fromid;
	private int toid;
	private String error;//错误信息
	
	//加好友发起者id
	private int useridSponsor;
	//被加好友
	private int useridBeAdd;
	//加好友结果
	private byte isAddFriendSccess;
	
	public int getUseridSponsor() {
		return useridSponsor;
	}

	public void setUseridSponsor(int useridSponsor) {
		this.useridSponsor = useridSponsor;
	}

	public int getUseridBeAdd() {
		return useridBeAdd;
	}

	public void setUseridBeAdd(int useridBeAdd) {
		this.useridBeAdd = useridBeAdd;
	}

	public byte getIsAddFriendSccess() {
		return isAddFriendSccess;
	}

	public void setIsAddFriendSccess(byte isAddFriendSccess) {
		this.isAddFriendSccess = isAddFriendSccess;
	}

	
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCommenttime() {
		return commenttime;
	}

	public void setCommenttime(String commenttime) {
		this.commenttime = commenttime;
	}

	public String getSendMessage() {
		return sendMessage;
	}

	public void setSendMessage(String sendMessage) {
		this.sendMessage = sendMessage;
	}

	public long getMessageid() {
		return messageid;
	}

	public void setMessageid(long messageid) {
		this.messageid = messageid;
	}

	public int getRevicerUserId() {
		return revicerUserId;
	}

	public void setRevicerUserId(int revicerUserId) {
		this.revicerUserId = revicerUserId;
	}

	public String[] getHeartStarttime() {
		return heartStarttime;
	}
	
	public void setHeartStarttime(String heartStarttime,int length,int index) {
		if(this.heartStarttime == null){
			this.heartStarttime = new String[length];
		}
		this.heartStarttime[index] = heartStarttime;
	}
	
	public String[] getHeartEndtime() {
		return heartEndtime;
	}
	
	public void setHeartEndtime(String heartEndtime,int length,int index) {
		if(this.heartEndtime == null){
			this.heartEndtime = new String[length];
		}
		this.heartEndtime[index] = heartEndtime;
	}
	
	public long[] getHeartInterval() {
		return heartInterval;
	}
	
	public void setHeartInterval(long heartInterval,int length,int index) {
		if(this.heartInterval == null){
			this.heartInterval = new long[length];
		}
		this.heartInterval[index] = heartInterval;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getRegInfo() {
		return regInfo;
	}

	public void setRegInfo(String regInfo) {
		this.regInfo = regInfo;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public short getTag() {
		return tag;
	}

	public void setTag(short tag) {
		this.tag = tag;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public long getTempMessageId(){
		return tempMessageId;
	}
	
	public void setTempMessageId(long tempMessageId){
		this.tempMessageId = tempMessageId;
	}
	
	public int getCid(){
		return cid;
	}
	
	public void setCid(int cid){
		this.cid = cid;
	}
	
	public int getHeartResponse(){
		return heartResponse;
	}
	
	public void setHeartResponse(int heartResponse){
		this.heartResponse = heartResponse;
	}
	
	public int getRefuseTag(){
		return refuseTag;
	}
	
	public void setRefuseTag(int refuseTag){
		this.refuseTag = refuseTag;
	}
	
	public String getNickName(){
		return nickName;
	}
	
	public void setNickName(String nickname){
		this.nickName = nickname;
	}
	
	public String getRemarks(){
		return remarks;
	}
	
	public void setRemarks(String remarks){
		this.remarks = remarks;
	}
	
	public byte getRepetSend(){
		return repetSend;
	}
	
	public void setRepetSend(byte repetsend){
		this.repetSend = repetsend;
	}
	
	public byte getServiceState(){
		return serviceState;
	}
	
	public void setServiceState(byte serviceState){
		this.serviceState = serviceState;
	}
	
	public ArrayList<MessageBody> getMessageBodies(){
		return messageBodies;
	}
	
	public void setMessageBodies(MessageBody messageBody){
		if(this.messageBodies == null){
			messageBodies = new ArrayList<MessageBody>();
		}
		messageBodies.add(messageBody);
	}
	
	public String getUserName(){
		return this.userName;
	}
	
	public void setUserName(String username){
		this.userName = username;
	}
	
	public int getIsRead(){
		return this.isRead;
	}
	
	public void setIsRead(int isread){
		this.isRead = isread;
	}
	
	public int getSendState(){
		return this.sendstate;
	}
	
	public void setSendState(int sendstate){
		this.sendstate = sendstate;
	}
	
	public int getFromId(){
		return fromid;
	}
	
	public void setFromId(int id){
		this.fromid = id;
	}
	
	public int getToId(){
		return toid;
	}
	
	public void setToId(int id){
		this.toid = id;
	}
	
	public String getError(){
		return error;
	}
	
	public void setError(String error){
		this.error = error;
	}
}
