package com.example.god.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

public boolean isSussess;

    private TextView txt_flag;
    public Handler handler1;
    public upload_thread mythread_upload;
    private Button upload;
    private ImageView image;
    private static final String NAMESPACE ="http://tempuri.org/"; //http://tempuri.org/
    // WebService地址
    private static String URL ="http://192.168.1.102:8086/WebService1.asmx?WSDL";
    private static final String METHOD_NAME ="FileUploadImage";
    private static String SOAP_ACTION ="http://tempuri.org/FileUploadImage";
    private static String PhotoName="";
    private final String IMAGE_TYPE = "image/*";
    private final int IMAGE_CODE = 0;
    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button makePhoto;
        Button select;
        image = (ImageView)this.findViewById(R.id.image);
        select=(Button)this.findViewById(R.id.select);

        makePhoto=(Button)this.findViewById(R.id.makephoto);
        upload=(Button)this.findViewById(R.id.upload);

        txt_flag=(TextView)findViewById(R.id.txt_flag);
        upload.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub




                handler1=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {

                        if(msg.what==0x12345){    //更新UI或其他操作

//                            String mythread_upload_flag =   mythread_upload.getList_result();
                            txt_flag.setText( mythread_upload.getList_result());




                        }
                    }
                };

                String str = testUpload();

                mythread_upload=new upload_thread("FileUploadImage",handler1);
                mythread_upload.setFileUploadImage_str(str);
                mythread_upload.start();







            }


            public String testUpload(){
                try{

                    String srcUrl =PhotoName; //"/mnt/sdcard/"; //路径
                    //String fileName = PhotoName+".jpg";  //文件名
                    FileInputStream fis = new FileInputStream(srcUrl);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int count = 0;
                    while((count = fis.read(buffer)) >= 0){
                        baos.write(buffer, 0, count);
                    }
                    String uploadBuffer = new String(Base64.encode(baos.toByteArray()));  //进行Base64编码
                    return uploadBuffer;

                }catch(Exception e){
                    e.printStackTrace();
                }
                //return soapObject;
                return null;

            }
//            private boolean connectWebService(String uploadBuffer) throws IOException {
//
//                SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
//                soapObject.addProperty("title", "");
//                soapObject.addProperty("contect","" );
//                soapObject.addProperty("bytestr", uploadBuffer);   //参数2  图片字符串
//                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
//                envelope.setOutputSoapObject(soapObject);
//                envelope.bodyOut = soapObject;
//                envelope.dotNet = true;
//                envelope.encodingStyle = SoapSerializationEnvelope.ENC;
//                HttpTransportSE httpTranstation = new HttpTransportSE(URL);
//                try {
//                    httpTranstation.call(SOAP_ACTION, envelope);
//                    return true;
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return false;
//                }
//
//
//            }

        });




        select.setOnClickListener(new Button.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
                getAlbum.setType(IMAGE_TYPE);
                startActivityForResult(getAlbum, IMAGE_CODE);

            }

        });

        makePhoto.setOnClickListener(new Button.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar ca = Calendar.getInstance();
                int year = ca.get(Calendar.YEAR);//获取年份
                int month=ca.get(Calendar.MONTH);//获取月份
                int day=ca.get(Calendar.DATE);//获取日
                int minute=ca.get(Calendar.MINUTE);//分
                int hour=ca.get(Calendar.HOUR);//小时
                int second=ca.get(Calendar.SECOND);//秒
                String fileName=String.valueOf(year)+String.valueOf(month)+String.valueOf(day)+String.valueOf(hour)+String.valueOf(minute)+String.valueOf(second);
                PhotoName="/mnt/sdcard/"+String.valueOf(year)+String.valueOf(month)+String.valueOf(day)+String.valueOf(hour)+String.valueOf(minute)+String.valueOf(second)+".jpg";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(),fileName+".jpg")));
                startActivityForResult(intent, 1);
            }


        });




    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != RESULT_OK) {        //此处的 RESULT_OK 是系统自定义得一个常量
            return;
        }
        if (requestCode == IMAGE_CODE) {


            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                //  text.setText(picturePath);
                PhotoName=picturePath;
                cursor.close();
                image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }
}

