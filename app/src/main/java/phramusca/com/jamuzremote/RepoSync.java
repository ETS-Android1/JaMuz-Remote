package phramusca.com.jamuzremote;

import android.util.Log;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author phramusca
 */
public final class RepoSync {

    private static final String TAG = RepoSync.class.getName();

    /**
     * Sets status to NEW if track does not exists
     * or to given status if track exists and has correct size.
     * File is deleted if not requested (not in files).
     * @param getAppDataPath application path
     * @param track the one to check
     * @param status status to set if returns true
     * @return true if receivedFile exists and length()==track.size
     */
    public synchronized static boolean checkFile(File getAppDataPath,
                                                 Track track,
                                                 Track.Status status) {

        File receivedFile = new File(getAppDataPath, track.relativeFullPath);
        int idFileRemote = HelperLibrary.musicLibrary.getTrackId(track.getIdFileServer());
        if(idFileRemote>=0) {
            if(checkFile(track, receivedFile)) {
                track.status = status;
                HelperLibrary.musicLibrary.updateStatus(track);
                return true;
            } else {
                track.status = Track.Status.NEW;
                HelperLibrary.musicLibrary.updateStatus(track);
            }
        } else {
            Log.w(TAG, "files does not contain file. Deleting " + receivedFile.getAbsolutePath());
            //noinspection ResultOfMethodCallIgnored
            receivedFile.delete();
        }
        return false;
    }

    /**
     * Checks if relativeFullPath is in database. Delete file if not.
     * @param relativeFullPath relative full path
     */
    public synchronized static boolean checkFile(File getAppDataPath, String relativeFullPath) {
        Track fileInfoReception = new Track();
        fileInfoReception.relativeFullPath=relativeFullPath;
        File file = new File(getAppDataPath, fileInfoReception.relativeFullPath);
        int idFileRemote = HelperLibrary.musicLibrary.getTrackId(file.getAbsolutePath());
        if(idFileRemote<0) {
            Log.i(TAG, "DELETE UNWANTED: "+relativeFullPath);
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * @param track the one to check
     * @param receivedFile the corresponding File
     * @return true if receivedFile exists and length()==track.size
     */
    private synchronized static boolean checkFile(Track track,
                                                  File receivedFile) {
        if(receivedFile.exists()) {
            if (receivedFile.length() == track.size) {
                Log.i(TAG, "Correct file size: " + receivedFile.length());
                return true;
            } else {
                Log.w(TAG, "File has wrong size. Deleting " + receivedFile.getAbsolutePath());
                //noinspection ResultOfMethodCallIgnored
                receivedFile.delete();
            }
        } else {
            Log.w(TAG, "File does not exits. "+receivedFile.getAbsolutePath());
        }
        return false;
    }

    /**
     * @param track the one to check
     * @return modified track with status to REC if it exists and status was NEW
     *
     */
    private synchronized static Track checkFile(Track track) {
        File file = new File(track.getPath());
        if(checkFile(track, file)) {
            if (track.status.equals(Track.Status.NEW)) {
                track.status = Track.Status.REC;
            }
        } else {
            track.status = Track.Status.NEW;
        }
        return track;
    }

    public synchronized static void receivedAck(Track track) {
        track.status = Track.Status.ACK;
        HelperLibrary.musicLibrary.updateStatus(track);
    }

    public synchronized static void set(Map<Integer, Track> newTracks) {
        HelperLibrary.musicLibrary.updateStatus();
        for(Map.Entry<Integer, Track> entry : newTracks.entrySet()) {
            Track track = entry.getValue();
            track=RepoSync.checkFile(track);
            if(track.getStatus().equals(Track.Status.REC)) {
                track.readTags();
            }
        }
        HelperLibrary.musicLibrary.insertTracks(newTracks.values());
    }

    public synchronized static int getRemainingSize() {
        return (HelperLibrary.musicLibrary.getTracks(Track.Status.NEW).size()
                +HelperLibrary.musicLibrary.getTracks(Track.Status.REC).size());
    }

    public synchronized static long getRemainingFileSize() {
        long nbRemaining=0;
        nbRemaining+=getRemainingFileSize(Track.Status.NEW);
        nbRemaining+=getRemainingFileSize(Track.Status.REC);
        return nbRemaining;
    }

    private synchronized static long getRemainingFileSize(Track.Status status) {
        long nbRemaining=0;
        for(Track fileInfoReception : HelperLibrary.musicLibrary.getTracks(status)) {
            nbRemaining+=fileInfoReception.size;
        }
        return nbRemaining;
    }

    public synchronized static int getTotalSize() {
        return HelperLibrary.musicLibrary.getTracks(Track.Status.NULL, true).size();
    }

    public synchronized static Track takeNew() {
        Iterator<Track> iterator = HelperLibrary.musicLibrary.getTracks(Track.Status.NEW).iterator();
        if(iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public synchronized static List<Track> getReceived() {
        return HelperLibrary.musicLibrary.getTracks(Track.Status.REC);
    }
}