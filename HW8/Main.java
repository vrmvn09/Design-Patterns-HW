import java.util.*;

// ==============================
// Task 1 — Iterator Pattern
// ==============================

// --- Iterator Interface ---
interface Iterator<T> {
    boolean hasNext();
    T next();
}

// --- Aggregate Interface ---
interface Aggregate<T> {
    Iterator<T> createIterator();
}

// --- Student Class ---
class Student {
    private String name;
    private String id;

    public Student(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public void displayInfo() {
        System.out.println("Student: " + name + " | ID: " + id);
    }

    public String getName() {
        return name;
    }
}

// --- StudentCollection Class ---
class StudentCollection implements Aggregate<Student> {
    private Student[] students;
    private int index = 0;

    public StudentCollection(int size) {
        students = new Student[size];
    }

    public void addStudent(Student student) {
        if (index < students.length) {
            students[index++] = student;
        }
    }

    @Override
    public Iterator<Student> createIterator() {
        return new StudentIterator(students);
    }
}

// --- StudentIterator Class ---
class StudentIterator implements Iterator<Student> {
    private Student[] students;
    private int position = 0;

    public StudentIterator(Student[] students) {
        this.students = students;
    }

    @Override
    public boolean hasNext() {
        return position < students.length && students[position] != null;
    }

    @Override
    public Student next() {
        return students[position++];
    }
}

// ==============================
// Task 2 — Mediator Pattern
// ==============================

// --- Mediator Interface ---
interface ChatMediator {
    void sendMessage(String message, User sender);
    void addUser(User user);
}

// --- Concrete Mediator ---
class CourseChatMediator implements ChatMediator {
    private List<User> users = new ArrayList<>();

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public void sendMessage(String message, User sender) {
        for (User user : users) {
            if (user != sender) {
                user.receive(message, sender.getName());
            }
        }
    }
}

// --- Abstract User Class ---
abstract class User {
    protected ChatMediator mediator;
    protected String name;

    public User(ChatMediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void send(String message);
    public abstract void receive(String message, String senderName);
}

// --- Concrete User Classes ---
class StudentUser extends User {
    public StudentUser(ChatMediator mediator, String name) {
        super(mediator, name);
    }

    @Override
    public void send(String message) {
        System.out.println(name + " (Student) sends: " + message);
        mediator.sendMessage(message, this);
    }

    @Override
    public void receive(String message, String senderName) {
        System.out.println(name + " (Student) received from " + senderName + ": " + message);
    }
}

class InstructorUser extends User {
    public InstructorUser(ChatMediator mediator, String name) {
        super(mediator, name);
    }

    @Override
    public void send(String message) {
        System.out.println(name + " (Instructor) sends: " + message);
        mediator.sendMessage(message, this);
    }

    @Override
    public void receive(String message, String senderName) {
        System.out.println(name + " (Instructor) received from " + senderName + ": " + message);
    }
}

// ==============================
// Main Class
// ==============================

public class Main {
    public static void main(String[] args) {

        System.out.println("===== Task 1: Iterator Pattern =====");
        StudentCollection collection = new StudentCollection(3);
        collection.addStudent(new Student("Alice", "S101"));
        collection.addStudent(new Student("Bob", "S102"));
        collection.addStudent(new Student("Charlie", "S103"));

        Iterator<Student> iterator = collection.createIterator();

        System.out.println("Iterating through students:");
        while (iterator.hasNext()) {
            Student s = iterator.next();
            s.displayInfo();
        }

        System.out.println("\n===== Task 2: Mediator Pattern =====");
        ChatMediator mediator = new CourseChatMediator();

        User instructor = new InstructorUser(mediator, "Dr. Smith");
        User student1 = new StudentUser(mediator, "Alice");
        User student2 = new StudentUser(mediator, "Bob");

        mediator.addUser(instructor);
        mediator.addUser(student1);
        mediator.addUser(student2);

        student1.send("Hello, everyone!");
        instructor.send("Hi Alice, welcome to the course!");
    }
}
