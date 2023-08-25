package qnopy.com.qnopyandroid.interfacemodel;

public interface AlertButtonOnClick {
    void positiveButtonClick();

    default void negativeButtonClick() {

    }
}
