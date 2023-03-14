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
			URL url = ResourceLoader.class.getResource(Constants.pathToRes + "logo.png");
			return new ImageIcon(url);

		} catch (Exception e) {
			Simply.onError(e.getMessage());
		}

		return null;
	}

	public static URL loadURL(String resource) {
		return ResourceLoader.class.getResource(Constants.pathToRes + resource);
	}

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		final URL resourceUrl = classLoader.getResource(u.getPath());
		return resourceUrl.openConnection();
	}
}
