package com.beheresoft.raspberryPi.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FontModel {

    private int width;
    private int height;
    private int boardXPixel;
    private int boardYPixel;
    private boolean compression;
    private String fontName;
    private Font font;
    private BufferedImage bufferedImage;
    private Graphics2D graphics;
    private SCAN_MODEL model = SCAN_MODEL.UP_TO_DOWN;
    private String saveDisk;

    public FontModel(int width, int height) {
        this(width, height, 8, 8, false, "Microsoft YaHei UI Light");
    }

    public FontModel(int width, int height, int boardXPixel, int boardYPixel, boolean compression, String fontName) {
        this.width = width;
        this.height = height;
        this.boardXPixel = boardXPixel;
        this.boardYPixel = boardYPixel;
        this.compression = compression;
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.setBackground(Color.BLACK);
        createFont(fontName);
    }

    public FontModel setScanModel(SCAN_MODEL model) {
        this.model = model;
        return this;
    }

    public FontModel setSaveDisk(String disk) {
        this.saveDisk = disk;
        return this;
    }

    public FontModel listAvirableFonts() {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] names = e.getAvailableFontFamilyNames();
        for (int i = 0; i < names.length; i++) {
            System.out.printf("%d : %s \n", i, names[i]);
        }
        return this;
    }

    public FontModel setFont(int i) {
        String name = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()[i];
        createFont(name);
        return this;
    }

    public FontModel setFont(String fontName) {
        createFont(fontName);
        return this;
    }

    private void createFont(String name) {
        this.fontName = name;
        this.font = new Font(name, Font.PLAIN, height);
        graphics.setFont(this.font);
    }

    public FontModel setFontFile(String fontName, String fontFile) throws IOException, FontFormatException {
        this.fontName = fontName;
        this.font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File(fontFile)));
        return this;
    }

    /**
     * one char use 4 boards
     * 1   |  2
     * ————
     * 3  |  4
     */
    public byte[] conventOneChar(char c) {
        clear();
        byte[] text = new byte[width * height];
        graphics.drawString(c + "", 0, height - height / boardYPixel);
        int rows = width / boardYPixel;
        int columns = height / boardXPixel;
        int index = 0;
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                for (int x = 0; x < boardXPixel; x++) {
                    for (int y = 0; y < boardYPixel; y++) {
                        text[index++] = getData(row, column, x, y);
                    }
                }
            }
        }
        return text;
    }

    private byte getData(int row, int column, int x, int y) {
        if (model == SCAN_MODEL.LEFT_TO_RIGHT) {
            return (byte) (bufferedImage.getRGB(x + row * boardYPixel, y + column * boardYPixel) > -16777216 ? 1 : 0);
        } else if (model == SCAN_MODEL.UP_TO_DOWN) {
            return (byte) (bufferedImage.getRGB(y + row * boardYPixel, x + column * boardYPixel) > -16777216 ? 1 : 0);
        }
        return Byte.parseByte(null);
    }

    public FontModel preview(char c, String savePath) {
        clear();
        graphics.drawString(c + "", 0, height - height / boardYPixel);
        try {
            File file = new File(savePath + genPreviewName(c));
            if (!file.exists()) {
                file.createNewFile();
            }
            ImageIO.write(bufferedImage, "PNG", new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    private void clear() {
        graphics.clearRect(0, 0, width, height);
    }

    public void finish() {

    }

    private String genFileName() {
        return fontName + (compression ? "_cpr" : "") + "_" + width + "_" + height + ".bft";
    }

    private String genPreviewName(char c) {
        return fontName + "_" + c + "_" + width + "_" + height + ".png";
    }

    enum SCAN_MODEL {
        UP_TO_DOWN,
        LEFT_TO_RIGHT
    }


}
