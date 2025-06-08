package it.hurts.octostudios.octolib.client.animation;

import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.modules.config.cfgbuilder.ObjectEntry;
import it.hurts.octostudios.octolib.util.AnimationUtils;

import java.util.function.Consumer;

public class MethodTweener extends Tweener {
    private double duration;
    private double delay;
    private TransitionType transitionType;
    private EaseType easeType;
    private Object initialValue;
    private Object deltaValue;
    private Object finalValue;
    private Consumer<Object> method;

    @Override
    public boolean step(double dt) {
        if (finished) {
            return false;
        }

        if (Tween.isObjectInvalid(method)) {
            finish();
            return false;
        }

        elapsedTime += dt;

        if (elapsedTime < delay) {
            return true;
        }

        Object currentValue;
        double time = Math.min(elapsedTime - delay, duration);
        if (time < duration) {
            currentValue = tween.interpolateVariable(initialValue, deltaValue, time, duration, transitionType, easeType);
        } else {
            currentValue = finalValue;
        }

        method.accept(currentValue);

        if (time < duration) {
            return true;
        }

        finish();
        return false;
    }

    public MethodTweener(Consumer<Object> method, Object from, Object to, double duration) {
        this.method = method;
        this.initialValue = from;
        this.deltaValue = AnimationUtils.subtract(to, from);
        this.finalValue = to;
        this.duration = duration;
    }

    public MethodTweener setTransitionType(TransitionType transitionType) {
        this.transitionType = transitionType;
        return this;
    }

    public MethodTweener setEaseType(EaseType easeType) {
        this.easeType = easeType;
        return this;
    }

    public MethodTweener setDelay(double delay) {
        this.delay = delay;
        return this;
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
}
