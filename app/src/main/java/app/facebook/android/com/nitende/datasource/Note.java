package app.facebook.android.com.nitende.datasource;

/**
 * Created by abala on 11/3/16.
 */
public class Note {
    private int id;
    private String note;
    private String notetime;

    public Note(int id, String note, String notetime) {
        this.id = id;
        this.note = note;
        this.notetime = notetime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNotetime() {
        return notetime;
    }

    public void setNotetime(String notetime) {
        this.notetime = notetime;
    }
}
