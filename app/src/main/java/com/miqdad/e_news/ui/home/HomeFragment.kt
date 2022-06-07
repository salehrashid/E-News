package com.miqdad.e_news.ui.home


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.miqdad.e_news.R
import com.miqdad.e_news.data.network.ArticlesItem
import com.miqdad.e_news.databinding.FragmentHomeBinding
import com.miqdad.e_news.ui.NewsAdapter
import com.miqdad.e_news.ui.OnItemClickCallback
import com.miqdad.e_news.ui.detail.DetailActivity

class HomeFragment : Fragment() {
    ///
    private val channelId = "channel_01"
    private val notificationId = 101
    ///

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var _viewModel: HomeViewModel? = null
    private val viewModel get() = _viewModel as HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        viewModel.getTopHeadlineNews("ID")
        viewModel.topHeadlineResponse.observe(viewLifecycleOwner){showData(it.articles as List<ArticlesItem>)}

        activity?.actionBar?.hide()

        //////
        createNotificationChannel()
        binding.btnExit.setOnClickListener {
            sendNotification()
        }
        ////
        return binding.root
    }

    private fun showData(data: List<ArticlesItem>) {
        binding.rvNews.apply {
            val mAdapter = NewsAdapter()
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            Log.i("apiData", "showData: $data")
            adapter = mAdapter
            mAdapter.setData(data)
            mAdapter.setOnItemClickCallback(object : OnItemClickCallback{
                override fun onItemClicked(item: ArticlesItem) {
                    startActivity(
                        Intent(context, DetailActivity::class.java)
                            .putExtra("EXTRA_DATA", item)
                    )
                }
            })
        }
    }

    private fun showError(isError: Throwable?) {
        Log.e("MainActivity", "Error get data ${isError.toString()}")
    }

    private fun showLoading(isLoading: Boolean?) {
        if(isLoading == true){
            binding.progressMain.visibility = View.VISIBLE
        } else {
            binding.progressMain.visibility = View.INVISIBLE
        }
    }

    //notif
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Text"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    private fun sendNotification(){
        val builder = context?.let {
            NotificationCompat.Builder(it, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Example title")
                .setContentText("example description")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }

        with(NotificationManagerCompat.from(this)){
            builder?.let { notify(notificationId, it.build()) }
        }
    }
}