package ru.tykvin.hermes.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.tykvin.hermes.file.model.DownloadingEntity;
import ru.tykvin.hermes.file.model.FileInfo;
import ru.tykvin.hermes.file.service.FileService;
import ru.tykvin.hermes.file.service.UploadService;
import ru.tykvin.hermes.security.CurrentUserHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class FileListController {

    private final FileService fileSerive;
    private final UploadService uploadService;
    private final CurrentUserHolder userHolder;

    @GetMapping("/api/file")
    public List<FileInfo> listUploadedFiles() {
        return fileSerive.geAllUserFiles(userHolder.get());
    }

    @PostMapping("/api/upload")
    public void handleFileUpload(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        uploadService.upload(userHolder.get(), request);
    }
}
