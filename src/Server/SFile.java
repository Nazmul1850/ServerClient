package Server;

import java.io.File;
import java.io.FileFilter;

public class SFile {
    private String fileName;
    private Integer id;
    private String studentId;
    private Integer size;
    private String type;
    private File file;

    @Override
    public String toString() {
        return "SFile{" +
                "fileName='" + fileName + '\'' +
                ", id=" + id +
                ", size=" + size +
                ", type='" + type + '\'' +
                '}';
    }

    public SFile(String fileName, Integer id, Integer size, String type,String SID) {
        this.fileName = fileName;
        this.id = id;
        this.size = size;
        this.type = type;
        this.studentId = SID;
    }

    public SFile() {
    }
    public File createFile(){
        System.out.println(studentId+File.separator+type+ File.separator+fileName+"("+id+")");
        file = new File(studentId+File.separator+type+ File.separator+fileName+"("+id+")");
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
}
