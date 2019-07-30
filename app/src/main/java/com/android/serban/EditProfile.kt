package com.android.serban

import android.view.ContextMenu
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class EditProfile : AppCompatActivity() {

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        setContentView(R.layout.profile_edit)
    }
}