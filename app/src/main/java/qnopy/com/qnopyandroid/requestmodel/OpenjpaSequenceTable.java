package qnopy.com.qnopyandroid.requestmodel;

public class OpenjpaSequenceTable {

    private Short id;

    private Long sequenceValue;

    public Long getSequenceValue() {
        return sequenceValue;
    }

    public void setSequenceValue(Long sequenceValue) {
        this.sequenceValue = sequenceValue;
    }

    public Short getId() {
        return this.id;
    }

    public void setId(Short id) {
        this.id = id;
    }

}
