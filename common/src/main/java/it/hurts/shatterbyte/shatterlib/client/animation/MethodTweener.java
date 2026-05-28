package it.hurts.shatterbyte.shatterlib.client.animation;

import it.hurts.shatterbyte.shatterlib.client.animation.easing.EaseType;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.TransitionType;
import it.hurts.shatterbyte.shatterlib.util.AnimationUtils;

import java.util.function.Consumer;

public class MethodTweener<T> extends Tweener {
    private double duration;
    private double delay;
    private TransitionType transitionType;
    private EaseType easeType;
    private T initialValue;
    private T deltaValue;
    private T finalValue;
    private Consumer<T> method;

    @Override
    public boolean step() {
        if (finished) {
            return false;
        }

        if (Tween.isObjectInvalid(method)) {
            finish();
            return false;
        }

        if (getElapsedTime() < delay) {
            return true;
        }

        T currentValue;
        double time = Math.min(getElapsedTime() - delay, duration);
        if (time < duration) {
            currentValue = tween.interpolateVariable(initialValue, deltaValue, time, duration, transitionType, easeType);
        } else {
            currentValue = finalValue;
        }

        TweenSystem.RenderThreadExecutor.runOnRenderThread(() -> method.accept(currentValue));

        if (time < duration) {
            return true;
        }

        finish();
        return false;
    }

    public MethodTweener(Consumer<T> method, T from, T to, double duration) {
        this.method = method;
        this.initialValue = from;
        this.deltaValue = AnimationUtils.subtract(to, from);
        this.finalValue = to;
        this.duration = duration;
    }

    public MethodTweener<T> setTransitionType(TransitionType transitionType) {
        this.transitionType = transitionType;
        return this;
    }

    public MethodTweener<T> setEaseType(EaseType easeType) {
        this.easeType = easeType;
        return this;
    }

    public MethodTweener<T> setDelay(double delay) {
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
