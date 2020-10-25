package seedu.quotesify.commands.done;

import seedu.quotesify.book.Book;
import seedu.quotesify.book.BookList;
import seedu.quotesify.commands.Command;
import seedu.quotesify.commands.done.DoneBookCommand;
import seedu.quotesify.lists.ListManager;
import seedu.quotesify.store.Storage;
import seedu.quotesify.todo.ToDo;
import seedu.quotesify.todo.ToDoList;
import seedu.quotesify.ui.TextUi;

public class DoneCommand extends Command {
    public String type;
    public String information;
    private String arguments;

    public DoneCommand(String arguments) {
        this.arguments = arguments;
        String[] details = arguments.split(" ", 2);

        // if user did not provide arguments, let details[1] be empty string
        if (details.length == 1) {
            details = new String[]{details[0], ""};
        }
        type = details[0];
        information = details[1];
    }

    @Override
    public void execute(TextUi ui, Storage storage) {
        switch (type) {
        case TAG_TODO:
            ToDoList toDos = (ToDoList) ListManager.getList(ListManager.TODO_LIST);
            int index = computeToDoIndex(information.trim());
            doneToDo(toDos,index,ui);
            break;
        case TAG_BOOK:
            new DoneBookCommand(arguments).execute(ui, storage);
            break;
        default:
            ui.printDoneCommandUsage();
            break;
        }
        storage.save();
    }

    private void doneToDo(ToDoList toDos, int index, TextUi ui) {
        ToDo targetTask = toDos.find(index);
        if (targetTask != null) {
            targetTask.setDone(true);
            ui.printDoneToDo(targetTask);
        } else {
            System.out.println(ERROR_TODO_NOT_FOUND);
        }
    }

    private int computeToDoIndex(String information) {
        int index = 0;
        try {
            index = Integer.parseInt(information);
        } catch (NumberFormatException e) {
            System.out.println(ERROR_INVALID_TODO_NUM);
        }

        return index;
    }

    @Override
    public boolean isExit() {
        return false;
    }
}