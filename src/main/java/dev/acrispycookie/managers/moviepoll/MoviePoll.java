package dev.acrispycookie.managers.moviepoll;

import dev.acrispycookie.Main;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MoviePoll {

    Main main;
    ArrayList<Movie> movies;
    ArrayList<Movie> winners;
    ScheduledFuture<?> f;
    long messageId;
    long endsIn;

    public MoviePoll(Main main, ArrayList<Movie> movies, long messageId, long endsIn) {
        this.main = main;
        this.movies = movies;
        this.messageId = messageId;
        this.endsIn = endsIn;
        scheduleEnd();
    }

    public void end(){
        f.cancel(true);
        selectWinner();
    }

    public void addMovie(String id){

    }

    private void scheduleEnd(){
        if(endsIn - System.currentTimeMillis() > 0) {
            ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
            f = ses.schedule(this::selectWinner, endsIn - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
        else {
            selectWinner();
        }
    }

    private void selectWinner(){
        for(Movie m : movies){
            if(winners.isEmpty()){
                winners.add(m);
            }
            else if(winners.get(0).getUpVotes() < m.getUpVotes()){
                winners.clear();
                winners.add(m);
            }
            else if(winners.get(0).getUpVotes() == m.getUpVotes()){
                winners.add(m);
            }
            main.getGuild().getTextChannelById(Long.parseLong(main.getConfigManager().get("features.movies.channel"))).retrieveMessageById(m.getMessageId()).complete().delete().queue();
        }
        editVoteMessage();
    }

    private void editVoteMessage(){

    }
}
