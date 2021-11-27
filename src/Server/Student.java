package Server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Student {
    private String port;
    private  String ID;
    private boolean connected = false;
    private ArrayList<SFile> publicFiles;
    private ArrayList<SFile> privateFiles;
    private Queue<RequestText> message;

    public Student(String port, String ID) {
        this.port = port;
        this.ID = ID;
        publicFiles = new ArrayList<>();
        privateFiles = new ArrayList<>();
        message = new LinkedList<>();
    }
    public void addMessage(RequestText msg){
        message.add(msg);
    }
    public Queue<RequestText> getAllMessage(){
        return message;
    }
    public void addPublicFile(SFile file){
        publicFiles.add(file);
    }
    public SFile searchPublicFiles(String fileName){
        for(SFile f:publicFiles){
            if(f.getFileName().equals(fileName)){
                return f;
            }
        }
        return null;
    }
    public SFile searchPublicFilesbyRequestedId(Integer rID){
        for(SFile f:publicFiles){
            if(f.getRequestID().equals(rID)){
                return f;
            }
        }
        return null;
    }
    public SFile searchPrivateFile(String fileName){
        for(SFile f:privateFiles){
            if(f.getFileName().equals(fileName)){
                return f;
            }
        }
        return null;
    }
    public void addPrivateFile(SFile file){
        publicFiles.add(file);
    }
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
    public boolean isConnected() {
        return connected;
    }

    public void setConnection(boolean connected) {
        this.connected = connected;
    }
}
