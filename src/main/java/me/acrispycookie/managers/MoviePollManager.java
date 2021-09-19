package me.acrispycookie.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.acrispycookie.Main;
import me.acrispycookie.managers.moviepoll.Movie;
import me.acrispycookie.managers.moviepoll.MoviePoll;

import java.util.ArrayList;

public class MoviePollManager {

    Main main;
    JsonObject object;
    boolean isActive;
    MoviePoll active;

    public MoviePollManager(Main main, JsonObject object){
        this.main = main;
        this.object = object;
        initialize();
    }

    public void start(long endsIn, long messageId){
        isActive = true;
        active = new MoviePoll(main, new ArrayList<>(), endsIn, messageId);
    }

    private void initialize(){
        isActive = object.getAsJsonPrimitive("active").getAsBoolean();
        if(isActive){
            long voteMessageId = object.getAsJsonPrimitive("voteMessageId").getAsLong();
            long endsIn = object.getAsJsonPrimitive("endsIn").getAsLong();
            ArrayList<Movie> movies = new ArrayList<>();
            JsonArray moviesJson = object.getAsJsonArray("movies");
            for(int i = 0; i < moviesJson.size(); i++){
                JsonObject object = moviesJson.get(i).getAsJsonObject();
                movies.add(new Movie(object.getAsJsonPrimitive("movieId").getAsString(), object.getAsJsonPrimitive("upVotes").getAsInt(), object.getAsJsonPrimitive("messageId").getAsLong()));
            }
            active = new MoviePoll(main, movies, endsIn, voteMessageId);
        }
    }

    public boolean isActive(){
        return isActive;
    }

    public MoviePoll getActive(){
        return active;
    }
}
