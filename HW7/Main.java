import java.util.*;


class Request {
    String type;
    String student;

    public Request(String type, String student) {
        this.type = type;
        this.student = student;
    }
}

abstract class Handler {
    protected Handler next;

    public Handler setNext(Handler next) {
        this.next = next;
        return next;
    }

    public abstract void handle(Request request);
}

class AdvisorHandler extends Handler {
    public void handle(Request request) {
        if (request.type.equals("course_change")) {
            System.out.println("Advisor: Одобрил смену курса для " + request.student);
        } else {
            if (next != null) next.handle(request);
        }
    }
}

class DeanHandler extends Handler {
    public void handle(Request request) {
        if (request.type.equals("academic_break")) {
            System.out.println("Dean: Одобрил академ отпуск для " + request.student);
        } else {
            if (next != null) next.handle(request);
        }
    }
}

class RectorHandler extends Handler {
    public void handle(Request request) {
        if (request.type.equals("expulsion_cancel")) {
            System.out.println("Rector: Отменил отчисление студента " + request.student);
        } else {
            System.out.println("Заявка отклонена");
        }
    }
}


interface Command {
    void execute();
}

class ApproveDormCommand implements Command {
    private String student;

    public ApproveDormCommand(String student) {
        this.student = student;
    }

    public void execute() {
        System.out.println("Dorm: Заселён студент " + student);
    }
}

class IssueStudentCardCommand implements Command {
    private String student;

    public IssueStudentCardCommand(String student) {
        this.student = student;
    }

    public void execute() {
        System.out.println("Card Office: Студентский выдан " + student);
    }
}

class Invoker {
    private Queue<Command> queue = new LinkedList<>();

    public void add(Command command) {
        queue.add(command);
    }

    public void run() {
        while (!queue.isEmpty()) {
            queue.poll().execute();
        }
    }
}


public class Main {
    public static void main(String[] args) {
        Handler advisor = new AdvisorHandler();
        Handler dean = new DeanHandler();
        Handler rector = new RectorHandler();

        advisor.setNext(dean).setNext(rector);

        advisor.handle(new Request("course_change", "Arman"));
        advisor.handle(new Request("academic_break", "Sabina"));
        advisor.handle(new Request("expulsion_cancel", "Arkhat"));
        advisor.handle(new Request("unknown", "Random Student"));

        System.out.println();

        Invoker invoker = new Invoker();
        invoker.add(new ApproveDormCommand("Arman"));
        invoker.add(new IssueStudentCardCommand("Sabina"));
        invoker.run();
    }
}
