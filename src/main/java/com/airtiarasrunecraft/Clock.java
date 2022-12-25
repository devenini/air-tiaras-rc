package com.airtiarasrunecraft;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
abstract class Clock
{
    protected String name;

    // last updated time (recorded as seconds since epoch)
    protected long lastUpdate;

    // whether the clock is currently running
    protected boolean active;

    Clock(String name)
    {
        this.name = name;
        this.lastUpdate = Instant.now().getEpochSecond();
        this.active = false;
    }

    abstract long getDisplayTime();

    abstract void setDuration(long duration);

    abstract boolean start();

    abstract boolean pause();

    String secondsToString(int pTime) {
        return String.format("%02d:%02d", pTime / 60, pTime % 60);
    }

    abstract void reset();
}
