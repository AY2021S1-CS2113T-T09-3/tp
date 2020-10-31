package seedu.quotesify.commands.find;

import seedu.quotesify.lists.ListManager;
import seedu.quotesify.rating.Rating;
import seedu.quotesify.rating.RatingList;
import seedu.quotesify.rating.RatingParser;
import seedu.quotesify.store.Storage;
import seedu.quotesify.ui.TextUi;
import seedu.quotesify.ui.UiMessage;

import java.util.logging.Level;

public class FindRatingCommand extends FindCommand {

    public FindRatingCommand(String arguments) {
        super(arguments);
    }

    @Override
    public void execute(TextUi ui, Storage storage) {
        RatingList ratings = (RatingList) ListManager.getList(ListManager.RATING_LIST);
        findRating(ratings, ui);
    }

    private void findRating(RatingList ratings, TextUi ui) {
        System.out.println(UiMessage.DIVIDER_LINE);
        boolean hasMissingInput = RatingParser.checkUserInput(information);
        if (hasMissingInput) {
            quotesifyLogger.log(Level.INFO, "user input is missing");
            System.out.println(UiMessage.DIVIDER_LINE);
            return;
        }
        
        String keyword = information.toLowerCase().trim();

        boolean isFound = false;
        for (Rating rating : ratings.getList()) {
            if (rating.getTitle().toLowerCase().contains(keyword)) {
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            System.out.printf(ERROR_RATING_NOT_FOUND + "\n", keyword);
            quotesifyLogger.log(Level.INFO, "ratings not found");
            System.out.println(UiMessage.DIVIDER_LINE);
            return;
        }
        ui.printFoundRating(ratings, keyword);
    }
}
