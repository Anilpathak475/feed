package com.twitter.feed

import androidx.lifecycle.AndroidViewModel
import java.util.concurrent.Executors

class TweetViewModel : AndroidViewModel() {

    private PostDao postDao;
    private ExecutorService executorService;

    public PostViewModel(@NonNull Application application) {
        super(application);
        postDao = PostsDatabase.getInstance(application).postDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    LiveData<List<Post>> getAllPosts() {
        return postDao.findAll();
    }

    void savePost(Post post) {
        executorService.execute(() -> postDao.save(post));
    }

    void deletePost(Post post) {
        executorService.execute(() -> postDao.delete(post));
    }
}