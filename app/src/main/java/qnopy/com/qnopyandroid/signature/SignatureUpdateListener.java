package qnopy.com.qnopyandroid.signature;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.requestmodel.CustomerSign;

public interface SignatureUpdateListener {
    void onSignatureRemoved(ArrayList<CustomerSign> sign);
    void onSignatureViewClicked();
}
