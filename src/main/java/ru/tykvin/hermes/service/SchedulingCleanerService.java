package ru.tykvin.hermes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.configuration.StorageConfiguration;
import ru.tykvin.hermes.dao.FilesDao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingCleanerService {

    private final FilesDao filesDao;
    private final StorageConfiguration sc;

    @Scheduled(cron = "0 * * * * *")
    public void removeExpired() {
        log.info("Start cleaning");
        filesDao.clearExpiredBindings();
        List<String> forDelete = filesDao.removeUnboundFiles();
        for (String uuid : forDelete) {
            Path path = Paths.get(sc.getRoot()).resolve(uuid);
            try {
                log.info("Remove {}", path);
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.error("Can't remove {}", path, e);
            }
        }
    }

}
