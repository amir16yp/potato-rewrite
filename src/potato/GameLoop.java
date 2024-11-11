package potato;

public class GameLoop implements Runnable {
    public static final float EXPECTED_FPS = 60.0f;
    private static final float NANOS_PER_SECOND = 1_000_000_000.0f;
    private static final float MILLIS_PER_SECOND = 1000.0f;

    private final Game game;
    private boolean running = false;
    private boolean paused = false;
    private long lastFrameTime;
    private float deltaTime; // In seconds
    private float deltaTimeMillis; // In milliseconds
    private int frameCount = 0;
    private float fpsTimer = 0;
    private long fps = 0;
    private final Thread thread = new Thread(this);

    public GameLoop(Game game) {
        this.game = game;
    }

    public int getTargetFPS() {
        return 0;
    }

    public float adjustSpeedForFrameRate(float speed) {
        return speed * (deltaTime * EXPECTED_FPS);
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public float getDeltaTimeMillis() {
        return deltaTimeMillis;
    }

    public long getFPS() {
        return fps;
    }

    public void start() {
        if (!running) {
            running = true;
            lastFrameTime = System.nanoTime();
            if (!thread.isAlive()) {
                thread.start();
            }
        }
    }

    public void stop() {
        running = false;
    }

    public void togglePause() {
        paused = !paused;
        if (!paused) {
            lastFrameTime = System.nanoTime(); // Reset lastFrameTime when unpausing
        }
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void run() {
        while (running) {
            if (!paused) {
                long currentTime = System.nanoTime();
                deltaTime = (currentTime - lastFrameTime) / NANOS_PER_SECOND;
                deltaTimeMillis = deltaTime * MILLIS_PER_SECOND;
                lastFrameTime = currentTime;

                game.update();
                game.render();
                updateFPS();

                int targetFPS = getTargetFPS();
                if (targetFPS > 0) {
                    long targetFrameTime = (long) (NANOS_PER_SECOND / targetFPS);
                    long frameTime = System.nanoTime() - currentTime;
                    if (frameTime < targetFrameTime) {
                        try {
                            Thread.sleep((targetFrameTime - frameTime) / 1_000_000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            } else {
                // When paused, sleep for a short time to avoid busy-waiting
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void updateFPS() {
        frameCount++;
        fpsTimer += deltaTime;
        if (fpsTimer >= 1.0f) {
            fps = frameCount;
            frameCount = 0;
            fpsTimer -= 1.0f;
        }
    }
}