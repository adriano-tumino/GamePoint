package it.adriano.tumino.gamepoint.ui.profile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import it.adriano.tumino.gamepoint.AuthenticationActivity;
import it.adriano.tumino.gamepoint.R;
import it.adriano.tumino.gamepoint.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private FragmentProfileBinding binding;
    private StorageReference storageReference;
    private String userID;

    private final ActivityResultLauncher<Intent> uploadImageFromGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    Uri imageUri = data.getData();
                    uploadImageToFirebase(imageUri);
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        binding.setProfile(user);
        userID = (user != null) ? user.getUid() : null;
        storageReference = FirebaseStorage.getInstance().getReference();

        binding.logOutButton.setOnClickListener(logOut);

        binding.changeShopsLayout.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigate_to_change_shops));
        binding.changePasswordLayout.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigate_to_change_password));
        binding.changeDisplayNameLayout.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigate_to_change_nickname));

        binding.changeProfileImageButton.setOnClickListener(v -> changeProfileImage());

        return binding.getRoot();
    }

    public void changeProfileImage() {
        Log.i(TAG, "Start Action Pick");
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        uploadImageFromGallery.launch(openGalleryIntent);
    }

    private void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileRef = storageReference.child("users/" + userID + "/profile.jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            Log.i(TAG, "Image uploaded successfully");
                            Picasso.get().load(uri).fit().into(binding.profileImage);
                            Toast.makeText(requireContext(), R.string.success_upload_image, Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(requireContext(), R.string.error_upload_image, Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(requireContext(), R.string.error_upload_image, Toast.LENGTH_SHORT).show();
                });
    }

    final View.OnClickListener logOut = v -> AuthUI.getInstance()
            .signOut(v.getContext())
            .addOnCompleteListener(task -> {
                Log.i(TAG, "User Log out");
                Toast.makeText(v.getContext(), R.string.user_log_out, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(v.getContext(), AuthenticationActivity.class);
                startActivity(i);
            });

}