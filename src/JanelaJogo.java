import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

// ela que monta toda a parte visual do jogo
public class JanelaJogo {

    // aqui a gente declara todos os componentes da tela
    private JFrame frame; // a janela principal
    private PainelTabuleiro painelTabuleiro; // o painel onde o tabuleiro é desenhado
    private JLabel labelStatus; // o texto que fica lá embaixo mostrando vez de quem
    
    // os componentes do placar
    private JPanel painelPlacar;
    private JLabel labelPlacarX;
    private JLabel labelPlacarO;

    // a referência pro nosso 'cérebro' do jogo, o controlador (a classe Lig4)
    private Lig4 jogo;

    public JanelaJogo() {
        // primeira coisa, cria o controlador e passa essa própria janela pra ele poder se comunicar de volta
        jogo = new Lig4(this);

        // configura as coisas basicas da janela: titulo, o que fazer quando fecha, tamanho etc
        frame = new JFrame("Jogo Lig-4 em Rede");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(700, 680);
        // define o layout como 'borderlayout', pra gente poder colocar coisa no norte, sul, centro, etc
        frame.setLayout(new BorderLayout());

        // ---- montando o placar (parte de cima) ----
        painelPlacar = new JPanel(new GridLayout(1, 2)); // um painel com grid pra dividir o placar em 2
        
        labelPlacarX = new JLabel("Jogador X: 0", SwingConstants.CENTER);
        labelPlacarX.setFont(new Font("Arial", Font.BOLD, 16));
        
        labelPlacarO = new JLabel("Jogador O: 0", SwingConstants.CENTER);
        labelPlacarO.setFont(new Font("Arial", Font.BOLD, 16));
        
        painelPlacar.add(labelPlacarX);
        painelPlacar.add(labelPlacarO);
        
        frame.add(painelPlacar, BorderLayout.NORTH); // adiciona o painel do placar na parte NORTE (em cima) da janela

        // ---- montando o tabuleiro (meio) ----
        // cria o nosso painel customizado do tabuleiro e coloca ele no CENTRO da janela
        painelTabuleiro = new PainelTabuleiro(jogo.getTabuleiro(), jogo);
        frame.add(painelTabuleiro, BorderLayout.CENTER);

        // ---- montando o status (embaixo) ----
        // cria a label de status e coloca ela no SUL (embaixo) da janela
        labelStatus = new JLabel("Conectando ao servidor...", SwingConstants.CENTER);
        labelStatus.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(labelStatus, BorderLayout.SOUTH);

        // finaliza a configuração da janela, centraliza na tela e deixa ela visivel
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        // depois de tudo montado, manda o controlador começar a se conectar e etc
        jogo.iniciarJogo();
    }
    
    // método pro controlador poder mudar o texto de status
    public void setStatus(String texto) {
        labelStatus.setText(texto);
    }
    
    // método pro controlador poder pedir pra gente redesenhar o tabuleiro
    public void redesenhar() {
        painelTabuleiro.repaint();
    }
    
    // método pro controlador avisar a gente pra atualizar os pontos do placar
    public void atualizarPlacar() {
        if (jogo != null && jogo.getPlacar() != null) {
            labelPlacarX.setText("Jogador X: " + jogo.getPlacar().getPontos('X'));
            labelPlacarO.setText("Jogador O: " + jogo.getPlacar().getPontos('O'));
        }
    }
}