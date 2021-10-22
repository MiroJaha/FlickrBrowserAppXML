package com.example.flickrbrowserappxml

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var baseLink: String
    private var call: Call<Rsp?>? =null

    private lateinit var list: ArrayList<Data>
    private lateinit var favoriteList: ArrayList<Data>
    private lateinit var rvMain: RecyclerView
    private lateinit var favoriteShowRV: RecyclerView
    private lateinit var rvAdapter: RVAdapter
    private lateinit var rvFavoriteAdapter: RVAdapter
    private lateinit var llBottom: LinearLayout
    private lateinit var etWord: EditText
    private lateinit var btSearch: Button
    private lateinit var moreImage: ImageView
    private lateinit var search: String
    private var count= 10
    private var mode= 1
    private var mode2=1
    private lateinit var photosGrid: GridView
    private lateinit var gridAdapter: GridAdapter
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.favorite -> {
                if (mode==1) {
                    this@MainActivity.title= "Favorites"
                    startFavorite()
                    item.setIcon(R.drawable.blank_heart)
                    favoriteShowRV.isVisible= true
                    rvMain.isVisible= false
                    moreImage.isVisible= false
                    llBottom.isVisible= false
                    photosGrid.isVisible= false
                    mode=2
                }
                else{
                    this@MainActivity.title = "FlickrBrowserApp"
                    endFavorite()
                    item.setIcon(R.drawable.full_heart)
                    favoriteShowRV.isVisible = false
                    moreImage.isVisible = true
                    llBottom.isVisible = true
                    mode= 1
                    if (mode2==1) {
                        rvMain.isVisible = true
                    }
                    else{
                        photosGrid.isVisible= true
                    }
                }
                return true
            }
            R.id.viewWay -> {
                startFavorite()
                this@MainActivity.title = "FlickrBrowserApp"
                endFavorite()
                mode= 1
                favoriteShowRV.isVisible = false
                moreImage.isVisible = true
                llBottom.isVisible = true
                if (mode2==1){
                    item.setIcon(R.drawable.list_view)
                    photosGrid.isVisible= true
                    rvMain.isVisible= false
                    mode2= 2
                }
                else{
                    item.setIcon(R.drawable.grid_view)
                    photosGrid.isVisible= false
                    rvMain.isVisible= true
                    mode2= 1
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun endFavorite() {
        for (image in list){
            image.checkBox= false
        }
        for (image in list)
            for (image2 in favoriteList)
                if (image.id == image2.id && image2.secret == image.secret)
                    image.checkBox= true
        rvAdapter.update()
        gridAdapter.notifyDataSetChanged()
    }

    private fun startFavorite() {
        for (image in list) {
            for (image2 in list){
                if (image.id == image2.id && image2.secret == image.secret)
                    image2.checkBox = image.checkBox
            }
        }
        favoriteList.removeAll { !it.checkBox }
        rvFavoriteAdapter.update()
        for (image in list){
            if (image.checkBox) {
                var check= false
                for (image2 in favoriteList)
                    if (image.id == image2.id && image2.secret == image.secret)
                        check= true
                if (!check)
                    favoriteList.add(image)
            }
        }
        for (image in favoriteList){
            image.checkBox =true
        }
        rvFavoriteAdapter.update()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list= arrayListOf()
        favoriteList= arrayListOf()
        rvMain = findViewById(R.id.rvMain)
        favoriteShowRV= findViewById(R.id.favoriteShowRV)
        etWord = findViewById(R.id.etWord)
        btSearch = findViewById(R.id.btSearch)
        moreImage= findViewById(R.id.moreImages)
        llBottom= findViewById(R.id.llBottom)
        photosGrid = findViewById(R.id.imageGrid)

        rvFavoriteAdapter= RVAdapter(favoriteList,2)
        favoriteShowRV.adapter= rvFavoriteAdapter
        favoriteShowRV.layoutManager= LinearLayoutManager(this)

        rvAdapter = RVAdapter(list,1)
        rvMain.adapter = rvAdapter
        rvMain.layoutManager = LinearLayoutManager(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please Wait:")
        progressDialog.setCancelable(false)

        btSearch.setOnClickListener {
            if(etWord.text.isNotEmpty()) {
                progressDialog.show()
                count= 10
                search= etWord.text.toString().replace(" ","&")
                search= search.replace(",","%2C")
                Log.d("MyData",search)
                Data.search= search
                Data.count= count
                updateList()
                moreImage.isVisible= true
            }
            else{
                Toast.makeText(this, "Please Enter Something", Toast.LENGTH_SHORT).show()
            }
        }

        moreImage.setOnClickListener{
            progressDialog.show()
            count+=10
            Data.count= count
            updateList()
        }

        gridAdapter = GridAdapter(this,list)
        photosGrid.adapter = gridAdapter
        photosGrid.setOnItemClickListener {
                _, _, position, _ ->
            Log.d("MyData","$position")
            val showImage= list[position]
            val intent= Intent(this@MainActivity,ImageShow::class.java)
            intent.putExtra("title",showImage.title)
            intent.putExtra("serverID",showImage.server)
            intent.putExtra("photoID",showImage.id)
            intent.putExtra("secretNumber",showImage.secret)
            startActivity(intent)
        }

        rvAdapter.setOnItemClickListener(object : RVAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val showImage= list[position]
                val intent= Intent(this@MainActivity,ImageShow::class.java)
                intent.putExtra("title",showImage.title)
                intent.putExtra("serverID",showImage.server)
                intent.putExtra("photoID",showImage.id)
                intent.putExtra("secretNumber",showImage.secret)
                startActivity(intent)
            }
        })

        rvFavoriteAdapter.setOnItemClickListener(object : RVAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val showImage= favoriteList[position]
                val intent= Intent(this@MainActivity,ImageShow::class.java)
                intent.putExtra("title",showImage.title)
                intent.putExtra("serverID",showImage.server)
                intent.putExtra("photoID",showImage.id)
                intent.putExtra("secretNumber",showImage.secret)
                startActivity(intent)
            }
        })
    }

    private fun scrollDown() {
        rvMain.scrollToPosition(list.size - 10)
    }

    private fun updateList (){
        baseLink= "https://www.flickr.com/services/rest/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseLink)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
        val feedAPI = retrofit.create(APIInterface::class.java)
        call = feedAPI.rsp("?method=flickr.photos.search&api_key=44492e6cc42daf626226793468b43635&tags=${Data.search}&per_page=${Data.count}&format=rest")
        Log.d("MyData","$retrofit $feedAPI $call")
        callingData()

    }

    private fun callingData() {
        call!!.enqueue(object : Callback<Rsp?> {
            override fun onResponse(call: Call<Rsp?>, response: Response<Rsp?>) {
                list.clear()
                //Log.d(tag, "onResponse: feed: " + response.body().toString())
                //Log.d(tag, "onResponse: Server Response: $response")
                val photos = response.body()!!.photo
                Log.d("MyData","${response.body()!!} $photos $call")
                for (photo in photos!!) {
                    Log.d("MyData", "onResponse: " + photo.title)
                    val photoTitle = photo.title!!
                    val photoServer = photo.server!!
                    val photoID = photo.id!!
                    val photoSecret = photo.secret!!
                    var checkBox = false
                    for (image in favoriteList)
                        if (photoID == image.id && photoSecret == image.secret)
                            checkBox = true
                    Log.d("MyData", "$photoTitle, $photoServer, $photoID, $photoSecret, $checkBox")
                    list.add(Data(photoTitle, photoServer, photoID, photoSecret, checkBox))
                }
                rvAdapter.update()
                gridAdapter.notifyDataSetChanged()
                etWord.text.clear()
                val view: View? = this@MainActivity.currentFocus
                if (view != null) {
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                if (list.size > 10)
                    scrollDown()
                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<Rsp?>, t: Throwable) {
                Log.e("MyData", "onFailure: Unable to retrieve RSS: " + t.message)
                Toast.makeText(this@MainActivity, "An Error Occurred", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        })
    }
}