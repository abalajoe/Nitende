package app.facebook.android.com.nitende;

public enum ModelObject {

    RED(R.layout.view_red),
    BLUE(R.layout.view_blue),
    GREEN(R.layout.view_green);

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int layoutResId) {
       // mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}