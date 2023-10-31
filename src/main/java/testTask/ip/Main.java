package testTask.ip;

import io.javalin.Javalin;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(8080);

        app.get("/", ctx -> {
            String htmlContent = readHtmlContent("index.html");
            ctx.html(htmlContent);
        });

        app.post("/scan", ctx -> {
            String ipAddressRange = ctx.formParam("ipAddressRange");
            int threadCount = Integer.parseInt(ctx.formParam("threadCount"));

            IPScanner ipScanner = new IPScanner(ipAddressRange, threadCount);
            ipScanner.scan();

            ctx.result("Scan initiated");
        });
    }

    private static String readHtmlContent(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get("src/main/resources/" + filePath)));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error reading file";
        }
    }
}