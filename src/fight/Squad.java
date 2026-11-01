package fight;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Squad{
    private final String name;
    private final List <Character> members;

    public Squad(String name){
        this.name = name;
        this.members = new ArrayList<>();
    }

    public void addMember(Character character){
        members.add(character);
    }

    public Character getRandomAliveCharacter(){
        List<Character> alive = new ArrayList<>();
        for (Character c : members){
            if (c.isAlive()){
                alive.add(c);
            }
        }
        if (alive.isEmpty()){
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(alive.size());
        return alive.get(index);
    }

    public boolean isDefeated(){
        for (Character c : members){
            if (c.isAlive()){
                return false;
            }
        }
        return true;
    }

    public String getName(){
        return name;
    }

    public List<Character> getMembers(){
        return members;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("Отряд " + name + ":\n");
        for (Character c : members){
            sb.append("  - ").append(c).append("\n");
        }
        return sb.toString();
    }
}