package com.example.gamefinal;

public class TimeControl {
    private int currentTime;
    private boolean finished = false;

    public void decreaseTime(){
        this.currentTime -= 1;
        if(this.currentTime <= 0){
            finished = true;
        }
    }
    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public boolean isFinished() {
        return finished;
    }

}
