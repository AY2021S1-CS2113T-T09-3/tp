package seedu.quotesify.commands.delete;

import seedu.quotesify.book.Book;
import seedu.quotesify.lists.ListManager;
import seedu.quotesify.rating.Rating;
import seedu.quotesify.rating.RatingList;
import seedu.quotesify.rating.RatingParser;
import seedu.quotesify.store.Storage;
import seedu.quotesify.ui.TextUi;
import seedu.quotesify.ui.UiMessage;

import java.util.logging.Level;

public class DeleteRatingCommand extends DeleteCommand {

    public DeleteRatingCommand(String arguments) {
        super(arguments);
    }

    @Override
    public void execute(TextUi ui, Storage storage) {
        RatingList ratings = (RatingList) ListManager.getList(ListManager.RATING_LIST);
        deleteRating(ratings, ui);
    }

    private void deleteRating(RatingList ratings, TextUi ui) {
        boolean hasMissingInput = RatingParser.checkUserInput(information);
        if (hasMissingInput) {
            quotesifyLogger.log(Level.INFO, "user input is missing");
            return;
        }

        String bookIndex = information.trim();
        Book bookToDeleteRating = RatingParser.checkBookExists(bookIndex);

        if (bookToDeleteRating == null) {
            return;
        }

        bookToDeleteRating.setRating(RatingParser.UNRATED);
        String title = bookToDeleteRating.getTitle();
        String author = bookToDeleteRating.getAuthor().getName();

        for (Rating rating : ratings.getList()) {
            if (rating.getTitle().equals(title) && rating.getAuthor().equals(author)) {
                ratings.delete(ratings.getList().indexOf(rating));
                ui.printDeleteRating(title, author);
                return;
            }
        }
        System.out.println(ERROR_RATING_DO_NOT_EXIST);
        quotesifyLogger.log(Level.INFO, "book has not been rated before");
    }
}
