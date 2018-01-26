package ru.tykvin.hermes.file.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.file.configuration.StorageConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Paths;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class ClearFilesOnStartup {

    private final StorageConfiguration sc;

    @PostConstruct
    public void clear() {
        try {
            FileUtils.deleteDirectory(Paths.get(sc.getRoot()).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
