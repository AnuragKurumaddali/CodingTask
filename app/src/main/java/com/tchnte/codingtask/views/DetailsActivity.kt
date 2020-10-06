package com.tchnte.codingtask.views

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tchnte.codingtask.R
import com.tchnte.codingtask.customviews.CustomProgressDialog
import com.tchnte.codingtask.customviews.ZoomImageView
import com.tchnte.codingtask.roomdb.UserEntity
import kotlinx.android.synthetic.main.activity_details.*
import java.io.*
import java.util.*

class DetailsActivity : AppCompatActivity() {

    private var fileFromSource: File? = null
    private var uriFromSource: Uri? = null
    private var selectedImageBitmap: Bitmap? = null
    val REQUEST_PICTURE_FROM_GALLERY = 1
    val REQUEST_GALLERY_PERMISSION = 2
    private val progressDialog = CustomProgressDialog()

    private var userEntity: UserEntity? = null

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        if(intent.extras != null && intent.hasExtra("UserData")){
            userEntity = intent.extras?.get("UserData") as UserEntity?
        }
        populateData()
        ziv_Image.setZoomViewClickListener(object: ZoomImageView.ZoomViewClick {
            override fun onClicked() {
                if(askForPermissions())
                    galleryIntent()
            }
        })

        btnContinue.setOnClickListener {
            setDataInCenter()
            showProgressBar()
            Thread(Runnable {
                if(selectedImageBitmap != null)
                    saveImageToExternalStorage(ziv_Image)
            }).start()
        }
    }

    fun showProgressBar(){
        progressDialog.show(this,"Please Wait...")
    }

    fun hideProgressBar(){
        progressDialog.dialog.dismiss()
    }

    private fun populateData(){
        if(userEntity != null){
            tv_UserId.text = "Id : "+userEntity?.id.toString()
            tv_UserName.text = "Name : \n"+userEntity?.name.toString()
            tv_UserEmail.text = "E-mail : \n"+userEntity?.email.toString()
            tv_UserGender.text = "Gender : "+userEntity?.gender.toString()
            tv_UserStatus.text = "Status : "+userEntity?.status.toString()
        }
    }

    private fun setDataInCenter(){
        ll_Data.gravity = Gravity.CENTER
        tv_UserId.gravity = Gravity.CENTER
        tv_UserName.gravity = Gravity.CENTER
        tv_UserEmail.gravity = Gravity.CENTER
        tv_UserGender.gravity = Gravity.CENTER
        tv_UserStatus.gravity = Gravity.CENTER
        if(userEntity != null){
            tv_UserId.text = "Id\n"+userEntity?.id.toString()
            tv_UserName.text = "Name\n"+userEntity?.name.toString()
            tv_UserEmail.text = "E-mail\n"+userEntity?.email.toString()
            tv_UserGender.text = "Gender\n"+userEntity?.gender.toString()
            tv_UserStatus.text = "Status\n"+userEntity?.status.toString()
        }
    }

    private fun galleryIntent() {
        if (fileFromSource == null) {
            try {
                fileFromSource = File.createTempFile("pic", "png", applicationContext.externalCacheDir)
                uriFromSource = Uri.fromFile(fileFromSource)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFromSource)
        startActivityForResult(
            intent,
            REQUEST_PICTURE_FROM_GALLERY
        )
    }

    private fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this as Activity,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(this as Activity,arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE),REQUEST_GALLERY_PERMISSION)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        when (requestCode) {
            REQUEST_GALLERY_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                    galleryIntent()
                } else {
                    // permission is denied, you can ask for permission again, if you want
                      askForPermissions()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel",null)
            .show()
    }

    private fun onSelectFromGalleryResult(data: Intent?) {
        if (data != null) {
            try {
                ziv_Image.resetImage()
                btnContinue.isEnabled = true
                selectedImageBitmap = if(Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(
                        applicationContext.contentResolver,
                        data.data!!
                    )
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, data.data!!)
                    ImageDecoder.decodeBitmap(source)
                }
                ziv_Image.setImageBitmap(selectedImageBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICTURE_FROM_GALLERY) onSelectFromGalleryResult(
                data
            )
        }
    }

    // Method to save an image to external storage
    private fun saveImageToExternalStorage(itemImage : View){
        val bitmap = getBitmapFromView(itemImage)
        val path = Environment.getStorageDirectory().absolutePath + "/emulated/0/"+Environment.DIRECTORY_DOWNLOADS
        val file = File(path, "touchnote_android_task_${(System.currentTimeMillis()/1000)}.png")
        downloadImageWithMediaStore(bitmap,file.name,path)
    }

    private fun downloadImageWithMediaStore(finalBitmap: Bitmap,fileName : String,filePath: String) {
        try {
            val fos: OutputStream
            fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver: ContentResolver = contentResolver
                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                contentValues.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS
                )
                val imageUri: Uri =
                    resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)!!
                resolver.openOutputStream(Objects.requireNonNull(imageUri))!!
            } else {
                val image = File(filePath, fileName)
                FileOutputStream(image)
            }
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            Objects.requireNonNull(fos).close()
            runOnUiThread(Runnable {
                hideProgressBar()
                Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show()
            })
        } catch (e: IOException) {
            e.printStackTrace()
            runOnUiThread(Runnable {
                hideProgressBar()
                Toast.makeText(this, "Error in Saving", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getBitmapAboveO(view, bitmapCallback = {

            })
        }
        else{
            return Bitmap.createBitmap(
                selectedImageBitmap?.width!!,
                selectedImageBitmap?.height!!,
                Bitmap.Config.ARGB_8888
            ).apply {
                Canvas(this).apply {
                    view.draw(this)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBitmapAboveO(view: View, bitmapCallback: (Bitmap)->Unit) : Bitmap{
        // Above Android O, use PixelCopy
        val bitmap = Bitmap.createBitmap(selectedImageBitmap?.width!!, selectedImageBitmap?.height!!, Bitmap.Config.ARGB_8888)
        val location = IntArray(2)
        view.getLocationInWindow(location)
        PixelCopy.request(window,
            Rect(location[0], location[1], location[0] + view.width, location[1] + view.height),
            bitmap,
            {
                if (it == PixelCopy.SUCCESS) {
                    bitmapCallback.invoke(bitmap)
                }
            },
            Handler(Looper.getMainLooper()) )
        return bitmap
    }

}