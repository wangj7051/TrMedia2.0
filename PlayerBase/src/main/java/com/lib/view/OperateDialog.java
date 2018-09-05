package com.lib.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tricheer.player.R;

/**
 * Two Button Operate Dialog
 * 
 * @author Jun.Wang
 */
public class OperateDialog extends AlertDialog {

	/**
	 * Context
	 */
	private Context mContext;

	/**
	 * Content View Resource ID
	 */
	private int mContentResID = -1;

	/**
	 * Thread Handler
	 */
	private Handler mHandler = new Handler();

	/**
	 * Dialog Opened Index
	 */
	private final int M_TIMEOUT_PERIOD = 5 * 1000;

	/**
	 * Message
	 */
	private TextView tvMsg;

	/**
	 * Operate Buttons
	 */
	private TextView btnOperate1, btnOperate2;

	/**
	 * Tag Object
	 */
	private Object mObjTag1, mObjTag2;

	/**
	 * Message / Operate1 Text / Operate2 Text
	 */
	private String mMsg = "", mOperate1Txt = "", mOperate2Txt = "";

	/**
	 * Operate1 click listener / Operate2 click listener
	 */
	private View.OnClickListener mOperate1OnClick, mOperate2OnClick;

	/**
	 * true - just has one operate.
	 */
	private boolean mIsJustToast = false;

	public OperateDialog(Context context, int contentResID) {
		super(context);
		this.mContext = context;
		this.mContentResID = contentResID;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mContentResID > 0) {
			setContentView(mContentResID);
		} else {
			setContentView(R.layout.lib_v_ioperate_2btn_dialog);
		}

		//
		tvMsg = (TextView) findViewById(R.id.tv_message);
		tvMsg.setText(mMsg);

		btnOperate1 = (TextView) findViewById(R.id.btn_operate_1);
		btnOperate1.setOnClickListener(mOperate1OnClick);
		if (!TextUtils.isEmpty(mOperate1Txt)) {
			btnOperate1.setText(mOperate1Txt);
		}
		if (mObjTag1 != null) {
			btnOperate1.setTag(mObjTag1);
		}

		//
		btnOperate2 = (TextView) findViewById(R.id.btn_operate_2);
		btnOperate2.setOnClickListener(mOperate2OnClick);
		if (!TextUtils.isEmpty(mOperate2Txt)) {
			btnOperate2.setText(mOperate2Txt);
		}
		if (mObjTag2 != null) {
			btnOperate2.setTag(mObjTag2);
		}

		//
		if (mIsJustToast) {
			findViewById(R.id.iv_v_seperate).setVisibility(View.GONE);
			btnOperate2.setVisibility(View.GONE);
		}
	}

	/**
	 * Set Message
	 */
	public void setMessage(int resID) {
		setMessage(mContext.getString(resID));
	}

	/**
	 * Set Message
	 */
	public void setMessage(CharSequence message) {
		if (message != null) {
			mMsg = message.toString();
			if (tvMsg != null) {
				tvMsg.setText(mMsg);
			}
		}
	}

	/**
	 * Operate First
	 */
	public void setOperate1Txt(int resID) {
		setOperate1Txt(mContext.getString(resID));
	}

	/**
	 * Operate First
	 */
	public void setOperate1Txt(CharSequence operate1Txt) {
		if (operate1Txt != null) {
			mOperate1Txt = operate1Txt.toString();
			if (btnOperate1 != null) {
				btnOperate1.setText(mOperate1Txt);
			}
		}
	}

	/**
	 * Operate First
	 */
	public void setOperate1Tag(Object objTag) {
		mObjTag1 = objTag;
		if (objTag != null && btnOperate1 != null) {
			btnOperate1.setTag(objTag);
		}
	}

	/**
	 * Operate Second
	 */
	public void setOperate2Txt(int resID) {
		setOperate2Txt(mContext.getString(resID));
	}

	/**
	 * Operate Twice
	 */
	public void setOperate2Txt(CharSequence operate2Txt) {
		if (operate2Txt != null) {
			mOperate2Txt = operate2Txt.toString();
			if (btnOperate2 != null) {
				btnOperate2.setText(mOperate2Txt);
			}
		}
	}

	/**
	 * Operate Twice
	 */
	public void setOperate2Tag(Object objTag) {
		mObjTag2 = objTag;
		if (objTag != null && btnOperate2 != null) {
			btnOperate2.setTag(objTag);
		}
	}

	/**
	 * Operate First Click Listener
	 */
	public void setOperate1OnClick(View.OnClickListener l) {
		mOperate1OnClick = l;
		if (btnOperate1 != null) {
			btnOperate1.setOnClickListener(l);
		}
	}

	/**
	 * Operate Second Click Listener
	 */
	public void setOperate2OnClick(View.OnClickListener l) {
		mOperate2OnClick = l;
		if (btnOperate2 != null) {
			btnOperate2.setOnClickListener(l);
		}
	}

	/**
	 * Just has one operate "sure"
	 * 
	 * @param isJustToast
	 *            : true - just has one operate.
	 */
	public void setJustToast(boolean isJustToast) {
		mIsJustToast = isJustToast;
	}

	/**
	 * Show Dialog
	 */
	public void show(boolean isAudoDismiss) {
		super.show();
		// Auto Dismiss Dialog
		if (isAudoDismiss) {
			mHandler.removeCallbacks(mTimeoutRunnable);
			mHandler.postDelayed(mTimeoutRunnable, M_TIMEOUT_PERIOD);
		}
	}

	private Runnable mTimeoutRunnable = new Runnable() {

		@Override
		public void run() {
			if (mOperate1OnClick != null) {
				if (isShowing()) {
					mOperate1OnClick.onClick(btnOperate1);
				}
			}
		}
	};

	@Override
	public void dismiss() {
		super.dismiss();
		mHandler.removeCallbacksAndMessages(null);
	}
}
