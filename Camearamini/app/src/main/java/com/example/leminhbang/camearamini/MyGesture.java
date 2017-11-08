package com.example.leminhbang.camearamini;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;

import static com.example.leminhbang.camearamini.MainActivity.actionBar;
import static com.example.leminhbang.camearamini.MainActivity.context;

/**
 * Created by LE MINH BANG on 10/10/2017.
 */

public class MyGesture extends GestureDetector.SimpleOnGestureListener implements BottomSheetListener {
    @Override
    public boolean onDown(MotionEvent e) {
        return super.onDown(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        if(actionBar.isShowing()) {
            //an thanh action bar khi cham vao anh
            //getSupportActionBar().hide();
            actionBar.hide();
            //an thanh bottom navigation bar

        } else {
            actionBar.show();
        }
        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getY() - e2.getY() > 100 && Math.abs(velocityY) > 100) {
            new BottomSheet.Builder(context)
                    .setSheet(R.menu.menu_bottom_sheet)
                    .setTitle("")
                    .grid()
                    .setListener(this)
                    .show();
        }
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    @Override
    public void onSheetShown(@NonNull BottomSheet bottomSheet) {

    }

    @Override
    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.action_change_shade:
                Intent intent = new Intent(context,ChangeShadeActivity.class);
                context.startActivity(intent);
                break;
            case R.id.action_clarify_image:
                Intent intent1 = new Intent(context,ClarifyPortraitActivity.class);
                context.startActivity(intent1);
                break;
            case R.id.action_change_portrait:
                Intent intent2 = new Intent(context,ClarifyPortraitActivity.class);
                context.startActivity(intent2);
                break;
            case R.id.action_change_color_image:
                Intent intent3 = new Intent(context,ChangeColorActivity.class);
                context.startActivity(intent3);
                break;
        }
    }

    @Override
    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @DismissEvent int i) {

    }
}



