package qnopy.com.qnopyandroid.clientmodel;

public class CopiedTemplate {
    private String fileName;
    private Long creationDate;
    private String copiedForm;

    public CopiedTemplate() {
    }

    public CopiedTemplate(String fileName, String copiedForm) {
        this.fileName = fileName;
        this.copiedForm = copiedForm;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getCopiedForm() {
        return copiedForm;
    }

    public void setCopiedForm(String copiedForm) {
        this.copiedForm = copiedForm;
    }
}
