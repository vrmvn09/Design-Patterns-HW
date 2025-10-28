import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// ====== Notification component ======
interface Notification {
    String getContent();
    void send(Recipient recipient);
}

// ====== Concrete component ======
class SimpleNotification implements Notification {
    private final String content;

    public SimpleNotification(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void send(Recipient recipient) {
        System.out.println("Sending to " + recipient.getName() + ": " + getContent());
    }
}

// ====== Base Decorator ======
abstract class NotificationDecorator implements Notification {
    protected final Notification wrapped;

    public NotificationDecorator(Notification wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getContent() {
        return wrapped.getContent();
    }

    @Override
    public void send(Recipient recipient) {
        wrapped.send(recipient);
    }
}

// ====== Compression Decorator ======
class CompressionDecorator extends NotificationDecorator {

    public CompressionDecorator(Notification wrapped) {
        super(wrapped);
    }

    private String compress(String s) {
        StringBuilder sb = new StringBuilder();
        int n = s.length();
        for (int i = 0; i < n; ) {
            char c = s.charAt(i);
            int j = i + 1;
            while (j < n && s.charAt(j) == c) j++;
            int run = j - i;
            if (run >= 3) {
                sb.append(c).append('x').append(run);
            } else {
                for (int k = 0; k < run; k++) sb.append(c);
            }
            i = j;
        }
        return sb.toString();
    }

    @Override
    public String getContent() {
        return compress(wrapped.getContent());
    }

    @Override
    public void send(Recipient recipient) {
        System.out.println("[CompressionDecorator] Compressed content for " + recipient.getName());
        System.out.println("Sending to " + recipient.getName() + ": " + getContent());
    }
}

// ====== Encryption Decorator ======
class EncryptionDecorator extends NotificationDecorator {
    private final int shift;

    public EncryptionDecorator(Notification wrapped, int shift) {
        super(wrapped);
        this.shift = shift;
    }

    private String caesar(String s, int shift) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append((char)('A' + (c - 'A' + shift + 26) % 26));
            } else if (Character.isLowerCase(c)) {
                sb.append((char)('a' + (c - 'a' + shift + 26) % 26));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public String getContent() {
        return caesar(wrapped.getContent(), shift);
    }

    @Override
    public void send(Recipient recipient) {
        System.out.println("[EncryptionDecorator] Encrypted content for " + recipient.getName());
        System.out.println("Sending to " + recipient.getName() + ": " + getContent());
    }
}

// ====== Timestamp Decorator ======
class TimestampDecorator extends NotificationDecorator {
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TimestampDecorator(Notification wrapped) {
        super(wrapped);
    }

    @Override
    public String getContent() {
        String ts = LocalDateTime.now().format(fmt);
        return "[" + ts + "] " + wrapped.getContent();
    }

    @Override
    public void send(Recipient recipient) {
        System.out.println("[TimestampDecorator] Adding timestamp for " + recipient.getName());
        System.out.println("Sending to " + recipient.getName() + ": " + getContent());
    }
}

// ====== Recipient component ======
interface Recipient {
    String getName();
    void receive(Notification notification);
}

// ====== Leaf (User) ======
class User implements Recipient {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void receive(Notification notification) {
        notification.send(this);
    }
}

// ====== Composite (Group) ======
class Group implements Recipient {
    private final String name;
    private final List<Recipient> children = new ArrayList<>();

    public Group(String name) {
        this.name = name;
    }

    public void add(Recipient r) {
        children.add(r);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void receive(Notification notification) {
        System.out.println("[Group] Sending to group '" + name + "' with " + children.size() + " members.");
        for (Recipient r : children) {
            r.receive(notification);
        }
    }
}

// ====== Main (Demo) ======
public class Main {
    public static void main(String[] args) {
        Notification base = new SimpleNotification("Hello team!!! This is an important message!!!!");

        // Decorate dynamically at runtime
        Notification decorated = new TimestampDecorator(
                                    new EncryptionDecorator(
                                        new CompressionDecorator(base), 3));

        // Create users and groups
        User alice = new User("Alice");
        User bob = new User("Bob");
        User carol = new User("Carol");

        Group backend = new Group("Backend Team");
        backend.add(alice);
        backend.add(bob);

        Group all = new Group("All Employees");
        all.add(backend);
        all.add(carol);

        // Send notification
        all.receive(decorated);
    }
}
