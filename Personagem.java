abstract class Personagem {
    protected String nome;
    protected int hp;
    protected int mp;
    protected int alcance;
    protected int movimento;
    protected int ataque;
    protected int posX;
    protected int posY;

    public Personagem(String nome, int hp, int mp, int alcance, int movimento, int ataque) {
        this.nome = nome;
        this.hp = hp;
        this.mp = mp;
        this.alcance = alcance;
        this.movimento = movimento;
        this.ataque = ataque;
    }

    public void mover(int novoX, int novoY) {
        this.posX = novoX;
        this.posY = novoY;
    }

    public void atacar(Personagem alvo) {
        int dano = this.ataque;
        alvo.receberDano(dano);
        System.out.println(this.nome + " atacou " + alvo.nome + " causando " + dano + " de dano!");
    }

    public void receberDano(int dano) {
        this.hp -= dano;
        if (this.hp < 0) this.hp = 0;
        System.out.println(this.nome + " recebeu " + dano + " de dano. HP restante: " + this.hp);
    }

    public void curar() {
        if (this.mp >= 10) {
            this.mp -= 10;
            int cura = 30;
            this.hp += cura;
            System.out.println(this.nome + " se curou em " + cura + " HP. HP atual: " + this.hp);
        } else {
            System.out.println("MP insuficiente para realizar a cura.");
        }
    }

    @Override
    public String toString() {
        return String.format("%s (HP: %d, MP: %d, Alcance: %d, Movimento: %d, Ataque: %d)",
                getClass().getSimpleName(), hp, mp, alcance, movimento, ataque);
    }


    // Getters
    public String getNome() { return nome; }
    public int getHp() { return hp; }
    public int getMp() { return mp; }
    public int getAlcance() { return alcance; }
    public int getMovimento() { return movimento; }
    public int getAtaque() { return ataque; }
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
}
