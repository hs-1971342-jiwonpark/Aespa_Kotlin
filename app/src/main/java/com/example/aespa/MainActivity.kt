package com.example.aespa
import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest
import android.app.AlertDialog
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.aespa.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.videobtn.setOnClickListener{
            dispatchTakeVideoIntent()
        }
    }

    private val REQUEST_VIDEO_CAPTURE = 1

    private fun dispatchTakeVideoIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_MEDIA_VIDEO)) {
                // Show an explanation to the user *asynchronously*.
                AlertDialog.Builder(this)
                    .setTitle("권한 필요")
                    .setMessage("이 기능을 사용하려면 외부 저장소 접근 권한이 필요합니다.")
                    .setPositiveButton("확인") { _, _ ->
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_MEDIA_VIDEO),
                            REQUEST_VIDEO_CAPTURE)
                    }
                    .setNegativeButton("취소", null)
                    .create()
                    .show()

            } else {
                // "Never ask again" selected, guide user to settings
                AlertDialog.Builder(this)
                    .setTitle("권한 설정")
                    .setMessage("외부 저장소 접근 권한이 필요합니다. 설정 메뉴로 이동하여 권한을 활성화해주세요.")
                    .setPositiveButton("설정으로 이동") { _, _ ->
                        startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)))
                    }
                    .setNegativeButton("취소", null)
                    .create()
                    .show()
            }
        } else {
            val takeVideoIntent = Intent(Intent.ACTION_GET_CONTENT)
            takeVideoIntent.type = "video/*"
            if (takeVideoIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            val videoUri: Uri? = data?.data
            // videoUri를 사용하여 동영상을 가져옵니다.

            videoUri?.let {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(this, it)
                val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()
                val minutes = duration?.div((1000 * 60))
                val seconds = (duration?.div(1000))?.rem(60)
                binding.textView.text = ""
                val cursor = contentResolver.query(videoUri, null, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        val fileName = cursor.getString(nameIndex)
                        binding.textView.append("File Name : $fileName")
                    }
                    cursor.close()
                }
                binding.textView.append("\nDuration : $minutes 분 $seconds 초")
            }
        }
    }





}
