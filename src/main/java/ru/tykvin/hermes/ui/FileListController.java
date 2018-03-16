package ru.tykvin.hermes.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.tykvin.hermes.file.model.DownloadingEntity;
import ru.tykvin.hermes.file.service.FileService;
import ru.tykvin.hermes.file.service.UploadService;
import ru.tykvin.hermes.security.CurrentUserHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class FileListController {

    private final FileService fileSerive;
    private final UploadService uploadService;
    private final CurrentUserHolder userHolder;

    @GetMapping("/")
    public String listUploadedFiles(Model model) {

        model.addAttribute("files", fileSerive.geAllUserFiles(userHolder.get()));

        return "uploadForm";
    }

    @PostMapping("/")
    public String handleFileUpload(RedirectAttributes redirectAttributes, HttpServletRequest request) {

        Set<DownloadingEntity> result = uploadService.upload(userHolder.get(), request);
        String files = result.stream().map(DownloadingEntity::getFileName).collect(Collectors.joining(" ,"));
        if (result.size() > 0) {
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded " + files + "!");
        } else {
            redirectAttributes.addFlashAttribute("message", "You need to choose file for upload");
        }

        return "redirect:/";
    }
}
