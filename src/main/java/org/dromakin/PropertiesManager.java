/*
 * File:     PropertiesManager
 * Package:  org.dromakin
 * Project:  netology_api_calls_homework_2
 *
 * Created by dromakin as 22.01.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.01.22
 * copyright - Echelon Inc. 2023
 */

package org.dromakin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

public class PropertiesManager {

    private static final Logger logger = LogManager.getLogger(PropertiesManager.class);

    private static final Path propertiesFile = Paths.get("config.properties");

    public void saveProperties() throws PropertiesManagerException {
        Scanner scanner = new Scanner(System.in);

        try (OutputStream output = Files.newOutputStream(propertiesFile)) {
            Properties prop = new Properties();

            logger.info("Creating properties file!");
            logger.info("Please paste your key:");
            String key = scanner.nextLine();
            // set the properties value
            prop.setProperty("api.key", key);

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException e) {
            throw new PropertiesManagerException(e.getMessage(), e);
        }
    }

    public String loadPropertiesFromFile() throws PropertiesManagerException {

        String key = null;

        try (InputStream input = Files.newInputStream(propertiesFile)) {
            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            key = prop.getProperty("api.key");

        } catch (IOException e) {
            throw new PropertiesManagerException(e.getMessage(), e);
        }

        return key;
    }


    public static void main(String[] args) {

    }

}
