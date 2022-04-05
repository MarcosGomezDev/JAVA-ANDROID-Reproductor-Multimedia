package com.example.mireproductormultimedia;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.PointerIcon;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener{

    private ImageButton btPlayPause, btRewind, btAdvance, btRec;
    private MediaPlayer mediaPlayer= null;
    private MediaRecorder mediaRecorder= null;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private boolean permissionToRecordAccepted = false;
    private boolean infoRec;
    private boolean preparedPlay;
    private final String pathString = "/data/data/com.example.mireproductormultimedia/files/Grabacion.3gp";
    private final Uri pathURI = Uri.parse("/data/data/com.example.mireproductormultimedia/files/Grabacion.3gp");
    private File data;

    /**
     * Se llama cuando se crea por primera vez la actividad. Aquí es donde debe realizar toda su
     * configuración estática normal: crear vistas, vincular datos a listas, etc.
     * @param savedInstanceState Si la actividad se reinicia después de haber sido cerrada
     *                           anteriormente, este paquete contiene los datos que proporcionó más recientemente .
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = new MediaPlayer();
        btPlayPause = findViewById(R.id.btPlayPause);
        btRewind = findViewById(R.id.btRewind);
        btAdvance = findViewById(R.id.btAdvance);
        btRec = findViewById(R.id.btRec);
        mediaPlayer.setOnCompletionListener(this);
        infoRec = true;

        existData();
    }

    /**
     * Comprueba si existe un archivo para reproducir.
     */
    public void existData() {
        data = new File(pathString);
        if(data.exists()) {
            try {
                mediaPlayer.setDataSource(this, pathURI);
                mediaPlayer.prepare();
                preparedPlay = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Existe una grabación", Toast.LENGTH_SHORT).show();
            enableButton(true, false, false, true);
        }else {
            preparedPlay = true;
            Toast.makeText(this, "No existe una grabación", Toast.LENGTH_SHORT).show();
            enableButton(false, false, false, true);
        }
    }

    /**
     * Evento de clic para los botones de la aplicación.
     * @param v Vista en la que se hizo clic.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btPlayPause:
                if(mediaPlayer.isPlaying()) {
                    pause();
                    btPlayPause.setImageResource(R.drawable.outline_play_circle_filled_white_48);
                }else {
                    play();
                    btPlayPause.setImageResource(R.drawable.outline_pause_circle_filled_white_48);
                }
                break;
            case R.id.btRewind:
                rewind();
                break;
            case R.id.btAdvance:
                advance();
                break;
            case R.id.btRec:
                if(infoRec == true) {
                    btRec.setImageResource(R.drawable.outline_stop_circle_white_48);
                    startRec();
                }else {
                    btRec.setImageResource(R.drawable.outline_fiber_manual_record_white_48);
                    stopRec();
                }
                break;
            case R.id.btSalir:
                exit();
                break;
        }
    }

    /**
     * Estable ce el estado enable/disable de los botones y determina su transpariencia según si
     * están activados o desactivados.
     * @param boolPlay Parametro para botón Play.
     * @param boolRewind Parametro para botón Retroceder 5 segundos.
     * @param boolAdvance Parametro para botón Avanzar 5 segundos.
     * @param boolRec Parametro para el botón Grabar.
     */
    public void enableButton(boolean boolPlay, boolean boolRewind, boolean boolAdvance, boolean boolRec) {
        btPlayPause.setEnabled(boolPlay);
        btRewind.setEnabled(boolRewind);
        btAdvance.setEnabled(boolAdvance);
        btRec.setEnabled(boolRec);

        if(boolPlay == false) {
            btPlayPause.getBackground().setAlpha(150);
        }else {
            btPlayPause.getBackground().setAlpha(255);
        }
        if(boolRewind == false) {
            btRewind.getBackground().setAlpha(150);
        }else {
            btRewind.getBackground().setAlpha(255);
        }
        if(boolAdvance == false) {
            btAdvance.getBackground().setAlpha(150);
        }else {
            btAdvance.getBackground().setAlpha(255);
        }
        if(boolRec == false) {
            btRec.getBackground().setAlpha(150);
        }else {
            btRec.getBackground().setAlpha(255);
        }
    }

    /**
     * Inicia la grabación.
     */
    public void startRec() {
        if(ContextCompat.checkSelfPermission(this, this.permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getFilesDir().getAbsoluteFile() + File.separator + "Grabacion.3gp");
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                e.printStackTrace();
            }
            mediaRecorder.start();
            infoRec = false;
        }else {
            ActivityCompat.requestPermissions(this, this.permissions, 5555);
        }
        enableButton(false, false, false, true);
    }

    /**
     * Finaliza la grabación.
     */
    public void stopRec() {
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
        mediaPlayer.reset();
        infoRec = true;
        enableButton(true, false, false, true);
    }

    /**
     * Comprueba que los permisos necesarios están concedidos.
     * @param requestCode Código de solicitud pasado.
     * @param permissions Permisos solicitados.
     * @param grantResults Resultado de la concesión para los permisos correspondientes. Nunca nulo.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 5555:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();
        else startRec();
    }

    /**
     * Prepara el reproductor si es neceario e inicia la reproducción..
     */
    public void play() {
        if(preparedPlay == true) {
            try {
                mediaPlayer.setDataSource(this, pathURI);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mediaPlayer.start();
        Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
        enableButton(true, true, true, false);
    }

    /**
     * Pausa la reproducción.
     */
    public void pause() {
        preparedPlay = false;
        mediaPlayer.pause();
        Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
        enableButton(true, false, false, true);
    }

    /**
     * Retrocede 5 segundos la reproducción.
     */
    public void rewind() {
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
    }

    /**
     * Avanza 5 segundo la reproducción.
     */
    public void advance() {
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
    }

    /**
     * Función para salir de la app.
     */
    public void exit() {
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        System.exit(0);
    }

    /**
     * Se llama cuando se llega al final de una fuente multimedia durante la reproducción. Reseta
     * para poder volver a reproducir la grabación.
     * @param mp MediaPlayer que ha llegado al final de la reproducción.
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer.reset();
        preparedPlay = true;
        infoRec = true;
        enableButton(true, false, false, true);
        btPlayPause.setImageResource(R.drawable.outline_play_circle_filled_white_48);
        Toast.makeText(this, "Reproducción finalizada", Toast.LENGTH_SHORT).show();
    }
}