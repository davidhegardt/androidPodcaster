package dahe0070.androidpodcaster;

import android.app.SearchManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DownloadedEpisodesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DownloadedEpisodesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DownloadedEpisodesFragment extends Fragment implements SearchView.OnQueryTextListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseHandler podDatabase;
    private ArrayList<PodEpisode> episodes;
    private Recycler_View_Adapter recyclerViewAdapter;
    private MenuItem search;
    private SearchView searchView;
    private OnFragmentInteractionListener mListener;
    private PlayerClicks listener;
    private static String TYPE = "DOWNLOAD";


    public DownloadedEpisodesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DownloadedEpisodesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DownloadedEpisodesFragment newInstance(String param1, String param2) {
        DownloadedEpisodesFragment fragment = new DownloadedEpisodesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        podDatabase = new DatabaseHandler(getActivity());
        listener = (PlayerClicks) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        return inflater.inflate(R.layout.fragment_downloaded_episodes, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.downloaded_menu,menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        search = menu.findItem(R.id.searchPodcast);

        searchView = (SearchView) MenuItemCompat.getActionView((menu.findItem(R.id.searchPodcast)));

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.sort_by_date :
                Toast.makeText(getContext(), R.string.ep_sorted_by_date,Toast.LENGTH_SHORT).show();
                if(!episodes.isEmpty()){
                    episodes = Helper.sortByDate(episodes);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
                return true;
            case R.id.sort_by_pod :
                Toast.makeText(getContext(), R.string.ep_sorted_by_podcast,Toast.LENGTH_SHORT).show();
                if(!episodes.isEmpty()){
                    episodes = Helper.sortByPodcast(episodes);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
                return true;
        }

        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        episodes = new ArrayList<>();

        episodes = podDatabase.getDownloadedEpisodes();
        if (!episodes.isEmpty()) {
            episodes = Helper.sortByDate(episodes);
            listener.loadDownloadedEpisodes(episodes);
            final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listDownloaded);

            recyclerViewAdapter = new Recycler_View_Adapter(episodes,getActivity(),this.TYPE);
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(10);
            recyclerView.setDrawingCacheEnabled(true);
            listener.switchMediaController(true);
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        recyclerViewAdapter.getFilter().filter(newText);

        return true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
