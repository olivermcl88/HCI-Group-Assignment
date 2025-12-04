package com.hci.ufosightings.web;

import com.hci.ufosightings.common.Sighting;
import com.hci.ufosightings.common.User;
import com.hci.ufosightings.dto.CommentWithUser;
import com.hci.ufosightings.service.AreaService;
import com.hci.ufosightings.service.CommentService;
import com.hci.ufosightings.service.SightingService;
import com.hci.ufosightings.service.TeamService;
import com.hci.ufosightings.service.UserService;
import com.hci.ufosightings.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SightingsController {

    private final UserService userService;
    private final AreaService areaService;
    private final TeamService teamService;
    private final SightingService sightingService;
    private final CommentService commentService;
    private final VoteService voteService;

    @GetMapping("sightings")
    public String sightings(Model model) {
        List<Sighting> allSightings = sightingService.getAllSightings();
        model.addAttribute("sightings", allSightings);
        Map<Long, String> areasMap = areaService.getAllAreas().stream()
                .collect(Collectors.toMap(a -> a.getAreaId(), a -> a.getAreaName()));
        model.addAttribute("areasMap", areasMap);

        if (!allSightings.isEmpty()) {
            Sighting firstSighting = allSightings.getFirst();
            model.addAttribute("currentSighting", firstSighting);
            
            User reporter = userService.getUserById(firstSighting.getReporterUserId());
            model.addAttribute("reporter", reporter);
            
            List<CommentWithUser> allComments = commentService.getCommentsBySightingId(firstSighting.getSightingId());
            List<CommentWithUser> comments = allComments.stream()
                .filter(comment -> !comment.getCommentText().startsWith("📎 Evidence uploaded:"))
                .collect(java.util.stream.Collectors.toList());
            model.addAttribute("comments", comments);

            // Add vote stats to model (default/demo current user id = 1)
            Long currentUserId = 1L;
            model.addAttribute("currentUserId", currentUserId);
            addVoteStatsToModel(model, firstSighting.getSightingId(), currentUserId);
        }
        
        return "sightings";
    }
    
    @GetMapping("sightings/{id}")
    public String viewSighting(@PathVariable Long id, Model model) {
        List<Sighting> allSightings = sightingService.getAllSightings();
        model.addAttribute("sightings", allSightings);
        Map<Long, String> areasMap = areaService.getAllAreas().stream()
                .collect(Collectors.toMap(a -> a.getAreaId(), a -> a.getAreaName()));
        model.addAttribute("areasMap", areasMap);
        
        Optional<Sighting> sighting = sightingService.getSightingById(id);
        if (sighting.isPresent()) {
            model.addAttribute("currentSighting", sighting.get());
            
            User reporter = userService.getUserById(sighting.get().getReporterUserId());
            model.addAttribute("reporter", reporter);
            
            List<CommentWithUser> allComments = commentService.getCommentsBySightingId(id);
            List<CommentWithUser> comments = allComments.stream()
                .filter(comment -> !comment.getCommentText().startsWith("📎 Evidence uploaded:"))
                .collect(java.util.stream.Collectors.toList());
            model.addAttribute("comments", comments);

            // Add vote stats to model (default/demo current user id = 1)
            Long currentUserId = 1L;
            model.addAttribute("currentUserId", currentUserId);
            addVoteStatsToModel(model, id, currentUserId);
        } else {
            return "redirect:/sightings";
        }
        
        return "sightings";
    }
    
    @PostMapping("sightings/{id}/vote")
    public String voteOnSighting(@PathVariable Long id, 
                               @RequestParam String voteType,
                               @RequestParam(defaultValue = "1") Long userId) {
        try {
            voteService.castVote(id, userId, voteType);
        } catch (IllegalArgumentException e) {
            log.error("Invalid vote type: {}", voteType, e);
            // Could add error message to model here
        }
        return "redirect:/sightings/" + id;
    }
    
    @PostMapping("sightings/{id}/comment")
    public String addComment(@PathVariable Long id, 
                           @RequestParam String commentText,
                           @RequestParam(defaultValue = "1") Long userId,
                           @RequestParam(required = false) Boolean isAnonymous,
                           @RequestParam(value = "attachment", required = false) MultipartFile attachmentFile,
                           @RequestParam(required = false) Long parentCommentId) {
        
        String attachmentFilename = null;
        String attachmentOriginalName = null;
        
        // Handle file upload if present
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            try {
                // Create uploads directory if it doesn't exist
                Path uploadPath = Paths.get("uploads/comments");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                // Generate unique filename
                String originalFilename = attachmentFile.getOriginalFilename();
                String cleanOriginalName = originalFilename != null ? originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_") : "file";
                attachmentFilename = "comment_" + id + "_" + UUID.randomUUID().toString() + "_" + cleanOriginalName;
                attachmentOriginalName = originalFilename;
                
                // Save file
                Path filePath = uploadPath.resolve(attachmentFilename);
                Files.copy(attachmentFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                log.info("Comment attachment uploaded: {} for sighting {}", attachmentFilename, id);
                
            } catch (IOException e) {
                log.error("Failed to upload comment attachment", e);
                // Continue without attachment
                attachmentFilename = null;
                attachmentOriginalName = null;
            }
        }
        
        if (commentText != null && !commentText.trim().isEmpty()) {
            commentService.addComment(id, userId, commentText.trim(), isAnonymous, attachmentFilename, attachmentOriginalName, parentCommentId);
        }
        
        return "redirect:/sightings/" + id;
    }

    @GetMapping("sightings/new")
    public String showNewSightingForm(Model model, @RequestParam(required = false) Long areaId) {
        Sighting s = new Sighting();
        model.addAttribute("areas", areaService.getAllAreas());
        if (areaId != null) {
            s.setAreaId(areaId);
            model.addAttribute("areaId", areaId);
            areaService.getAreaById(areaId).ifPresent(area -> model.addAttribute("areaName", area.getAreaName()));
        }
        model.addAttribute("sighting", s);
        return "report-sighting";
    }

    @PostMapping("sightings/new")
    public String submitNewSighting(@RequestParam String title,
                                    @RequestParam(required = false) String sightingDate,
                                    @RequestParam(required = false) Integer durationMinutes,
                                    @RequestParam(required = false) String location,
                                    @RequestParam(required = false) Double latitude,
                                    @RequestParam(required = false) Double longitude,
                                    @RequestParam(required = false) String shape,
                                    @RequestParam(required = false) String description,
                                    @RequestParam(required = false) String areaId) {

        Long parsedAreaId = null;
        if (areaId != null && !areaId.trim().isEmpty()) {
            try {
                parsedAreaId = Long.parseLong(areaId);
            } catch (NumberFormatException ex) {
                log.warn("Invalid areaId submitted: {}", areaId);
            }
        }

        Sighting s = Sighting.builder()
                .title(title)
                .reporterUserId(1L) // default/demo user
                .sightingDate(sightingDate != null && !sightingDate.isEmpty() ? java.time.LocalDate.parse(sightingDate) : null)
                .durationMinutes(durationMinutes)
                .location(location)
                .latitude(latitude)
                .longitude(longitude)
                .shape(shape)
                .description(description)
                .areaId(parsedAreaId)
                .legitVotes(0)
                .uncertainVotes(0)
                .hoaxVotes(0)
                .build();

        Sighting saved = sightingService.saveSighting(s);
        if (saved != null && saved.getSightingId() != null) {
            return "redirect:/sightings/" + saved.getSightingId();
        }
        return "redirect:/sightings";
    }

    @PostMapping("sightings/{id}/upload-evidence")
    public String uploadEvidence(@PathVariable Long id,
                               @RequestParam("evidence") MultipartFile file,
                               @RequestParam(defaultValue = "1") Long userId) {
        if (!file.isEmpty()) {
            try {
                // Create uploads directory if it doesn't exist
                Path uploadPath = Paths.get("uploads/evidence");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generate unique filename that preserves original name
                String originalFilename = file.getOriginalFilename();
                String cleanOriginalName = originalFilename != null ? originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_") : "file";
                String filename = "evidence_" + id + "_" + UUID.randomUUID().toString() + "_" + cleanOriginalName;

                // Save file
                Path filePath = uploadPath.resolve(filename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                log.info("Evidence file uploaded: {} for sighting {}", filename, id);

            } catch (IOException e) {
                log.error("Failed to upload evidence file", e);
            }
        }
        return "redirect:/sightings/" + id;
    }

    @GetMapping("sightings/{id}/evidence")
    public String viewEvidence(@PathVariable Long id, Model model) {
        List<String> evidenceFiles = getEvidenceFiles(id);
        model.addAttribute("evidenceFiles", evidenceFiles);
        model.addAttribute("sightingId", id);

        // Also get sighting info for context
        Optional<Sighting> sighting = sightingService.getSightingById(id);
        sighting.ifPresent(value -> model.addAttribute("sighting", value));

        return "evidence-view";
    }

    private List<String> getEvidenceFiles(Long sightingId) {
        List<String> files = new ArrayList<>();

        try {
            Path evidencePath = Paths.get("uploads/evidence");
            if (Files.exists(evidencePath)) {
                try (Stream<Path> paths = Files.list(evidencePath)) {
                    paths.filter(path -> {
                        String filename = path.getFileName().toString();
                        return filename.startsWith("evidence_" + sightingId + "_");
                    })
                    .forEach(path -> {
                        String storedFilename = path.getFileName().toString();
                        String originalName = extractOriginalFilenameFromPattern(storedFilename);
                        files.add(storedFilename + "|" + originalName);
                    });
                }
            }
        } catch (IOException e) {
            log.error("Failed to list evidence files for sighting " + sightingId, e);
        }
        return files;
    }

    private String extractOriginalFilenameFromPattern(String storedFilename) {
        // Pattern: evidence_{sightingId}_{uuid}_{originalFilename}
        String[] parts = storedFilename.split("_", 4);
        if (parts.length >= 4) {
            return parts[3]; // The original filename part
        }

        // Fallback for old pattern
        String extension = "";
        int lastDot = storedFilename.lastIndexOf('.');
        if (lastDot > 0) {
            extension = storedFilename.substring(lastDot);
        }
        return "Evidence File" + extension;
    }

    @PostMapping("admin/cleanup-evidence-comments")
    public String cleanupEvidenceComments() {
        // This is a one-time cleanup method - you can call it to remove existing evidence comments
        // In a real application, you'd want proper admin authentication here
        try {
            commentService.deleteCommentsByPattern("📎 Evidence uploaded:");
            log.info("Evidence comments cleaned up successfully");
        } catch (Exception e) {
            log.error("Failed to cleanup evidence comments", e);
        }
        return "redirect:/sightings";
    }

    @GetMapping("comment-attachment/{filename}")
    public ResponseEntity<Resource> downloadCommentAttachment(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/comments").resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            }
        } catch (Exception e) {
            log.error("Failed to download comment attachment: " + filename, e);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("evidence/{filename}")
    public ResponseEntity<Resource> downloadEvidence(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/evidence").resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            }
        } catch (Exception e) {
            log.error("Failed to download evidence file: " + filename, e);
        }
        return ResponseEntity.notFound().build();
    }

    // Helper to add vote counts, percentages and current user's vote to the model
    private void addVoteStatsToModel(Model model, Long sightingId, Long currentUserId) {
        long legitVotes = voteService.getLegitVotes(sightingId);
        long uncertainVotes = voteService.getUncertainVotes(sightingId);
        long hoaxVotes = voteService.getHoaxVotes(sightingId);
        long totalVotes = voteService.getTotalVotes(sightingId);

        int legitPercentage = 0;
        int uncertainPercentage = 0;
        int hoaxPercentage = 0;

        if (totalVotes > 0) {
            legitPercentage = (int) Math.round((legitVotes * 100.0) / totalVotes);
            uncertainPercentage = (int) Math.round((uncertainVotes * 100.0) / totalVotes);
            hoaxPercentage = (int) Math.round((hoaxVotes * 100.0) / totalVotes);

            // Ensure percentages sum to 100 (adjust the largest if rounding leaves a remainder)
            int sum = legitPercentage + uncertainPercentage + hoaxPercentage;
            if (sum != 100) {
                int max = Math.max(legitPercentage, Math.max(uncertainPercentage, hoaxPercentage));
                if (max == legitPercentage) {
                    legitPercentage += 100 - sum;
                } else if (max == uncertainPercentage) {
                    uncertainPercentage += 100 - sum;
                } else {
                    hoaxPercentage += 100 - sum;
                }
            }
        }

        model.addAttribute("legitVotes", legitVotes);
        model.addAttribute("uncertainVotes", uncertainVotes);
        model.addAttribute("hoaxVotes", hoaxVotes);
        model.addAttribute("totalVotes", totalVotes);
        model.addAttribute("legitPercentage", legitPercentage);
        model.addAttribute("uncertainPercentage", uncertainPercentage);
        model.addAttribute("hoaxPercentage", hoaxPercentage);

        // Current user's vote (optional)
        voteService.getUserVote(sightingId, currentUserId).ifPresent(vote -> model.addAttribute("userVote", vote));
    }

}
