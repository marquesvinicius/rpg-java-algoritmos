public class Tabuleiro {
    private final int tamanho = 29;
    private Personagem[][] tabuleiro = new Personagem[tamanho][tamanho];
    
    public void posicionarPersonagem(Personagem p, int x, int y) {
        tabuleiro[x][y] = p;
    }

    public void moverPersonagem(Personagem p, int oldX, int oldY, int newX, int newY) {
        // Limpa a posição antiga
        if (tabuleiro[oldX][oldY] == p) {
            tabuleiro[oldX][oldY] = null;
        }

        // Verifica se a nova posição está ocupada
        if (tabuleiro[newX][newY] == null) {
            // Coloca o personagem na nova posição
            tabuleiro[newX][newY] = p;
        } else {
            System.out.println("A posição [" + newX + ", " + newY + "] já está ocupada!");
        }
    }


    public void imprimirTabuleiro() {
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                if (tabuleiro[i][j] == null) {
                    System.out.print("[ ]");
                } else {
                    System.out.print("[" + tabuleiro[i][j].getClass().getSimpleName().charAt(0) + "]");
                }
            }
            System.out.println();
        }
    }
}
