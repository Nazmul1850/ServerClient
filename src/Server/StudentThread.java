package Server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Queue;

public class StudentThread extends Thread{
    Socket socket;
    ArrayList<Student> students;
    private Student currentStudent;
    private Integer fileID = 0;
    private final Integer MAX_Buffer_Size;
    private Integer currentBuffer;
    private boolean fileRequested;
    private Integer currentRequestID;

    public StudentThread(Socket socket, ArrayList<Student> students, Integer max_buffer_size,Integer currentBuffer) {
        this.socket = socket;
        this.students = students;
        this.MAX_Buffer_Size = max_buffer_size;
        this.currentBuffer = currentBuffer;
        this.fileRequested = false;
        this.currentRequestID = 0;
    }
    public void run(){
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            String id = (String)in.readObject();

            System.out.println("Id-->" + id + " Port-->" + socket.getPort());
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
                            try {
                                cmd = (String) in.readObject();
                            }catch (EOFException e) {
                                System.out.println("Unexpectedly disconnected");
                                currentStudent.setConnection(false);
                                break;
                            }
                        }catch (SocketException e) {
                            System.out.println("Socket disconnected");
                            currentStudent.setConnection(false);
                            break;
                        }
                    }
                    System.out.println(cmd);
                    int MAX_Chunk_Size = 2*1024;
                    int MIN_Chunk_Size = 100;
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
                        case "UR":
                            out.writeObject("Give RequestID");
                            currentRequestID = Integer.parseInt((String)in.readObject());
                            System.out.println(currentRequestID);
                            boolean requestIdValidity = false;
                            for(Student x:students){
                                for(RequestText r: x.getAllMessage()){
                                    System.out.println(r);
                                    if(r.getRequestId().equals(currentRequestID)){
                                        requestIdValidity = true;
                                        break;
                                    }
                                }
                            }
                            System.out.println(requestIdValidity);
                            if(requestIdValidity){
                                out.writeObject("Upload The File");
                                fileRequested = true;
                            }else{
                                out.writeObject("Invalid ID");
                            }
                            break;
                        case "upload":
                            out.writeObject("public or private?");
                            String type = (String)in.readObject();
                            System.out.println(type);
                            out.writeObject("Send File Name and Size");
                            String fileName = (String)in.readObject();
                            out.writeObject("");
                            String fileSize = in.readObject() + "";
                            System.out.println(fileName + fileSize);
                            Integer fileSizeint = Integer.parseInt(fileSize);
                            if(MAX_Buffer_Size - (currentBuffer+fileSizeint) >= 30) {
                                SFile uploadingFile = new SFile(fileName,fileID++,fileSizeint,type,id);
                                System.out.println(uploadingFile);
                                int chunk = (int) ((Math.random() * (MAX_Chunk_Size - MIN_Chunk_Size)) + MIN_Chunk_Size);
                                out.writeObject(chunk);
                                System.out.println((String)in.readObject());
                                out.writeObject((fileID-1));
                                int loopNo = (int)in.readObject();
                                //System.out.println(loopNo);
                                out.writeObject("Send chunks");
                                byte[] fileBytes = new byte[fileSizeint + 300];
                                File newFile = uploadingFile.createFile();
                                int offset = 0;
                                int lastChunk = fileSizeint%chunk;
                                int updateSize = 0;
                                InputStream fin = socket.getInputStream();
                                OutputStream fout = new FileOutputStream(newFile);
                                boolean gotIt = false;
                                try {
                                    for (int i = 0; i < loopNo; i++) {
                                        if (i == loopNo - 1) {
                                            updateSize += fin.read(fileBytes, offset, lastChunk);
                                            fout.write(fileBytes, offset, lastChunk);
                                            offset += lastChunk;
                                            out.writeObject("Got the last Chunk");
                                        } else {
                                            updateSize += fin.read(fileBytes, offset, chunk);
                                            fout.write(fileBytes, offset, chunk);
                                            offset += chunk;
                                            out.writeObject("Got the chunk (" + (i + 1) + ")");
                                        }

                                    }
                                    if (updateSize == fileSizeint) {
                                        System.out.println(updateSize + "<==>" + fileSizeint);
                                        gotIt = true;
                                    }
                                    String rTimeOut = (String) in.readObject();
                                    if(rTimeOut.equals("TO")) {
                                        System.out.println("Timed out Uploading Failed");
                                        uploadingFile.deleteFile();
                                        gotIt = false;
                                    }else{
                                        System.out.println(rTimeOut);
                                    }
                                }catch (SocketException | EOFException e) {
                                    System.out.println("Socket Disconnected File Sharing is not complete");
                                    uploadingFile.deleteFile();
                                    break;
                                }

                                if(gotIt){
                                    if(fileRequested && currentRequestID != 0){
                                        uploadingFile.setRequestID(currentRequestID);
                                    }
                                    //System.out.println(uploadingFile);
                                    currentBuffer += fileSizeint;
                                    //System.out.println(updateSize + "--Length-->" + fileSizeint);
                                    if(uploadingFile.getType().equals("public")) {
                                        currentStudent.addPublicFile(uploadingFile);
                                    }
                                    if(uploadingFile.getType().equals("private")) {
                                        currentStudent.addPrivateFile(uploadingFile);
                                    }
                                    if(fileRequested && currentRequestID != 0){
                                        System.out.println(currentStudent.getID()+" <Student>");
                                        RequestText requestText = null;
                                        for(RequestText r:currentStudent.getAllMessage()){
                                            System.out.println(r.getRequestId() +" <==> " +currentRequestID);
                                            if(r.getRequestId().equals(currentRequestID)){
                                                System.out.println("Req ID" + r.getRequestId());
                                                requestText = r;
                                                break;
                                            }
                                        }
                                        if(requestText != null) {
                                            String msg = currentStudent.getID() + " Uploaded file "+uploadingFile.getFileName()+" for the request " +requestText.getRequestId();
                                            for(Student x:students){
                                                if(requestText.getStudentId().equals(x.getID())) {
                                                    x.addMessage(new RequestText(msg));
                                                }
                                            }
                                        }

                                        currentRequestID = 0;
                                        fileRequested = false;
                                    }
                                }
                            }else{
                                out.writeObject("Server is Full. Uploading failed");
                            }
                            break;
                        case "exit":
                            currentStudent.setConnection(false);
                            System.out.println(currentStudent.getID() + "--> Logged Out");
                            out.writeObject("You Are Logged Out");
                            try{
                                socket.close();
                            }catch (SocketException e) {
                                System.out.println("Socket is closed");
                            }

                            break;
                        case "download":
                            out.writeObject("Give ID");
                            String dID = (String)in.readObject();
                            boolean own = false;
                            if(dID.equals("own")){
                                dID = currentStudent.getID();
                                own = true;
                            }
                            out.writeObject("Give FileName");
                            String dFileName = (String)in.readObject();
                            System.out.println(dFileName + "-->" + dID);
                            SFile downloadingFile= new SFile();
                            for(Student x:students){
                                if(x.getID().equals(dID)) {
                                    if(own){
                                        downloadingFile =x.searchPrivateFile(dFileName);
                                        System.out.println("private file");
                                        if(downloadingFile == null){
                                            downloadingFile = x.searchPublicFiles(dFileName);
                                            System.out.println("public file");
                                        }
                                    }else{
                                        downloadingFile = x.searchPublicFiles(dFileName);
                                    }
                                }
                            }
                            if(downloadingFile == null){
                                System.out.println("Didnt Found the file");
                                out.writeObject("No");
                                System.out.println((String)in.readObject());
                            }else {
                                out.writeObject("yes");
                                System.out.println((String)in.readObject());
                                System.out.println(downloadingFile);
                                File download = downloadingFile.getFile();
                                int dSize = (int) download.length();
                                System.out.println(dSize);
                                int loopNo = dSize / MAX_Chunk_Size;
                                if (dSize % MAX_Chunk_Size > 0) {
                                    loopNo++;
                                }
                                out.writeObject(dSize);
                                System.out.println((String) in.readObject());
                                out.writeObject(loopNo);
                                System.out.println((String) in.readObject());
                                out.writeObject(MAX_Chunk_Size);
                                byte[] buffer = new byte[dSize];
                                int offset = 0;
                                InputStream fis = new FileInputStream(download);
                                OutputStream fout = socket.getOutputStream();
                                System.out.println(loopNo + "--" + dSize);
                                for (int i = 0; i < loopNo; i++) {
                                    int count;
                                    if (i == loopNo - 1) {
                                        count = fis.read(buffer, offset, dSize % MAX_Chunk_Size);
                                    } else {
                                        count = fis.read(buffer, offset, MAX_Chunk_Size);
                                    }
                                    fout.write(buffer, offset, count);
                                    System.out.println("Sending (" + offset +") to ("+(offset+count) + ")");
                                    offset += MAX_Chunk_Size;
                                }
                                System.out.println("finishing Download");
                                System.out.println((String) in.readObject());
                            }
                            break;
                        case "request":
                            out.writeObject("Describe The File");
                            String description = (String)in.readObject();
                            int requestID = description.hashCode();
                            for(Student x:students){
                                if(!currentStudent.getID().equals(x.getID())){
                                    RequestText request = new RequestText(description,requestID,currentStudent.getID());
                                    request.setSeen(false);
                                    System.out.println(request);
                                    x.addMessage(request);
                                }
                            }
                            break;
                        case "show":
                            Queue<RequestText> allReq = currentStudent.getAllMessage();
                            String allMsg = "";
                            for(RequestText r:allReq){
                                if(!r.isSeen()) {
                                    if(r.getRequestId() == 0) {
                                        allMsg += "\"" + r.getMessage() + "\"\n";
                                    }else{
                                        allMsg += "\"" + r.getMessage() + "\"(Request ID) => " + r.getRequestId() + "\n";
                                    }
                                    r.setSeen(true);

                                }
                            }
                            out.writeObject(allMsg);
                            break;
                        default:
                            try{
                                out.writeObject("Command Error");
                            }catch (SocketException e) {
                                System.out.println("Socket Disconnected -");
                                currentStudent.setConnection(false);
                                break;
                            }

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
            if(!x.getID().equals(currentStudent.getID())) {
                str += x.getID();
                if(x.isConnected()) str += "--> Online\n";
                else str += "--> Offline\n";
            }
        }
        return str;

    }

}
