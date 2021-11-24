package Client;

import org.w3c.dom.ls.*;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File file;
        Socket socket = new Socket("localhost",6666);
        System.out.println("Connection established");
        System.out.println("Remote port: " + socket.getPort());
        System.out.println("Local port: " + socket.getLocalPort());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        System.out.println("Enter Your ID");
        Scanner sc = new Scanner(System.in);
        String id = sc.nextLine();
        out.writeObject(id);
        String reply = (String)in.readObject();
        System.out.println(reply);
        if(Objects.equals(reply, "Connection Successful")) {
            while(true) {
                System.out.println("Enter Your Command");
                String cmd = sc.nextLine();
                out.writeObject(cmd);
                if(cmd.equals("upload")) {
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
                            while (fileSize > 0){
                                int count = fis.read(buffer,offset,Math.min(chunk,fileSize));
                                //out.write(buffer,offset,count);
                                System.out.println(offset + "-->" + fileSize + "-->" + count);
                                offset += chunk;
                                fileSize -= chunk;
                            }

                        }catch (ClassCastException e){
                            System.out.println("Uploading Failed due to Server Storage shortage");
                        }

                    }
                }

                String replyFromServer = (String)in.readObject();
                System.out.println(replyFromServer);
                if(replyFromServer.equals("LO")) {
                    break;
                }
            }
        }

    }
}
