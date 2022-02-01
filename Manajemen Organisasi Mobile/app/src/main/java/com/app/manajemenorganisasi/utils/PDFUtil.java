package com.app.manajemenorganisasi.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class PDFUtil {
    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    public static void openGeneratedPDF(Context context, File file){
        Intent target = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        target.setDataAndType(uri, "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = Intent.createChooser(target, "Open File");

        try {
            context.startActivity(intent);
        }
        catch(ActivityNotFoundException e)
        {
            Toast.makeText(context, "Aplikasi tidak ditemukan, pastikan terdapat aplikas pembuka dokumen!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void createPdf(Context context, View view){
        Bitmap bitmap = PDFUtil.loadBitmapFromView(view, view.getWidth(), view.getHeight());
        try{
            //File temp = File.createTempFile("minat_bakat", ".pdf");
            File file = new File(context.getFilesDir(), "data");
            File temp = new File(file, "test.pdf");
            if(!file.exists()){
                file.mkdirs();
            }
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(temp));

            document.open();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Image image = Image.getInstance(byteArray);

            float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - 0)/ image.getWidth()) * 100;

            image.scalePercent(scaler);
            image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);

            document.add(image);
            document.close();

            openGeneratedPDF(context, temp);
        }catch (Exception e){
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
