package com.tricheer.app.receiver;

/**
 * Radio Receiver Actions
 * 
 * @author Jun.Wang
 */
public interface RadioReceiverActions extends BaseActions {
	/**
	 * 打开收音机Action
	 */
	public final String RADIO_OPEN = "com.tricheer.radio.OPEN_RADIO_PLAYER";
	/**
	 * 关闭收音机Action
	 */
	public final String RADIO_CLOSE = "com.tricheer.radio.CLOSE_RADIO_PLAYER";

	/**
	 * 收音机操作Action
	 * <p>
	 * 参数1 : radio_action
	 */
	// ---- 参数1取值 ----
	// "searchMold_auto" ~ "自动搜索"
	// "searchMold_up" ~ "向上搜索"，搜索上一个电台并播放
	// "searchMold_down" ~ "向下搜索"，搜索下一个电台并播放
	// "step_pre" ~ "上一个步进"，步进 -0.1
	// "step_next" ~ "下一个步进"，步进 +0.1
	// "channel_pre" ~ "上一个电台"，播放列表中上一个频率
	// "channel_next" ~ "下一个电台"，播放列表中下一个频率
	// "channelMold_changed" ~ "频段切换"
	// "FM_91.60" || "AM_738" ~ 频率设置
	public final String OPERATE = "com.tricheer.radio.action";

	/**
	 * 蓝牙电话通话结束
	 */
	public final String BT_CALL_END = "com.tricheer.bt.CALL_END";

	/**
	 * MediaSession改变广播
	 * <P>
	 * 收到该广播表示有第三方的音频播放，此时应该退出收音机，类似于失去声音焦点
	 */
	// 接到该广播表示，第三方音频打开了，此时要退出播放器，类似于播放器失去了声音焦点
	public final String MEDIA_SESSION_CHANGE = "com.tricheer.MEDIA_SESSION_CHANGE";
}
