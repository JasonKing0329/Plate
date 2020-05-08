package com.king.app.plate.utils;

import com.king.app.plate.conf.AppConfig;
import com.king.app.plate.model.BaseProperty;
import com.king.app.plate.model.db.AppDatabase;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataExporter {

    public static void exportDatabase() {
        String dbPath = AppDatabase.Companion.getDbFolder();
        String targetPath = AppConfig.Companion.getExportPath();
        try {
            copyDirectory(dbPath, targetPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void exportPreference() {
        String path = BaseProperty.Companion.getPrefFolder();
        String targetPath = AppConfig.Companion.getExportPath();
        try {
            copyDirectory(path, targetPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	public static void execute() {
        exportDatabase();
        exportPreference();
	}

	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {

		// 新建文件输入流并对它进行缓冲
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inbuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲
		FileOutputStream out = new FileOutputStream(targetFile);
		BufferedOutputStream outbuff = new BufferedOutputStream(out);

		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len = 0;
		while ((len = inbuff.read(b)) != -1) {
			outbuff.write(b, 0, len);
		}

		// 刷新此缓冲的输出流
		outbuff.flush();

		// 关闭流
		inbuff.close();
		outbuff.close();
		out.close();
		input.close();

	}

	public static void exportAsHistory() {
        exportDbAsHistory();
        exportPrefAsHistory();
	}

    public static void exportDbAsHistory() {
        String dbPath = AppDatabase.Companion.getDbPath();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String date = sdf.format(new Date());

        StringBuffer target = new StringBuffer();
        target.append(AppConfig.Companion.getHistoryDbPath()).append("/plate_");
        target.append(date);
        target.append(".db");
        try {
            copyFile(new File(dbPath), new File(target.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportPrefAsHistory() {
        String dbPath = BaseProperty.Companion.getPrefPath();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String date = sdf.format(new Date());

        StringBuffer target = new StringBuffer();
        target.append(AppConfig.Companion.getHistoryPrefPath()).append("/plate_");
        target.append(date);
        target.append(".xml");
        try {
            copyFile(new File(dbPath), new File(target.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static void copyDirectory(String sourceDir, String targetDir)
			throws IOException {
		DebugLog.e("copy from [" + sourceDir + "] to [" + targetDir + "]");
		// 新建目标目录
		File target = new File(targetDir);
		if (!target.exists()) {
			target.mkdirs();
		}

		// 获取源文件夹当下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		if (file == null) {
			return;
		}

		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(
						new File(targetDir).getAbsolutePath() + File.separator
								+ file[i].getName());

				copyFile(sourceFile, targetFile);

			}

			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();

				copyDirectory(dir1, dir2);
			}
		}

	}
}
