import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image {
    int width;
    int height;
    int[] pixels;

    public Image(int w, int h) {
        this.width = w;
        this.height = h;
        this.pixels = new int[width * height];
    }

    public Image(String path) {
        if (path == null)
            return;

        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert image != null;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pixels = image.getRGB(0, 0, width, height, null, 0, width);
    }

    public void saveImage(String path, String name) {
        File outputFile = new File(path + "/" + name + ".png");
        if (outputFile.exists())
            outputFile.delete();
        try {
            ImageIO.write(this.getImage(), "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }

    public final int getPixel(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return 0xffff00ff;
        return pixels[x + y * width];
    }

    public final void setPixel(int x, int y, int color) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;
        pixels[x + y * width] = color;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }
}
