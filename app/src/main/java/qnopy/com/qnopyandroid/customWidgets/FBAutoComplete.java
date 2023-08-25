package qnopy.com.qnopyandroid.customWidgets;

//import org.xml.sax.Parser;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import qnopy.com.qnopyandroid.uiutils.FormMaster;


public class FBAutoComplete extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    Context ObjectContext;

    FormMaster adaptercontext;

    public FBAutoComplete(Context context) {
        super(context);

        ObjectContext = context;

        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    public void setLocationAdapterContext(FormMaster context) {
        this.adaptercontext = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        super.onTouchEvent(event);
        System.out.println("KKKK " + " onTouchEvent " + getText() + " instance" + this.getText());
        performFiltering(getText(), 0);
        return true;
    }

    public void initialSetUp() {

        final FBAutoComplete autCompleteContext = this;

        this.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                InputMethodManager in = (InputMethodManager) ObjectContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(arg1.getApplicationWindowToken(), 0);

                String posStr = (String) autCompleteContext.getTag();
                String displayValue = (String) autCompleteContext.getText().toString();
                int pos = Integer.parseInt(posStr);

//                autCompleteContext.adaptercontext.updateData(posStr, displayValue, pos);
//                autCompleteContext.adaptercontext.updateChild(posStr, displayValue, pos);
//                autCompleteContext.adaptercontext.update_NavigateTo_formID(posStr, displayValue);
//                autCompleteContext.adaptercontext.notifyDataSetChanged();

            }
        });



        this.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

                String posStr = (String) autCompleteContext.getTag();
                String displayValue = (String) autCompleteContext.getText().toString();

                if (!hasFocus){
                    initialSetUp();

                }

            }

        });
    }


}
