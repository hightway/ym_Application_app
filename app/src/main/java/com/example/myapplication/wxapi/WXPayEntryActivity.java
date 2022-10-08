package com.example.myapplication.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.http.Api;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements
		IWXAPIEventHandler, View.OnClickListener {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	public boolean pay = false;

	private TextView tv_pay_result, tv_main_title;
	
	private RelativeLayout rl_back;
	private TextView back_btn;

	public static final int SERVER_PAY_SUCCESS = 101; // 服务器支付成功

	public static final int SERVER_PAY_FAIL = 102; // 服务器支付失败



	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() != ConstantsAPI.COMMAND_PAY_BY_WX) {
			return;
		}
		switch (resp.errCode) {
		case 0:
			Message msg = new Message();
			msg.what = SERVER_PAY_SUCCESS;
			mHandler.sendMessage(msg);
			break;
		case -1:
			tv_pay_result.setText("支付失败，请联系商家");
			break;
		case -2:
			tv_pay_result.setText("已取消支付");
			break;
		default:
			break;
		}

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SERVER_PAY_SUCCESS: {
				tv_pay_result.setText("支付成功");
				break;
			}
			case SERVER_PAY_FAIL: {

				tv_pay_result.setText("支付失败，请联系商家");
				break;
			}
			default:
				break;
			}
		};
	};


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);

		back_btn = findViewById(R.id.back_btn);
		back_btn.setOnClickListener(this);
		tv_pay_result = findViewById(R.id.tv_pay_reault);

		api = WXAPIFactory.createWXAPI(this, Api.APP_ID);
		api.handleIntent(getIntent(), this);
	};


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.back_btn:
				WXPayEntryActivity.this.finish();
				break;
			default:
				break;
		}
	}

}