package Server;

public class SFile {
    private String fileName;
    private Integer id;
    private Integer size;
    private String type;

    @Override
    public String toString() {
        return "SFile{" +
                "fileName='" + fileName + '\'' +
                ", id=" + id +
                ", size=" + size +
                ", type='" + type + '\'' +
                '}';
    }

    public SFile(String fileName, Integer id, Integer size, String type) {
        this.fileName = fileName;
        this.id = id;
        this.size = size;
        this.type = type;
    }

    public SFile() {
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
