package phramusca.com.jamuzremote;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by raph on 03/03/18.
 */

public class AdapterTrack extends AdapterAbstract {

    AdapterTrack(Context context, List<Track> tracks, int positionPlaying) {
        super(context, tracks, positionPlaying);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getLayout(position, convertView, parent,
                tracks.get(position).getArtist(),
                tracks.get(position).getTitle(),
                tracks.get(position).getAlbum());
    }
}
