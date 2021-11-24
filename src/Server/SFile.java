package Server;

public class SFile {
    private String fileName;
    private Integer id;
    private Integer size;
    private String type;
    private byte[] buffer;

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
        this.buffer = new byte[size];
    }

    public SFile() {
    }

    public void setBuffer(byte[] buffer){
        this.buffer = buffer;
    }
    public byte[] getBuffer(){
        return this.buffer;
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
