import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorLig4 {
    public static void main(String[] args) throws Exception {
        try (ServerSocket servidor = new ServerSocket(Config.getPorta(), 2, InetAddress.getByName(Config.getIp()))) {
            
            System.out.println("Servidor Lig4 Inicializado\n");

            Socket jogadorX;
            System.out.println("Esperando por Conexão (Jogador Sol)");
            jogadorX = servidor.accept();
            System.out.println("Conexão Recebida: " + jogadorX.toString() + ":" + jogadorX.getPort() + "\n");

            ObjectOutputStream entradaJogadorX;
            entradaJogadorX = new ObjectOutputStream(jogadorX.getOutputStream());
            entradaJogadorX.flush();
            entradaJogadorX.writeObject("X;true");

            ObjectInputStream saidaJogadorX;
            saidaJogadorX = new ObjectInputStream(jogadorX.getInputStream());

            Socket jogadorO;
            System.out.println("Esperando por Conexão (Jogador Lua)");
            jogadorO = servidor.accept();
            System.out.println("Conexão Recebida: " + jogadorO.toString() + ":" + jogadorO.getPort() + "\n");

            ObjectOutputStream entradaJogadorO;
            entradaJogadorO = new ObjectOutputStream(jogadorO.getOutputStream());
            entradaJogadorO.flush();
            entradaJogadorO.writeObject("O;false");

            ObjectInputStream saidaJogadorO;
            saidaJogadorO = new ObjectInputStream(jogadorO.getInputStream());

            Thread thread1 = new Thread(new GerenciadorDeJogadas(saidaJogadorX, entradaJogadorO));
            Thread thread2 = new Thread(new GerenciadorDeJogadas(saidaJogadorO, entradaJogadorX));

            thread1.start();
            thread2.start();
        }
    }
}