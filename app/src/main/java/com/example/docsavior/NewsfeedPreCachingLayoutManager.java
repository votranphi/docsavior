package com.example.docsavior;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewsfeedPreCachingLayoutManager extends LinearLayoutManager {
    private int defaultExtraLayoutSpace = 800;
    private int extraLayoutSpace = -1;
    private Context context;

    public NewsfeedPreCachingLayoutManager(Context context) {
        super(context);
        this.context = context;
    }

    public NewsfeedPreCachingLayoutManager(Context context, int extraLayoutSpace) {
        super(context);
        this.extraLayoutSpace = extraLayoutSpace;
    }

    public NewsfeedPreCachingLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.context = context;
    }

    public void setExtraLayoutSpace(int extraLayoutSpace) {
        this.extraLayoutSpace = extraLayoutSpace;
    }

    @Override
    public int getExtraLayoutSpace(RecyclerView.State state) {
        if (extraLayoutSpace > 0) {
            return extraLayoutSpace;
        } else {
            return defaultExtraLayoutSpace;
        }
    }
}
