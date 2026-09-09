package com.laporansiswa.app.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.laporansiswa.app.R;
import com.laporansiswa.app.model.ModelLaporan;
import com.laporansiswa.app.utils.KategoriHelper;

import java.util.List;

/**
 * Adapter kartu riwayat laporan.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<ModelLaporan> modelLaporan;
    private final Context mContext;
    private final HistoryAdapterCallback mAdapterCallback;

    public HistoryAdapter(Context context, List<ModelLaporan> modelLaporanList,
                          HistoryAdapterCallback adapterCallback) {
        this.mContext = context;
        this.modelLaporan = modelLaporanList;
        this.mAdapterCallback = adapterCallback;
    }

    public void setDataAdapter(List<ModelLaporan> items) {
        modelLaporan.clear();
        modelLaporan.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ModelLaporan data = modelLaporan.get(position);

        holder.tvKategori.setText(data.getKategori());
        holder.tvNama.setText(data.isAnonim()
                ? mContext.getString(R.string.pelapor_anonim)
                : mContext.getString(R.string.format_nama_kelas, data.getNama(), data.getKelas()));
        holder.tvDate.setText(data.getTanggal());
        holder.tvLokasi.setText(data.getLokasi());
        holder.tvIsiLaporan.setText(data.getIsiLaporan());

        holder.layoutHeader.setBackgroundColor(KategoriHelper.warnaKategori(mContext, data.getKategori()));
    }

    @Override
    public int getItemCount() {
        return modelLaporan.size();
    }

    public interface HistoryAdapterCallback {
        void onItemClicked(ModelLaporan modelLaporan);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvKategori, tvNama, tvDate, tvLokasi, tvIsiLaporan;
        public CardView cvHistory;
        public LinearLayout layoutHeader;

        public ViewHolder(View itemView) {
            super(itemView);
            tvKategori = itemView.findViewById(R.id.tvKategori);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);
            tvIsiLaporan = itemView.findViewById(R.id.tvIsiLaporan);
            cvHistory = itemView.findViewById(R.id.cvHistory);
            layoutHeader = itemView.findViewById(R.id.layoutHeader);

            cvHistory.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mAdapterCallback.onItemClicked(modelLaporan.get(position));
                }
            });
        }
    }

}
