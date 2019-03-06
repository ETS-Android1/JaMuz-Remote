package phramusca.com.jamuzremote;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

/**
 * Created by raph on 03/03/18.
 */

public class AdapterAlbum extends AdapterAbstract {

    AdapterAlbum(Context context, List<Track> tracks, int positionPlaying) {
        super(context, tracks, positionPlaying);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Track track = tracks.get(position);
        return getLayout(position, convertView, parent,
                track.getAlbum(),
                track.getArtist(),
                String.format(Locale.ENGLISH,"%d %s. %d/5 %s",
                        track.getPlayCounter(), //Includes nb of tracks
                        parent.getContext().getString(R.string.nbTracks),
                        track.getRating(),
                        track.getGenre()));
    }
}
