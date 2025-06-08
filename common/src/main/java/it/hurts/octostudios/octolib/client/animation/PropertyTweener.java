package it.hurts.octostudios.octolib.client.animation;

import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.util.AnimationUtils;
import lombok.SneakyThrows;
import oshi.util.tuples.Pair;

import java.lang.reflect.Field;

public class PropertyTweener extends Tweener {
    private final Object target;
    private final String[] field;
    private Object initialValue;
    private Object baseFinalValue;
    private Object finalValue;
    private Object deltaValue;
    private final double duration;
    private TransitionType transitionType;
    private EaseType easeType;
    private Runnable customMethod;
    private double delay = 0;
    boolean doContinue = true;
    boolean doContinueDelayed = false;
    boolean relative = false;

    PropertyTweener(Object target, String field, Object to, double duration) {
        this.target = target;
        this.field = field.split("\\.");
        this.initialValue = getField(target, this.field);
        this.baseFinalValue = to;
        this.finalValue = baseFinalValue;
        this.duration = duration;
    }

    @Override
    public void start() {
        super.start();

        if (Tween.isObjectInvalid(target)) {
            OctoLib.LOGGER.warn("Target object is invalid!");
            return;
        }

        if (doContinue) {
            if (delay < 0.001) {
                initialValue = getField(target, field);
            } else {
                doContinueDelayed = true;
            }
        }

        if (relative) {
            finalValue = AnimationUtils.add(initialValue, baseFinalValue);
        }

        deltaValue = AnimationUtils.subtract(finalValue, initialValue);
    }

    @Override
    public boolean step(double dt) {
        if (finished) {
            return false;
        }

        if (Tween.isObjectInvalid(target)) {
            finish();
            return false;
        }

        elapsedTime += dt;

        if (elapsedTime < delay) {
            return true;
        } else if (doContinueDelayed && delay >= 0.001) {
            initialValue = getField(target, field);
            deltaValue = AnimationUtils.subtract(finalValue, initialValue);
            doContinueDelayed = false;
        }

        double time = Math.min(elapsedTime - delay, duration);
        if (time < duration) {
//            if (customMethod.isValid())
            setField(target, field, tween.interpolateVariable(initialValue, deltaValue, time, duration, transitionType, easeType));
            return true;
        }

//        if (customMethod.isValid())
        setField(target, field, finalValue);
        finish();
        return false;
    }

    @Override
    public void setTween(Tween tween) {
        super.setTween(tween);
        if (transitionType == null) {
            transitionType = tween.getDefaultTransition();
        }
        if (easeType == null) {
            easeType = tween.getDefaultEase();
        }
    }

    public PropertyTweener asRelative() {
        this.relative = true;
        return this;
    }

    public PropertyTweener setTransitionType(TransitionType transitionType) {
        this.transitionType = transitionType;
        return this;
    }

    public PropertyTweener setEaseType(EaseType easeType) {
        this.easeType = easeType;
        return this;
    }

    public PropertyTweener setDelay(double delay) {
        this.delay = delay;
        return this;
    }

    public PropertyTweener fromCurrent() {
        this.doContinue = false;
        return this;
    }

    public PropertyTweener from(Object value) {
        initialValue = value;
        doContinue = false;
        return this;
    }

    @SneakyThrows
    public static Object getField(Object object, String[] field) {
        Pair<Object, Field> finalField = getFinalField(object, field);
        return finalField.getB().get(finalField.getA());
    }

    @SneakyThrows
    public static void setField(Object object, String[] field, Object value) {
        Pair<Object, Field> finalField = getFinalField(object, field);
        finalField.getB().set(finalField.getA(), value);
    }

    private static Pair<Object, Field> getFinalField(Object object, String[] field) {
        Field currentField = null;
        boolean isStatic = object instanceof Class<?>;

        for (String s : field) {
            try {
                if (currentField != null) {
                    object = currentField.get(object);
                }

                if (isStatic) {
                    currentField = ((Class<?>) object).getDeclaredField(s);
                    currentField.setAccessible(true);
                    continue;
                }
                currentField = object.getClass().getDeclaredField(s);
                currentField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Attempted to tween a nonexistent field: " + s);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return new Pair<>(object, currentField);
    }
}
