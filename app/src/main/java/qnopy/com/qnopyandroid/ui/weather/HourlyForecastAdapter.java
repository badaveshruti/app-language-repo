package qnopy.com.qnopyandroid.ui.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.responsemodel.WeatherResponse;
import qnopy.com.qnopyandroid.util.Util;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder> {

    private ArrayList<WeatherResponse.Forecast.Forecastday.Hour> hoursList;

    public HourlyForecastAdapter(ArrayList<WeatherResponse.Forecast.Forecastday.Hour> hoursList,
                                 Context context) {
        this.hoursList = hoursList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_forecast_hourly, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherResponse.Forecast.Forecastday.Hour hour = hoursList.get(position);

        holder.tvTime.setText(Util.getHourFromDateString(hour.getTime()));

        Picasso.get().load("http://" + hour.getCondition().getIcon())
                .placeholder(R.drawable.ic_sunrise)
                .error(R.drawable.ic_sunrise)
                .into(holder.ivCondition);

        holder.tvTemp.setText(hour.getTemp_c() + " °C");
        holder.tvWind.setText(hour.getWind_mph());
        holder.tvWindDir.setText(hour.getWind_dir());
    }

    @Override
    public int getItemCount() {
        return hoursList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTime;
        private final ImageView ivCondition;
        private final TextView tvTemp;
        private final TextView tvWind;
        private final TextView tvWindDir;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivCondition = itemView.findViewById(R.id.ivCondition);
            tvTemp = itemView.findViewById(R.id.tvTemp);
            tvWind = itemView.findViewById(R.id.tvWind);
            tvWindDir = itemView.findViewById(R.id.tvDirection);
        }
    }
}
