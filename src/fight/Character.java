package fight;

public class Character{
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int attack;
    protected int defense;
    protected boolean isAlive;
    protected int abilityCooldown;

    public Character(String name, int hp, int attack, int defense){
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attack = attack;
        this.defense = defense;
        this.isAlive = true;
        this.abilityCooldown = 0;
    }

    public void takeDamage(int damage){
        int realDamage = Math.max(0, damage - defense);
        hp -= realDamage;

        if (hp <= 0){
            hp = 0;
            isAlive = false;
            System.out.println("!!! " + name + " погиб !!!");
        }
    }

    public void heal(int amount){
        hp = Math.min(maxHp, hp + amount);
    }

    public boolean isAlive(){
        return isAlive;
    }

    public String getName(){
        return name;
    }

    public int getHp(){
        return hp;
    }


    @Override 
    public String toString(){
        return name + " [HP: " + hp + "/" + maxHp + "]";
    }

    public void act(Character target) {
        System.out.println(name + " атакует " + target.getName() + " и наносит " + attack + " урона.");
        target.takeDamage(attack);
        abilityCooldown--;
    }
}