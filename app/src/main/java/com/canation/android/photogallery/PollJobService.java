package com.canation.android.photogallery;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by CangNguyen on 3/22/2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PollJobService extends JobService {
    private static final String TAG ="Poll Job Service";
    PollTask mPollTask;
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.i(TAG, "Job service started");
        mPollTask = new PollTask(){
            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        mPollTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mPollTask != null) {
            mPollTask.cancel(true);
        }
        return true;
    }

    private class PollTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            String query = QueryPreferences.getStoredQuery(PollJobService.this);
            String lastResultId = QueryPreferences.getLastResultId(PollJobService.this);

            List<GalleryItem> items;
            if (query == null)
                items = new FlickrFetchr().fetchRecentPhotos();
            else
                items = new FlickrFetchr().searchPhotos(query);

            if (items.size() == 0)
                return null;

            String resultId = items.get(0).getId();
            if (resultId.equals(lastResultId))
                Log.i(TAG, "Got an old result: " + resultId);
            else {
                Log.i(TAG, "Got a new result: " + resultId);

                Resources resources = getResources();
                Intent i = PhotoGalleryActivity.newIntent(PollJobService.this);
                PendingIntent pi = PendingIntent.getActivity(PollJobService.this, 0, i, 0);

                Notification notification = new NotificationCompat.Builder(PollJobService.this)
                        .setTicker(resources.getString(R.string.new_pictures_title))
                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .setContentTitle(resources.getString(R.string.new_pictures_title))
                        .setContentText(resources.getString(R.string.new_pictures_text))
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(PollJobService.this);
                notificationManager.notify(0, notification);
            }
            QueryPreferences.setLastResultId(PollJobService.this, resultId);
            return null;
        }
    }
}
