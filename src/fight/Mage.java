package fight;
public class Mage extends Character{
    
    public Mage(String name){
        super(name, 80, 25, 4, 2);
    }

    @Override 
    public void act(Character target){
        if (abilityCooldown > 0){
            super.act(target);
        } else{
            System.out.println("Маг " + name + " кастует заклинание в " + target.getName() + " и наносит " + attack + " урона сквозь броню!");
            target.hp -= attack;
            if (target.hp <= 0){
                target.hp = 0;
                target.isAlive = false;
                System.out.println("!!! " + target.getName() + " погиб !!!");
            }
            abilityCooldown = 2;
        }
    }
}
