public class GameStarter {
    public static void main(String[] args) {
        GameFrame gf = new GameFrame(720, 540, "Biting on Fish");
        gf.setUpGUI();
        gf.addKeyBindings();
    }
}
