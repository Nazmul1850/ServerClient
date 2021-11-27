package Server;

import java.io.File;
import java.io.FileFilter;

public class SFile {
    private String fileName;
    private Integer id;
    private String studentId;
    private Integer requestID;
    private Integer size;
    private String type;
    private File file;

    @Override
    public String toString() {
        return "SFile{" +
                "fileName='" + fileName + '\'' +
                ", id=" + id +
                ", size=" + size +
                ", type='" + type +
                ", requestID" + requestID + '\n'+
                '}';
    }

    public SFile(String fileName, Integer id, Integer size, String type,String SID) {
        this.fileName = fileName;
        this.id = id;
        this.size = size;
        this.type = type;
        this.studentId = SID;
        this.requestID = 0;
    }

    public SFile() {
    }
    public File createFile(){
        System.out.println(studentId+File.separator+type+ File.separator+fileName);
        file = new File(studentId+File.separator+type+ File.separator+fileName);
        return file;
    }

    public String getType() {
        return type;
    }

    public Integer getRequestID() {
        return requestID;
    }

    public void setRequestID(Integer requestID) {
        this.requestID = requestID;
    }

    public File getFile(){
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void deleteFile() {
        if(file.delete()){
            System.out.println("Deleted File");
        }else{
            System.out.println("Cant delete the File");
        }
    }
}
