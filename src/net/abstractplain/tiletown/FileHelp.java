package net.abstractplain.tiletown;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

public class FileHelp {

	public static BufferedImage loadImage(String imgName) throws IOException {
		String basedir = "bin/";
		BufferedImage img = null;
		File f = new File(basedir + imgName);
		img = ImageIO.read(f);
		return img;
	}

	public static List<String> loadLines(String filename) throws IOException {
		String basedir = "bin/";
		File f = new File(basedir + filename);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		LinkedList<String> lines = new LinkedList<String>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}

	public static void demo() throws IOException {
		FileHelp.loadImage("basic/bg001.png");
	}
}
