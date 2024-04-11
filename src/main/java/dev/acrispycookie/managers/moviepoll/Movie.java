package dev.acrispycookie.managers.moviepoll;

public class Movie {

    String movieId;
    int upVotes;
    long messageId;

    public Movie(String movieId, int upVotes, long messageId){
        this.movieId = movieId;
        this.upVotes = upVotes;
        this.messageId = messageId;
    }

    public String getMovieId(){
        return movieId;
    }

    public int getUpVotes(){
        return upVotes;
    }

    public long getMessageId(){
        return messageId;
    }
}
