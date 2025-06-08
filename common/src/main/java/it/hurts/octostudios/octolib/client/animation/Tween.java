package it.hurts.octostudios.octolib.client.animation;

import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.util.AnimationUtils;
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

    @Getter
    double totalTime = 0;
    int currentStep = -1;
    @Getter
    int loops = 1;
    int loopsDone = 0;

    float speedScale = 1;
    @Getter
    boolean ignoreTimeScale = false;

    @Getter
    boolean started = false;
    @Getter
    boolean running = true;
    boolean dead = false;
    @Getter
    boolean valid = false;
    boolean defaultParallel = false;
    boolean parallelEnabled = false;

    private void startTweeners() {
        if (tweeners.isEmpty()) {
            dead = true;
            OctoLib.LOGGER.warn("Tween without commands, aborting.");
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
            totalTime = 0;
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
        if (!valid) OctoLib.LOGGER.warn("Tween invalid. Probably already finished.");
        if (dead) OctoLib.LOGGER.warn("Can't play finished Tween, use stop() first to reset its state.");
        this.running = true;
    }

    public void kill() {
        running = false;
        valid = false;
        dead = true;
    }

    public void clear() {
        valid = false;
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

    protected boolean customStep(double dt) {
        boolean runningBefore = running;
        running = true;
        boolean stepResult = this.step(dt);
        running = running && runningBefore;
        return stepResult;
    }

    protected boolean step(double dt) {
        if (dead) {
            return false;
        }

        if (!running) {
            return true;
        }

        if (!started) {
            if (tweeners.isEmpty()) {
                OctoLib.LOGGER.warn("Tween started with no tweeners :(");
                return false;
            }

            currentStep = 0;
            loopsDone = 0;
            totalTime = 0;
            this.startTweeners();
            started = true;
        }

        double adjustedDelta = dt * speedScale;
        boolean stepActive = false;
        totalTime += adjustedDelta;

        while (adjustedDelta > 0 && running) {
            double stepDelta = adjustedDelta;

            for (Tweener tweener : tweeners.get(currentStep)) {
                stepActive = tweener.step(adjustedDelta) || stepActive;
                stepDelta = Math.min(adjustedDelta, stepDelta);
            }

            adjustedDelta = stepDelta;

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
        Tween tween = new Tween();
        tween.valid = true;
        TweenSystem.addTween(tween);
        return tween;
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
