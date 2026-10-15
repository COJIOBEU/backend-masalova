package fight;

public class Main {
    public static void main(String[] args) {
        System.out.println("Добро пожаловать!");

        Squad squad1 = new Squad("Пупа");
        squad1.addMember(new Warrior("Петр"));
        squad1.addMember(new Mage("Ягъ"));
        squad1.addMember(new Barbarian("Ceo"));

        Squad squad2 = new Squad("Победиторы");
        squad2.addMember(new Warrior("Майкл"));
        squad2.addMember(new Mage("Йен"));
        squad2.addMember(new Barbarian("Rfqlfy"));

        Battle battle = new Battle(squad1, squad2, 20);

        battle.fight();
    }
}
