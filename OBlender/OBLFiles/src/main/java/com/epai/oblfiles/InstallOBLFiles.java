package com.epai.oblfiles;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class InstallOBLFiles {
    private boolean copyAsset(AssetManager theAssetMgr,
                              String thePathFrom,
                              String thePathTo) {
        try (InputStream aStreamIn = theAssetMgr.open(thePathFrom)) {
            File aFileTo = new File(thePathTo);
            File parent = aFileTo.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                return false;
            }
            if (aFileTo.exists() && aFileTo.length() > 0) {
                return true;
            }
            try (OutputStream aStreamOut = new FileOutputStream(thePathTo)) {
                FileUtils.copyStreamContent(aStreamIn, aStreamOut);
                aStreamOut.flush();
            }
            return true;
        } catch (Exception theError) {
            theError.printStackTrace();
            return false;
        }
    }
    private boolean copyAssetFolder(AssetManager theAssetMgr,
                                    String theAssetFolder,
                                    String theFolderPathTo) {
        try {
            String[] aFiles = theAssetMgr.list(theAssetFolder);
            if (aFiles == null) {
                return false;
            }
            File aFolder = new File(theFolderPathTo);
            if (aFolder.exists()){
                return true;
            }
            if (!aFolder.exists() && !aFolder.mkdirs()) {
                return false;
            }
            boolean isOk = true;
            for (String aFileIter : aFiles) {
                String assetPath = theAssetFolder + "/" + aFileIter;
                if (theAssetMgr.list(assetPath).length<1) {
                    String stringFilePathTo = theFolderPathTo + "/" + aFileIter;
                    if (!FileUtils.exist(stringFilePathTo)) {
                        isOk &= copyAsset(theAssetMgr,
                                assetPath,
                                stringFilePathTo);
                    }
                } else {
                    isOk &= copyAssetFolder(theAssetMgr,
                            assetPath,
                            theFolderPathTo + "/" + aFileIter);
                }
            }
            return isOk;
        } catch (Exception theError) {
            theError.printStackTrace();
            return false;
        }
    }
    public class OBLFilePath{
        public String mStringHomePath;
        public String mStringConfigPath;
    }
    public OBLFilePath installOBLFiles(Context context){
        OBLFilePath oblFilePath=new OBLFilePath();
        AssetManager assetManager=context.getAssets();
        {
            //  创建home文件夹
            oblFilePath.mStringHomePath=FileUtils.getExternStorageDir(context,"")+ File.separator;

            String[] stringsAppRootFilesFolders = {
                    "examples","Desktop","Documents","Downloads","Music","Pictures","Videos"
            };
            for (String stringFolder : stringsAppRootFilesFolders) {
                copyAssetFolder(assetManager, stringFolder, oblFilePath.mStringHomePath  + stringFolder);
            }
        }
        {
            oblFilePath.mStringConfigPath = context.getFilesDir().getAbsolutePath()+ File.separator;
            String[] stringsAppRootFilesFolders = {
                    "python","4.0", "scripts","usd"
            };
            for (String stringFolder : stringsAppRootFilesFolders) {
                copyAssetFolder(assetManager, stringFolder, oblFilePath.mStringConfigPath  + stringFolder);
            }
            copyAsset(assetManager,
                    "scripts/obl_vn_startup.py",
                    oblFilePath.mStringConfigPath + "scripts" + File.separator + "obl_vn_startup.py");
            copyAsset(assetManager,
                    "scripts/startup/obl_vn_startup.py",
                    oblFilePath.mStringConfigPath + "scripts" + File.separator + "startup" + File.separator + "obl_vn_startup.py");
        }
        return oblFilePath;
    }
}
