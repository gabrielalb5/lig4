public class ClienteLig4 {
    public static void main(String[] args) throws Exception {
        
        // o 'invokeLater' é o jeito certo de iniciar a interface do swing
        // ele garante que a janela seja criada na thread de eventos (EDT), evitando bugs
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // cria a nossa janela principal, que cuida de montar e iniciar o resto
                new JanelaJogo();
            }
        });
    }
}