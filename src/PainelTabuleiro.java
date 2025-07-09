import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

// essa classe é o nosso painel, a "tela" onde o tabuleiro é desenhado
// ela herda de JPanel pra gente poder ter uma área de desenho só nossa
public class PainelTabuleiro extends JPanel {

    // referencias pro tabuleiro e pro jogopra gente saber o que desenhar e pra quem avisar do clique
    private Tabuleiro tabuleiro;
    private Lig4 jogo;
    
    // as imagens das peças que a gente vai desenhar
    private Image imagemX;
    private Image imagemO;

    // construtor: recebe os objetos principais, carrega as imagens e prepara o listener de mouse
    public PainelTabuleiro(Tabuleiro tabuleiro, Lig4 jogo) {
        this.tabuleiro = tabuleiro;
        this.jogo = jogo;

        carregarImagens();

        // esse listener "ouve" os cliques do mouse no painel
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // quando rola um clique, a gente calcula em qual coluna foi
                int larguraColuna = getWidth() / Tabuleiro.COLUNAS;
                int colunaClicada = e.getX() / larguraColuna;
                // e avisa o controlador pra ele tentar fazer a jogada
                jogo.tentarJogada(colunaClicada + 1);
            }
        });
    }
    
    // método que carrega as imagens 'peca_x.png' e 'peca_o.png' de dentro da pasta 'src'
    private void carregarImagens() {
        try {
            // usa o getResourceAsStream pra funcionar em qualquer IDE e no .jar final (precaucao pelo msm problema do config.xml q conversamos)
            imagemX = ImageIO.read(getClass().getResourceAsStream("peca_x.png"));
            imagemO = ImageIO.read(getClass().getResourceAsStream("peca_o.png"));
            System.out.println("Imagens das peças carregadas com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao carregar as imagens das peças! Verifique se elas estão na pasta 'src'.");
        }
    }

    // aqui onde de fato se desenha
    // o swing chama ele toda vez que a tela precisa ser redesenhada (ex: quando a gente chama repaint())
    @Override
    protected void paintComponent(Graphics g) {
        // importante chamar o super.paintComponent pra limpar a tela antiga antes de desenhar a nova
        super.paintComponent(g);
        // so um antialiasing pras borda fica suave 
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // primeiro, desenha o fundo azul do tabuleiro
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // aqui a gente calcula o tamanho de cada peça pra que o tabuleiro sempre ocupe o painel todo
        int diametroPeca = Math.min(getWidth() / Tabuleiro.COLUNAS, getHeight() / Tabuleiro.LINHAS);
        int margem = (int) (diametroPeca * 0.1); // uma margemzinha pra peça nao ficar colada na borda
        int diametroReal = diametroPeca - (margem * 2);

        // loop pra passar por cada "buraco" do tabuleiro
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int col = 0; col < Tabuleiro.COLUNAS; col++) {
                // calcula a posição x e y de cada buraco na tela
                int x = col * diametroPeca + margem;
                int y = linha * diametroPeca + margem;
                
                //  desenha sempre o buraco cinza, como se estivesse vazio
                g.setColor(Color.LIGHT_GRAY);
                g.fillOval(x, y, diametroReal, diametroReal);

                // verifica se tem alguma peça ('X' ou 'O') naquela posição
                char peca = tabuleiro.getPeca(linha, col);
                // se tiver, desenha a imagem da peça por cima do buraco cinza
                if (peca == 'X' && imagemX != null) {
                    g.drawImage(imagemX, x, y, diametroReal, diametroReal, null);
                } else if (peca == 'O' && imagemO != null) {
                    g.drawImage(imagemO, x, y, diametroReal, diametroReal, null);
                }
            }
        }
    }
}