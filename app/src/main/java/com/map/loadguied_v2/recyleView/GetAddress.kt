package com.map.loadguied_v2.recyleView

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.map.loadguied_v2.R
import com.map.loadguied_v2.showGuide.showGuideMap
import java.io.File

class getAddress  : AppCompatActivity() {

    // RecyclerView 및 어댑터 선언
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_address_list)

        recyclerView = findViewById(R.id.Address_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 파일 목록을 가져와 어댑터에 전달하여 리사이클러뷰에 표시
        val fileList = getSavedFileList()
        adapter = FileAdapter(fileList)
        recyclerView.adapter = adapter
    }

    // 저장된 파일 목록을 가져오는 함수
    private fun getSavedFileList(): List<String> {
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "roadGuide")
        if (!dir.exists()) {
            return emptyList()
        }

        val fileList = dir.listFiles()?.map { it.name } ?: emptyList()
        Log.d("파일 목록", fileList.toString())
        return fileList
    }

    // 어댑터 클래스 정의
    class FileAdapter(private val fileList: List<String>) :
        RecyclerView.Adapter<FileAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val fileNameTextView: TextView = itemView.findViewById(R.id.place_name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val fileName = fileList[position]
            holder.fileNameTextView.text = fileName

            // 파일 클릭 이벤트를 여기에 추가하면 됩니다.
            holder.itemView.setOnClickListener {
                // 클릭된 파일에 대한 동작을 정의
                val intent = Intent(holder.itemView.context, showGuideMap::class.java)
                intent.putExtra("file_name", fileName)
                Toast.makeText(holder.itemView.context, "클릭된 파일: $fileName", Toast.LENGTH_SHORT).show()
                holder.itemView.context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return fileList.size
        }
    }
}