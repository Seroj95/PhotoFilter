package com.example.photofilter

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.example.photofilter.Utils.BitmapUtils
import com.example.photofilter.Utils.NonSwipeableViewPager
import com.example.photofilter.adapter.ViewPagerAdapter
import com.example.photofilter.interfacee.EditImageFragmenListener
import com.example.photofilter.interfacee.FilterListFragmentListener
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), FilterListFragmentListener, EditImageFragmenListener {

    val SELECT_GALLERY_PERMISSION = 1000

    init {
System.loadLibrary("NativeImageProcessor")
    }


    internal var originalImage: Bitmap? = null
    internal lateinit var filteredImage: Bitmap
    internal lateinit var finalImage: Bitmap
    internal lateinit var filterListFragment: FilterListFragment
    internal lateinit var editImageFragment: EditImageFragment
    internal var brightnessFinal = 0
    internal var saturationFinal = 1.0f
    internal var contrasFinal = 1.0f

    object Main {
        internal val IMAGE_NAME = "flash.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Set Toolbra
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Photo Filter"
        loadImage()
      
    }

    private fun setupViewPager(viewPager: NonSwipeableViewPager?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        //add filter list fragment
        filterListFragment = FilterListFragment()
        filterListFragment.setListener(this)
        //add image fragment
        editImageFragment = EditImageFragment()
        editImageFragment.setListener(this)

        adapter.addFragment(filterListFragment, "FILTERS")
        adapter.addFragment(editImageFragment, "EDIT")
        viewPager!!.adapter = adapter
    }

    private fun loadImage() {
        originalImage = BitmapUtils.getBitmapFromAssets(this, Main.IMAGE_NAME, 300, 300)
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        finalImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.source.setImageBitmap(originalImage)

    }

    override fun onFilterSelected(filter: Filter) {
        resetControles()
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.source.setImageBitmap(filter.processFilter(filteredImage))
        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true)
    }

    private fun resetControles() {
        if (editImageFragment != null)
            editImageFragment.resetControles()
        brightnessFinal = 0
        saturationFinal = 1.0f
        contrasFinal = 1.0f


    }

    override fun onBrightnessChanged(brightnes: Int) {
        brightnessFinal = brightnes
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightnes))
        image_preview.source.setImageBitmap(
            myFilter.processFilter(
                finalImage.copy(
                    Bitmap.Config.ARGB_8888,
                    true
                )
            )
        )
    }

    override fun onSaturationChanged(saturation: Float) {
        saturationFinal = saturation
        val myFilter = Filter()
        myFilter.addSubFilter(SaturationSubfilter(saturation))
        image_preview.source.setImageBitmap(
            myFilter.processFilter(
                finalImage.copy(
                    Bitmap.Config.ARGB_8888,
                    true
                )
            )
        )
    }

    override fun onConstrantChanged(constrant: Float) {
        contrasFinal = constrant
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(constrant))
        image_preview.source.setImageBitmap(
            myFilter.processFilter(
                finalImage.copy(
                    Bitmap.Config.ARGB_8888,
                    true
                )
            )
        )


    }

    override fun onEditStarted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEditCompleted() {
        val bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true)
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(contrasFinal))
        myFilter.addSubFilter(SaturationSubfilter(saturationFinal))
        myFilter.addSubFilter(BrightnessSubFilter(brightnessFinal))
        finalImage = myFilter.processFilter(bitmap)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        if (id == R.id.action_open) {
            openImageFromGalery()
            return true
        } else if (id == R.id.action_save) {
            saveImageToGallery()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveImageToGallery() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val path = BitmapUtils.insertImage(
                            contentResolver,
                            finalImage,
                            System.currentTimeMillis().toString() + "_profile.jpg",
                            ""
                        )
                        if (!TextUtils.isEmpty(path)) {
                            val snackBar = Snackbar.make(
                                coordinator,
                                "Image saved to gallery",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("OPEN", {
                                    openImage(path)
                                })
                            snackBar.show()
                        } else {

                            val snackBar = Snackbar.make(
                                coordinator,
                                "Unable to save image",
                                Snackbar.LENGTH_LONG
                            )

                            snackBar.show()


                        }
                    } else {
                        Toast.makeText(applicationContext, "Permision denid", Toast.LENGTH_SHORT)
                            .show()

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            }).check()
    }

    private fun openImage(path: String?) {
val intent=Intent()
        intent.action=Intent.ACTION_VIEW
        intent.setDataAndType(Uri.parse(path),"image/*")
        startActivity(intent)
    }

    private fun openImageFromGalery() {
        //We  will use Dexter to request runtime permission and process
        val withListener = Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"

                        startActivityForResult(intent, SELECT_GALLERY_PERMISSION)
                    } else {
                        Toast.makeText(applicationContext, "permision", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_GALLERY_PERMISSION) {
            val bitmap = BitmapUtils.getBitmapFromGallery(this, data!!.data!!, 800, 800)
            //clear bitmap memory
            originalImage!!.recycle()
            finalImage!!.recycle()
            filteredImage!!.recycle()

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
            finalImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
            bitmap.recycle()
            //render select image thumb
            filterListFragment.displayImage(bitmap)


        }
    }

}
