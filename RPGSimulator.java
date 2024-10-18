import java.util.Scanner;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class RPGSimulator {
    private static final int TAMANHO_TABULEIRO = 30;
    private Personagem jogador;
    private Personagem inimigo;
    private char[][] tabuleiro;
    private Random random;
    private Scanner scanner;
    private List<Class<? extends Personagem>> classesDisponiveis;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";

    public RPGSimulator() {
        tabuleiro = new char[TAMANHO_TABULEIRO][TAMANHO_TABULEIRO];
        random = new Random();
        scanner = new Scanner(System.in);
        classesDisponiveis = new ArrayList<>();
        classesDisponiveis.add(Monge.class);
        classesDisponiveis.add(Guerreiro.class);
        classesDisponiveis.add(Arqueiro.class);
        classesDisponiveis.add(Mago.class);
        inicializarTabuleiro();
    }

    private void inicializarTabuleiro() {
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                tabuleiro[i][j] = '.';
            }
        }
    }

    public void iniciarJogo() {
        escolherPersonagemJogador();
        criarInimigo();
        posicionarPersonagens();
        imprimirTabuleiro();

        while (jogador.getHp() > 0 && inimigo.getHp() > 0) {
            executarTurnoJogador();
            if (inimigo.getHp() > 0) {
                executarTurnoInimigo();
            }
            imprimirTabuleiro();
        }

        System.out.println(jogador.getHp() > 0 ? "Você venceu!" : "Você perdeu!");
    }

    private void escolherPersonagemJogador() {
        System.out.println("Escolha seu personagem:");
        for (int i = 0; i < classesDisponiveis.size(); i++) {
            try {
                Personagem exemplo = classesDisponiveis.get(i).getDeclaredConstructor(String.class).newInstance("Exemplo");
                System.out.println((i + 1) + ". " + exemplo);
            } catch (Exception e) {
                System.out.println("Erro ao criar exemplo de personagem: " + e.getMessage());
            }
        }
        int escolha = scanner.nextInt();
        if (escolha < 1 || escolha > classesDisponiveis.size()) {
            System.out.println("Escolha inválida. Selecionando Guerreiro por padrão.");
            jogador = new Guerreiro("Jogador");
        } else {
            try {
                jogador = classesDisponiveis.get(escolha - 1).getDeclaredConstructor(String.class).newInstance("Jogador");
            } catch (Exception e) {
                System.out.println("Erro ao criar personagem. Selecionando Guerreiro por padrão.");
                jogador = new Guerreiro("Jogador");
            }
        }
        System.out.println("Você escolheu: " + jogador);
    }

    private void criarInimigo() {
        int escolha = random.nextInt(classesDisponiveis.size());
        try {
            inimigo = classesDisponiveis.get(escolha).getDeclaredConstructor(String.class).newInstance("Inimigo");
        } catch (Exception e) {
            System.out.println("Erro ao criar inimigo. Selecionando Guerreiro por padrão.");
            inimigo = new Guerreiro("Inimigo");
        }
        System.out.println("O inimigo é um " + inimigo);
    }


    private void posicionarPersonagens() {
        jogador.posX = random.nextInt(TAMANHO_TABULEIRO);
        jogador.posY = random.nextInt(TAMANHO_TABULEIRO);
        do {
            inimigo.posX = random.nextInt(TAMANHO_TABULEIRO);
            inimigo.posY = random.nextInt(TAMANHO_TABULEIRO);
        } while (inimigo.posX == jogador.posX && inimigo.posY == jogador.posY);

        tabuleiro[jogador.posX][jogador.posY] = 'J';
        tabuleiro[inimigo.posX][inimigo.posY] = 'I';
    }


    private void executarTurnoJogador() {
        System.out.println("\n" + ANSI_GREEN + "Seu turno. Escolha uma ação:" + ANSI_RESET);
        System.out.println("1. Andar (" + jogador.getMovimento() + " casas)");
        System.out.println("2. Atacar (Dano: " + jogador.getAtaque() + ")");
        System.out.println("3. Curar (Custo: 10 MP, Cura: 30 HP)");
        System.out.println("4. Passar a vez");
        int acao = scanner.nextInt();

        switch (acao) {
            case 1:
                if (estaNoAlcance(jogador, inimigo)) {
                    System.out.println(ANSI_YELLOW + "O adversário já está no alcance. Você não pode se mover." + ANSI_RESET);
                } else {
                    moverPersonagem(jogador, inimigo);
                }
                break;
            case 2:
                if (estaNoAlcance(jogador, inimigo)) {
                    jogador.atacar(inimigo);
                } else {
                    System.out.println(ANSI_YELLOW + "Inimigo fora de alcance. Você não pode atacar." + ANSI_RESET);
                }
                break;
            case 3:
                jogador.curar();
                break;
            case 4:
                System.out.println(ANSI_PURPLE + "Você passou a vez." + ANSI_RESET);
                break;
            default:
                System.out.println(ANSI_YELLOW + "Ação inválida. Passando a vez." + ANSI_RESET);
        }
    }


    private void executarTurnoInimigo() {
        System.out.println("\n" + ANSI_RED + "Turno do inimigo." + ANSI_RESET);
        if (estaNoAlcance(inimigo, jogador)) {
            inimigo.atacar(jogador);
        } else {
            moverPersonagem(inimigo, jogador);
            System.out.println(ANSI_RED + "Inimigo se moveu para (" + inimigo.posX + ", " + inimigo.posY + ")" + ANSI_RESET);
        }
    }

    private void moverPersonagem(Personagem ativo, Personagem passivo) {
        int movimentosRestantes = ativo.getMovimento();
        while (movimentosRestantes > 0 && !estaNoAlcance(ativo, passivo)) {
            int dx = Integer.compare(passivo.posX, ativo.posX);
            int dy = Integer.compare(passivo.posY, ativo.posY);

            int novoX = Math.min(Math.max(ativo.posX + dx, 0), TAMANHO_TABULEIRO - 1);
            int novoY = Math.min(Math.max(ativo.posY + dy, 0), TAMANHO_TABULEIRO - 1);

            tabuleiro[ativo.posX][ativo.posY] = '.';
            ativo.mover(novoX, novoY);
            tabuleiro[ativo.posX][ativo.posY] = (ativo == jogador) ? 'J' : 'I';

            movimentosRestantes--;
        }
        System.out.println(ativo.getNome() + " se moveu para (" + ativo.posX + ", " + ativo.posY + ")");
    }

    private boolean estaNoAlcance(Personagem personagem, int x, int y) {
        int distancia = Math.max(Math.abs(personagem.posX - x), Math.abs(personagem.posY - y));
        return distancia <= personagem.getAlcance();
    }

    private boolean estaNoAlcance(Personagem atacante, Personagem alvo) {
        return estaNoAlcance(atacante, alvo.posX, alvo.posY);
    }

    private void imprimirTabuleiro() {
        System.out.println("\nEstado atual do tabuleiro:");
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                if (i == jogador.posX && j == jogador.posY) {
                    System.out.print(ANSI_BLUE + "J " + ANSI_RESET);
                } else if (i == inimigo.posX && j == inimigo.posY) {
                    System.out.print(ANSI_RED + "I " + ANSI_RESET);
                } else if (estaNoAlcance(jogador, i, j)) {
                    System.out.print(ANSI_CYAN + "* " + ANSI_RESET);
                } else if (estaNoAlcance(inimigo, i, j)) {
                    System.out.print(ANSI_YELLOW + "# " + ANSI_RESET);
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println(ANSI_BLUE + "J" + ANSI_RESET + " - Jogador, " +
                ANSI_RED + "I" + ANSI_RESET + " - Inimigo, " +
                ANSI_CYAN + "*" + ANSI_RESET + " - Alcance do Jogador, " +
                ANSI_YELLOW + "#" + ANSI_RESET + " - Alcance do Inimigo");

        System.out.println("\nJogador: " + ANSI_GREEN + jogador + ANSI_RESET +
                ", Posição: (" + jogador.posX + ", " + jogador.posY + ")");
        System.out.println("Inimigo: " + ANSI_RED + inimigo + ANSI_RESET +
                ", Posição: (" + inimigo.posX + ", " + inimigo.posY + ")");
    }

    public static void main(String[] args) {
        RPGSimulator jogo = new RPGSimulator();
        jogo.iniciarJogo();
    }
}