package com.aiprocess.backendonia.service;

import com.aiprocess.backendonia.config.AppProperties;
import com.aiprocess.backendonia.domain.ProposalRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class KnowledgeBaseService {

    private static final Pattern KB_FILE_PATTERN = Pattern.compile("^KB-(\\d{4})-(.+)\\.md$");

    private final AppProperties properties;

    public KnowledgeBaseService(AppProperties properties) {
        this.properties = properties;
    }

    public Path approveProposal(ProposalRecord proposal, String content) {
        try {
            Path kbDir = kbDirectory();
            Files.createDirectories(kbDir);

            int nextNumber = nextKnowledgeBaseNumber(kbDir);
            String fileName = "KB-%04d-%s.md".formatted(nextNumber, slugify(proposal.getTitle()));
            Path filePath = kbDir.resolve(fileName);
            Files.writeString(filePath, markdownFor(proposal.getTitle(), content), StandardCharsets.UTF_8);

            updateIndexFile(kbDir);
            updateClaudeFile(kbDir, nextNumber, fileName);
            return filePath;
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao gravar arquivos da base de conhecimento", exception);
        }
    }

    private Path kbDirectory() {
        return Path.of(properties.getKnowledgeBase().getBaseDir(), "wiki", "kb");
    }

    private int nextKnowledgeBaseNumber(Path kbDir) throws IOException {
        if (!Files.exists(kbDir)) {
            return 1;
        }

        return Files.list(kbDir)
                .map(path -> path.getFileName().toString())
                .map(KB_FILE_PATTERN::matcher)
                .filter(Matcher::matches)
                .mapToInt(matcher -> Integer.parseInt(matcher.group(1)))
                .max()
                .orElse(0) + 1;
    }

    private void updateIndexFile(Path kbDir) throws IOException {
        List<String> entries = Files.list(kbDir)
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .filter(name -> KB_FILE_PATTERN.matcher(name).matches())
                .sorted(Comparator.naturalOrder())
                .map(name -> "- [" + name.replace(".md", "") + "](wiki/kb/" + name + ")")
                .toList();

        Path indexPath = Path.of(properties.getKnowledgeBase().getBaseDir(), "index.md");
        Files.createDirectories(indexPath.getParent());
        String content = "# Index da Knowledge Base\n\n" +
                (entries.isEmpty() ? "Nenhum documento aprovado ainda.\n" : String.join("\n", entries) + "\n");
        Files.writeString(indexPath, content, StandardCharsets.UTF_8);
    }

    private void updateClaudeFile(Path kbDir, int lastNumber, String fileName) throws IOException {
        long totalFiles = Files.list(kbDir)
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .filter(name -> KB_FILE_PATTERN.matcher(name).matches())
                .count();

        Path claudePath = Path.of(properties.getKnowledgeBase().getBaseDir(), "CLAUDE.md");
        Files.createDirectories(claudePath.getParent());
        String content = """
                # CLAUDE Metadata

                approved_documents: %d
                last_generated_code: KB-%04d
                last_generated_file: %s
                """.formatted(totalFiles, lastNumber, fileName);
        Files.writeString(claudePath, content, StandardCharsets.UTF_8);
    }

    private String markdownFor(String title, String content) {
        return """
                # %s

                %s
                """.formatted(title, content);
    }

    private String slugify(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? "documento" : normalized;
    }
}
