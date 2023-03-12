package it.silma.simply;

import javax.swing.ImageIcon;

import org.junit.Test;

public class TestSimply {

    @Test
    public void test() {
        new ImageIcon(this.getClass().getResource("/logo.png"));
    }

}
