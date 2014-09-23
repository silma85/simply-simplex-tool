package it.silma.simply.test;

import javax.swing.ImageIcon;

import org.junit.Test;

public class TestSimply {

    @Test
    public void test() {
        new ImageIcon(this.getClass().getResource("../res/logo.png"));
    }

}
