package fight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Battle {
    private final Squad squad1;
    private final Squad squad2;
    private final int maxRounds;
    private int currentRound;

    public Battle(Squad squad1, Squad squad2, int maxRounds){
        this.squad1 = squad1;
        this.squad2 = squad2;
        this.maxRounds = maxRounds;
        this.currentRound = 0;
    }

    public void fight(){
        System.out.println("БОЙ НАЧИНАЕТСЯ!");

        System.out.println(squad1);
        System.out.println(squad2);
        
        while (!squad1.isDefeated() && !squad2.isDefeated() && currentRound < maxRounds){
            currentRound++;
            System.out.println("РАУНД " + currentRound);

            List<Character> allAlive = new ArrayList<>();

            for (Character c: squad1.getMembers()){
                if (c.isAlive()) allAlive.add(c);
            }
            for (Character c: squad2.getMembers()){
                if (c.isAlive()) allAlive.add(c);
            }

            Collections.shuffle(allAlive);

            for (Character c : allAlive){
                if (!c.isAlive()) continue;

                Squad enemySquad;

                if(squad1.getMembers().contains(c)){
                    enemySquad = squad2;
                } else {
                    enemySquad = squad1;
                }

                Character target = enemySquad.getRandomAliveCharacter();

                if (target != null){
                    c.act(target);
                }
            }
            System.out.println("\n");

        }
        printResult();
    }

    public void printResult(){
        System.out.println("БОЙ ОКОНЧЕН!");
        if (squad1.isDefeated() && squad2.isDefeated()){
            System.out.println("Ничья. Оба отряда повержены!");
        } else if (currentRound > maxRounds) {
            System.out.println("Ничья. Бой окончен по лимиту раундов");
        } else if (squad1.isDefeated()){
            System.out.println("Победил отряд " + squad2.getName() + "!!!");
        } else{
            System.out.println("Победил отряд " + squad1.getName() + "!!!");
        }

        System.out.println("Финальное состояние отрядов: ");
        System.out.println(squad1);
        System.out.println(squad2);
    }


}
