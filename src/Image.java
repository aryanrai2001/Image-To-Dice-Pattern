import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image {
    private int width, height;
    private int[] pixelData;

    // Constructor to create an empty Image of specified width and height.
    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixelData = new int[width * height];
    }

    // Constructor to load Image from the specified image file.
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
        this.pixelData = image.getRGB(0, 0, width, height, null, 0, width);
    }

    // Function to save the image to a file on specified path.
    public void saveToFile(String path, String name) {
        File outputFile = new File(path + "/" + name + ".png");
        if (outputFile.exists())
            outputFile.delete();
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            image.setRGB(0, 0, width, height, pixelData, 0, width);
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function to get the color value of the pixel at image coordinate (x, y).
    public final int getPixel(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return 0xffff00ff;
        return pixelData[x + y * width];
    }

    // Function to set the color value of the pixel at image coordinate (x, y).
    public final void setPixel(int x, int y, int color) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;
        pixelData[x + y * width] = color;
    }

    // Function to get the width of the image.
    public final int getWidth() {
        return width;
    }

    // Function to get the height of the image.
    public final int getHeight() {
        return height;
    }
}
