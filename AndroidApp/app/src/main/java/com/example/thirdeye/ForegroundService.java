package com.example.thirdeye;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {

    // Tag pour les messages dans les logs
    private static final String TAG = "Foreground service";

    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        //Put this Service in foreground
        // -> will not be killed by the system if low on memory
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        // ATTENTION : Cette partie est valide uniquement sur des versions récentes d'Android,
        //      comme l'est celle du Téléphone utilisé pour les tests.
        // Pour la rétrocompatibilité avec des versions plus anciennes, utiliser les notifications simples
        // TODO: Créer une disjonction de cas suivant la version de l'API pour adapter la notification.

        NotificationChannel chan = new NotificationChannel(
                "MyChannelId",
                "My Foreground Service",
                NotificationManager.IMPORTANCE_MAX);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        Notification notif = new NotificationCompat.Builder(this, "MyChannelId")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ThirdEye")
                .setContentText("Le monitoring est en cours")
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build();

        //On place le service en premier plan
        startForeground(1337, notif);

        // Appel au scheduler pour lancer les jobs.
        scheduleJobEmergency();

    }

    /**
     * Gère le lancement des Job
     */
    private void scheduleJobEmergency() {
        Log.d(TAG, "scheduleJobEmergency: debut");
        ComponentName serviceName = new ComponentName(this, EmergencyJobService.class);

        // Les versions récentes d'Android ne supportent pas des périodes inférieures à 15 minutes.
        // Pour contourner cela, on va "tricher" en appelant la méthode scheduleJobWiFi dans le
        //      job lui même, c'est à dire que chaque instance va programmer la suivante.
        // On utilise alors la propriété SetMinimumLatency pour forcer une durée inter-job
        //      minimale, et donc éviter leur prolifération.
        //      Cela correspond donc à la période effective.
        JobInfo jobInfo = new JobInfo.Builder(0, serviceName).setMinimumLatency(1000).build();

        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Schedule job avec succès");
        }
        Log.d(TAG, "scheduleJobEmergency: fin");
    }

}