package it.hurts.shatterbyte.shatterlib.client.animation;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.EaseType;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.TransitionType;
import it.hurts.shatterbyte.shatterlib.util.AnimationUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Tween {
    private final List<List<Tweener>> tweeners = new ArrayList<>();

    @Getter
    TransitionType defaultTransition = TransitionType.LINEAR;
    @Getter
    EaseType defaultEase = EaseType.EASE_IN_OUT;

    long startTimestampMillis;

    int currentStep = -1;
    @Getter
    int loops = 1;
    int loopsDone = 0;

    float speedScale = 1;
    @Getter
    boolean ignoreTimeScale = false;

    @Getter
    volatile boolean started = false;
    @Getter
    volatile boolean running = true;
    volatile boolean dead = false;
    boolean defaultParallel = false;
    boolean parallelEnabled = false;

    public void start() {
        TweenSystem.addTween(this);
    }

    public double getTotalTime() {
        return (System.currentTimeMillis() - startTimestampMillis) / 1000d;
    }

    private void startTweeners() {
        if (tweeners.isEmpty()) {
            dead = true;
            ShatterLib.LOGGER.warn("Tween without commands, aborting.");
            return;
        }

        for (Tweener tweener : tweeners.get(currentStep)) {
            tweener.start();
        }
    }

    private void stopInternal(boolean reset) {
        running = false;
        if (reset) {
            started = false;
            dead = false;
            startTimestampMillis = System.currentTimeMillis();
        }
    }

    protected void append(Tweener tweener) {
        tweener.setTween(this);

        if (parallelEnabled) {
            currentStep = Math.max(currentStep, 0);
        } else {
            currentStep++;
        }

        parallelEnabled = defaultParallel;

        if (tweeners.size() <= currentStep) {
            tweeners.add(new ArrayList<>());
        }
        tweeners.get(currentStep).add(tweener);
    }

    public void stop() {
        this.stopInternal(true);
    }

    public void pause() {
        this.stopInternal(false);
    }

    public void play() {
        if (dead) ShatterLib.LOGGER.warn("Can't play finished Tween, use stop() first to reset its state.");
        this.running = true;
    }

    public void kill() {
        running = false;
        dead = true;
    }

    public void clear() {
        tweeners.clear();
    }

    public Tween setIgnoreTimeScale(boolean ignore) {
        ignoreTimeScale = ignore;
        return this;
    }

    public Tween setParallel(boolean parallel) {
        defaultParallel = parallel;
        parallelEnabled = parallel;
        return this;
    }

    public Tween setLoops(int loops) {
        this.loops = loops;
        return this;
    }

    public int getLoopsLeft() {
        return loops <= 0 ? -1 : loops - loopsDone;
    }

    public Tween setSpeedScale(float speedScale) {
        this.speedScale = speedScale;
        return this;
    }

    public Tween setTransitionType(TransitionType transitionType) {
        this.defaultTransition = transitionType;
        return this;
    }

    public Tween setEase(EaseType easeType) {
        this.defaultEase = easeType;
        return this;
    }

    public Tween parallel() {
        parallelEnabled = true;
        return this;
    }

    public Tween chain() {
        parallelEnabled = false;
        return this;
    }

    protected boolean step() {
        if (dead) {
            return false;
        }

        if (!running) {
            return true;
        }

        if (!started) {
            if (tweeners.isEmpty()) {
                ShatterLib.LOGGER.warn("Tween started with no tweeners :(");
                return false;
            }

            currentStep = 0;
            loopsDone = 0;
            startTimestampMillis = System.currentTimeMillis();
            this.startTweeners();
            started = true;
        }

        boolean stepActive = false;

        while (speedScale > 0 && running) {
            for (Tweener tweener : tweeners.get(currentStep)) {
                stepActive = tweener.step() || stepActive;
            }

            if (stepActive) {
                return true;
            }

            currentStep++;

            if (currentStep != tweeners.size()) {
                startTweeners();
                break;
            }

            loopsDone++;
            if (loopsDone == loops) {
                running = false;
                dead = true;
                break;
            }

            currentStep = 0;
            startTweeners();
        }

        return true;
    }

    public static Tween create() {
        return new Tween();
    }

    protected double runEquation(TransitionType transitionType, EaseType easeType, double time, double initial, double delta, double duration) {
        if (duration <= 0) {
            return initial + delta;
        }

        return transitionType.runEquation(easeType, time, initial, delta, duration);
    }

    protected <T> T interpolateVariable(T initialValue, T deltaValue, double time, double duration, TransitionType transitionType, EaseType easeType) {
        T added = AnimationUtils.add(initialValue, deltaValue);
        return AnimationUtils.lerp(initialValue, added, runEquation(transitionType, easeType, time, 0.0, 1.0, duration));
    }

    /**
     *
     * @deprecated not actually deprecated, but probably don't use... uses reflection and has its own limitations
     */
    @Deprecated
    public <T> PropertyTweener<T> tweenProperty(Object target, String property, T to, double durationInSeconds) {
        PropertyTweener<T> tweener = new PropertyTweener<>(target, property, to, durationInSeconds);
        this.append(tweener);
        return tweener;
    }
    
    public IntervalTweener tweenInterval(double durationInSeconds) {
        IntervalTweener tweener = new IntervalTweener(durationInSeconds);
        this.append(tweener);
        return tweener;
    }

    public RunnableTweener tweenRunnable(Runnable runnable) {
        RunnableTweener tweener = new RunnableTweener(runnable);
        this.append(tweener);
        return tweener;
    }

    public <T> MethodTweener<T> tweenMethod(Consumer<T> method, T from, T to, double duration) {
        MethodTweener<T> tweener = new MethodTweener<>(method, from, to, duration);
        this.append(tweener);
        return tweener;
    }

    public static boolean isObjectInvalid(Object object) {
        return object == null;
    }
}
