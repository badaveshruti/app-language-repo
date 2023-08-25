/**
 *
 */
package qnopy.com.qnopyandroid.customWidgets;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import qnopy.com.qnopyandroid.uiutils.FormMaster;


/**
 * @author Akshata
 */

public class FBAlphaTextView extends androidx.appcompat.widget.AppCompatEditText {
    String TAG = "FBAlphaTextView";
    Context ObjectContext;

    FormMaster form_master_context;

    public FBAlphaTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        ObjectContext = context;

    }

    public FormMaster getForm_master_context() {
        return form_master_context;
    }

    public void setForm_master_context(FormMaster form_master_context) {
        this.form_master_context = form_master_context;
    }

    // TODO: 06-Jul-17 AUTO SUBMITS THE FIELDS
    public void setListeners() {

        final FBAlphaTextView alphaTextView = this;


        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String keyStr = (String) alphaTextView.getTag();
                String value = (String) alphaTextView.getText().toString();

//                form_master_context.handleTextData(meta, value,form_master_context.getCurrentSetID());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

}
