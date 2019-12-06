package com.beheresoft.raspberryPi.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;

public class FontModel {

    private int width;
    private int height;
    private int boardXPixel;
    private int boardYPixel;
    private String fontName;
    private Font font;
    private BufferedImage bufferedImage;
    private Graphics2D graphics;
    private SCAN_MODEL model = SCAN_MODEL.UP_TO_DOWN;
    private String saveDisk;
    private Charset chinese = Charset.forName("GB2312");

    public FontModel(int width, int height) {
        this(width, height, 8, 8, "Microsoft YaHei UI Light");
    }

    public FontModel(int width, int height, int boardXPixel, int boardYPixel, String fontName) {
        this.width = width;
        this.height = height;
        this.boardXPixel = boardXPixel;
        this.boardYPixel = boardYPixel;
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
        if (!disk.endsWith("/") || !disk.endsWith("\\")) {
            this.saveDisk = this.saveDisk + "/";
        }
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

    private FontModel createFont(String name) {
        this.fontName = name;
        this.font = new Font(name, Font.PLAIN, height);
        graphics.setFont(this.font);
        return this;
    }

    public FontModel setFontFile(String fontName, String fontFile) throws IOException, FontFormatException {
        this.fontName = fontName;
        this.font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File(fontFile)));
        return this;
    }

    public FontModel display(short[] bytes, int width) {
        StringBuilder[] boards = new StringBuilder[bytes.length * 8 / boardXPixel / boardYPixel];
        for (int i = 0; i < boards.length; i++) {
            boards[i] = new StringBuilder();
        }
        int key[] = { 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01 };
        for (int b = 0; b < bytes.length; b++) {
            short now = bytes[b];
            StringBuilder board = boards[b / boardYPixel];
            for (int i = 0; i < boardXPixel; i++) {
                if ((now & key[i]) == 0x01) {
                    board.append('●');
                } else {
                    board.append('○');
                }
                board.append(' ');
            }
            if (model == SCAN_MODEL.LEFT_TO_RIGHT) {

            }
        }

        for (int i = 0; i < boardYPixel; i++) {
            for (int k = 0; k < boardYPixel * 2; k += 2) {
                System.out.print(boards[0].charAt(i * 8 + k));
                System.out.print(boards[0].charAt(i * 8 + k + 1));
            }
            for (int m = 0; m < boardYPixel * 2; m += 2) {
                System.out.print(boards[1].charAt(i * 8 + m));
                System.out.print(boards[1].charAt(i * 8 + m + 1));
            }
            System.out.println();
        }

        for (int i = 0; i < boardYPixel; i++) {
            for (int k = 0; k < boardYPixel * 2; k += 2) {
                System.out.print(boards[2].charAt(i * 8 + k));
                System.out.print(boards[2].charAt(i * 8 + k + 1));
            }
            for (int m = 0; m < boardYPixel * 2; m += 2) {
                System.out.print(boards[3].charAt(i * 8 + m));
                System.out.print(boards[3].charAt(i * 8 + m + 1));
            }
            System.out.println();
        }

        return this;
    }

    /**
     * one char use 4 boards
     * 1   |  2
     * ————
     * 3  |  4
     */
    public short[] conventOneChar(char c) {
        return conventOneString(c + "");
    }

    private short[] conventOneString(String s) {
        clear();
        byte[] text = new byte[width * height];
        graphics.drawString(s, 0, height - height / boardYPixel);
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

        short[] res = new short[text.length / 8];
        for (int i = 0; i < res.length; i++) {
            short b = 0x00;
            for (int j = 0; j < 8; j++) {
                b |= text[i * 8 + j];
                b = (short) (b << 1);
            }
            res[i] = b;
        }
        return res;
    }

    /**
    StringBuilder sbd = new StringBuilder();
    int bufferLength = 0;**/

    private byte getData(int row, int column, int x, int y) {
        if (model == SCAN_MODEL.LEFT_TO_RIGHT) {
            /**
            sbd.append(bufferedImage.getRGB(y + row * boardYPixel, x + column * boardYPixel) > -16777216 ? "● " : "○ ");

            if (++bufferLength % 8 == 0) {
                sbd.append("\n");
            }**/
            return (byte) (bufferedImage.getRGB(y + row * boardYPixel, x + column * boardYPixel) > -16777216 ? 0x01 : 0x00);
        } else if (model == SCAN_MODEL.UP_TO_DOWN) {
            return (byte) (bufferedImage.getRGB(x + row * boardYPixel, y + column * boardYPixel) > -16777216 ? 0x01 : 0x00);
        }
        return Byte.parseByte(null);
    }

    public FontModel preview(char c) {
        clear();
        graphics.drawString(c + "", 0, height - height / boardYPixel);
        try {
            File file = new File(saveDisk + genPreviewName(c));
            if (!file.exists()) {
                file.createNewFile();
            }
            ImageIO.write(bufferedImage, "PNG", new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void binaryChineseFont() {
        try {
            File file = new File(saveDisk + genFileName());
            if (file.exists() && file.delete()) {

            }
            file.createNewFile();
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            //raf.write(new byte[]{0x2, 0xB, 0xF, 0xA, 0x0, 0x0, 0x0, 0x1});
            for (int h = 0xA1; h <= 0xF7; h++) {
                for (int l = 0xA0; l <= 0xFE; l++) {
                    short[] shorts = conventOneString(new String(new byte[]{(byte) h, (byte) l}, chinese));
                    for (int i = 0 ; i < shorts.length ; i++){
                        raf.writeShort(shorts[i]);
                    }
                    //raf.write(conventOneString(new String(new byte[]{(byte) h, (byte) l}, chinese)));
                }
            }
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        graphics.clearRect(0, 0, width, height);
    }

    private String genFileName() {
        return fontName + "_cpr" + "_" + width + "_" + height + ".bft";
    }

    private String genPreviewName(char c) {
        return fontName + "_" + c + "_" + width + "_" + height + ".png";
    }

    public enum SCAN_MODEL {
        UP_TO_DOWN,
        LEFT_TO_RIGHT
    }


}
