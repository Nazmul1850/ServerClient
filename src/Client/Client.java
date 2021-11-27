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
                if(clientHandler.getConnection()){
                    if(cmd.equals("upload")) {
                        System.out.println("1.for Request \n 2.only Upload?");
                        String choice = sc.nextLine();
                        if(choice.equals("1")){
                            clientHandler.write("UR");
                            System.out.println(clientHandler.read());
                            String requestID = sc.nextLine();
                            clientHandler.write(requestID);
                            String valid = clientHandler.read();
                            if(valid.equals("Invalid ID")){
                                System.out.println("Requested Id Not Found");
                            }else{
                                clientHandler.write(cmd);
                                clientHandler.upload(true);
                            }
                        }else if(choice.equals("2")) {
                            clientHandler.write(cmd);
                            clientHandler.upload(false);
                        }else{
                            System.out.println("Wrong Command");
                        }
                    }else if (cmd.equals("exit")){
                        clientHandler.write(cmd);
                        String replyFromServer = clientHandler.read();
                        System.out.println(replyFromServer);
                        System.out.println("Please Connect Again!");
                        if(replyFromServer.equals("You Are Logged Out")) {
                            clientHandler.setConnection(false);
                            clientHandler.handleId();
                        }
                    }else if (cmd.equals("download")){
                        clientHandler.write(cmd);
                        clientHandler.download();
                    }else if (cmd.equals("request")){
                        clientHandler.write(cmd);
                        clientHandler.request();
                    }else if(cmd.equals("show")){
                        clientHandler.write(cmd);
                        clientHandler.show();
                    }else if(cmd.equals("look for")){
                        clientHandler.write(cmd);
                        System.out.println(clientHandler.read());
                        String studentID = sc.nextLine();
                        clientHandler.write(studentID);
                        System.out.println(clientHandler.read());
                    }
                    else{
                        clientHandler.write(cmd);
                        String replyFromServer = clientHandler.read();
                        System.out.println(replyFromServer);
                    }
                }else{
                    System.out.println("You Are Not Connected Right now");
                }
            }
        }

    }
}
