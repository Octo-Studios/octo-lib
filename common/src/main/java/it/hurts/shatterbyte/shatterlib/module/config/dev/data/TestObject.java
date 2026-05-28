package it.hurts.shatterbyte.shatterlib.module.config.dev.data;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@EqualsAndHashCode
public class TestObject {
    private String bleh = "im a strin bruh";
    private boolean checkbox = false;
    private SomeOtherObject otherObject = new SomeOtherObject();

    public static class SomeOtherObject {
        private ArrayList<Boolean> list = new ArrayList<>();
    }
}
