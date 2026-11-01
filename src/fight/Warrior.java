package fight;

import java.util.concurrent.ThreadLocalRandom;

public class Warrior extends Character{
    
    public Warrior(String name){
        super(name, 120, 20, 8, 3);
    }

    @Override 
    public void act(Character target){
        if (abilityCooldown > 0){
            super.act(target);
        }else{
            if (ThreadLocalRandom.current().nextDouble() < 0.5){
                int critDamage = attack * 2;
                System.out.println("Крит урон! " + name + " мощно атакует " + target.getName() + " и наносит " + critDamage + " урона!");
                target.takeDamage(critDamage);
            } else{
                super.act(target);
            }
            abilityCooldown = 3;
        }
    }
}
