// -------- Factory Method --------
abstract class Book {
    public abstract void displayInfo();
}

class FictionBook extends Book {
    public void displayInfo() {
        System.out.println("Fiction Book created.");
    }
}

class ScienceBook extends Book {
    public void displayInfo() {
        System.out.println("Science Book created.");
    }
}

abstract class BookFactory {
    public abstract Book createBook();
}

class FictionBookFactory extends BookFactory {
    public Book createBook() {
        return new FictionBook();
    }
}

class ScienceBookFactory extends BookFactory {
    public Book createBook() {
        return new ScienceBook();
    }
}

// -------- Abstract Factory --------
interface Reader {
    void read();
}

class FictionReader implements Reader {
    public void read() {
        System.out.println("Reading Fiction Book...");
    }
}

class ScienceReader implements Reader {
    public void read() {
        System.out.println("Reading Science Book...");
    }
}

interface LibraryFactory {
    Book createBook();
    Reader createReader();
}

class FictionLibraryFactory implements LibraryFactory {
    public Book createBook() {
        return new FictionBook();
    }
    public Reader createReader() {
        return new FictionReader();
    }
}

class ScienceLibraryFactory implements LibraryFactory {
    public Book createBook() {
        return new ScienceBook();
    }
    public Reader createReader() {
        return new ScienceReader();
    }
}

// -------- Client --------
public class study {
    public static void main(String[] args) {
        // Factory Method usage
        BookFactory factory = new FictionBookFactory();
        Book book = factory.createBook();
        book.displayInfo();

        // Abstract Factory usage
        LibraryFactory sciLib = new ScienceLibraryFactory();
        Book sciBook = sciLib.createBook();
        Reader sciReader = sciLib.createReader();
        sciBook.displayInfo();
        sciReader.read();
    }
}