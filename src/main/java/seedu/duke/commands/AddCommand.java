package seedu.duke.commands;

import seedu.duke.author.Author;
import seedu.duke.book.Book;
import seedu.duke.book.BookList;
import seedu.duke.category.Category;
import seedu.duke.category.CategoryList;
import seedu.duke.category.CategoryParser;
import seedu.duke.exception.QuotesifyException;
import seedu.duke.lists.ListManager;
import seedu.duke.quote.Quote;
import seedu.duke.quote.QuoteList;
import seedu.duke.quote.QuoteParser;
import seedu.duke.rating.Rating;
import seedu.duke.rating.RatingList;
import seedu.duke.rating.RatingParser;
import seedu.duke.todo.ToDo;
import seedu.duke.todo.ToDoList;
import seedu.duke.ui.TextUi;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.ArrayList;

public class AddCommand extends Command {
    private String type;
    private String information;

    public static Logger addLogger = Logger.getLogger("QuotesifyLogger");

    public AddCommand(String arguments) {
        String[] details = arguments.split(" ", 2);

        // if user did not provide arguments, let details[1] be empty string
        if (details.length == 1) {
            details = new String[]{details[0], ""};
        }
        type = details[0];
        information = details[1];
    }

    public void execute(TextUi ui) {
        switch (type) {
        case TAG_BOOK:
            addLogger.log(Level.INFO, "going to add book to booklist");
            BookList books = (BookList) ListManager.getList(ListManager.BOOK_LIST);
            addBook(books, ui);
            addLogger.log(Level.INFO, "added book to booklist");
            break;
        case TAG_QUOTE:
            addLogger.log(Level.INFO, "going to add quote to quote list");
            QuoteList quotes = (QuoteList) ListManager.getList(ListManager.QUOTE_LIST);
            addQuote(quotes, ui);
            break;
        case TAG_CATEGORY:
            addLogger.log(Level.INFO, "going to add category to book/quote");
            CategoryList categories = (CategoryList) ListManager.getList(ListManager.CATEGORY_LIST);
            addCategoryToBookOrQuote(categories, ui);
            break;
        case TAG_RATING:
            addLogger.log(Level.INFO, "going to add rating to book");
            RatingList ratings = (RatingList) ListManager.getList(ListManager.RATING_LIST);
            addRating(ratings, ui);
            addLogger.log(Level.INFO, "rating of book has completed");
            break;
        case TAG_TODO:
            ToDoList toDos = (ToDoList) ListManager.getList(ListManager.TODO_LIST);
            ToDo newToDo = addToDo(toDos);
            ui.printAddToDo(newToDo);
            break;
        default:
        }
    }

    private void addBook(BookList books, TextUi ui) {
        try {
            String[] titleAndAuthor = information.split(Command.FLAG_AUTHOR, 2);
            // if user did not provide author, let titleAndAuthor[1] be empty string
            if (titleAndAuthor.length == 1) {
                titleAndAuthor = new String[]{titleAndAuthor[0], ""};
            }

            String title = titleAndAuthor[0].trim();
            String authorName = titleAndAuthor[1].trim();
            if (authorName.isEmpty()) {
                throw new QuotesifyException(ERROR_NO_AUTHOR_NAME);
            }

            ensureNoSimilarBooks(books, title, authorName);
            Book newBook = createNewBook(title, authorName);
            books.add(newBook);
            ui.printAddBook(newBook);

        } catch (QuotesifyException e) {
            ui.printErrorMessage(e.getMessage());
            addLogger.log(Level.INFO, "add book to booklist failed");
        }
    }

    private void ensureNoSimilarBooks(BookList books, String title, String authorName) throws QuotesifyException {
        ArrayList<Book> similarBooks = books.find(title, authorName);
        if (!similarBooks.isEmpty()) {
            throw new QuotesifyException(ERROR_BOOK_ALREADY_EXISTS);
        }
    }

    public Book createNewBook(String title, String authorName) {
        Author author = new Author(authorName);
        Book newBook = new Book(author, title);

        return newBook;
    }

    private void addQuote(QuoteList quotes, TextUi ui) {
        try {
            Quote quote = QuoteParser.parseAddParameters(information);
            quotes.add(quote);
            ui.printAllQuotes(quotes);
            addLogger.log(Level.INFO, "add quote to quote list success");
        } catch (QuotesifyException e) {
            System.out.println(e.getMessage());
            addLogger.log(Level.INFO, "add quote to quote list failed");
            addLogger.log(Level.WARNING, e.getMessage());
        }
    }

    private void addCategoryToBookOrQuote(CategoryList categories, TextUi ui) {
        String[] tokens = information.split(" ");
        String[] parameters = CategoryParser.getRequiredParameters(tokens);
        if (CategoryParser.isValidParameters(parameters)) {
            executeParameters(categories, parameters, ui);
        }
    }

    private void executeParameters(CategoryList categories, String[] parameters, TextUi ui) {
        try {
            String categoryName = parameters[0];
            assert !categoryName.isEmpty() : "category name should not be empty";

            addCategoryToList(categories, categoryName);
            Category category = categories.getCategoryByName(categoryName);

            String bookTitle = parameters[1];
            String quoteNum = parameters[2];

            addCategoryToBook(category, bookTitle, ui);
            addCategoryToQuote(category, quoteNum, ui);
        } catch (QuotesifyException e) {
            addLogger.log(Level.WARNING, e.getMessage());
            ui.printErrorMessage(e.getMessage());
        }
    }

    private void addCategoryToList(CategoryList categories, String categoryName) {
        if (!categories.isExistingCategory(categoryName)) {
            categories.add(new Category(categoryName));
        }
    }

    private void addCategoryToBook(Category category, String bookTitle, TextUi ui) {
        // ignore this action if user did not provide book title
        if (bookTitle.isEmpty()) {
            return;
        }

        BookList bookList = (BookList) ListManager.getList(ListManager.BOOK_LIST);
        try {
            Book book = bookList.findByTitle(bookTitle);
            book.setCategory(category);
            addLogger.log(Level.INFO, "add category to book success");
            ui.printAddCategoryToBook(bookTitle, category.getCategoryName());
        } catch (NullPointerException e) {
            addLogger.log(Level.WARNING, ERROR_NO_BOOK_FOUND);
            ui.printErrorMessage(ERROR_NO_BOOK_FOUND);
        }
    }

    private void addCategoryToQuote(Category category, String index, TextUi ui) {
        // ignore this action if user did not provide quote number
        if (index.isEmpty()) {
            return;
        }

        QuoteList quoteList = (QuoteList) ListManager.getList(ListManager.QUOTE_LIST);
        ArrayList<Quote> quotes = quoteList.getList();
        try {
            int quoteNum = Integer.parseInt(index) - 1;
            Quote quote = quotes.get(quoteNum);
            quote.setCategory(category);
            ui.printAddCategoryToQuote(quotes.get(quoteNum).getQuote(), category.getCategoryName());
            addLogger.log(Level.INFO, "add category to quote success");
        } catch (IndexOutOfBoundsException e) {
            addLogger.log(Level.WARNING, ERROR_NO_QUOTE_FOUND);
            ui.printErrorMessage(ERROR_NO_QUOTE_FOUND);
        } catch (NumberFormatException e) {
            addLogger.log(Level.WARNING, ERROR_INVALID_QUOTE_NUM);
            ui.printErrorMessage(ERROR_INVALID_QUOTE_NUM);
        }
    }

    private void addRating(RatingList ratings, TextUi ui) {
        String[] ratingDetails = information.split(" ", 2);
        String titleOfBookToRate = ratingDetails[1].trim();

        int ratingScore = RatingParser.checkFormatOfRatingValue(ratingDetails[0]);
        if (ratingScore == 0) {
            return;
        }
        boolean isValid = RatingParser.checkRangeOfRatingValue(ratingScore);
        if (isValid && !isRated(ratings, titleOfBookToRate) && isExistingBook(titleOfBookToRate)) {
            ratings.add(new Rating(ratingScore, titleOfBookToRate));
            ui.printAddRatingToBook(ratingScore, titleOfBookToRate);
        }
    }

    private boolean isExistingBook(String titleOfBookToRate) {
        BookList bookList = (BookList) ListManager.getList(ListManager.BOOK_LIST);
        ArrayList<Book> existingBooks = bookList.getList();
        boolean doesExist = false;
        assert existingBooks.size() != 0 : "List of books should not be empty";
        for (Book existingBook : existingBooks) {
            if (existingBook.getTitle().equals(titleOfBookToRate)) {
                doesExist = true;
                break;
            }
        }
        if (!doesExist) {
            addLogger.log(Level.INFO, "book does not exist");
            System.out.println(ERROR_BOOK_TO_RATE_NOT_FOUND);
        }
        return doesExist;
    }

    private boolean isRated(RatingList ratings, String titleOfBookToRate) {
        boolean isRated = false;
        String titleOfRatedBook;
        for (Rating rating : ratings.getList()) {
            titleOfRatedBook = rating.getTitleOfRatedBook();
            if (titleOfRatedBook.equals(titleOfBookToRate)) {
                isRated = true;
                break;
            }
        }

        if (isRated) {
            addLogger.log(Level.INFO, "book has been rated");
            System.out.println(ERROR_RATING_EXIST);
            return true;
        }
        return false;
    }

    private ToDo addToDo(ToDoList toDos) {
        String[] taskNameAndDeadline = information.split("/by", 2);
        String taskName = taskNameAndDeadline[0].trim();
        String deadline = taskNameAndDeadline[1].trim();
        ToDo newToDo = new ToDo(taskName,deadline);
        toDos.add(newToDo);

        return newToDo;
    }

    public boolean isExit() {
        return false;
    }
}
