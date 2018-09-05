package com.tricheer.player.version.base.view;

import android.content.Context;
import android.view.View;

import com.lib.view.OperateDialog;
import com.tricheer.player.R;

public class PromptDialog {
	private Context mContext;
	private int mContentResID = -1;
	private OperateDialog mOperateDialog;

	private PromptListener mPromptListener;

	public interface PromptListener {
		public void afterPromptDialogOpened();

		public void afterPrompDialogSureDismissed();
	}

	public PromptDialog(Context cxt, PromptListener l) {
		this.mContext = cxt;
		this.mPromptListener = l;
	}

	public PromptDialog(Context cxt, int contentResID, PromptListener l) {
		this.mContext = cxt;
		this.mContentResID = contentResID;
		this.mPromptListener = l;
	}

	public void showDialog(int msgResID) {
		if (mOperateDialog == null) {
			if (mContentResID == -1) {
				mOperateDialog = new OperateDialog(mContext, R.layout.zpt_lv8918_slb_v_dialog_operates);
			} else {
				mOperateDialog = new OperateDialog(mContext, mContentResID);
			}
			mOperateDialog.setJustToast(true);
			mOperateDialog.setCancelable(false);
			mOperateDialog.setCanceledOnTouchOutside(false);
			mOperateDialog.setOperate1OnClick(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mOperateDialog.dismiss();
					afterPrompDialogSureDismissed();
				}
			});
		}

		if (!mOperateDialog.isShowing()) {
			mOperateDialog.setMessage(msgResID);
			mOperateDialog.show(true);
			afterPromptDialogOpened();
		}
	}

	private void afterPrompDialogSureDismissed() {
		if (mPromptListener != null) {
			mPromptListener.afterPrompDialogSureDismissed();
		}
	}

	private void afterPromptDialogOpened() {
		if (mPromptListener != null) {
			mPromptListener.afterPromptDialogOpened();
		}
	}

	public boolean isShowing() {
		if (mOperateDialog != null) {
			return mOperateDialog.isShowing();
		}
		return false;
	}
}
