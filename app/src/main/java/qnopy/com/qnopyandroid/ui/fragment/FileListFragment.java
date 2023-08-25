package qnopy.com.qnopyandroid.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.ListFragment;

import java.io.File;
import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.BuildConfig;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem;
import qnopy.com.qnopyandroid.db.FileFolderDataSource;
import qnopy.com.qnopyandroid.filefolder.FileFolderAdapter;
import qnopy.com.qnopyandroid.util.Util;

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FileListFragment extends ListFragment implements AdapterView.OnItemClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = FileListFragment.class.getSimpleName();
    // TODO: Customize parameters
    private int mColumnCount = 1;
    Context context;
    LinearLayout linearLayout;
    LinearLayout naviBar;
    TextView valueTV;
    ImageView seperatorIV;
    ImageView homeiv;
    private OnListFragmentInteractionListener mListener;
    String siteID;
    LinearLayout mLinearLayoutListView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FileListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FileListFragment newInstance(int columnCount) {
        Log.i(TAG, "New Instance");
        FileListFragment fragment = new FileListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListtoDefault();
        getListView().setOnItemClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_list, container, false);

        linearLayout = view.findViewById(R.id.navigationContainer);
        naviBar = view.findViewById(R.id.navigationbar);
        homeiv = view.findViewById(R.id.navhomeiv);
        //LinearLayout layout = (LinearLayout) findViewById(R.id.info);
        mLinearLayoutListView = view.findViewById(R.id.linearLayoutListView);
        context = getContext();
        siteID = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "Details:" + id);

        FileFolderItem selected = (FileFolderItem) parent.getItemAtPosition(position);

        int selID = Integer.parseInt(selected.getItemID());
        if (selected.getItemType().equals("folder")) {

            setListtoSelectedID(selID + "", siteID);
            addNavEntry(context, selected.getItemTitle(), selID);
        } else {

            String fileDir = Util.getFileFolderDirPath(context, siteID);

            if (fileDir.isEmpty()) {
                Toast.makeText(context, "No files found", Toast.LENGTH_SHORT).show();
                return;
            }

            File file = new File(fileDir, selected.getItemGuid());
            MimeTypeMap map = MimeTypeMap.getSingleton();
            String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
            String type = map.getMimeTypeFromExtension(ext);

            if (type == null) {
                type = "*/*";
            }

            Uri data = Uri.fromFile(file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            } else {
                data = Uri.fromFile(file);
                //data = Uri.parse(path);
            }

            //content://com.aqua.fieldbuddy.provider/external_files/Android/data/com.aqua.fieldbuddy/files/FileFolder/4684/43790f03-2f01-4f16-a1e2-15cc61f0b7aa.jpg

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(data, type);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, data, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }

            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> resInfos = pm.queryIntentActivities(intent, 0);

            if (resInfos.size() > 0) {
                startActivity(intent);
            }
            else if(type.equals("application/pdf")){
                Intent showPdfItent = new Intent(requireActivity(), ViewPdfActivity.class);
                showPdfItent.putExtra(ViewPdfActivity.PDF_URI, data.toString());
                startActivity(showPdfItent);
            }
            else {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setNeutralButton(getString(R.string.ok), null);

                if (Locale.getDefault().getLanguage().contains("en"))
                    builder.setMessage("No suitable application installed on your device to view this(." + ext + ") file.");
                else
                    builder.setMessage(R.string.no_app_installed_to_view_this_file);

                builder.setTitle("Oops!");
                AlertDialog dialog = builder.create();
                dialog.show();

                //  Toast.makeText(context, "No suitable application installed to view this file", Toast.LENGTH_LONG).show();
            }
        }
        final int count = naviBar.getChildCount();
        Log.i(TAG, "Child Count:" + count);
//        if (count <= 0) {
//            homeiv.setVisibility(View.GONE);
//        } else {
//            homeiv.setVisibility(View.VISIBLE);
//        }

        homeiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naviBar.removeAllViews();
                setListtoDefault();
            }
        });
    }

    View.OnClickListener onClicklistner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int fileIDasViewID = v.getId();
            int dirCount = naviBar.getChildCount();
            int position = 0;

            for (int i = 0; i < dirCount; ++i) {
                int currentViewId = naviBar.getChildAt(i).getId();

                if (currentViewId == fileIDasViewID) {
                    position = i;
                }
            }

            int removefrom = position + 2;
            int removeCount = dirCount - removefrom;
            Log.i(TAG, "Remove From:" + removefrom + " Count :" + removeCount);
            if (removeCount > 0) {
                naviBar.removeViews(removefrom, removeCount);
                setListtoSelectedID(fileIDasViewID + "", siteID);
            }
        }
    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(FileFolderItem item);
    }

    private void setListtoDefault() {
        List<FileFolderItem> list2 = new FileFolderDataSource(context).getHomeFileFolderItemList(siteID);

        if (list2.size() < 1) {
            homeiv.setVisibility(View.GONE);
            // startActivity(new Intent(context,FileFolderSyncActivity.class));
        } else {
            FileFolderAdapter fa2 = new FileFolderAdapter(getActivity(), R.layout.fragment_file_item, list2);
            setListAdapter(fa2);
        }

    }

    private void setListtoSelectedID(String selID, String siteID) {
        List<FileFolderItem> list = new FileFolderDataSource(context).getSubFileFolderItemList(selID, siteID);
        FileFolderAdapter fa = new FileFolderAdapter(getActivity(), R.layout.fragment_file_item, list);
        setListAdapter(fa);
    }

    private void addNavEntry(Context context, String Title, int ID) {
        valueTV = new TextView(context);
        seperatorIV = new ImageView(context);
        seperatorIV.setImageDrawable(getResources().getDrawable(R.mipmap.ic_list_more));
        valueTV.setText(Title);
        valueTV.setTextSize(20);
        valueTV.setTypeface(Typeface.DEFAULT_BOLD);

        valueTV.setId(ID);
        valueTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        naviBar.addView(valueTV);
        naviBar.addView(seperatorIV);
        valueTV.setOnClickListener(onClicklistner);
    }
}
