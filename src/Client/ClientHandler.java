package Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.Scanner;

public class ClientHandler {
    private String id;
    private Socket socket;
    private Scanner sc;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connection = false;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.sc = new Scanner(System.in);
    }

    public void setConnection(boolean connection) {
        this.connection = connection;
    }

    public void handleId() throws IOException, ClassNotFoundException {
        System.out.println("Enter Your Id");
        String id = sc.nextLine();
        out.writeObject(id);
        connectionCheck();
    }

    public void connectionCheck() throws IOException, ClassNotFoundException {
        String reply = (String) in.readObject();
        System.out.println(reply);
        if (Objects.equals(reply, "Connection Successful")) {
            connection = true;
        }
    }

    public void write(String toServer) throws IOException {
        out.writeObject(toServer);
    }

    public String read() throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }

    public boolean getConnection() {
        return connection;
    }

    public void upload() throws IOException, ClassNotFoundException {
        File file;
        System.out.println((String) in.readObject());
        String choice = sc.nextLine();
        out.writeObject(choice);
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Choose a file to upload");
        if (jFileChooser.showOpenDialog(null) == jFileChooser.APPROVE_OPTION) {
            file = jFileChooser.getSelectedFile();
            System.out.println("File you want to sent ==> " + file.getName());
            System.out.println("Size of the File ==> " + file.length());
            System.out.println((String) in.readObject());
            out.writeObject(file.getName());
            System.out.println((String) in.readObject());
            out.writeObject(file.length());
            try {
                int chunk = (int) in.readObject();
                System.out.println("File Send in Chunk of " + chunk);
                out.writeObject("");
                Integer fileId = (Integer) in.readObject();
                System.out.println("FileID " + fileId);
                int fileSize = (int) file.length();
                int loopNo = (fileSize / chunk);
                if (fileSize % chunk > 0) {
                    loopNo++;
                }
                System.out.println(loopNo);
                out.writeObject(loopNo);
                System.out.println((String) in.readObject());
                byte[] buffer = new byte[fileSize];
                int offset = 0;
                InputStream fis = new FileInputStream(file);
                OutputStream fout = socket.getOutputStream();
                boolean timeOut = false;
                for (int i = 0; i < loopNo; i++) {
                    int count = fis.read(buffer, offset, Math.min(chunk, fileSize));
                    fout.write(buffer, offset, count);
                    System.out.println(offset + "-->" + fileSize + "-->" + count);
                    offset += chunk;
                    fileSize -= chunk;
                    try {
                        socket.setSoTimeout(3000);
                        System.out.println((String) in.readObject());
                    }catch (SocketTimeoutException e) {
                        timeOut = true;
                        break;
                    }
                }
                if(timeOut){
                    write("TO");
                }else{
                    write("Done");
                }

            } catch (ClassCastException e) {
                System.out.println("Uploading Failed due to Server Storage shortage");
            }

        }
    }

    public void download() throws IOException, ClassNotFoundException {
        System.out.println(read());
        String studentId = sc.nextLine();
        write(studentId);
        System.out.println(read());
        String fileName = sc.nextLine();
        write(fileName);
        String reply = read();
        System.out.println(reply);
        if(reply.equals("No")){
            write("Closing Client Download");
            return;
        }else{
            write("Starting Download");
        }
        int dSize = (int) in.readObject();
        write("");
        int loopNo = (int) in.readObject();
        write("");
        int chunk = (int)in.readObject();
        System.out.println(loopNo);
        byte[] fileBytes = new byte[dSize + 300];
        String dirPath = "src"+File.separator+"Client"+File.separator+"files";
        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdir();
        }
        File newFile = new File(dirPath +File.separator + fileName);
        int offset = 0;
        int updateSize =0;
        InputStream fin = socket.getInputStream();
        OutputStream fout = new FileOutputStream(newFile);
        int lastChunk = dSize%chunk;
        boolean gotIt = false;
        System.out.printf(loopNo + "--" + dSize);
        for (int i = 0; i < loopNo; i++) {
            System.out.println("Receiving " + i);
            if(i==loopNo-1) {
                updateSize += fin.read(fileBytes, offset, lastChunk);
            }else {
                updateSize += fin.read(fileBytes, offset, chunk);
            }
            fout.write(fileBytes, offset, Math.min(lastChunk,chunk));
            offset += chunk;
            }
        if(updateSize == dSize) {
            System.out.println(updateSize + "<==>" + dSize);
            write("Got The File");
        }
        }

    public void request() throws IOException, ClassNotFoundException {
        System.out.println((String)in.readObject());
        String description = sc.nextLine();
        write(description);
    }

    public void show() throws IOException, ClassNotFoundException {
        String allMsg = (String)in.readObject();
        System.out.println(allMsg);
    }
}
