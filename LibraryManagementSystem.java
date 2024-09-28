import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// LibraryItem.java (Abstract Class for Library Items)
abstract class LibraryItem implements Serializable {
    private String title;
    private String author;
    private String category;
    private boolean isCheckedOut;

    public LibraryItem(String title, String author, String category) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.isCheckedOut = false;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public boolean isCheckedOut() {
        return isCheckedOut;
    }

    public void checkOut() {
        isCheckedOut = true;
    }

    public void returnItem() {
        isCheckedOut = false;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author + ", Category: " + category;
    }
}

// Book.java (Subclass of LibraryItem)
class Book extends LibraryItem {
    public Book(String title, String author, String category) {
        super(title, author, category);
    }
}

// Magazine.java (Subclass of LibraryItem)
class Magazine extends LibraryItem {
    public Magazine(String title, String author, String category) {
        super(title, author, category);
    }
}

// DVD.java (Subclass of LibraryItem)
class DVD extends LibraryItem {
    public DVD(String title, String author, String category) {
        super(title, author, category);
    }
}

// User.java (Abstract Class for Users)
abstract class User {
    protected String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void viewItems(Library library);
}

// Patron.java (Subclass of User)
class Patron extends User {
    public Patron(String name) {
        super(name);
    }

    @Override
    public void viewItems(Library library) {
        System.out.println("Available items in the library:");
        library.listItems();
    }

    public void checkOutItem(Library library, String title) {
        if (library.checkOutItem(title)) {
            System.out.println("Item checked out successfully.");
        } else {
            System.out.println("Item not available or already checked out.");
        }
    }

    public void returnItem(Library library, String title) {
        if (library.returnItem(title)) {
            System.out.println("Item returned successfully.");
        } else {
            System.out.println("Error returning item.");
        }
    }
}

// Librarian.java (Subclass of User)
class Librarian extends User {
    public Librarian(String name) {
        super(name);
    }

    @Override
    public void viewItems(Library library) {
        System.out.println("Available items in the library:");
        library.listItems();
    }

    public void addItem(Library library, LibraryItem item) {
        library.addItem(item);
        System.out.println("Item added successfully.");
    }
}

// Library.java (Manage Items and Persistence)
class Library {
    private List<LibraryItem> items;

    public Library() {
        items = new ArrayList<>();
        loadItems();
    }

    public void addItem(LibraryItem item) {
        items.add(item);
        saveItems();
    }

    public void listItems() {
        for (LibraryItem item : items) {
            if (!item.isCheckedOut()) {
                System.out.println(item);
            }
        }
    }

    public boolean checkOutItem(String title) {
        for (LibraryItem item : items) {
            if (item.getTitle().equalsIgnoreCase(title) && !item.isCheckedOut()) {
                item.checkOut();
                saveItems();
                return true;
            }
        }
        return false;
    }

    public boolean returnItem(String title) {
        for (LibraryItem item : items) {
            if (item.getTitle().equalsIgnoreCase(title) && item.isCheckedOut()) {
                item.returnItem();
                saveItems();
                return true;
            }
        }
        return false;
    }

    private void saveItems() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("library_items.dat"))) {
            oos.writeObject(items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadItems() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("library_items.dat"))) {
            items = (List<LibraryItem>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            items = new ArrayList<>();
        }
    }
}

// (Main Class)
public class LibraryManagementSystem {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);

        Librarian librarian = new Librarian("Alice");
        Patron patron = new Patron("Bob");

        boolean exit = false;
        while (!exit) {
            System.out.println("1. Add Item (Librarian)");
            System.out.println("2. View Items (Patron)");
            System.out.println("3. Check Out Item (Patron)");
            System.out.println("4. Return Item (Patron)");
            System.out.println("5. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("Enter item type (Book, Magazine, DVD):");
                    String type = scanner.nextLine();
                    System.out.println("Enter title:");
                    String title = scanner.nextLine();
                    System.out.println("Enter author:");
                    String author = scanner.nextLine();
                    System.out.println("Enter category:");
                    String category = scanner.nextLine();

                    LibraryItem item = null;
                    if (type.equalsIgnoreCase("Book")) {
                        item = new Book(title, author, category);
                    } else if (type.equalsIgnoreCase("Magazine")) {
                        item = new Magazine(title, author, category);
                    } else if (type.equalsIgnoreCase("DVD")) {
                        item = new DVD(title, author, category);
                    }

                    if (item != null) {
                        librarian.addItem(library, item);
                    }
                    break;

                case 2:
                    patron.viewItems(library);
                    break;

                case 3:
                    System.out.println("Enter title of item to check out:");
                    String checkoutTitle = scanner.nextLine();
                    patron.checkOutItem(library, checkoutTitle);
                    break;

                case 4:
                    System.out.println("Enter title of item to return:");
                    String returnTitle = scanner.nextLine();
                    patron.returnItem(library, returnTitle);
                    break;

                case 5:
                    exit = true;
                    break;

                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }

        scanner.close();
    }
}