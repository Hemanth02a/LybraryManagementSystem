import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

// Book class to store book details
class Book {
    private String bookId;
    private String title;
    private String author;
    private String isbn;
    private int publicationYear;
    private int availableCopies;
    private int totalCopies;

    // Constructors
    public Book(String bookId, String title, String author, String isbn, int publicationYear, int totalCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public Book(String bookId, String title, String author) {
        this(bookId, title, author, "Unknown", LocalDate.now().getYear(), 1);
    }

    // Getters and Setters
    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int copies) { this.availableCopies = copies; }
    public int getTotalCopies() { return totalCopies; }

    @Override
    public String toString() {
        return String.format("Book: %s by %s (ID: %s, ISBN: %s, Available: %d/%d)",
                title, author, bookId, isbn, availableCopies, totalCopies);
    }
}

// Member class to store member details
class Member {
    private String memberId;
    private String name;
    private String email;
    private String phoneNumber;
    private ArrayList<Transaction> borrowedBooks;
    private final int maxBooksAllowed = 3;

    // Constructors
    public Member(String memberId, String name, String email, String phoneNumber) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.borrowedBooks = new ArrayList<>();
    }

    public Member(String memberId, String name) {
        this(memberId, name, "unknown@email.com", "0000000000");
    }

    // Getters and Setters
    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public ArrayList<Transaction> getBorrowedBooks() { return borrowedBooks; }
    public int getMaxBooksAllowed() { return maxBooksAllowed; }

    @Override
    public String toString() {
        return String.format("Member: %s (ID: %s, Email: %s, Phone: %s, Books Borrowed: %d)",
                name, memberId, email, phoneNumber, borrowedBooks.size());
    }
}

// Transaction class to track book borrowing
class Transaction {
    private Book book;
    private Member member;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Transaction(Book book, Member member) {
        this.book = book;
        this.member = member;
        this.issueDate = LocalDate.now();
        this.dueDate = issueDate.plusDays(14);
        this.returnDate = null;
    }

    public Book getBook() { return book; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate date) { this.returnDate = date; }

    public long calculateFine() {
        if (returnDate == null && LocalDate.now().isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, LocalDate.now()) * 5; // ₹5 per day
        }
        return 0;
    }
}

// LibraryManager class to handle operations
class LibraryManager {
    private ArrayList<Book> books;
    private ArrayList<Member> members;
    private HashMap<String, Book> bookMap;
    private HashMap<String, Member> memberMap;
    private ArrayList<Transaction> transactions;

    public LibraryManager() {
        books = new ArrayList<>();
        members = new ArrayList<>();
        bookMap = new HashMap<>();
        memberMap = new HashMap<>();
        transactions = new ArrayList<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Adding sample books
        books.add(new Book("B001", "Effective Java", "Joshua Bloch", "978-0134685991", 2018, 3));
        books.add(new Book("B002", "Clean Code", "Robert Martin", "978-0132350884", 2008, 2));
        books.add(new Book("B003", "To Kill a Mockingbird", "Harper Lee", "978-0446310789", 1960, 4));
        books.add(new Book("B004", "1984", "George Orwell", "978-0451524935", 1949, 3));
        books.add(new Book("B005", "The Alchemist", "Paulo Coelho", "978-0062315007", 1988, 2));
        books.add(new Book("B006", "Pride and Prejudice", "Jane Austen", "978-0141439518", 1813, 2));
        books.add(new Book("B007", "The Catcher in the Rye", "J.D. Salinger", "978-0316769488", 1951, 3));
        books.add(new Book("B008", "Lord of the Rings", "J.R.R. Tolkien", "978-0544003415", 1954, 2));
        books.add(new Book("B009", "Java Concurrency", "Brian Goetz", "978-0321349606", 2006, 2));
        books.add(new Book("B010", "Animal Farm", "George Orwell", "978-0451526342", 1945, 3));

        // Adding to bookMap
        for (Book book : books) {
            bookMap.put(book.getBookId(), book);
        }

        // Adding sample members
        members.add(new Member("M001", "John Smith", "john@email.com", "1234567890"));
        members.add(new Member("M002", "Jane Doe", "jane@email.com", "0987654321"));
        members.add(new Member("M003", "Alice Johnson", "alice@email.com", "5555555555"));
        members.add(new Member("M004", "Bob Wilson", "bob@email.com", "4444444444"));
        members.add(new Member("M005", "Emma Brown", "emma@email.com", "3333333333"));

        // Adding to memberMap
        for (Member member : members) {
            memberMap.put(member.getMemberId(), member);
        }
    }

    public void addBook(Book book) throws Exception {
        if (bookMap.containsKey(book.getBookId())) {
            throw new Exception("Book with ID " + book.getBookId() + " already exists");
        }
        books.add(book);
        bookMap.put(book.getBookId(), book);
    }

    public void addMember(Member member) throws Exception {
        if (memberMap.containsKey(member.getMemberId())) {
            throw new Exception("Member with ID " + member.getMemberId() + " already exists");
        }
        members.add(member);
        memberMap.put(member.getMemberId(), member);
    }

    public void issueBook(String bookId, String memberId) throws Exception {
        Book book = bookMap.get(bookId);
        Member member = memberMap.get(memberId);

        if (book == null) throw new Exception("Book not found");
        if (member == null) throw new Exception("Member not found");
        if (book.getAvailableCopies() <= 0) throw new Exception("No copies available");
        if (member.getBorrowedBooks().size() >= member.getMaxBooksAllowed()) {
            throw new Exception("Member has reached maximum borrowing limit");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        Transaction transaction = new Transaction(book, member);
        member.getBorrowedBooks().add(transaction);
        transactions.add(transaction);

        System.out.println("Book issued successfully!");
        System.out.println("Member: " + member.getName() + " (" + member.getEmail() + ")");
        System.out.println("Book: \"" + book.getTitle() + "\" by " + book.getAuthor());
        System.out.println("Issue Date: " + transaction.getIssueDate());
        System.out.println("Due Date: " + transaction.getDueDate());
        System.out.println("Remaining copies: " + book.getAvailableCopies());
    }

    public void returnBook(String bookId, String memberId) throws Exception {
        Book book = bookMap.get(bookId);
        Member member = memberMap.get(memberId);

        if (book == null) throw new Exception("Book not found");
        if (member == null) throw new Exception("Member not found");

        Transaction transaction = null;
        for (Transaction t : member.getBorrowedBooks()) {
            if (t.getBook().getBookId().equals(bookId)) {
                transaction = t;
                break;
            }
        }

        if (transaction == null) throw new Exception("This book was not borrowed by this member");

        transaction.setReturnDate(LocalDate.now());
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        long fine = transaction.calculateFine();
        member.getBorrowedBooks().remove(transaction);

        System.out.println("Book returned successfully!");
        if (fine > 0) {
            System.out.println("Fine incurred: ₹" + fine);
        }
    }

    public void searchBooks(String query) {
        System.out.println("\nSearch Results:");
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                book.getIsbn().toLowerCase().contains(query.toLowerCase())) {
                System.out.println(book);
            }
        }
    }

    public void displayAvailableBooks() {
        System.out.println("\nAvailable Books:");
        for (Book book : books) {
            if (book.getAvailableCopies() > 0) {
                System.out.println(book);
            }
        }
    }

    public void displayMemberDetails(String memberId) throws Exception {
        Member member = memberMap.get(memberId);
        if (member == null) throw new Exception("Member not found");

        System.out.println("\nMember Details:");
        System.out.println(member);
        System.out.println("Borrowed Books:");
        for (Transaction transaction : member.getBorrowedBooks()) {
            System.out.println("- " + transaction.getBook().getTitle() +
                    " (Due: " + transaction.getDueDate() +
                    ", Fine: ₹" + transaction.calculateFine() + ")");
        }
    }

    public void generateOverdueReport() {
        System.out.println("\nOverdue Books Report:");
        for (Transaction transaction : transactions) {
            if (transaction.getReturnDate() == null && LocalDate.now().isAfter(transaction.getDueDate())) {
                System.out.println("Member: " + transaction.getMember().getName() +
                        ", Book: " + transaction.getBook().getTitle() +
                        ", Overdue by: " + ChronoUnit.DAYS.between(transaction.getDueDate(), LocalDate.now()) + " days" +
                        ", Fine: ₹" + transaction.calculateFine());
            }
        }
    }
}

// Main class with console interface
public class LibraryManagementSystem {
    public static void main(String[] args) {
        LibraryManager library = new LibraryManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== LIBRARY MANAGEMENT SYSTEM ===");
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search Books");
            System.out.println("6. View Member Details");
            System.out.println("7. Display Available Books");
            System.out.println("8. Overdue Report");
            System.out.println("9. Exit");
            System.out.print("\nEnter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter Book ID: ");
                        String bookId = scanner.nextLine();
                        System.out.print("Enter Title: ");
                        String title = scanner.nextLine();
                        System.out.print("Enter Author: ");
                        String author = scanner.nextLine();
                        System.out.print("Enter ISBN: ");
                        String isbn = scanner.nextLine();
                        System.out.print("Enter Publication Year: ");
                        int year = scanner.nextInt();
                        System.out.print("Enter Total Copies: ");
                        int copies = scanner.nextInt();
                        scanner.nextLine();
                        library.addBook(new Book(bookId, title, author, isbn, year, copies));
                        System.out.println("Book added successfully!");
                        break;

                    case 2:
                        System.out.print("Enter Member ID: ");
                        String memberId = scanner.nextLine();
                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter Phone Number: ");
                        String phone = scanner.nextLine();
                        library.addMember(new Member(memberId, name, email, phone));
                        System.out.println("Member added successfully!");
                        break;

                    case 3:
                        System.out.println("\n=== ISSUE BOOK ===");
                        System.out.print("Enter Member ID: ");
                        memberId = scanner.nextLine();
                        System.out.print("Enter Book ID: ");
                        bookId = scanner.nextLine();
                        library.issueBook(bookId, memberId);
                        break;

                    case 4:
                        System.out.println("\n=== RETURN BOOK ===");
                        System.out.print("Enter Member ID: ");
                        memberId = scanner.nextLine();
                        System.out.print("Enter Book ID: ");
                        bookId = scanner.nextLine();
                        library.returnBook(bookId, memberId);
                        break;

                    case 5:
                        System.out.print("Enter search query (title/author/ISBN): ");
                        String query = scanner.nextLine();
                        library.searchBooks(query);
                        break;

                    case 6:
                        System.out.print("Enter Member ID: ");
                        memberId = scanner.nextLine();
                        library.displayMemberDetails(memberId);
                        break;

                    case 7:
                        library.displayAvailableBooks();
                        break;

                    case 8:
                        library.generateOverdueReport();
                        break;

                    case 9:
                        System.out.println("Thank you for using Library Management System!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine(); // Clear scanner buffer
            }
        }
    }
}
