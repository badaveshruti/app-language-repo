package qnopy.com.qnopyandroid.clientmodel.metaForms;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.util.Util;

public class MetaFormsJsonResponse {

    private boolean success;
    private String responseCode;
    private String message;
    private FormData data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FormData getData() {
        return data;
    }

    public void setData(FormData data) {
        this.data = data;
    }

    public class FormData {
        private long lastFetchDate;
        private ArrayList<Forms> forms;

        public long getLastFetchDate() {
            return lastFetchDate;
        }

        public void setLastFetchDate(long lastFetchDate) {
            this.lastFetchDate = lastFetchDate;
        }

        public ArrayList<Forms> getForms() {
            return forms;
        }

        public void setForms(ArrayList<Forms> forms) {
            this.forms = forms;
        }
    }

    public class Forms {
        private Integer formId;
        private String formData;
        private FormsData formsDetails;//this will have decoded base64 formData for personal use

        public FormsData getFormsDetails() {
            return formsDetails;
        }

        public void setFormsDetails(FormsData formsDetails) {
            this.formsDetails = formsDetails;
        }

        public Integer getFormId() {
            return formId;
        }

        public void setFormId(Integer formId) {
            this.formId = formId;
        }

        public String getFormData() {
            return formData;
        }

        public void setFormData(String formData) {
            this.formData = Util.covertBase64ToString(formData);
        }
    }
}
