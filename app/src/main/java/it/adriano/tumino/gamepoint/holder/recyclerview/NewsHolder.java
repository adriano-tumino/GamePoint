package it.adriano.tumino.gamepoint.holder.recyclerview;

import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import it.adriano.tumino.gamepoint.BR;
import it.adriano.tumino.gamepoint.data.News;
import it.adriano.tumino.gamepoint.databinding.NewsLayoutBinding;
import it.adriano.tumino.gamepoint.ui.news.NewsDialog;

public class NewsHolder extends RecyclerView.ViewHolder {
    public NewsLayoutBinding binding;

    public NewsHolder(NewsLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Object obj) {
        binding.setVariable(BR.news, obj);
        binding.executePendingBindings();

        binding.newsLayout.setOnClickListener(v -> {
            News news = (News) obj;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getNewsUrl()));
            binding.newsLayout.getContext().startActivity(intent);
        });

        binding.newsLayout.setOnLongClickListener(v -> {
            NewsDialog newsDialog = new NewsDialog((News) obj);
            FragmentActivity activity = (FragmentActivity) binding.newsLayout.getContext();
            newsDialog.show(activity.getSupportFragmentManager(), "News Dialog");
            return true;
        });
    }
}
