/*
 * Copyright 2017 Julia Kozhukhovskaya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appchamp.wordchunks.ui.customviews

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import kotlinx.android.synthetic.main.dialog_level_solved.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size


class LevelSolvedDialog : DialogFragment() {

    private var nextLevelColor: Int? = null // it's own color for daily puzzle, and for game finished
    private var isDaily: Boolean = false
    private var isAlreadySolved: Boolean = false

    companion object {
        @JvmStatic
        fun newInstance(color: Int, isDaily: Boolean, isAlreadySolved: Boolean = false): LevelSolvedDialog {
            val dialog = LevelSolvedDialog()
            val args = Bundle()
            args.putInt("color", color)
            args.putBoolean("isDaily", isDaily)
            args.putBoolean("isAlreadySolved", isAlreadySolved)
            dialog.arguments = args
            return dialog
        }
    }

    // Defines the listener interface with a method passing back data result.
    interface LevelSolvedDialogListener {
        fun onNextBtnClickedDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nextLevelColor = arguments.getInt("color")
        isDaily = arguments.getBoolean("isDaily")
        isAlreadySolved = arguments.getBoolean("isAlreadySolved")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.dialog_level_solved, container, false)
        // Set background transparent for rounded corners in the dialog.
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Return input text back to activity through the implemented listener
        val listener: LevelSolvedDialogListener = activity as LevelSolvedDialogListener

        btnNext.setOnClickListener { listener.onNextBtnClickedDialog() }

        btnNext.background.colorFilter = nextLevelColor?.let { PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN) }
        if (isDaily) {
            btnNext.text = "BACK"
            tvHintsEarned.text = "+ 3 HINTS ADDED!"

        } else {
            btnNext.text = "NEXT"
            if (isAlreadySolved) {
                tvHintsEarned.text = "LEVEL WAS ALREADY SOLVED!"
            } else {
                tvHintsEarned.text = "+ 2 HINTS ADDED!"
            }
        }

        viewKonfetti?.build()
                ?.addColors(
                        Color.parseColor("#fce18a"),
                        Color.parseColor("#ff726d"),
                        Color.parseColor("#b48def"),
                        Color.parseColor("#f4306d"))
                ?.setDirection(90.0)
                ?.setSpeed(4f, 7f)
                ?.setFadeOutEnabled(true)
                ?.setTimeToLive(15000L)
                ?.addShapes(Shape.RECT, Shape.CIRCLE)
                ?.addSizes(Size(8), Size(8, 5f))
                ?.setPosition(0f, 1100f, 0f, null)
                ?.stream(200, 15000L)
    }

    override fun onDestroy() {
        super.onDestroy()
//        viewKonfetti.destroyDrawingCache()
    }
}