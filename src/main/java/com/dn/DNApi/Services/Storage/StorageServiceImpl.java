package com.dn.DNApi.Services.Storage;

import com.dn.DNApi.Configurations.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {
    @Autowired
    Env env;
    String storageFolder = null;
    Path root = null;


    @PostConstruct
    public void init() {
        storageFolder = (String) env.getProperty("application.dnlib.storagefolder");

    }

    @Override
    public String store(MultipartFile file) {
        try {
            String nowIs = String.valueOf(new Date().getTime());
            Path path = Paths.get(storageFolder + "/" + nowIs);
            Files.createDirectory(path);
            Files.copy(file.getInputStream(), path.resolve(nowIs + ".jpg"));
            return path.resolve(nowIs + ".jpg").toString();
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public Path load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return Paths.get(resource.getURI());
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        return null;
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }
}
