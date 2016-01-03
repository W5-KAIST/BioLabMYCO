package kr.ac.kaist.mr.biolab_myco;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//import android.util.Log;
//import android.view.animation.AlphaAnimation;
//import android.view.animation.Animation;

/**
 * Created by wjuni on 15. 11. 20..
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.PersonViewHolder>{

    List<DisplayItem> dp_items;
    ArrayList<PersonViewHolder> view_items = new ArrayList<>();
    ArrayList<Integer> view_items_pos = new ArrayList<>();
    private LayoutInflater layoutInflater;
    RecyclerViewAdapter(List<DisplayItem> disp, Context context){
        dp_items = new ArrayList<DisplayItem>();
        dp_items.addAll(disp);
            layoutInflater = LayoutInflater.from(context);

        }


    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item, parent, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        view_items.add(i, personViewHolder);
        personViewHolder.tv_main.setText(dp_items.get(i).name);
        personViewHolder.tv_sub.setText(dp_items.get(i).sub);
        personViewHolder.tv_add1.setText(dp_items.get(i).add1);
        personViewHolder.tv_add2.setText(dp_items.get(i).add2);
        personViewHolder.itemView.setTag(dp_items.get(i));
        if(!dp_items.get(i).isGreen)
            personViewHolder.img_color.setImageResource(R.drawable.circle_red);
        else
            personViewHolder.img_color.setImageResource(R.drawable.circle_green);
        Log.e("ADD", "ADD " + dp_items.get(i).name);

    }
    public void add(DisplayItem item, int position) {
        dp_items.add(position, item);

        notifyItemInserted(position);
    }

    public void remove(DisplayItem item) {
        int position = dp_items.indexOf(item);
        dp_items.remove(position);
        notifyItemRemoved(position);
    }
    public void update(DisplayItem item, int position) {
        dp_items.set(position, item);
        notifyItemChanged(position);
    }
    @Override
    public int getItemCount() {
        return dp_items.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        TextView tv_main;
        TextView tv_sub;
        ImageView img_color;
        private int originalHeight = 0;
        private boolean mIsViewExpanded = false;
        private View mSplit;
        private LinearLayout mAdditionalView;
        private TextView tv_add1;
        private TextView tv_add2;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            tv_main = (TextView)itemView.findViewById(R.id.tv_main);
            tv_sub = (TextView)itemView.findViewById(R.id.tv_sub);
            mSplit = itemView.findViewById(R.id.primerdivisor);
            mAdditionalView = (LinearLayout) itemView.findViewById(R.id.layout_additional);
            tv_add1 = (TextView) itemView.findViewById(R.id.tv_detail_1);
            tv_add2 = (TextView) itemView.findViewById(R.id.tv_detail_2);
            img_color = (ImageView) itemView.findViewById(R.id.img_color);
            cv.setOnClickListener(this);

            if (mIsViewExpanded == false) {
                hideDetail();
            }
        }
        public void showDetail(){
            mSplit.setVisibility(View.VISIBLE);
            mAdditionalView.setVisibility(View.VISIBLE);
        }
        public void hideDetail(){
            mSplit.setVisibility(View.GONE);
            mAdditionalView.setVisibility(View.GONE);
        }
        @Override
        public void onClick(final View view) {
            // If the originalHeight is 0 then find the height of the View being used
            // This would be the height of the cardview
            if (originalHeight == 0) {
                originalHeight = view.getHeight();
//                Log.e(this.getClass().getName(), "Height=" + originalHeight);
            }
            // Declare a ValueAnimator object
            ValueAnimator valueAnimator;
            if (!mIsViewExpanded) {
//                cv.setVisibility(View.VISIBLE);
                cv.setEnabled(true);
                mIsViewExpanded = true;
                showDetail();
                valueAnimator = ValueAnimator.ofInt(originalHeight, originalHeight + (int) (originalHeight * 1.5)); // These values in this method can be changed to expand however much you like
            } else {
                mIsViewExpanded = false;
                hideDetail();
                valueAnimator = ValueAnimator.ofInt(originalHeight + (int) (originalHeight * 1.5), originalHeight);

//                Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out
//
//                a.setDuration(200);
//                // Set a listener to the animation and configure onAnimationEnd
//                a.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
////                        cv.setVisibility(View.INVISIBLE);
////                        cv.setEnabled(false);
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//
//                    }
//                });
//
//                // Set the animation on the custom view
//                cv.startAnimation(a);
            }
            valueAnimator.setDuration(200);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    view.getLayoutParams().height = value.intValue();
                    view.requestLayout();
                }
            });
            valueAnimator.start();

        }

    }
}
