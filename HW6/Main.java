import java.util.*;

// ========== Flyweight: shared intrinsic state ==========
class ImageData {
    private final String name;
    private final String bytes; 

    public ImageData(String name) {
        this.name = name;
        this.bytes = "BINARY_DATA_OF_" + name + "_" + UUID.randomUUID().toString().substring(0, 8);
        System.out.println("[ImageData] created intrinsic data for: " + name);
    }

    public String getName() {
        return name;
    }

    public String getBytes() {
        return bytes;
    }
}

// Flyweight Factory возвращает общие ImageData
class ImageFlyweightFactory {
    private static final Map<String, ImageData> pool = new HashMap<>();

    public static synchronized ImageData getImageData(String name) {
        ImageData d = pool.get(name);
        if (d == null) {
            d = new ImageData(name);
            pool.put(name, d);
        }
        return d;
    }

    public static int getPoolSize() {
        return pool.size();
    }

    public static Set<String> getKeys() {
        return Collections.unmodifiableSet(pool.keySet());
    }
}

// ========== Image interface ==========
interface Image {
    void display(String user); 
    String getName();
}

// RealImage использует Flyweight
class RealImage implements Image {
    private final ImageData data;

    public RealImage(ImageData data) {
        this.data = data;
        System.out.println("[RealImage] instantiated for: " + data.getName());
    }

    @Override
    public void display(String user) {
        System.out.println("Displaying '" + data.getName() + "' to user '" + user + "' (data id: " + data.getBytes() + ")");
    }

    @Override
    public String getName() {
        return data.getName();
    }
}

// ========== Proxy pattern ==========
class ImageProxy implements Image {
    private final String name;
    private ImageData flyweightData;
    private RealImage realImage;

    private static final Map<String, RealImage> realImageCache = new HashMap<>();

    public ImageProxy(String name) {
        this.name = name;
    }

    private boolean authorized(String user) {
        if (name.startsWith("private_")) {
            return "admin".equalsIgnoreCase(user);
        }
        return true;
    }

    // Логирование доступа
    private void logAccess(String user) {
        System.out.println("[ImageProxy] user='" + user + "' requests '" + name + "'");
    }

    // Получаем или создаём RealImage через FlyweightFactory 
    private RealImage getRealImage() {
        if (realImage != null) return realImage;

        synchronized (realImageCache) {
            RealImage cached = realImageCache.get(name);
            if (cached != null) {
                realImage = cached;
                System.out.println("[ImageProxy] RealImage taken from cache for: " + name);
                return realImage;
            }
            flyweightData = ImageFlyweightFactory.getImageData(name);
            realImage = new RealImage(flyweightData);

            realImageCache.put(name, realImage);
            return realImage;
        }
    }

    @Override
    public void display(String user) {
        logAccess(user);

        // Контроль доступа
        if (!authorized(user)) {
            System.out.println("[ImageProxy] ACCESS DENIED for user '" + user + "' to image '" + name + "'");
            return;
        }

        // Lazy-load + caching через getRealImage()
        RealImage ri = getRealImage();
        ri.display(user);
    }

    @Override
    public String getName() {
        return name;
    }

    // Статистика кеша 
    public static int cacheSize() {
        synchronized (realImageCache) {
            return realImageCache.size();
        }
    }

    public static void clearCache() {
        synchronized (realImageCache) {
            realImageCache.clear();
        }
    }
}

// ========== Facade pattern: упрощённый интерфейс работы с медиасистемой ==========
class MediaFacade {

    public void viewImage(String imageName, String user) {
        ImageProxy proxy = new ImageProxy(imageName);
        proxy.display(user);
    }

    public void viewGallery(List<String> imageNames, String user) {
        System.out.println("\n[MediaFacade] Viewing gallery for user: " + user);
        for (String name : imageNames) {
            ImageProxy proxy = new ImageProxy(name);
            proxy.display(user);
        }
    }

    public void preloadImages(List<String> imageNames) {
        System.out.println("\n[MediaFacade] Preloading images: " + imageNames);
        for (String name : imageNames) {
            ImageFlyweightFactory.getImageData(name);
        }
    }

    // Показать статистику 
    public void showStats() {
        System.out.println("\n[MediaFacade] Stats:");
        System.out.println(" - Flyweight pool size: " + ImageFlyweightFactory.getPoolSize());
        System.out.println(" - RealImage cache size: " + ImageProxy.cacheSize());
        System.out.println(" - Flyweights: " + ImageFlyweightFactory.getKeys());
    }

    // Очистить кэш real images
    public void clearRealImageCache() {
        ImageProxy.clearCache();
        System.out.println("[MediaFacade] RealImage cache cleared.");
    }
}

// ========== Демонстрация в main ==========
public class Main {
    public static void main(String[] args) {
        MediaFacade facade = new MediaFacade();

        // Список изображений
        List<String> gallery = Arrays.asList(
                "img_sunset.jpg",
                "img_sunset.jpg",
                "img_portrait.jpg",
                "img_portrait.jpg",
                "img_portrait.jpg",
                "private_secret_event.png", // только admin
                "img_landscape.jpg",
                "img_sunset.jpg",
                "img_landscape.jpg"
        );

        // 1) ленивая загрузка через Proxy
        System.out.println("=== Демонстрация: ленивый режим (lazy) через Proxy ===");
        facade.viewGallery(gallery, "alice"); 
        facade.showStats();

        // 2) Попытка доступа к приватному 
        System.out.println("\n=== Попытка доступа к приватному изображению обычным пользователем ===");
        facade.viewImage("private_secret_event.png", "bob");

        // 3) Доступ к приватному как admin 
        System.out.println("\n=== Доступ admin к приватному изображению ===");
        facade.viewImage("private_secret_event.png", "admin");
        facade.showStats();

        // 4) Демонстрация предварительной загрузки
        List<String> preloadList = Arrays.asList("img_new.png", "img_portrait.jpg", "img_landscape.jpg");
        facade.preloadImages(preloadList);
        facade.showStats();

        // 5) Очистим кеш real-images 
        System.out.println("\n=== Очистка кеша RealImage и повторный просмотр ===");
        facade.clearRealImageCache();
        facade.viewGallery(Arrays.asList("img_sunset.jpg", "img_portrait.jpg"), "charlie");
        facade.showStats();

        // 6) Демонстрация экономии памяти 
        System.out.println("\n=== Вывод — Flyweight уменьшает количество тяжёлых объектов ===");
        System.out.println("Created flyweights: " + ImageFlyweightFactory.getPoolSize());
        System.out.println("RealImage cache entries: " + ImageProxy.cacheSize());
    }
}
