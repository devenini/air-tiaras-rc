package com.airtiarasrunecraft;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
class Stopwatch extends Clock
{
    // the number of seconds elapsed, as of last updated time
    private long elapsed = 0;

    // a list of lap times (recorded as seconds since epoch)
    private List<Long> laps = new ArrayList<>();

    Stopwatch(String name)
    {
        super(name);
    }

    @Override
    long getDisplayTime()
    {
        if (!active)
        {
            return elapsed;
        }

        return Math.max(0, elapsed + (Instant.now().getEpochSecond() - lastUpdate));
    }

    @Override
    void setDuration(long duration)
    {
        elapsed = duration;
    }

    @Override
    boolean start()
    {
        if (!active)
        {
            lastUpdate = Instant.now().getEpochSecond();
            active = true;
            return true;
        }

        return false;
    }

    @Override
    boolean pause()
    {
        if (active)
        {
            active = false;
            elapsed = Math.max(0, elapsed + (Instant.now().getEpochSecond() - lastUpdate));
            lastUpdate = Instant.now().getEpochSecond();
            return true;
        }

        return false;
    }

    void lap()
    {
        laps.add(getDisplayTime());
        elapsed = 0;
        lastUpdate = Instant.now().getEpochSecond();
    }

    String getFormatedBestLap()
    {
        if(laps == null) {
            return "--";
        }

        return secondsToString(Math.toIntExact(laps.get(laps.indexOf(Collections.min(laps)))));
    }

    @Override
    void reset()
    {
        active = false;
        elapsed = 0;
        laps.clear();
        lastUpdate = Instant.now().getEpochSecond();
    }
}
