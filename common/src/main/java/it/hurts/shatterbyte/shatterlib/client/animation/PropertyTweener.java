package it.hurts.shatterbyte.shatterlib.client.animation;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.EaseType;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.TransitionType;
import it.hurts.shatterbyte.shatterlib.util.AnimationUtils;
import lombok.SneakyThrows;
import oshi.util.tuples.Pair;

import java.lang.reflect.Field;

public class PropertyTweener<T> extends Tweener {
    private final Object target;
    private final String[] field;
    private T initialValue;
    private T baseFinalValue;
    private T finalValue;
    private T deltaValue;
    private final double duration;
    private TransitionType transitionType;
    private EaseType easeType;
    private Runnable customMethod;
    private double delay = 0;
    boolean doContinue = true;
    boolean doContinueDelayed = false;
    boolean relative = false;

    PropertyTweener(Object target, String field, T to, double duration) {
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
            ShatterLib.LOGGER.warn("Target object is invalid!");
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
    public boolean step() {
        if (finished) {
            return false;
        }

        if (Tween.isObjectInvalid(target)) {
            finish();
            return false;
        }

        if (getElapsedTime() < delay) {
            return true;
        } else if (doContinueDelayed && delay >= 0.001) {
            initialValue = getField(target, field);
            deltaValue = AnimationUtils.subtract(finalValue, initialValue);
            doContinueDelayed = false;
        }

        double time = Math.min(getElapsedTime() - delay, duration);
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

    public PropertyTweener<T> asRelative() {
        this.relative = true;
        return this;
    }

    public PropertyTweener<T> setTransitionType(TransitionType transitionType) {
        this.transitionType = transitionType;
        return this;
    }

    public PropertyTweener<T> setEaseType(EaseType easeType) {
        this.easeType = easeType;
        return this;
    }

    public PropertyTweener<T> setDelay(double delay) {
        this.delay = delay;
        return this;
    }

    public PropertyTweener<T> fromCurrent() {
        this.doContinue = false;
        return this;
    }

    public PropertyTweener<T> from(T value) {
        initialValue = value;
        doContinue = false;
        return this;
    }

    @SneakyThrows
    public T getField(Object object, String[] field) {
        Pair<Object, Field> finalField = getFinalField(object, field);
        return (T) finalField.getB().get(finalField.getA());
    }

    @SneakyThrows
    public void setField(Object object, String[] field, T value) {
        Pair<Object, Field> finalField = getFinalField(object, field);
        finalField.getB().set(finalField.getA(), value);
    }

    private Pair<Object, Field> getFinalField(Object object, String[] field) {
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
