package com.nadafeteiha.drawingapp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.nadafeteiha.drawingapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() , View.OnClickListener{

    private lateinit var binding: ActivityMainBinding
    private var lastToolSelected:Int = Constants.ACTION_PENCIL
    // arraylist contains all imageButton to control selector
    private val tools = ArrayList<ImageButton>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
        setDefaultLayout(lastToolSelected)
    }

    private fun initLayout(){
        tools.add(0,binding.ibPencil)
        tools.add(1,binding.ibRectangle)
        tools.add(2,binding.ibArrow)
        tools.add(3,binding.ibEllipse)
        tools.add(4,binding.ibColorPalette)

        binding.ibPencil.setOnClickListener(this)
        binding.ibRectangle.setOnClickListener(this)
        binding.ibArrow.setOnClickListener(this)
        binding.ibEllipse.setOnClickListener(this)
        binding.ibColorPalette.setOnClickListener(this)
    }

    // set the pencil the default tool for use
    private fun setDefaultLayout(resID: Int){
        selectToolView(resID)
        binding.drawingView.setDrawingTool(resID)
        binding.llColor.visibility = View.INVISIBLE
    }

    // this function is selector
    // highlight the selected tool by change vector image color and background
    private fun selectToolView(selector: Int){
        var color: Int
        for (tool in tools){
             if (tool.id == selector) {
                 color =  R.color.black
                 tool.setBackgroundResource(R.drawable.rounded_corner_btn)
             } else{
                 color = R.color.grey
                 tool.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
             }
            DrawableCompat.setTint(tool.drawable,ContextCompat
                    .getColor(this,color))
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ib_pencil , R.id.ib_rectangle , R.id.ib_arrow , R.id.ib_ellipse-> {
                lastToolSelected = v.id
                selectToolView(v.id)
                binding.drawingView.setDrawingTool(v.id)
                binding.llColor.visibility = View.INVISIBLE
            }
            R.id.ib_color_palette ->{
                selectToolView(v.id)
                binding.drawingView.setDrawingTool(v.id)
                binding.llColor.visibility = View.VISIBLE
            }
        }
    }

     fun getSelectedColor(v: View) {
        binding.drawingView.setColor(v.tag.toString())
        binding.llColor.visibility = View.INVISIBLE
        setDefaultLayout(lastToolSelected)
    }
}