package com.yellowpineapple.wakup.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.twincoders.twinpush.sdk.TwinPushSDK;
import com.twincoders.twinpush.sdk.activities.RichNotificationActivity;
import com.twincoders.twinpush.sdk.entities.TwinPushOptions;
import com.twincoders.twinpush.sdk.notifications.PushNotification;
import com.twincoders.twinpush.sdk.services.NotificationIntentService;
import com.yellowpineapple.wakup.R;
import com.yellowpineapple.wakup.views.PullToRefreshLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.Date;

@EActivity(R.layout.activity_offers)
@OptionsMenu(R.menu.main_menu)
public class OffersActivity extends OfferListActivity {

    @ViewById StaggeredGridView gridView;
    @ViewById View navigationView;
    @ViewById PullToRefreshLayout ptrLayout;
    @ViewById View emptyView;

    Date backPressedTime = null;

    private static final String BIG_OFFER_URL = "http://app.wakup.net/offers/highlighted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        /* TwinPush setup */
        TwinPushSDK twinPush = TwinPushSDK.getInstance(this);
        // Setup TwinPush SDK
        TwinPushOptions options = new TwinPushOptions();                // Initialize options
        options.twinPushAppId =     "afb821e1c8c715c7";                 // - APP ID
        options.twinPushApiKey =    "965aac21649e505ab3d1bc9e9402b8ff"; // - API Key
        options.gcmProjectNumber =  "614578197410";                     // - GCM Project Number
        options.subdomain =         TwinPushOptions.DEFAULT_SUBDOMAIN;  // - Application subdomain
        options.notificationIcon =  R.drawable.ic_action_logo;          // - Notification icon
        twinPush.setup(options);                                        // Call setup
        twinPush.register();

        // Check push notification
        checkPushNotification(getIntent());
    }

    @AfterViews
    void afterViews() {
        setupOffersGrid(gridView, navigationView, emptyView, true);
    }

    @Override
    void onRequestOffers(final int page, final Location location) {
        offersRequest = getRequestClient().findOffers(location, page, getOfferListRequestListener());
    }

    @Override
    public void onBackPressed() {
        long diff = backPressedTime != null ? new Date().getTime() - backPressedTime.getTime(): Long.MAX_VALUE;
        float secondsDiff = diff / 1000;
        if (secondsDiff > 0.5 && secondsDiff < 3) {
            finish();
        } else {
            backPressedTime = new Date();
            Toast.makeText(this, R.string.back_button_once, Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.btnBigOffer)
    void bigOfferPressed() {
        WebViewActivity_.intent(this).url(BIG_OFFER_URL).titleId(R.string.big_offer).start();
        slideInTransition();
    }

    @Click(R.id.btnMap)
    void mapButtonPressed() {
        OfferMapActivity_.intent(this).offers(offers).location(currentLocation).start();
        slideInTransition();
    }

    @Click(R.id.btnMyOffers)
    void myOffersPressed() {
        SavedOffersActivity_.intent(this).start();
        slideInTransition();
    }

    @Override
    public PullToRefreshLayout getPullToRefreshLayout() {
        return ptrLayout;
    }

    // Push notifications

    @Override
    protected void onNewIntent(Intent intent) {
        checkPushNotification(intent);
        super.onNewIntent(intent);
    }

    // Checks if the intent contains a Push notification and displays rich content when appropriated
    void checkPushNotification(Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(NotificationIntentService.ON_NOTIFICATION_OPENED_ACTION)) {
            PushNotification notification = (PushNotification) intent.getSerializableExtra(NotificationIntentService.EXTRA_NOTIFICATION);
            TwinPushSDK.getInstance(this).onNotificationOpen(notification);

            if (notification != null && notification.isRichNotification()) {
                Intent richIntent = new Intent(this, RichNotificationActivity.class);
                richIntent.putExtra(NotificationIntentService.EXTRA_NOTIFICATION, notification);
                startActivity(richIntent);
            }
        }
    }

    @OptionsItem
    void menuSearchSelected() {
        SearchActivity_.intent(this).start();
    }
}