package com.lamadesign.smartalarm.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.lamadesign.smartalarm.ActivityHelpers.AlarmAdapter;
import com.lamadesign.smartalarm.ActivityHelpers.ClickListener;
import com.lamadesign.smartalarm.ActivityHelpers.DividerItemDecoration;
import com.lamadesign.smartalarm.ActivityHelpers.OnSwitchChange;
import com.lamadesign.smartalarm.ActivityHelpers.RecyclerTouchListener;
import com.lamadesign.smartalarm.Models.Alarm;
import com.lamadesign.smartalarm.R;
import com.lamadesign.smartalarm.Services.AlarmService;
import com.lamadesign.smartalarm.Utils.DBOperations;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Paint p = new Paint();
    private RecyclerView recyclerView;
    private List<Alarm> alarmList;
    private AlarmAdapter alarmAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, AlarmService.class));

        findViewById(R.id.activity_main_new_alarm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewAlarmActivity.class);
                startActivity(intent);
            }
        });

        setList();
    }

/*    private OnSwitchChange.OnItemSwitchCallback onItemSwitchCallback = new OnSwitchChange.OnItemSwitchCallback() {

        @Override
        public void onItemSwitched(CompoundButton compoundButton, Alarm alarm, boolean isChecked) {
            alarm.setSwitchOn(isChecked);
            DBOperations.updateAlarm(MainActivity.this, alarm);
        }
    };*/

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }



            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Alarm alarm = alarmList.get(position);
                if (direction == ItemTouchHelper.LEFT){
                    if(!alarm.isSwitchOn()){
                        alarm.setSwitchOn(true);
                        DBOperations.updateAlarm(MainActivity.this, alarm);
                    }
                } else {
                    if(alarm.isSwitchOn()){
                        alarm.setSwitchOn(false);
                        DBOperations.updateAlarm(MainActivity.this, alarm);
                    }
                }
                alarmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_alarm_off_white_36dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_alarm_on_white_36dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setList(){

        alarmList = DBOperations.getEvents(MainActivity.this);

        alarmAdapter = new AlarmAdapter(alarmList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.activity_main_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(alarmAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.hasFixedSize();
        initSwipe();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Alarm alarmFromCalendar = alarmList.get(position);
                Intent intent = new Intent(MainActivity.this, AlarmDetailActivity.class);
                intent.putExtra("id", alarmFromCalendar.alarmID);
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }

            /*@Override
            public void onLongClick(View view, int position) {
                final int p = position;
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        MainActivity.this);
                alert.setTitle(R.string.titleDialog);
                alert.setMessage(R.string.confirmMessage);
                alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Alarm alarm = alarmList.get(p);
                        DBOperations.deleteAlarm(MainActivity.this, alarm);
                        setList();
                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alert.show();

            }*/
        }));

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, AlarmSettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_account) {
            Intent intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_refresh) {
            setList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
