// Generated by view binder compiler. Do not edit!
package se.umu.dv21jen.picdaily.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import se.umu.dv21jen.picdaily.R;

public final class ActivityCollectionBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final MaterialToolbar appBar;

  @NonNull
  public final AppBarLayout appBarLayout;

  @NonNull
  public final ImageView collectionCurrentImageShown;

  @NonNull
  public final TextView collectionGoalText;

  @NonNull
  public final ConstraintLayout collectionImageContainer;

  @NonNull
  public final ImageView collectionLeftImageButton;

  @NonNull
  public final TextView collectionPurposeTitle;

  @NonNull
  public final ImageView collectionRightImageButton;

  @NonNull
  public final TextView collectionStartedText;

  @NonNull
  public final Button collectionViewImageGallery;

  @NonNull
  public final TextView currentNumImageShown;

  @NonNull
  public final CircularProgressIndicator imageLoadingProgressBar;

  @NonNull
  public final TextView pictureTakenTodaySubtext;

  @NonNull
  public final TextView pictureTakenTodayText;

  @NonNull
  public final FloatingActionButton takePictureButton;

  private ActivityCollectionBinding(@NonNull ConstraintLayout rootView,
      @NonNull MaterialToolbar appBar, @NonNull AppBarLayout appBarLayout,
      @NonNull ImageView collectionCurrentImageShown, @NonNull TextView collectionGoalText,
      @NonNull ConstraintLayout collectionImageContainer,
      @NonNull ImageView collectionLeftImageButton, @NonNull TextView collectionPurposeTitle,
      @NonNull ImageView collectionRightImageButton, @NonNull TextView collectionStartedText,
      @NonNull Button collectionViewImageGallery, @NonNull TextView currentNumImageShown,
      @NonNull CircularProgressIndicator imageLoadingProgressBar,
      @NonNull TextView pictureTakenTodaySubtext, @NonNull TextView pictureTakenTodayText,
      @NonNull FloatingActionButton takePictureButton) {
    this.rootView = rootView;
    this.appBar = appBar;
    this.appBarLayout = appBarLayout;
    this.collectionCurrentImageShown = collectionCurrentImageShown;
    this.collectionGoalText = collectionGoalText;
    this.collectionImageContainer = collectionImageContainer;
    this.collectionLeftImageButton = collectionLeftImageButton;
    this.collectionPurposeTitle = collectionPurposeTitle;
    this.collectionRightImageButton = collectionRightImageButton;
    this.collectionStartedText = collectionStartedText;
    this.collectionViewImageGallery = collectionViewImageGallery;
    this.currentNumImageShown = currentNumImageShown;
    this.imageLoadingProgressBar = imageLoadingProgressBar;
    this.pictureTakenTodaySubtext = pictureTakenTodaySubtext;
    this.pictureTakenTodayText = pictureTakenTodayText;
    this.takePictureButton = takePictureButton;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCollectionBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCollectionBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_collection, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCollectionBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.app_bar;
      MaterialToolbar appBar = ViewBindings.findChildViewById(rootView, id);
      if (appBar == null) {
        break missingId;
      }

      id = R.id.app_bar_layout;
      AppBarLayout appBarLayout = ViewBindings.findChildViewById(rootView, id);
      if (appBarLayout == null) {
        break missingId;
      }

      id = R.id.collection_current_image_shown;
      ImageView collectionCurrentImageShown = ViewBindings.findChildViewById(rootView, id);
      if (collectionCurrentImageShown == null) {
        break missingId;
      }

      id = R.id.collection_goal_text;
      TextView collectionGoalText = ViewBindings.findChildViewById(rootView, id);
      if (collectionGoalText == null) {
        break missingId;
      }

      id = R.id.collection_image_container;
      ConstraintLayout collectionImageContainer = ViewBindings.findChildViewById(rootView, id);
      if (collectionImageContainer == null) {
        break missingId;
      }

      id = R.id.collection_left_image_button;
      ImageView collectionLeftImageButton = ViewBindings.findChildViewById(rootView, id);
      if (collectionLeftImageButton == null) {
        break missingId;
      }

      id = R.id.collection_purpose_title;
      TextView collectionPurposeTitle = ViewBindings.findChildViewById(rootView, id);
      if (collectionPurposeTitle == null) {
        break missingId;
      }

      id = R.id.collection_right_image_button;
      ImageView collectionRightImageButton = ViewBindings.findChildViewById(rootView, id);
      if (collectionRightImageButton == null) {
        break missingId;
      }

      id = R.id.collection_started_text;
      TextView collectionStartedText = ViewBindings.findChildViewById(rootView, id);
      if (collectionStartedText == null) {
        break missingId;
      }

      id = R.id.collection_view_image_gallery;
      Button collectionViewImageGallery = ViewBindings.findChildViewById(rootView, id);
      if (collectionViewImageGallery == null) {
        break missingId;
      }

      id = R.id.current_num_image_shown;
      TextView currentNumImageShown = ViewBindings.findChildViewById(rootView, id);
      if (currentNumImageShown == null) {
        break missingId;
      }

      id = R.id.image_loading_progress_bar;
      CircularProgressIndicator imageLoadingProgressBar = ViewBindings.findChildViewById(rootView, id);
      if (imageLoadingProgressBar == null) {
        break missingId;
      }

      id = R.id.picture_taken_today_subtext;
      TextView pictureTakenTodaySubtext = ViewBindings.findChildViewById(rootView, id);
      if (pictureTakenTodaySubtext == null) {
        break missingId;
      }

      id = R.id.picture_taken_today_text;
      TextView pictureTakenTodayText = ViewBindings.findChildViewById(rootView, id);
      if (pictureTakenTodayText == null) {
        break missingId;
      }

      id = R.id.take_picture_button;
      FloatingActionButton takePictureButton = ViewBindings.findChildViewById(rootView, id);
      if (takePictureButton == null) {
        break missingId;
      }

      return new ActivityCollectionBinding((ConstraintLayout) rootView, appBar, appBarLayout,
          collectionCurrentImageShown, collectionGoalText, collectionImageContainer,
          collectionLeftImageButton, collectionPurposeTitle, collectionRightImageButton,
          collectionStartedText, collectionViewImageGallery, currentNumImageShown,
          imageLoadingProgressBar, pictureTakenTodaySubtext, pictureTakenTodayText,
          takePictureButton);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}