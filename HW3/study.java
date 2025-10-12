// Singleton
class DBConnector {
    private static DBConnector connection;

    private DBConnector() {
        System.out.println("Подключение к базе данных успешно выполнено.");
    }

    public static DBConnector getConnection() {
        if (connection == null) {
            connection = new DBConnector();
        }
        return connection;
    }

    public void insertOrder(String data) {
        System.out.println("Данные о заказе сохранены: " + data);
    }
}

// Builder + Prototype
class FoodOrder implements Cloneable {
    private String dish;
    private String beverage;
    private String delivery;

    public void setDish(String dish) { this.dish = dish; }
    public void setBeverage(String beverage) { this.beverage = beverage; }
    public void setDelivery(String delivery) { this.delivery = delivery; }

    public String getDetails() {
        return "Блюдо: " + dish + ", напиток: " + beverage + ", способ доставки: " + delivery;
    }

    @Override
    public FoodOrder clone() {
        try {
            return (FoodOrder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Ошибка при клонировании заказа");
        }
    }
}

// Builder для FoodOrder
class FoodOrderBuilder {
    private final FoodOrder foodOrder;

    public FoodOrderBuilder() {
        this.foodOrder = new FoodOrder();
    }

    public FoodOrderBuilder addDish(String dish) {
        foodOrder.setDish(dish);
        return this;
    }

    public FoodOrderBuilder addBeverage(String beverage) {
        foodOrder.setBeverage(beverage);
        return this;
    }

    public FoodOrderBuilder chooseDelivery(String delivery) {
        foodOrder.setDelivery(delivery);
        return this;
    }

    public FoodOrder build() {
        return foodOrder;
    }
}

// Демонстрация работы
public class study {
    public static void main(String[] args) {

        DBConnector db = DBConnector.getConnection();

        FoodOrder order = new FoodOrderBuilder()
                .addDish("Пицца")
                .addBeverage("Лимонад")
                .chooseDelivery("Самовывоз")
                .build();

        System.out.println("Новый заказ создан:");
        System.out.println(order.getDetails());

        db.insertOrder(order.getDetails());

        FoodOrder duplicatedOrder = order.clone();
        duplicatedOrder.setBeverage("Чай");

        System.out.println("\nСкопированный заказ (изменён напиток):");
        System.out.println(duplicatedOrder.getDetails());

        System.out.println("\nЭто разные объекты? " + (order != duplicatedOrder));

        DBConnector db2 = DBConnector.getConnection();
        System.out.println("db и db2 указывают на один объект? " + (db == db2));
    }
}
