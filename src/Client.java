import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            // 192.168.201.117
            Socket socket = new Socket("localhost", 8888);

            DataOutputStream dataOutputStream =
                    new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream =
                    new DataInputStream(socket.getInputStream());

            String hello = dataInputStream.readUTF();
            if(hello.equals("Hello!"))
            {
                dataOutputStream.writeUTF(hello);
                dataOutputStream.flush();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (socket.isConnected())
                    {
                        try {
                            System.out.println(dataInputStream.readUTF());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            while (socket.isConnected())
            {
                Scanner scanner = new Scanner(System.in);
                String message = scanner.nextLine();

                dataOutputStream.writeUTF(message);
                dataOutputStream.flush();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
