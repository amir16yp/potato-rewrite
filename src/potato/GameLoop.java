package potato;

public class GameLoop implements Runnable {
    public static final float EXPECTED_FPS = 60.0f;
    private static final float NANOS_PER_SECOND = 1_000_000_000.0f;
    private static final float MILLIS_PER_SECOND = 1000.0f;
    private static final int FPS_SAMPLE_SIZE = 60; // Rolling average window size

    private boolean running = false;
    private boolean paused = false;
    private long lastFrameTime;
    private float deltaTime; // In seconds
    private float deltaTimeMillis; // In milliseconds

    // Improved FPS tracking
    private final long[] frameTimes = new long[FPS_SAMPLE_SIZE];
    private int frameTimeIndex = 0;
    private int frameCount = 0;
    private long fps = 0;

    private int targetFPS;

    private final Thread thread = new Thread(this);
    private long lastFPSUpdateTime = 0;

    public GameLoop() {
        // Initialize frame times array
        for (int i = 0; i < FPS_SAMPLE_SIZE; i++) {
            frameTimes[i] = 0;
        }
    }


    public int getTargetFPS() {
        return targetFPS;
    }

    public void setTargetFPS(int targetFPS) {
        this.targetFPS = targetFPS;
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
                long frameStartTime = System.nanoTime();

                deltaTime = (frameStartTime - lastFrameTime) / NANOS_PER_SECOND;
                deltaTimeMillis = deltaTime * MILLIS_PER_SECOND;
                lastFrameTime = frameStartTime;

                Game.RENDERER.update();
                Game.GAME.repaint();

                updateFPS(frameStartTime);

                int targetFPS = getTargetFPS();
                if (targetFPS > 0) {
                    long targetFrameTime = (long) (NANOS_PER_SECOND / targetFPS);
                    long frameTime = System.nanoTime() - frameStartTime;
                    if (frameTime < targetFrameTime) {
                        try {
                            Thread.sleep((targetFrameTime - frameTime) / 1_000_000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    private void updateFPS(long currentTime) {
        frameTimes[frameTimeIndex] = currentTime;
        frameTimeIndex = (frameTimeIndex + 1) % FPS_SAMPLE_SIZE;
        frameCount++;

        if (frameCount >= FPS_SAMPLE_SIZE) {
            long oldestFrame = frameTimes[(frameTimeIndex + 1) % FPS_SAMPLE_SIZE];
            if (oldestFrame > 0) {
                double timeElapsed = (currentTime - oldestFrame) / (double) NANOS_PER_SECOND;

                // Update FPS once per second
                if (currentTime - lastFPSUpdateTime >= NANOS_PER_SECOND) {
                    fps = Math.round(FPS_SAMPLE_SIZE / timeElapsed);
                    lastFPSUpdateTime = currentTime;
                }
            }
        }
    }

}