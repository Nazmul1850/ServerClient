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
        ClientHandler clientHandler = new ClientHandler(socket);
        clientHandler.handleId();
        if(clientHandler.getConnection()) {
            while(true) {
                System.out.println("Enter Your Command");
                Scanner sc = new Scanner(System.in);
                String cmd = sc.nextLine();
                if(cmd.equals("upload")) {
                    clientHandler.write(cmd);
                    clientHandler.upload();
                }else if (cmd.equals("exit")){
                    clientHandler.write(cmd);
                    String replyFromServer = clientHandler.read();
                    System.out.println(replyFromServer);
                    if(replyFromServer.equals("LO")) {
                        break;
                    }
                }else if (cmd.equals("download")){
                    clientHandler.write(cmd);
                    clientHandler.download();
                }
                else{
                    clientHandler.write(cmd);
                    String replyFromServer = clientHandler.read();
                    System.out.println(replyFromServer);
                }
            }
        }

    }
}
