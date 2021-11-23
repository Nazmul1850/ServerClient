package Server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Objects;

public class StudentThread extends Thread{
    Socket socket;
    ArrayList<Student> students;
    private Student currentStudent;
    private Integer fileID = 0;
    private Integer MAX_Buffer_Size;
    private final int MAX_Chunk_Size=100;
    private final int MIN_Chunk_Size=30;
    public StudentThread(Socket socket, ArrayList<Student> students, Integer max_buffer_size) {
        this.socket = socket;
        this.students = students;
        MAX_Buffer_Size = max_buffer_size;
    }
    public void run(){
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            String id = (String)in.readObject();

            System.out.println("-->"+socket.getPort() + id);
            boolean preConnected = false;
            for(Student x: students) {
                if(Objects.equals(x.getID(), id)) {
                    preConnected = true;
                    break;
                }
            }
            if(preConnected) {
                out.writeObject("Already Connected");
            }else {
                out.writeObject("Connection Successful");
                currentStudent = new Student(""+socket.getPort(), id);
                currentStudent.setConnection(true);
                students.add(currentStudent);
                File file = new File(id);
                if(file.mkdir()) {
                    File pub = new File(id+"/public");
                    pub.mkdir();
                    File pri = new File(id+"/private");
                    pri.mkdir();
                    System.out.println("Folder Created");
                }else{
                    System.out.println("Cant create folder");
                }
                while(true){
                    Thread.sleep(1000);
                    String cmd = "";
                    if(socket.isConnected()){
                        try {
                            cmd = (String) in.readObject();
                        }catch (SocketException e) {
                            System.out.println("Socket disconnected");
                        }
                    }
                    System.out.println(cmd);
                    String[] cmdSplit = cmd.split(" ");
                    switch (cmd){
                        case "lookup user":
                            String toStudent = lookupUser();
                            System.out.println(toStudent);
                            out.writeObject(toStudent);
                            break;
                        case "lookup files":
                            toStudent = lookupFile(currentStudent.getID(),true);
                            System.out.println(toStudent);
                            out.writeObject(toStudent);
                            break;
                        case "look for":
                            out.writeObject("Give ID");
                            String ID = (String)in.readObject();
                            toStudent = lookupFile(ID,false);
                            System.out.println(toStudent);
                            out.writeObject(toStudent);
                            break;
                        case "upload":
                            out.writeObject("public or private?");
                            String type = (String)in.readObject();
                            System.out.println(type);
                            out.writeObject("Send File Name and Size");
                            String fileName = (String)in.readObject();
                            out.writeObject("");
                            String fileSize = (Long)in.readObject() + "";
                            System.out.println(fileName + fileSize);
                            Integer fileSizeint = Integer.parseInt(fileSize);
                            if(MAX_Buffer_Size - (fileSizeint/1024) >= 30) {
                                SFile uploadingFile = new SFile(fileName,fileID++,fileSizeint,type);
                                System.out.println(uploadingFile);
                                int chunk = (int) ((Math.random() * (MAX_Chunk_Size - MIN_Chunk_Size)) + MIN_Chunk_Size);
                                out.writeObject(chunk);
                                System.out.println((String)in.readObject());
                                out.writeObject((fileID-1));
                            }else{
                                out.writeObject("Server is Full. Uploading failed");
                            }
                            break;
                        case "exit":
                            currentStudent.setConnection(false);
                            System.out.println(currentStudent.getID() + "--> Logged Out");
                            out.writeObject("LO");
                            try{
                                socket.close();
                            }catch (SocketException e) {
                                System.out.println("Socket is closed");
                            }

                            break;
                        default:
                            break;
                    }
                }
            }


        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }



    private String lookupFile(String id, boolean self) {
        String str = "";
        String[] pubFiles = new String[0];
        String[] priFiles = new String[0];
        File pub = new File(id+"/public");
        if(pub.isDirectory()) {
            pubFiles = pub.list();
        }
        str += "=========PUBLIC==========\n";
        assert pubFiles != null;
        for(String x:pubFiles) {
            str += x + "\n";
        }
        if(self){
            File pri = new File(id+"/private");
            if(pri.isDirectory()) {
                priFiles = pri.list();
            }
            str += "=========PRIVATE==========\n";
            assert priFiles != null;
            for(String x:priFiles) {
                str += x + "\n";
            }
        }

        return str;
    }

    private String lookupUser() {
        String str = "";
        for(Student x:students) {
            str += x.getID();
            if(x.isConnected()) str += "--> Online\n";
            else str += "--> Offline\n";
        }
        return str;

    }

}
