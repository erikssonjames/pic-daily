// Generated by view binder compiler. Do not edit!
package se.umu.dv21jen.picdaily.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import se.umu.dv21jen.picdaily.R;

public final class ActivityLoginBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final TextView forgottenPassword;

  @NonNull
  public final Button loginButton;

  @NonNull
  public final TextInputLayout loginEmailInput;

  @NonNull
  public final TextInputLayout loginPasswordInput;

  @NonNull
  public final MaterialToolbar toolbar;

  private ActivityLoginBinding(@NonNull CoordinatorLayout rootView,
      @NonNull TextView forgottenPassword, @NonNull Button loginButton,
      @NonNull TextInputLayout loginEmailInput, @NonNull TextInputLayout loginPasswordInput,
      @NonNull MaterialToolbar toolbar) {
    this.rootView = rootView;
    this.forgottenPassword = forgottenPassword;
    this.loginButton = loginButton;
    this.loginEmailInput = loginEmailInput;
    this.loginPasswordInput = loginPasswordInput;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_login, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityLoginBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.forgotten_password;
      TextView forgottenPassword = ViewBindings.findChildViewById(rootView, id);
      if (forgottenPassword == null) {
        break missingId;
      }

      id = R.id.login_button;
      Button loginButton = ViewBindings.findChildViewById(rootView, id);
      if (loginButton == null) {
        break missingId;
      }

      id = R.id.login_email_input;
      TextInputLayout loginEmailInput = ViewBindings.findChildViewById(rootView, id);
      if (loginEmailInput == null) {
        break missingId;
      }

      id = R.id.login_password_input;
      TextInputLayout loginPasswordInput = ViewBindings.findChildViewById(rootView, id);
      if (loginPasswordInput == null) {
        break missingId;
      }

      id = R.id.toolbar;
      MaterialToolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      return new ActivityLoginBinding((CoordinatorLayout) rootView, forgottenPassword, loginButton,
          loginEmailInput, loginPasswordInput, toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}