package mobilecrowdsourceStudy.nctu.minuku_2.controller;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chiaenchiang on 13/12/2018.
 */

public class ResponseResult {

    @SerializedName("fields")
    @Expose
    private String fields;
    @SerializedName("files")
    @Expose
    private String files;

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

}

