package com.symatechlabs.toplinemarketing.utilities;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class mediaFunctions {

	Context context;
	final int REQUIRED_SIZE = 140;
	public Activity activity;
	File file;
	FileOutputStream fos;
	ByteArrayOutputStream bos;
    Utilities util;
	String fileName = null;

	public mediaFunctions(Context context) {

		this.context = context;
		activity = (Activity) context;
		util = new Utilities();


	}

	public Bitmap downSampleImage(Uri selectedImage) {

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(selectedImage), null, o);

			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE) {
					break;
				}
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(selectedImage), null, o2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.d("FILE_ERROR : ", e.getMessage());
		}
		return null;
	}

	public String getGalleryPhotoPath(Uri uri) {
		try {
			if (uri == null) {
				// TODO perform some logging or show user feedback
				return null;
			}

			String[] projection = { MediaStore.Images.Media.DATA };
			@SuppressWarnings("deprecation")
			Cursor cursor = this.activity.managedQuery(uri, projection, null,
					null, null);
			if (cursor != null) {
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				return cursor.getString(column_index);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return uri.getPath();
	}


	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	public String getRealPathFromURI(Uri uri) {
		Cursor cursor = this.context.getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}

	public File createFileFromBitMap(Bitmap bitMap) throws IOException {
		
		file = new File(this.context.getCacheDir(), fileName );

		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		bos = new ByteArrayOutputStream();
		bitMap.compress(CompressFormat.JPEG, 25, bos);
		
		byte[] bitmapdata = bos.toByteArray();
		

		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fos.write(bitmapdata);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fos.flush();
		fos.close();

		return file;

	}
	
	public File getTempFile(Context context) {


		File path = new File(android.os.Environment.getExternalStorageDirectory(),
				context.getPackageName());
		if (!path.exists()) {
			path.mkdir();
		}
		fileName = util.randomString(8)+util.getDate()+".jpg";
		return new File(path, fileName);
	}

	private File createImageFile(Context context) throws IOException {
		// Create an image file name
		String mCurrentPhotoPath;
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}



}
