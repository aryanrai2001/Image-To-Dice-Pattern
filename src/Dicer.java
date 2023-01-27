import java.nio.file.FileSystems;

public class Dicer {
    private static Image[] diceSprites;
    private static String filePath, fileName, inputImagePath, diceSheetPath;
    private static int sampleRes, diceRes;

    public static void main(String[] args) {
        initialize();
        loadDiceSprites();
        convert();
    }

    private static void initialize() {
        diceRes = 16;
        sampleRes = 10;
        diceSheetPath = "res/Dice/DiceSheet.png";
        inputImagePath = "res/Image/6.png";

        diceSheetPath = FileSystems.getDefault().getPath(diceSheetPath).normalize().toAbsolutePath().toString();
        inputImagePath = FileSystems.getDefault().getPath(inputImagePath).normalize().toAbsolutePath().toString();

        int pathSeparator = inputImagePath.lastIndexOf('/');
        if (pathSeparator == -1)
            pathSeparator = inputImagePath.lastIndexOf('\\');

        filePath = inputImagePath.substring(0, pathSeparator);
        fileName = inputImagePath.substring(pathSeparator + 1, inputImagePath.lastIndexOf('.'));
    }

    private static void loadDiceSprites() {
        Image diceSheet = new Image(diceSheetPath);
        diceSprites = new Image[6];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                diceSprites[i * 2 + j] = new Image(diceRes, diceRes);
                for (int y = 0; y < diceRes; y++) {
                    for (int x = 0; x < diceRes; x++) {
                        int yIndex = diceRes * i + y;
                        int xIndex = diceRes * j + x;
                        diceSprites[i * 2 + j].setPixel(x, y, diceSheet.getPixel(xIndex, yIndex));
                    }
                }
            }
        }
    }

    private static void convert() {
        Image inputImage = new Image(inputImagePath);
        Image outputImage = new Image((inputImage.getWidth() / sampleRes) * diceRes, (inputImage.getHeight() / sampleRes) * diceRes);
        int diceH = inputImage.getHeight() / sampleRes;
        int diceW = inputImage.getWidth() / sampleRes;
        for (int y = 0; y < diceH; y++) {
            for (int x = 0; x < diceW; x++) {
                int yStart = y * sampleRes;
                int yEnd = yStart + sampleRes;
                int xStart = x * sampleRes;
                int xEnd = xStart + sampleRes;
                int intensity = 0;
                for (int yPos = yStart; yPos < yEnd; yPos++) {
                    for (int xPos = xStart; xPos < xEnd; xPos++) {
                        int color = inputImage.getPixel(xPos, yPos);
                        int pixIntensity = ((color & 255) + ((color >> 8) & 255) + ((color >> 16) & 255)) / 128;
                        intensity += pixIntensity;
                    }
                }
                intensity /= (sampleRes * sampleRes);
                Image currDice = diceSprites[intensity];

                yStart = y * diceRes;
                yEnd = yStart + diceRes;
                xStart = x * diceRes;
                xEnd = xStart + diceRes;
                for (int yPos = yStart; yPos < yEnd; yPos++) {
                    for (int xPos = xStart; xPos < xEnd; xPos++) {
                        int color = currDice.getPixel(xPos % diceRes, yPos % diceRes);
                        outputImage.setPixel(xPos, yPos, (0xff000000 | color));
                    }
                }
            }
        }
        outputImage.saveImage(filePath, fileName + "_Diced");
    }
}
