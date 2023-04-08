import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResolversTests {

    @TempDir
    static Path testRootDirectory;

    @BeforeAll
    private void setUp() {
        createTestTree();
    }

    @Test
    private void testA() {
        assertTrue(new File(testRootDirectory.toString() + "another").exists());
    }

    private void createTestTree() {
        try {
            Files.createFile(Path.of(testRootDirectory.toString(), "another"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
