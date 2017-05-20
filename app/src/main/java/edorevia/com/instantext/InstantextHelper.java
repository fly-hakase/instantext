package edorevia.com.instantext;

import android.telephony.SmsManager;

import java.util.ArrayList;

public class InstantextHelper {

    public static void sendSMSMessage(String phoneNo, ArrayList<String> parts, SmsManager smsManager) {
        try {
            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
