package com.gomdev.gles;

import java.util.ArrayList;

public class GLESNode extends GLESSpatial {
    static final String CLASS = "GLESNode";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    protected GLESNodeListener mListener = null;

    private ArrayList<GLESSpatial> mChildList = new ArrayList<GLESSpatial>();

    public GLESNode() {
        super();

        init();
    }

    public GLESNode(String name) {
        super(name);

        init();
    }

    private void init() {
        mChildList.clear();
    }

    public void addChild(GLESSpatial spatial) {
        mChildList.add(spatial);
        spatial.setParent(this);
    }

    public void removeChild(GLESSpatial spatial) {
        mChildList.remove(spatial);
    }

    public void removeAll() {
        mChildList.clear();
    }

    public int getNumOfChild() {
        return mChildList.size();
    }

    public void setListener(GLESNodeListener listener) {
        mListener = listener;
    }

    public GLESNodeListener getListener() {
        return mListener;
    }

    @Override
    public void draw(GLESRenderer renderer) {
        if (getVisibility() == false) {
            return;
        }

        int size = mChildList.size();
        for (int i = 0; i < size; i++) {
            mChildList.get(i).draw(renderer);
        }
    }

    @Override
    public void update(double applicationTime, boolean parentHasChanged) {
        if (getVisibility() == false) {
            return;
        }

        if (parentHasChanged == true) {
            needToUpdate();
        }

        if (mListener != null) {
            mListener.update(this);
        }

        boolean hasChanged = getNeedToUpdate();
        updateWorldData(applicationTime);

        int size = mChildList.size();
        for (int i = 0; i < size; i++) {
            mChildList.get(i).update(applicationTime, hasChanged);
        }
    }

    public ArrayList<GLESSpatial> getChildList() {
        return mChildList;
    }

    @Override
    public void dump() {
        super.dump();

        int size = mChildList.size();
        for (int i = 0; i < size; i++) {
            mChildList.get(i).dump();
        }
    }
}
