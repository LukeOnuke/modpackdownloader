package com.lukeonuke.modpackdownloader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.therandomlabs.curseapi.CurseAPI;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class ModPackDownloader {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Gson gson = new Gson();

        System.out.print("Enter path to manifest : ");
        String pathToManifest = scanner.next();
        File manifest = new File(pathToManifest).getAbsoluteFile();
        while (!manifest.exists() || manifest.isDirectory()) {
            System.err.println("Could not find manifest \"" + manifest + "\"");
            System.out.println("\nEnter path to manifest : ");
            pathToManifest = scanner.next();
            manifest = new File(pathToManifest);
        }

        scanner.close();

        System.out.println("Purging mods path...");
        purgeDirectory(new File(manifest.getParent() + "/mods"));
        System.out.println("Purged mods path successfully!");

        JsonObject jsonObject = null;
        AtomicInteger maxSize = new AtomicInteger();
        try {
            jsonObject = gson.fromJson(new FileReader(pathToManifest), JsonObject.class);

            JsonArray manifestFiles = jsonObject.getAsJsonArray("files");
            maxSize.set(manifestFiles.size());
            final AtomicInteger size = new AtomicInteger(manifestFiles.size());

            manifestFiles.forEach(jsonElement -> {
                System.out.println(size.get() + " - " + jsonElement);
                size.getAndDecrement();
            });

            size.set(manifestFiles.size());

            final File finalManifest = manifest;
            manifestFiles.forEach(file -> {
                System.out.println(file);
                final JsonObject fileObject = (JsonObject) file;
                try {
                    System.out.println(standardLine() + size.get() + standardLine());
                    System.out.printf("Downloading file projID=%d fileID=%d | %d/%d\n",
                            fileObject.get("projectID").getAsInt(),
                            fileObject.get("fileID").getAsInt(),
                            size.get(),
                            maxSize.get());

                    Optional<Path> curseFIle = CurseAPI.downloadFileToDirectory(
                            fileObject.get("projectID").getAsInt(),
                            fileObject.get("fileID").getAsInt(),
                            getChildFile(finalManifest.getParent(), "/mods").toPath());

                    curseFIle.ifPresent(path -> System.out.println("Done downloading file " + path));

                    size.getAndDecrement();
                } catch (Exception e) {
                    System.err.println("An exception occurred while downloading file");
                    e.printStackTrace();
                    System.exit(-1);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        //Create ready to paste modpack
        try {
            File overrides = getChildFile(manifest.getParent(),
                    "/" + jsonObject.get("overrides").getAsString());

            if (overrides.exists()) {
                System.out.println("Detected curseforge modpack package.");

                File modpack = getChildFile(manifest.getParent(), "/modpack");

                System.out.println("Purging /modpack");
                purgeDirectory(modpack);

                System.out.println("Creating modpack package...");


                FileUtils.copyDirectory(getChildFile(manifest.getParent(), "/mods"),
                        getChildFile(modpack.getPath(), "/mods"));
                FileUtils.copyDirectory(overrides, modpack);

                System.out.println("Done creating modpack package at " + modpack.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(standardLine() + "> Report <" + standardLine());

        System.out.println("Modpack info : ");
        System.out.println("\t- Modpack name : " + jsonObject.get("name"));
        System.out.println("\t- Modpack author : " + jsonObject.get("author"));
        System.out.println("\t- Modpack version : " + jsonObject.get("version"));
        System.out.println("\t- Ammount of mods : " + maxSize.get());

        JsonObject minecraft = jsonObject.getAsJsonObject("minecraft");

        System.out.println("Modloader acceptable versions : ");
        minecraft.get("modLoaders").getAsJsonArray().forEach(jsonElement -> {
            JsonObject modLoader = (JsonObject) jsonElement;
            System.out.println("\t- " + modLoader.get("id").getAsString()
                    + " | recommended : " + modLoader.get("primary").getAsBoolean() + "\n");
        });

        System.out.println("Minecraft version : " + minecraft.get("version"));
        System.exit(0);
    }

    private static void purgeDirectory(@NotNull File dir) {
        if (!dir.exists()) return;
        if (dir.listFiles() == null) return;
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                purgeDirectory(file);
            }
            file.delete();
        }
    }

    private static String standardLine() {
        return "=".repeat(12);
    }

    private static File getChildFile(String file, String path) {
        return new File(new File(file).getAbsoluteFile().getPath() + path.replaceAll("(\\|/)", File.separator));
    }
}

