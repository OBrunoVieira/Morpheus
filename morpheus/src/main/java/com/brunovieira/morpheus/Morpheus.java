package com.brunovieira.morpheus;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.AnimRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatDialog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;

import java.lang.ref.WeakReference;

public class Morpheus extends AppCompatDialog implements View.OnClickListener, DialogInterface.OnCancelListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener {

    // TODO: remove static state initialized in constructor
    private static WeakReference<Morpheus> instance;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    Builder builder;

    private Morpheus(Builder builder, int theme) {
        super(builder.context, theme);
        this.builder = builder;

        // TODO: make it a singleton in roder to avoid mixing state of static and instance variables
        instance = new WeakReference<>(this);
        Initialize.now(this);
    }

    private Morpheus(Builder builder) {
        super(builder.context);
        this.builder = builder;

        // TODO: make it a singleton in roder to avoid mixing state of static and instance variables
        instance = new WeakReference<>(this);
        Initialize.now(this);
    }

    public Builder getBuilder() {
        return builder;
    }

    @UiThread
    @Override
    public void show() {
        try {
            super.show();
        } catch (WindowManager.BadTokenException e) {
            // TODO: better handle this exception. We are overriding its message here...
            throw new WindowManager.BadTokenException("Bad window token, you cannot show a dialog before an Activity is created or after it's hidden.");
        }
    }

    @Override
    public void onClick(View view) {
        if (view != null && builder.contentClickListener != null) {
            builder.contentClickListener.get(view.getId()).onClickDialog(this, view);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        builder.onCancelListener.onCancelDialog(this);
        clear();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        builder.onDismissListener.onDismissDialog(this);
        clear();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        builder.onCancelListener.onCancelDialog(this);
    }

    public interface OnClickListener {
        void onClickDialog(@NonNull Morpheus dialog, @NonNull View view);
    }

    public interface OnCancelListener {
        void onCancelDialog(@NonNull Morpheus dialog);
    }

    public interface OnDismissListener {
        void onDismissDialog(@NonNull Morpheus dialog);
    }

    public interface OnShowListener {
        void onShowDialog(@NonNull Morpheus dialog);
    }

    public static class Builder {
        final Context context;
        int layoutResID;
        int themeId;

        // TODO: there is plenty of double inititalization here
        // There is a call to initialize sparse array in the constructor so this is not needed
        SparseIntArray contentAnimation = new SparseIntArray();
        SparseIntArray contentImage = new SparseIntArray();
        SparseIntArray contentImageButton = new SparseIntArray();
        SparseArray<CharSequence> contentText = new SparseArray<>();
        SparseArray<Animation.AnimationListener> contentAnimationListener = new SparseArray<>();
        SparseArray<OnClickListener> contentClickListener = new SparseArray<>();
        SparseArray<Typeface> contentTypeFace = new SparseArray<>();
        SparseArray<Tag> contentTag = new SparseArray<>();
        SparseArray<Bitmap> contentBitmap = new SparseArray<>();

        OnCancelListener onCancelListener;
        OnDismissListener onDismissListener;
        OnShowListener onShowListener;

        public Builder(@NonNull Context context) {
            this.context = context;
            initializeSparseArray();
        }

        public Builder(@NonNull android.support.v4.app.Fragment fragment) {
            this.context = fragment.getContext();
            initializeSparseArray();
        }

        public Builder addTag(@IdRes int viewId, @NonNull Tag tag) {
            if (contentTag != null) {
                this.contentTag.put(viewId, tag);
            }
            return this;
        }

        public Builder addFontType(@IdRes int viewId, @NonNull Typeface typeface) {
            if (contentTypeFace != null) {
                contentTypeFace.put(viewId, typeface);
            }
            return this;
        }

        public Builder addButton(@IdRes int viewId, @StringRes int intRes, @NonNull Typeface typeface) {
            addButton(viewId, this.context.getString(intRes), typeface);
            return this;
        }

        public Builder addButton(@IdRes int viewId, @NonNull CharSequence charSequence, @NonNull Typeface typeface) {
            if (contentText != null) {
                contentText.put(viewId, charSequence);
            }

            if (contentTypeFace != null) {
                contentTypeFace.put(viewId, typeface);
            }
            return this;
        }

        public Builder addButton(@IdRes int viewId, @StringRes int intRes) {
            addButton(viewId, this.context.getString(intRes));
            return this;
        }

        public Builder addButton(@IdRes int viewId, @NonNull CharSequence charSequence) {
            if (contentText != null) {
                contentText.put(viewId, charSequence);
            }
            return this;
        }

        public Builder addButton(@IdRes int viewId, @DrawableRes int drawable, @StringRes int intRes) {
            addButton(viewId, drawable, this.context.getString(intRes));
            return this;
        }

        public Builder addButton(@IdRes int viewId, @DrawableRes int drawable, @NonNull CharSequence charSequence) {
            if (contentText != null) {
                contentText.put(viewId, charSequence);
            }

            if (contentImageButton != null) {
                contentImageButton.put(viewId, drawable);
            }
            return this;
        }

        public Builder addButton(@IdRes int viewId, @DrawableRes int drawable, @StringRes int intRes, @NonNull Typeface typeface) {
            addButton(viewId, drawable, this.context.getString(intRes), typeface);
            return this;
        }

        public Builder addButton(@IdRes int viewId, @DrawableRes int drawable, @NonNull CharSequence charSequence, @NonNull Typeface typeface) {
            if (contentText != null) {
                contentText.put(viewId, charSequence);
            }

            if (contentTypeFace != null) {
                contentTypeFace.put(viewId, typeface);
            }

            if (contentImageButton != null) {
                contentImageButton.put(viewId, drawable);
            }
            return this;
        }

        public Builder addText(@IdRes int viewId, @StringRes int intRes, @NonNull Typeface typeface) {
            addText(viewId, this.context.getString(intRes), typeface);
            return this;
        }

        public Builder addText(@IdRes int viewId, @NonNull CharSequence charSequence, @NonNull Typeface typeface) {
            if (contentText != null) {
                contentText.put(viewId, charSequence);
            }

            if (contentTypeFace != null) {
                contentTypeFace.put(viewId, typeface);
            }
            return this;
        }

        public Builder addText(@IdRes int viewId, @StringRes int intRes) {
            addText(viewId, this.context.getString(intRes));
            return this;
        }

        public Builder addText(@IdRes int viewId, @NonNull CharSequence charSequence) {
            if (contentText != null) {
                contentText.put(viewId, charSequence);
            }
            return this;
        }

        public Builder theme(@StyleRes int themeId) {
            this.themeId = themeId;
            return this;
        }

        public Builder contentView(@LayoutRes int layoutResID) {
            this.layoutResID = layoutResID;
            return this;
        }

        public Builder addViewToAnim(@IdRes int id, @AnimRes int anim) {
            if (contentAnimation != null) {
                contentAnimation.put(id, anim);
            }
            return this;
        }

        public Builder addViewToAnim(@IdRes int id, @AnimRes int anim, @NonNull Animation.AnimationListener animationListener) {
            if (contentAnimation != null) {
                contentAnimation.put(id, anim);
            }

            if (contentAnimationListener != null) {
                contentAnimationListener.put(id, animationListener);
            }
            return this;
        }

        public Builder addClickToView(@IdRes int id, @NonNull OnClickListener OnClickListener) {
            if (contentClickListener != null) {
                contentClickListener.put(id, OnClickListener);
            }
            return this;
        }

        public Builder addImage(int id, @DrawableRes int drawable) {
            if (contentImage != null) {
                contentImage.put(id, drawable);
            }
            return this;
        }

        public Builder addImage(int id, @NonNull Bitmap bitmap) {
            if (contentBitmap != null) {
                contentBitmap.put(id, bitmap);
            }
            return this;
        }

        public Builder cancelListener(@NonNull OnCancelListener onCancelListener) {
            this.onCancelListener = onCancelListener;
            return this;
        }

        public Builder dismissListener(@NonNull OnDismissListener onDismissListener) {
            this.onDismissListener = onDismissListener;
            return this;
        }

        public Builder showListener(@NonNull OnShowListener onShowListener) {
            this.onShowListener = onShowListener;
            return this;
        }

        @UiThread
        public Morpheus startAnimation() {
            Initialize.startAnimation(instance.get());
            return instance.get();
        }

        @UiThread
        public Morpheus show() {
            Morpheus morpheus;
            if (themeId != 0) {
                morpheus = new Morpheus(this, themeId);
            } else {
                morpheus = new Morpheus(this);
            }
            morpheus.show();
            return morpheus;
        }

        private void initializeSparseArray() {
            contentAnimation = new SparseIntArray();
            contentImage = new SparseIntArray();
            contentImageButton = new SparseIntArray();
            contentText = new SparseArray<>();
            contentAnimationListener = new SparseArray<>();
            contentClickListener = new SparseArray<>();
            contentTypeFace = new SparseArray<>();
            contentTag = new SparseArray<>();
        }
    }

    private void clear() {
        builder.contentAnimation = null;
        builder.contentImage = null;
        builder.contentImageButton = null;
        builder.contentText = null;
        builder.contentAnimationListener = null;
        builder.contentClickListener = null;
        builder.contentTypeFace = null;
        builder.contentTag = null;

        builder.onCancelListener = null;
        builder.onDismissListener = null;
        builder.onShowListener = null;
    }

    public static class Tag {

        @IntegerRes
        private int key;

        // TODO: lint warning here. Class and field with same name
        private Object tag;

        public Tag(int key, Object tag) {
            this.key = key;
            this.tag = tag;
        }

        public Tag(Object tag) {
            this.tag = tag;
        }

        public Tag() {
        }

        // Will return 0 if not initialized. Common practice says we should use -1
        // for non-initialized primitives
        public int getKey() {
            return key;
        }

        public Object getTag() {
            return tag;
        }
    }
}
