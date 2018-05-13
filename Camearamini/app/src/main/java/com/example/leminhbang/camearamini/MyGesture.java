package com.example.leminhbang.camearamini;

import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;

import static com.example.leminhbang.camearamini.MainActivity.actionBar;
import static com.example.leminhbang.camearamini.MainActivity.bitmapMain;
import static com.example.leminhbang.camearamini.MainActivity.bitmapTemp;
import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.showDialogSave;

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
        /*if (bitmapMain != null) {
            //bottomSheet.invalidateOptionsMenu();
            Drawable d = new BitmapDrawable(context.getResources(),
                    bitmapMain);
            MenuItem item1 = (MenuItem) bottomSheet.findViewById(
                    R.id.action_change_shade);
            item1.setIcon(d);
            MenuItem item2 = (MenuItem) bottomSheet.findViewById(
                    R.id.action_clarify_image);
            item2.setIcon(d);
            MenuItem item3 = (MenuItem) bottomSheet.findViewById(
                    R.id.action_change_portrait);
            item3.setIcon(d);
            MenuItem item4 = (MenuItem) bottomSheet.findViewById(
                    R.id.action_change_color_image);
            item4.setIcon(d);
        }*/
    }

    @Override
    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (bitmapMain != null) {
            switch (id) {
                case R.id.action_change_shade:
                    showDialogSave(bitmapTemp,InterfaceClass.ChangeShadeClass);
                    break;
                case R.id.action_clarify_image:
                    showDialogSave(bitmapTemp,InterfaceClass.ClarifyClass);
                    break;
                case R.id.action_change_portrait:
                    showDialogSave(bitmapTemp,InterfaceClass.ChangePortrait);
                    break;
                case R.id.action_change_color_image:
                    showDialogSave(bitmapTemp,InterfaceClass.ChangeColorClass);
                    break;
            }
        }
    }

    @Override
    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @DismissEvent int i) {

    }
}




