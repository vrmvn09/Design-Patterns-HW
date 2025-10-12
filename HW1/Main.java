// SRP — Single Responsibility Principle

/*
    * каждый класс выполняет только одну задачу
    * сохранение данных фильма
*/
class Movie {
    private String title;
    private double price;

    public Movie(String title, double price) {
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }
}

// OCP — Open/Closed Principle
/*
   * классы должны быть открыты для расширения, но закрыты для изменения
   * например можно добавить оплату через каспи
*/

interface PaymentMethod {
    void pay(double amount);
}

class CreditCardPayment implements PaymentMethod {
    @Override
    public void pay(double amount) {
        System.out.println("Оплата " + amount + " через банковскую карту");
    }
}

class PayPalPayment implements PaymentMethod {
    @Override
    public void pay(double amount) {
        System.out.println("Оплата " + amount + " через PayPal");
    }
}

// LSP — Liskov Substitution Principle
/*
    объекты подклассов должны заменять объекты базового класса
    без нарушения работы программы
*/
class BrokenMovie extends Movie {
    public BrokenMovie(String title) {
        super(title, -1);
    }

    @Override
    public double getPrice() {
        throw new UnsupportedOperationException("У этого фильма нельзя получить цену!");
    }
}

class FreeMovie extends Movie {
    public FreeMovie(String title) {
        super(title, 0);
    }

    @Override
    public double getPrice() {
        return super.getPrice();
    }
}

// ISP — Interface Segregation Principle
/*
    * лучше много маленьких интерфейсов, чем один большой, чтобы избежать ненужных зависимостей
    и ошибок
    * интерфейс Notifier разделен на разные реализации
*/

interface Notifier {
    void notifyUser(String message);
}

class EmailNotifier implements Notifier {
    @Override
    public void notifyUser(String message) {
        System.out.println("Отправлено письмо: " + message);
    }
}

class SMSNotifier implements Notifier {
    @Override
    public void notifyUser(String message) {
        System.out.println("Отправлено SMS: " + message);
    }
}

// DIP — Dependency Inversion Principle

/*
    * модули верхнего уровня не должны зависеть от модулей нижнего уровня
    * оба должны зависеть от абстракций
    * тут использую интерфейсы PaymentMethod и Notifier а не SMSNotifier(EmailNotifier), CreditCardPayment(PayPalPayment)
    *
*/
class RentalService {
    private PaymentMethod paymentMethod;
    private Notifier notifier;

    public RentalService(PaymentMethod paymentMethod, Notifier notifier) {
        this.paymentMethod = paymentMethod;
        this.notifier = notifier;
    }

    public void rentMovie(Movie movie) {
        paymentMethod.pay(movie.getPrice());
        notifier.notifyUser("Вы арендовали фильм: " + movie.getTitle());
    }
}

public class Main {
    public static void main(String[] args) {
        Movie movie = new Movie("Inception", 5.99);

        PaymentMethod payment = new CreditCardPayment();
        Notifier notifier = new EmailNotifier();

        RentalService rentalService = new RentalService(payment, notifier);
        rentalService.rentMovie(movie);

        // Можно легко поменять способ оплаты и уведомлений без изменения класса RentalService
        RentalService rentalService2 = new RentalService(new PayPalPayment(), new SMSNotifier());
        rentalService2.rentMovie(movie);

        // Проверка LSP
        Movie free = new FreeMovie("Educational Film");
        System.out.println("Цена бесплатного фильма: " + free.getPrice());

        //крашнет код из-за нарушения LSP (нарушение контракта базового класса)
        // Movie broken = new BrokenMovie("Bad Film");
        // System.out.println(broken.getPrice());
    }
}
