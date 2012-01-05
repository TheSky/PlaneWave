import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

public class WaveEffect extends JFrame {

    class DrawCanvas extends JLabel implements Runnable {

        private Thread thread = null;

        @Override
        protected void paintComponent(Graphics g) {
            g.drawImage(image_to_draw, 0, 0, width, height, null);
        }

        public final void start() {
            if (thread == null) {
                thread = new java.lang.Thread(this);
                thread.start();
            }
        }

        public void run() {
            while (thread != null) {
                repaint();
                try {
                    synchronized (this) {
                        Thread.sleep(20);
                        wave.randomize_params();
                        wave.propagate();
                        image_to_draw = deepCopy(image_original);
                        wave.stretch(image_original, image_to_draw);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private final String img = "img/sky-1.jpg";

    private BufferedImage image_original;
    private BufferedImage image_to_draw;

    private final int width;
    private final int height;

    private final Wave wave;

    public WaveEffect() {
        setTitle("Wave Effect by TheSky");
        try {
            image_original = ImageIO.read(getClass().getResourceAsStream(img));
            image_to_draw = deepCopy(image_original);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        width = image_original.getWidth(this);
        height = image_original.getHeight(this);
        setSize(width, height);
        setMaximizedBounds(new Rectangle(width, height));

        DrawCanvas dc = new DrawCanvas();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(dc);

        wave = new Wave(height, width);
        dc.start();
    }

    private BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static void main(String[] args) {
        new WaveEffect().setVisible(true);
    }
}
