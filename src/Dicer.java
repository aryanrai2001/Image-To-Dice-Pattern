import java.nio.file.FileSystems;

public class Dicer {
    private static Image[] diceSprites;
    private static String filePath, fileName, inputImagePath, diceSheetPath;
    private static int sampleRes, diceRes;

    // Function to initialize all the private variables.
    private static void initialize() {
        /*
         * These values can be changed based on user preference to manipulate the output image.
         * diceRes specifies the resolution of each dice face in the dice sheet (Currently only supports 16x16).
         * sampleRes specifies the resolution of sampling grid that'll be used to plot each dice face.
         * diceSheetPath specifies the path for the dice sheet image file.
         * inputImagePath specifies the path for the input image file.
         */
        diceRes = 16;
        sampleRes = 10;
        diceSheetPath = "res/Dice/DiceSheet.png";
        inputImagePath = "res/Image/6.png";

        // Converting the relative paths to absolute paths.
        diceSheetPath = FileSystems.getDefault().getPath(diceSheetPath).normalize().toAbsolutePath().toString();
        inputImagePath = FileSystems.getDefault().getPath(inputImagePath).normalize().toAbsolutePath().toString();

        // Extracting the file name and file path from the full input image path.
        int pathSeparator = inputImagePath.lastIndexOf('/');
        if (pathSeparator == -1)
            pathSeparator = inputImagePath.lastIndexOf('\\');
        filePath = inputImagePath.substring(0, pathSeparator);
        fileName = inputImagePath.substring(pathSeparator + 1, inputImagePath.lastIndexOf('.'));
    }

    // Function to load and extract dice sprites from the dice sheet image file.
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

    // Function to create a dice-pattern from the input image and save it to an image file.
    private static void createDicePattern() {
        // Load input and output image into memory.
        Image inputImage = new Image(inputImagePath);
        Image outputImage = new Image((inputImage.getWidth() / sampleRes) * diceRes, (inputImage.getHeight() / sampleRes) * diceRes);

        // Calculate the number of dice horizontally and vertically based on the value of sampleRes.
        int diceH = inputImage.getHeight() / sampleRes;
        int diceW = inputImage.getWidth() / sampleRes;

        // Iterate for every dice face in the output image.
        for (int y = 0; y < diceH; y++) {
            for (int x = 0; x < diceW; x++) {
                // Calculate the bounds of the sample grid that map to each dice face.
                int yStart = y * sampleRes;
                int yEnd = yStart + sampleRes;
                int xStart = x * sampleRes;
                int xEnd = xStart + sampleRes;

                // Add the color intensity of each pixel in the sample grid.
                int intensity = 0;
                for (int yPos = yStart; yPos < yEnd; yPos++) {
                    for (int xPos = xStart; xPos < xEnd; xPos++) {
                        int color = inputImage.getPixel(xPos, yPos);
                        /*
                         * Extract the red, green, and blue components from the pixel value and divide by 3
                         * to find the average pixel intensity. Then divide it by 255 to get normalized pixel intensity
                         * between 0 and 1. Then multiply it by 6 to find the corresponding dice face for that pixel.
                         *
                         * Here division by 3, then by 255, and then multiplication by 6 can be simplified into
                         * division by 128.
                         *
                         * Lastly we are adding up the resulting dice face index for all pixels in the grid to later
                         * calculate an overall average dice face index for the sample grid.
                         */
                        int pixIntensity = ((color & 255) + ((color >> 8) & 255) + ((color >> 16) & 255)) / 128;
                        intensity += pixIntensity;
                    }
                }

                intensity /= (sampleRes * sampleRes); // Calculate the average intensity to get a normalized contrast value.
                Image currDice = diceSprites[intensity]; // Get the dice face corresponding to the above contrast value.

                // Recalculate the bounds, but now for the dice face.
                yStart = y * diceRes;
                yEnd = yStart + diceRes;
                xStart = x * diceRes;
                xEnd = xStart + diceRes;

                // Plot the resulting dice face within calculated bounds in the final output image.
                for (int yPos = yStart; yPos < yEnd; yPos++) {
                    for (int xPos = xStart; xPos < xEnd; xPos++) {
                        int color = currDice.getPixel(xPos % diceRes, yPos % diceRes);
                        outputImage.setPixel(xPos, yPos, (0xff000000 | color));
                    }
                }
            }
        }

        // Save the output image date to an image file in the same directory as the input image file.
        outputImage.saveToFile(filePath, fileName + "_Diced");
    }

    // Entry Point.
    public static void main(String[] args) {
        initialize();
        loadDiceSprites();
        createDicePattern();
    }
}
