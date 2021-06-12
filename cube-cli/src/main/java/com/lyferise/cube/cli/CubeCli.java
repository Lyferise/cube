package com.lyferise.cube.cli;

import com.lyferise.cube.client.CubeClient;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

public class CubeCli {
    public void startCli(final String[] args) {
        try {
            System.out.format("Cube client version %s%n", getAppVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArgumentParser parser = new ArgumentParser();
        if (parser.parse(args)) {
            CubeClient client = new CubeClient(parser.getHost(), parser.getUser(), parser.getPassword());
        } else {
            System.out.println("I'm done, bye.");
        }
    }

    private String getAppVersion() throws IOException {
        String appVersion = null;
        Enumeration<URL> resources;
        resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            Manifest manifest = new Manifest(resources.nextElement().openStream());
            appVersion = manifest.getMainAttributes().getValue("Implementation-Version");
        }
        return appVersion;
    }
}