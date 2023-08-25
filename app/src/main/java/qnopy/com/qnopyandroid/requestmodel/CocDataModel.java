package qnopy.com.qnopyandroid.requestmodel;

import java.util.List;

/**
 * Created by QNOPY on 3/16/2018.
 */

public class CocDataModel {
    private List<SCocDetails> sCocDetails;
    private List<SCocMaster> sCocMaster;
    private List<CocMethod> cocMethod;
    private String lastSyncDate;

    public String getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(String lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    public List<CocMethod> getCocMethodsList() {
        return cocMethod;
    }

    public void setCocMethod(List<CocMethod> cocMethod) {
        this.cocMethod = cocMethod;
    }

    public List<SCocDetails> getCocDetailsList() {
        return sCocDetails;
    }

    public void setCocDetailsList(List<SCocDetails> cocDetailsList) {
        this.sCocDetails = cocDetailsList;
    }

    public List<SCocMaster> getCocMasterList() {
        return sCocMaster;
    }

    public void setCocMasterList(List<SCocMaster> cocMasterList) {
        this.sCocMaster = cocMasterList;
    }

    public static class CocMethod {
        private String analyses;

        private String container;

        private String preservative;

        private String noOfContainer;

        private String methodId;

        private String methodName;

        private String labName;

        private String suggQty;

        private String matrix;

        private String holdTime;

        public String getAnalyses() {
            return analyses;
        }

        public void setAnalyses(String analyses) {
            this.analyses = analyses;
        }

        public String getContainer() {
            return container;
        }

        public void setContainer(String container) {
            this.container = container;
        }

        public String getPreservative() {
            return preservative;
        }

        public void setPreservative(String preservative) {
            this.preservative = preservative;
        }

        public String getNoOfContainer() {
            return noOfContainer;
        }

        public void setNoOfContainer(String noOfContainer) {
            this.noOfContainer = noOfContainer;
        }

        public String getMethodId() {
            return methodId;
        }

        public void setMethodId(String methodId) {
            this.methodId = methodId;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getLabName() {
            return labName;
        }

        public void setLabName(String labName) {
            this.labName = labName;
        }

        public String getSuggQty() {
            return suggQty;
        }

        public void setSuggQty(String suggQty) {
            this.suggQty = suggQty;
        }

        public String getMatrix() {
            return matrix;
        }

        public void setMatrix(String matrix) {
            this.matrix = matrix;
        }

        public String getHoldTime() {
            return holdTime;
        }

        public void setHoldTime(String holdTime) {
            this.holdTime = holdTime;
        }
    }
}
