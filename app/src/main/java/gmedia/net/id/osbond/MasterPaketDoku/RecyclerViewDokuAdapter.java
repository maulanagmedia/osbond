package gmedia.net.id.osbond.MasterPaketDoku;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gmedia.net.id.osbond.ChangeToRupiah;
import gmedia.net.id.osbond.MainActivity;
import gmedia.net.id.osbond.R;
import gmedia.net.id.osbond.menuMidtrans.SetGetMasterPaketBaru;

public class RecyclerViewDokuAdapter extends RecyclerView.Adapter<RecyclerViewDokuAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SetGetMasterPaketBaru> rvData;
    private int lastPosition = -1;
    private ChangeToRupiah changeToRupiah;

    public RecyclerViewDokuAdapter(Context context, ArrayList<SetGetMasterPaketBaru> rvData) {
        this.context = context;
        this.rvData = rvData;
        changeToRupiah = new ChangeToRupiah();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView gambar;
        private TextView paket, keterangan, harga;
        private LinearLayout group;
        private RadioButton pilihan;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            gambar = (ImageView) itemView.findViewById(R.id.icon);
            paket = (TextView) itemView.findViewById(R.id.txtPaket);
            keterangan = (TextView) itemView.findViewById(R.id.txtKeterangan);
            harga = (TextView) itemView.findViewById(R.id.txtHarga);
            group = (LinearLayout) itemView.findViewById(R.id.layoutPilihPaket);
            group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastPosition = getAdapterPosition();
                    notifyDataSetChanged();

                }
            });
            pilihan = (RadioButton) itemView.findViewById(R.id.pilihan);
            /*pilihan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });*/
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_popup_menu_doku_baru, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewDokuAdapter.ViewHolder holder, final int position) {
        Picasso.with(context).load(rvData.get(position).getIcon()).into(holder.gambar);
        holder.paket.setText(rvData.get(position).getPaket());
        holder.keterangan.setText(rvData.get(position).getKeterangan());
        holder.harga.setText(changeToRupiah.ChangeToRupiahFormat(rvData.get(position).getHarga()));
        /*holder.group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                holder.pilihan.setChecked(true);
            }
        });*/
        if (lastPosition == -1) {
            MainActivity.kosong = true;
        }
        holder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPosition = position;
                if (rvData.get(position).getFlag().equals("1")) {
                    MainActivity.layoutDPCabang.setVisibility(View.GONE);
                    MainActivity.kosong = false;
                    MainActivity.posisiPaket = 1;
                } else {
                    MainActivity.layoutDPCabang.setVisibility(View.VISIBLE);
                    MainActivity.kosong = false;
                    MainActivity.posisiPaket = 2;
                }
                MainActivity.selectedPaket = rvData.get(position);
                notifyDataSetChanged();
            }
        });
        holder.pilihan.setChecked(lastPosition == position);

    }

    @Override
    public int getItemCount() {
        return rvData.size();
    }


}
