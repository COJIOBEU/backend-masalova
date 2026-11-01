package fight;

public class Barbarian extends Character{
    
    public Barbarian(String name){
        super(name, 150, 16, 6,4);
    }

    @Override 
    public void act(Character target){
        if (abilityCooldown > 0){
            super.act(target);
        } else{
            int totalDamage = 2 * attack;
            System.out.println(name + " наносит " + target.getName() + " двойной урон. Суммарный урон: " + totalDamage);
            target.takeDamage(totalDamage);
            abilityCooldown = 4;
        }
    }
}
