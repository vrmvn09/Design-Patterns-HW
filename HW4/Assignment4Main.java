import java.util.ArrayList;
import java.util.List;

// Unified alert interface (Adapter target)
interface AlertService {
    void sendAlert(String level, String text);
}

// External service 1 — Chat system
class DiscordClient {
    public void sendMessage(String channel, String message) {
        System.out.println("DISCORD API: Sending to channel '" + channel + "'");
        System.out.println("Body: " + message);
        System.out.println("---------------------------------");
    }
}

// External service 2 — Old alert logger
class LegacyAlertLogger {
    public void recordIssue(int severityCode, String details, boolean urgent) {
        System.out.println("LEGACY LOGGER: Issue Code " + severityCode);
        System.out.println("Details: " + details);
        System.out.println("Requires immediate response: " + urgent);
        System.out.println("---------------------------------");
    }
}

// Adapter for DiscordClient
class DiscordAlertAdapter implements AlertService {
    private DiscordClient discord;
    private String channel;

    public DiscordAlertAdapter(DiscordClient discord, String channel) {
        this.discord = discord;
        this.channel = channel;
    }

    public void sendAlert(String level, String text) {
        String formattedMsg = "[" + level.toUpperCase() + "] " + text;
        discord.sendMessage(channel, formattedMsg);
    }
}

// Adapter for LegacyAlertLogger
class LegacyAlertAdapter implements AlertService {
    private LegacyAlertLogger legacy;

    public LegacyAlertAdapter(LegacyAlertLogger legacy) {
        this.legacy = legacy;
    }

    public void sendAlert(String level, String text) {
        int code = 0;
        boolean urgent = false;

        if ("CRITICAL".equals(level)) {
            code = 100;
            urgent = true;
        } else if ("WARNING".equals(level)) {
            code = 40;
        }

        legacy.recordIssue(code, text, urgent);
    }
}

// Bridge Implementor
interface ReportOutput {
    void open();
    String createTitle(String title);
    String createParagraph(String content);
    void close();
    String getOutput();
}

// HTML Implementation
class HtmlOutput implements ReportOutput {
    private StringBuilder builder = new StringBuilder();

    public void open() {
        builder.append("<html><body>\n");
    }

    public String createTitle(String title) {
        builder.append("<h2>").append(title).append("</h2>\n");
        return title;
    }

    public String createParagraph(String content) {
        builder.append("<p>").append(content).append("</p>\n");
        return content;
    }

    public void close() {
        builder.append("</body></html>\n");
    }

    public String getOutput() {
        return builder.toString();
    }
}

// Markdown Implementation
class MarkdownOutput implements ReportOutput {
    private StringBuilder builder = new StringBuilder();

    public void open() {}

    public String createTitle(String title) {
        builder.append("## ").append(title).append("\n");
        return title;
    }

    public String createParagraph(String content) {
        builder.append(content).append("\n\n");
        return content;
    }

    public void close() {}

    public String getOutput() {
        return builder.toString();
    }
}

// Abstraction
abstract class DocumentReport {
    protected ReportOutput output;

    public DocumentReport(ReportOutput output) {
        this.output = output;
    }

    public String build() {
        output.open();
        output.createTitle(getTitle());
        for (String part : getBody()) {
            output.createParagraph(part);
        }
        output.createParagraph(getFooter());
        output.close();
        return output.getOutput();
    }

    protected abstract String getTitle();
    protected abstract List<String> getBody();
    protected abstract String getFooter();
}

// Concrete Report 1
class DailyRevenueReport extends DocumentReport {
    public DailyRevenueReport(ReportOutput output) {
        super(output);
    }

    protected String getTitle() {
        return "Daily Revenue Summary";
    }

    protected List<String> getBody() {
        List<String> info = new ArrayList<>();
        info.add("Revenue Collected: $17,500");
        info.add("New Clients: 9");
        return info;
    }

    protected String getFooter() {
        return "Generated automatically by Analytics System";
    }
}

// Concrete Report 2
class YearlyPerformanceReport extends DocumentReport {
    public YearlyPerformanceReport(ReportOutput output) {
        super(output);
    }

    protected String getTitle() {
        return "Yearly Performance Report";
    }

    protected List<String> getBody() {
        List<String> body = new ArrayList<>();
        body.add("KPI 1: Achieved");
        body.add("KPI 2: Pending");
        body.add("KPI 3: Failed");
        return body;
    }

    protected String getFooter() {
        return "Confidential Report — Company Use Only";
    }
}

// Main demo
public class Assignment4Main {
    public static void main(String[] args) {

        System.out.println("======= ADAPTER PATTERN DEMO =======");
        System.out.println("Demonstrating communication between incompatible alert systems.\n");

        DiscordClient discordClient = new DiscordClient();
        LegacyAlertLogger legacySystem = new LegacyAlertLogger();

        AlertService discordAdapter = new DiscordAlertAdapter(discordClient, "server-alerts");
        AlertService legacyAdapter = new LegacyAlertAdapter(legacySystem);

        List<AlertService> alertSystems = new ArrayList<>();
        alertSystems.add(discordAdapter);
        alertSystems.add(legacyAdapter);

        System.out.println("Sending WARNING alert to all systems...");
        for (AlertService alert : alertSystems) {
            alert.sendAlert("WARNING", "Memory usage exceeded 75%");
        }

        System.out.println("\nSending CRITICAL alert to all systems...");
        for (AlertService alert : alertSystems) {
            alert.sendAlert("CRITICAL", "Backend API is down!");
        }

        System.out.println("\n======= BRIDGE PATTERN DEMO =======");
        System.out.println("Demonstrating separation of Report abstraction from Output format.\n");

        ReportOutput html = new HtmlOutput();
        ReportOutput md = new MarkdownOutput();

        DocumentReport dailyHtml = new DailyRevenueReport(html);
        System.out.println("--- Daily Revenue (HTML) ---");
        System.out.println(dailyHtml.build());

        DocumentReport dailyMd = new DailyRevenueReport(md);
        System.out.println("--- Daily Revenue (Markdown) ---");
        System.out.println(dailyMd.build());

        DocumentReport yearlyHtml = new YearlyPerformanceReport(new HtmlOutput());
        System.out.println("--- Yearly Performance (HTML) ---");
        System.out.println(yearlyHtml.build());

        System.out.println("Bridge pattern demo complete. No duplication across report types or formats.");
    }
}
