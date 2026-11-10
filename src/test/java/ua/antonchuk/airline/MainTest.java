package ua.antonchuk.airline;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
    private final PrintStream out = new PrintStream(outBuffer, true, StandardCharsets.UTF_8);
    private final PrintStream err = new PrintStream(errBuffer, true, StandardCharsets.UTF_8);

    private Path createDataFile() throws IOException {
        Path data = tempDir.resolve("airplanes.txt");
        Files.writeString(data, "PASSENGER,Boeing 737,5400,2.5,189", StandardCharsets.UTF_8);
        return data;
    }

    private Path createConfig(Path dataFile) throws IOException {
        Path config = tempDir.resolve("app.properties");
        String content = String.join("\n",
                "airline.name=Test Air",
                "data.file=" + dataFile.toString().replace('\\', '/'),
                "search.fuel.min=2.0",
                "search.fuel.max=5.0");
        Files.writeString(config, content, StandardCharsets.UTF_8);
        return config;
    }

    private String output() {
        return outBuffer.toString(StandardCharsets.UTF_8);
    }

    private String errors() {
        return errBuffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void runWithValidConfigPrintsReportAndReturnsZero() throws IOException {
        Path config = createConfig(createDataFile());

        int exitCode = Main.run(new String[] {config.toString()}, out, err);

        assertEquals(0, exitCode);
        assertTrue(output().contains("Airline: Test Air"));
        assertEquals("", errors());
    }

    @Test
    void runWithMissingConfigFileReturnsOne() {
        Path missing = tempDir.resolve("missing.properties");

        int exitCode = Main.run(new String[] {missing.toString()}, out, err);

        assertEquals(1, exitCode);
        assertTrue(errors().startsWith("Error:"));
        assertEquals("", output());
    }

    @Test
    void runWithMissingDataFileReturnsOne() throws IOException {
        Path config = createConfig(tempDir.resolve("missing.txt"));

        int exitCode = Main.run(new String[] {config.toString()}, out, err);

        assertEquals(1, exitCode);
        assertTrue(errors().startsWith("Error:"));
    }

    @Test
    void runWithInvalidPathArgumentReturnsOne() {
        int exitCode = Main.run(new String[] {"bad\0path"}, out, err);

        assertEquals(1, exitCode);
        assertTrue(errors().startsWith("Error:"));
    }

    @Test
    void runWithoutArgumentsUsesDefaultConfig() {
        int exitCode = Main.run(new String[0], out, err);

        assertEquals(0, exitCode);
        assertTrue(output().startsWith("Airline:"));
        assertEquals("", errors());
    }

    @Test
    void mainRunsSuccessfullyWithValidConfig() throws IOException {
        Path config = createConfig(createDataFile());

        assertDoesNotThrow(() -> Main.main(new String[] {config.toString()}));
    }
}