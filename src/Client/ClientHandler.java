package Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
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
    public void handleId() throws IOException, ClassNotFoundException {
        String id = sc.nextLine();
        out.writeObject(id);
        connectionCheck();
    }
    public void connectionCheck() throws IOException, ClassNotFoundException {
        String reply = (String)in.readObject();
        System.out.println(reply);
        if(Objects.equals(reply, "Connection Successful")){
            connection = true;
        }
    }
    public void write(String toServer) throws IOException {
        out.writeObject(toServer);
    }
    public String read() throws IOException, ClassNotFoundException {
        return (String)in.readObject();
    }

    public boolean getConnection() {
        return connection;
    }

    public void upload() throws IOException, ClassNotFoundException {
        File file;
        System.out.println((String)in.readObject());
        String choice = sc.nextLine();
        out.writeObject(choice);
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Choose a file to upload");
        if(jFileChooser.showOpenDialog(null) == jFileChooser.APPROVE_OPTION){
            file = jFileChooser.getSelectedFile();
            System.out.println("File you want to sent ==> " + file.getName());
            System.out.println("Size of the File ==> " + file.length());
            System.out.println((String)in.readObject());
            out.writeObject(file.getName());
            System.out.println((String)in.readObject());
            out.writeObject(file.length());
            try {
                int chunk = (int) in.readObject();
                System.out.println("File Send in Chunk of " + chunk);
                out.writeObject("");
                Integer fileId = (Integer)in.readObject();
                System.out.println("FileID "+ fileId);
                int fileSize = (int)file.length();
                int loopNo = (fileSize/chunk);
                if(fileSize%chunk > 0){
                    loopNo++;
                }
                System.out.println(loopNo);
                out.writeObject(loopNo);
                System.out.println((String)in.readObject());
                byte[] buffer = new byte[fileSize];
                int offset = 0;
                InputStream fis = new FileInputStream(file);
                OutputStream fout = socket.getOutputStream();
                for(int i=0;i<loopNo;i++){
                    int count = fis.read(buffer,offset,Math.min(chunk,fileSize));
                    fout.write(buffer,offset,count);
                    System.out.println(offset + "-->" + fileSize + "-->" + count);
                    offset += chunk;
                    fileSize -= chunk;
                    System.out.println((String)in.readObject());
                }

            }catch (ClassCastException e){
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
    }
}
