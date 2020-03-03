package gmedia.net.id.osbond;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	private static SmsListener mListener;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		SmsMessage[] smsm = null;
		String sms_str = "";

		if (bundle != null) {
			// Get the SMS message
			Object[] pdus = (Object[]) bundle.get("pdus");
			smsm = new SmsMessage[pdus.length];
			for (int i = 0; i < smsm.length; i++) {
				smsm[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				sms_str += "\r\nMessage: ";
				sms_str += smsm[i].getMessageBody().toString();
				sms_str += "\r\n";

				String Sender = smsm[i].getOriginatingAddress();
				//Check here sender is yours
				Intent smsIntent = new Intent("otp");
				smsIntent.putExtra("message", sms_str);

				LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);

			}
		}
	}

	public static void bindListener(SmsListener listener) {
		mListener = listener;
	}
}
