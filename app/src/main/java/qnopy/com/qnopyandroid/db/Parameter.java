package qnopy.com.qnopyandroid.db;

public class Parameter {
    int ParamID;
    String ParamLabel;
    int InputTypeID;
    String NameValuePair;
    String DesiredUnis;
    String ValueType;
    String Required_Y_N;
    double Warning_high;
    double Warning_Low;
    double high_limit;
    double Low_limit;
    int RowOrder;
    int ColOrder;


    int getParamID() {
        return ParamID;
    }

    void setParamID(int ParamID) {
        this.ParamID = ParamID;
    }

    String getParamLabel() {
        return ParamLabel;
    }

    void setParamLable(String Label) {
        ParamLabel = Label;
    }

    int getInputType() {
        return InputTypeID;
    }

    void setInputType(int Type) {
        InputTypeID = Type;
    }

    int getRowOrder() {
        return RowOrder;
    }

    void setRowOrder(int Order) {
        RowOrder = Order;
    }

    int getColOrder() {
        return ColOrder;
    }

    void setColOrder(int Order) {
        ColOrder = Order;
    }
}