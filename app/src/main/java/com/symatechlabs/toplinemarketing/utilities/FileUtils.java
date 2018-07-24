package com.symatechlabs.toplinemarketing.utilities;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;


public class FileUtils {
    private static FileUtils instance;

    private FileUtils() {
    }

    public static FileUtils getInstance() {
        if (instance == null) {
            instance = new FileUtils();
        }
        return instance;
    }

    public String getUriRealPath(Context ctx, Uri uri) {
        String ret = "";
        if (isAboveKitKat()) {
            return getUriRealPathAboveKitkat(ctx, uri);
        }
        return getImageRealPath(getContentResolver(ctx), uri, null);
    }

    @TargetApi(19)
    private String getUriRealPathAboveKitkat(Context ctx, Uri uri) {
        String ret = "";
        if (ctx == null || uri == null) {
            return ret;
        }
        if (isContentUri(uri)) {
            if (isGooglePhotoDoc(uri.getAuthority())) {
                return uri.getLastPathSegment();
            }
            return getImageRealPath(getContentResolver(ctx), uri, null);
        } else if (isFileUri(uri)) {
            return uri.getPath();
        } else {
            if (!isDocumentUri(ctx, uri)) {
                return ret;
            }
            String documentId = DocumentsContract.getDocumentId(uri);
            String uriAuthority = uri.getAuthority();
            String[] idArr;
            String realDocId;
            if (isMediaDoc(uriAuthority)) {
                idArr = documentId.split(":");
                if (idArr.length != 2) {
                    return ret;
                }
                String docType = idArr[0];
                realDocId = idArr[1];
                Uri mediaContentUri = Media.EXTERNAL_CONTENT_URI;
                if ("image".equals(docType)) {
                    mediaContentUri = Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(docType)) {
                    mediaContentUri = Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(docType)) {
                    mediaContentUri = Audio.Media.EXTERNAL_CONTENT_URI;
                }
                return getImageRealPath(getContentResolver(ctx), mediaContentUri, "_id = " + realDocId);
            } else if (isDownloadDoc(uriAuthority)) {
                return getImageRealPath(getContentResolver(ctx), ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId).longValue()), null);
            } else if (!isExternalStoreDoc(uriAuthority)) {
                return ret;
            } else {
                idArr = documentId.split(":");
                if (idArr.length != 2) {
                    return ret;
                }
                String type = idArr[0];
                realDocId = idArr[1];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + realDocId;
                }
                return ret;
            }
        }
    }

    private ContentResolver getContentResolver(Context context) {
        return context.getContentResolver();
    }

    private boolean isAboveKitKat() {
        return VERSION.SDK_INT >= 19;
    }

    @TargetApi(19)
    private boolean isDocumentUri(Context ctx, Uri uri) {
        if (ctx == null || uri == null) {
            return false;
        }
        return DocumentsContract.isDocumentUri(ctx, uri);
    }

    private boolean isContentUri(Uri uri) {
        if (uri == null) {
            return false;
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {

            return true;
        }
        return false;
    }

    private boolean isFileUri(Uri uri) {
        if (uri == null) {
            return false;
        }
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return true;
        }
        return false;
    }

    private boolean isExternalStoreDoc(String uriAuthority) {
        if ("com.android.externalstorage.documents".equals(uriAuthority)) {
            return true;
        }
        return false;
    }

    private boolean isDownloadDoc(String uriAuthority) {
        if ("com.android.providers.downloads.documents".equals(uriAuthority)) {
            return true;
        }
        return false;
    }

    private boolean isMediaDoc(String uriAuthority) {
        if ("com.android.providers.media.documents".equals(uriAuthority)) {
            return true;
        }
        return false;
    }

    private boolean isGooglePhotoDoc(String uriAuthority) {
        if ("com.google.android.apps.photos.content".equals(uriAuthority)) {
            return true;
        }
        return false;
    }

    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
        String ret = "";
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return ret;
        }
        String columnName = "_data";
        if (uri == Media.EXTERNAL_CONTENT_URI) {
            columnName = "_data";
        } else if (uri == Audio.Media.EXTERNAL_CONTENT_URI) {
            columnName = "_data";
        } else if (uri == Video.Media.EXTERNAL_CONTENT_URI) {
            columnName = "_data";
        }
        return cursor.getString(cursor.getColumnIndex(columnName));
    }
}
