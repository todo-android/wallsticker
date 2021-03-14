package com.example.wallsticker.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.wallsticker.R
import com.example.wallsticker.Utilities.Const
import com.example.wallsticker.Utilities.InternetCheck
import com.example.wallsticker.ViewModel.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var offset = 0
    private lateinit var randomQuote: TextView
    private lateinit var shareTodayQuote: ImageView
    private var trying: Int = 0
    private lateinit var lightMode: Switch
    val styles = arrayOf("Light", "Dark", "System default")
    var firstcheck: Boolean = false
    private lateinit var internetCheck: InternetCheck

    private lateinit var mainViewModel: MainViewModel


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        initView(view)

        btn_Images.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
            findNavController().navigate(action)

            //ShareTask(context).execute ("https://media.tenor.com/images/2d7258334277cd56b0fca431286df23e/tenor.gif")

        }

        btn_Stickers.setOnClickListener {
            val actionToStickers = HomeFragmentDirections.actionHomeFragmentToStickersFragment()
            findNavController().navigate(actionToStickers)
        }

        btn_quotes.setOnClickListener {
            val goToQuotes = HomeFragmentDirections.actionHomeFragmentToHomeQuotes()
            findNavController().navigate(goToQuotes)
        }




        shareTodayQuote.setOnClickListener {
            var packageTxt: String? = ""
            if (Const.enable_share_with_package)
                packageTxt =
                    "\n" + resources.getString(R.string.share_text) + "\n${resources.getString(R.string.store_prefix) + context?.packageName}"
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, randomQuote.text.toString() + packageTxt)
            startActivity(Intent.createChooser(shareIntent, "Share To"))
        }


        mainViewModel.readtheme.observe(viewLifecycleOwner, { theme ->
            lightMode.isChecked = theme.contains("YES")
            if (lightMode.isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        })

        mainViewModel.readRandomQuote.observe(viewLifecycleOwner, { quote ->
            if (!quote.equals("none"))
                randomQuote.text = quote.toString()

        })
        lightMode.setOnCheckedChangeListener { buttonview, ischakced ->
            if (ischakced) {

                mainViewModel.saveToDataStore("YES")
            } else {

                mainViewModel.saveToDataStore("NO")
            }
        }

    }

    private fun initView(view: View) {
        randomQuote = view.findViewById(R.id.txt_today)
        shareTodayQuote = view.findViewById(R.id.share)
        lightMode = view.findViewById(R.id.modeChange)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)


    }


}