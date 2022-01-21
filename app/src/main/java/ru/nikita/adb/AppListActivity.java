package ru.nikita.adb;

import java.util.List;
import android.net.Uri;
import android.os.Bundle;
import android.os.AsyncTask;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import android.content.Intent;
import android.graphics.drawable.Drawable;


public class AppListActivity extends ListActivity{

	private static int LOAD_APP_LIST = 1;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		new AppLoader().execute();
    }
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		super.onListItemClick(l,v,position,id);
		Intent intent = new Intent();
		intent.setData(Uri.parse(apps[position].path));
		setResult(RESULT_OK, intent);
		finish();
	}

	private class App {
		App(String name, String pkg, String path, Drawable icon){
			this.name=name;
			this.pkg=pkg;
			this.path=path;
			this.icon=icon;
		}
		public String name;
		public String pkg;
		public String path;
		public Drawable icon;
	}

	private class AppLoader extends AsyncTask<Void,Void,App[]>{
		@Override
		protected void onPreExecute(){
			pd = new ProgressDialog(AppListActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage("Loading applications...");
			pd.show();
		}
		@Override
		protected void onPostExecute(App[] apps){
			setListAdapter(new AppListAdapter(AppListActivity.this, apps));
			pd.dismiss();
		}
		@Override
		protected App[] doInBackground(Void... voids){
			PackageManager pm = getPackageManager();
			List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
			apps = new App[packages.size()];
			for(int i = 0; i < packages.size(); i++){
				ApplicationInfo info = packages.get(i);
				apps[i] = new App(
					pm.getApplicationLabel(info).toString(),
					info.packageName,
					info.sourceDir,
					pm.getApplicationIcon(info)
				);
			}
			return apps;
		}
		private ProgressDialog pd;
	}
	
	private class AppListAdapter extends ArrayAdapter<App>{
		AppListAdapter(Context context, App[] apps){
			super(context,R.layout.app_list_item,apps);
		}
		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if(view == null){
				view = getLayoutInflater().inflate(R.layout.app_list_item, viewGroup, false);

				ViewHolder viewHolder = new ViewHolder();
				viewHolder.iconView = (ImageView) view.findViewById(R.id.app_icon);
				viewHolder.nameView = (TextView) view.findViewById(R.id.app_name);
				viewHolder.pkgView =  (TextView) view.findViewById(R.id.app_package);

				view.setTag(viewHolder);
			}
			ViewHolder viewHolder = (ViewHolder) view.getTag();
			App app = getItem(i);
			viewHolder.nameView.setText(app.name);
			viewHolder.pkgView.setText(app.pkg);
			viewHolder.iconView.setImageDrawable(app.icon);
			return view;
		}
		private class ViewHolder{
			public ImageView iconView;
			public TextView nameView;
			public TextView pkgView;
		}
	}

	private App[] apps;
}


