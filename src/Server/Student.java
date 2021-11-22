package Server;

public class Student {
    private String port;
    private  String ID;
    private boolean connected = false;

    public Student(String port, String ID) {
        this.port = port;
        this.ID = ID;
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
