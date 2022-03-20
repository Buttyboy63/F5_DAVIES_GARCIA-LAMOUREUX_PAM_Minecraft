package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer

import android.app.Activity
import android.app.Application
import android.content.Context
import android.widget.Toast

class ToastHelper {
    companion object {
        fun printToastShort(app: Application, resID: Int) {
                Toast.makeText(app, resID, Toast.LENGTH_SHORT)
                    .show()
        }

        fun printToastLong(app: Application, resID: Int) {
            Toast.makeText(app, resID, Toast.LENGTH_LONG)
                .show()
        }
    }
}
