package it.silma.simply.utils;

import it.silma.simply.main.Simply;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class ResourceLoader extends URLStreamHandler {

	private final ClassLoader classLoader;

	private ResourceLoader() {
		this.classLoader = getClass().getClassLoader();
	}

	public static ImageIcon loadImageIcon(String resource) {

		try {
			/*InputStream in = ImageIcon.class.getResourceAsStream("/" + Constants.pathToRes + resource);
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			int d;
			while ((d = in.read()) != -1) {
				out.write(d);
			}

			byte[] bytes = out.toByteArray();*/
			URL url = ResourceLoader.class.getResource(Constants.pathToRes + "logo.png");
			return new ImageIcon(url);

		} catch (Exception e) {
			Simply.onError(e.getMessage());
		}

		return null;
	}

	public static URL loadURL(String resource) {

		try {
			return new URL(null, "classpath:" + Constants.pathToRes + resource, new ResourceLoader());
		} catch (MalformedURLException e) {
			Simply.onError(e.getMessage());
		}

		return null;
	}

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		final URL resourceUrl = classLoader.getResource(u.getPath());
		return resourceUrl.openConnection();
	}
}
