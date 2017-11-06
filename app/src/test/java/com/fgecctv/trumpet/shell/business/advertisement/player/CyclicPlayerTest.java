package com.fgecctv.trumpet.shell.business.advertisement.player;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class CyclicPlayerTest {

    @Test
    public void add() throws Exception {

    }

    @Test
    public void remove() throws Exception {
        CyclicPlayer.View mockView = Mockito.mock(CyclicPlayer.View.class);
        CyclicPlayer player = new CyclicPlayer(mockView);

        for (int i = 0; i < 5; i++) {
            Playable mockPlayable = Mockito.mock(Playable.class);
            Mockito.when(mockPlayable.getId()).thenReturn(String.valueOf(i));
            player.add(mockPlayable);
        }

        player.remove("3");

    }
}