package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerClass {




    public static void main(String[] args) throws IOException {
         ArrayList<Student> students = new ArrayList<>();
        Integer MAX_BUFFER_SIZE = 1024 * 1024;
        Integer currentBuffer = 0;
        ServerSocket welcomeSocket = new ServerSocket(6666);
        while(true){
            System.out.println("Waiting for connection");
            Socket socket = welcomeSocket.accept();
            System.out.println("Connection Established");
            System.out.println(MAX_BUFFER_SIZE);
            Thread student = new StudentThread(socket,students,MAX_BUFFER_SIZE,currentBuffer);
            student.start();
        }
    }
}
