package com.example.gamefinal;

public class ScoreControl {
    private int currentScore = 0;
    private int spriteNum = 0;
    private int diamondNum = 0;

    public int getShootNum() {
        return shootNum;
    }

    public void setShootNum(int shootNum) {
        this.shootNum = shootNum;
    }

    private int shootNum = 0;

    public void addScore(int kind, int value){
        this.currentScore += value;
        this.shootNum ++;
        switch (kind){
            case 2:
                this.diamondNum ++;
                break;
            case 3:
                this.spriteNum ++;
                break;
            default:
                break;
        }
    }

    public int getSettleScore(){
        return this.currentScore + spriteNum*200 + diamondNum*100;
    }

    public int getCurrentScore() {
        return currentScore;
    }

}
