package com.basketball.play.bean;

import android.content.Context;
import cn.bmob.v3.BmobObject;


public class GroupMsg extends BmobObject{

	private static final long serialVersionUID = 1L;
	  private String tag;
	  private String conversationId;
	  private String content;
	  private String toGroupId;
	  private String belongId;
	  private String belongAvatar;
	  private String belongNick;
	  private String belongUsername;
	  private Integer msgType;
	  private String msgTime;
	  private Integer isReaded;
	  private Integer status;

	  public GroupMsg()
	  {
	  }

	  public GroupMsg(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, Integer paramInteger1, Integer paramInteger2, Integer paramInteger3)
	  {
	    this.tag = paramString1;
	    this.conversationId = paramString2;
	    this.toGroupId = paramString4;
	    this.content = paramString3;
	    this.msgTime = paramString9;
	    this.belongAvatar = paramString7;
	    this.belongNick = paramString8;
	    this.belongUsername = paramString6;
	    this.belongId = paramString5;
	    this.isReaded = paramInteger2;
	    this.msgType = paramInteger1;
	    this.status = paramInteger3;
	  }

	  public GroupMsg(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, Integer paramInteger)
	  {
	    this.tag = paramString1;
	    this.toGroupId = paramString2;
	    this.msgTime = paramString7;
	    this.belongAvatar = paramString5;
	    this.belongNick = paramString6;
	    this.belongUsername = paramString4;
	    this.belongId = paramString3;
	    this.isReaded = paramInteger;
	  }
	  
	  public static GroupMsg createTextSendMsg(Context context,UserBean user,String groupId,String message){
		  GroupMsg groupMsg = new GroupMsg();
		  groupMsg.setBelongAvatar(user.getAvatar());
		  groupMsg.setBelongId(user.getObjectId());
		  groupMsg.setBelongNick(user.getNick());
		  groupMsg.setContent(message);
		  groupMsg.setConversationId(user.getObjectId()+"&"+groupId);
		  groupMsg.setIsReaded(0);
		  groupMsg.setMsgTime(System.currentTimeMillis()+"");
		  groupMsg.setMsgType(1);
		  groupMsg.setStatus(1);
		  groupMsg.setTag("");
		  groupMsg.setToGroupId(groupId);
		return groupMsg;
		  
	  }

	  public static GroupMsg createFriendMsg()
	  {
	    return null;
	  }

	  public String getTag()
	  {
	    return this.tag;
	  }

	  public void setTag(String paramString)
	  {
	    this.tag = paramString;
	  }

	  public String getToGroupId()
	  {
	    return this.toGroupId;
	  }

	  public void setToGroupId(String paramString)
	  {
	    this.toGroupId = paramString;
	  }

	  public String getBelongAvatar()
	  {
	    return this.belongAvatar;
	  }

	  public void setBelongAvatar(String paramString)
	  {
	    this.belongAvatar = paramString;
	  }

	  public String getBelongNick()
	  {
	    return this.belongNick;
	  }

	  public void setBelongNick(String paramString)
	  {
	    this.belongNick = paramString;
	  }

	  public String getBelongUsername()
	  {
	    return this.belongUsername;
	  }

	  public void setBelongUsername(String paramString)
	  {
	    this.belongUsername = paramString;
	  }

	  public String getMsgTime()
	  {
	    return this.msgTime;
	  }

	  public void setMsgTime(String paramString)
	  {
	    this.msgTime = paramString;
	  }

	  public String getContent()
	  {
	    return this.content;
	  }

	  public void setContent(String paramString)
	  {
	    this.content = paramString;
	  }

	  public String getConversationId()
	  {
	    return this.conversationId;
	  }

	  public void setConversationId(String paramString)
	  {
	    this.conversationId = paramString;
	  }

	  public Integer getMsgType()
	  {
	    return this.msgType;
	  }

	  public void setMsgType(Integer paramInteger)
	  {
	    this.msgType = paramInteger;
	  }

	  public Integer getIsReaded()
	  {
	    return this.isReaded;
	  }

	  public void setIsReaded(Integer paramInteger)
	  {
	    this.isReaded = paramInteger;
	  }

	  public Integer getStatus()
	  {
	    return this.status;
	  }

	  public void setStatus(Integer paramInteger)
	  {
	    this.status = paramInteger;
	  }

	  public String getBelongId()
	  {
	    return this.belongId;
	  }

	  public void setBelongId(String paramString)
	  {
	    this.belongId = paramString;
	  }

}
