package apply;

import refactor.EndlessLinkedList;
import implement.ArrayDeque;

import java.util.Iterator;
import java.util.Random;

public class Quackify implements StaticQuackify {
    private boolean isPlaying;
    private EndlessLinkedList<String> playlist;
    private Iterator<String> iterator;
    private Random rand;
    private String curr;
    private ArrayDeque<String> removedSongs = new ArrayDeque<>();
    private ArrayDeque<String> undoHistory = new ArrayDeque<>();
    private ArrayDeque<String> redoHistory = new ArrayDeque<>();

    /**
     * A constructor used to create a new instance of Quackify.
     * Defaults isPlaying to false, playlist to an empty ELL, and resets the playlist iterator.
     */
    public Quackify() {
        isPlaying = false;
        playlist = new EndlessLinkedList<>();
        iterator = null;
        curr = null;
        this.rand = new Random();
    }

    /**
     * A constructor used to create an instance of Quackify using a random argument.
     *
     * @param rand A random argument for the constructor to utilize.
     */
    public Quackify(Random rand) {
        isPlaying = false;
        playlist = new EndlessLinkedList<>();
        this.rand = rand;
        iterator = null;
        curr = null;
    }

    @Override
    public void play() {
        if (isPlaying) {
            throw new IllegalStateException("Your playlist is already playing.");
        } else if (playlist.size() == 0) {
            throw new IllegalStateException("Your playlist doesn't contain any songs!");
        }
        iterator = playlist.iterator();
        curr = playlist.getHead().getData();
        iterator.next();
        isPlaying = true;
    }

    @Override
    public void stop() {
        if (!isPlaying) {
            throw new IllegalStateException("Your playlist is already stopped.");
        } else if (playlist.size() == 0) {
            throw new IllegalStateException("Your playlist doesn't contain any songs!");
        }
        iterator = null;
        isPlaying = false;
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public EndlessLinkedList<String> getPlaylist() {
        return playlist;
    }

    @Override
    public int size() {
        return playlist.size();
    }

    @Override
    public String currentSong() {
        if (!isPlaying) {
            throw new IllegalStateException("No song is currently playing.");
        } else if (playlist.size() == 0) {
            throw new IllegalStateException("Your playlist contains no songs to play!");
        }
        return curr;
    }

    @Override
    public String nextSong() {
        if (!isPlaying) {
            throw new IllegalStateException("No song is currently playing.");
        } else if (playlist.size() == 0) {
            throw new IllegalStateException("Your playlist doesn't contain any songs!");
        }
        curr = iterator.next();
        return curr;
    }

    @Override
    public void addSong(String song) {
        if (isPlaying) {
            throw new IllegalStateException("You can not add music to the queue while songs are playing.");
        } else if (song == null || song.isBlank()) {
            throw new IllegalArgumentException("You must enter a song name to add!");
        }
        curr = song;
        playlist.addFirst(song);
        undoHistory.addLast("addSong");
        if (redoHistory.size() > 0) {
            redoHistory.clear();
        }
    }

    @Override
    public void removeSong(String song) {
        if (isPlaying) {
            throw new IllegalStateException("You can not remove music from the queue while song is playing.");
        } else if (song == null || song.isBlank()) {
            throw new IllegalArgumentException("You must enter a song name to remove!");
        }
        int index = playlist.removeValue(song);
        removedSongs.addLast(song);
        undoHistory.addLast("removeSong;" + index);
        if (redoHistory.size() > 0) {
            redoHistory.clear();
        }
    }

    @Override
    public void reverse() {
        if (isPlaying) {
            throw new IllegalStateException("You can not reverse the queue while songs are playing.");
        } else if (playlist.size() == 0) {
            throw new IllegalStateException("Your playlist doesn't contain any songs to reverse!");
        }
        playlist.reverse();
        undoHistory.addLast("reverse");
        if (redoHistory.size() > 0) {
            redoHistory.clear();
        }
    }

    @Override
    public String randomSong() {
        if (!isPlaying) {
            throw new IllegalStateException("You must be listening to music to randomize songs.");
        }
        int randNum = rand.nextInt(playlist.size());
        iterator = playlist.iterator();
        curr = playlist.get(randNum);
        for (int i = 0; i <= randNum; i++) {
            iterator.next();
        }
        return curr;
    }

    @Override
    public boolean isPalindrome() {
        if (playlist.size() == 0) {
            throw new IllegalStateException("Your playlist is currently empty. There are no palindromes.");
        } else if (playlist.size() == 1) {
            return true;
        }
        ArrayDeque<String> queue = new ArrayDeque<>(playlist);
        while (queue.size() > 1 && queue.getFirst().equals(queue.getLast())) {
            queue.removeFirst();
            queue.removeLast();
        }
        return queue.size() < 2;
    }

    @Override
    public void undo() {
        if (undoHistory.size() < 1) {
            throw new IllegalStateException("There is nothing to undo!");
        } else if (isPlaying) {
            throw new IllegalStateException("You can not modify the playlist while it's playing!");
        }
        String data = undoHistory.getLast();
        switch (data.split(";")[0]) {
        case "addSong":
            removedSongs.addLast(playlist.getHead().getData());
            playlist.removeFirst();
            undoHistory.removeLast();
            redoHistory.addLast("addSong");
            break;
        case "removeSong":
            int index = Integer.parseInt(data.split(";")[1]);
            playlist.addAtIndex(index, removedSongs.getLast());
            iterator = playlist.iterator();
            for (int i = 0; i <= index; i++) {
                if (iterator.hasNext()) {
                    curr = iterator.next();
                }
            }
            undoHistory.removeLast();
            redoHistory.addLast("removeSong;" + index);
            break;
        case "reverse":
            playlist.reverse();
            undoHistory.removeLast();
            redoHistory.addLast("reverse");
            break;
        default:
            throw new IllegalStateException("There is nothing to undo!");
        }
    }

    @Override
    public void redo() {
        if (redoHistory.size() < 1) {
            throw new IllegalStateException("There is nothing to redo!");
        } else if (isPlaying) {
            throw new IllegalStateException("You can not modify the playlist while it's playing!");
        }
        String data = redoHistory.getLast();
        switch (data.split(";")[0]) {
        case "addSong":
            playlist.addFirst(removedSongs.getLast());
            removedSongs.removeLast();
            redoHistory.removeLast();
            undoHistory.addLast("addSong");
            break;
        case "removeSong":
            int index = Integer.parseInt(data.split(";")[1]);
            playlist.removeAtIndex(index);
            iterator = playlist.iterator();
            for (int i = 0; i <= index; i++) {
                if (iterator.hasNext()) {
                    curr = iterator.next();
                }
            }
            redoHistory.removeLast();
            undoHistory.addLast("removeSong;" + index);
            break;
        case "reverse":
            playlist.reverse();
            redoHistory.removeLast();
            undoHistory.addLast("reverse");
            break;
        default:
            throw new IllegalStateException("There is nothing to redo!");
        }
    }
}
