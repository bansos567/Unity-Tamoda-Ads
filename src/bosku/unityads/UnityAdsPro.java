package com.bosku.unityads;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.common.ComponentCategory;
import android.content.Context;
import android.app.Activity;
import android.view.ViewGroup;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

@DesignerComponent(version = 2, description = "Unity Ads Full Extension by Bosku", category = ComponentCategory.EXTENSION, nonVisible = true, iconName = "images/extension.png")
@SimpleObject(external = true)
@UsesLibraries(libraries = "unity-ads.aar")
@UsesPermissions(permissionNames = "android.permission.INTERNET, android.permission.ACCESS_NETWORK_STATE")
public class UnityAdsPro extends AndroidNonvisibleComponent {
    
    private Context context;
    private Activity activity;

    public UnityAdsPro(ComponentContainer container) {
        super(container.$form());
        this.context = container.$context();
        this.activity = (Activity) container.$context();
    }

    // ==========================================
    // FUNGSI INISIALISASI
    // ==========================================
    @SimpleFunction(description = "Inisialisasi Unity Ads dengan Game ID")
    public void Initialize(String gameId, boolean testMode) {
        UnityAds.initialize(context, gameId, testMode, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InitializationSuccess();
                    }
                });
            }

            @Override
            public void onInitializationFailed(final UnityAds.UnityAdsInitializationError error, final String message) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InitializationFailed(message);
                    }
                });
            }
        });
    }

    // ==========================================
    // FUNGSI LOAD & SHOW IKLAN
    // ==========================================
    @SimpleFunction(description = "Load Iklan berdasarkan Ad Unit ID")
    public void LoadAd(final String adUnitId) {
        UnityAds.load(adUnitId, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(final String placementId) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AdLoaded(placementId);
                    }
                });
            }

            @Override
            public void onUnityAdsFailedToLoad(final String placementId, UnityAds.UnityAdsLoadError error, final String message) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AdFailedToLoad(placementId, message);
                    }
                });
            }
        });
    }

    @SimpleFunction(description = "Tampilkan Iklan Video/Interstitial yang sudah di-load")
    public void ShowAd(final String adUnitId) {
        UnityAds.show(activity, adUnitId, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
            @Override
            public void onUnityAdsShowFailure(final String placementId, UnityAds.UnityAdsShowError error, final String message) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AdFailedToShow(placementId, message);
                    }
                });
            }

            @Override
            public void onUnityAdsShowStart(final String placementId) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AdStarted(placementId);
                    }
                });
            }

            @Override
            public void onUnityAdsShowClick(final String placementId) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AdClicked(placementId);
                    }
                });
            }

            @Override
            public void onUnityAdsShowComplete(final String placementId, final UnityAds.UnityAdsShowCompletionState state) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isCompleted = state.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED);
                        AdCompleted(placementId, isCompleted);
                    }
                });
            }
        });
    }

    // ==========================================
    // FUNGSI BANNER IKLAN
    // ==========================================
    @SimpleFunction(description = "Tampilkan Iklan Banner di dalam Horizontal/Vertical Arrangement")
    public void ShowBanner(AndroidViewComponent layoutContainer, String adUnitId) {
        try {
            ViewGroup viewGroup = (ViewGroup) layoutContainer.getView();
            BannerView bannerView = new BannerView(activity, adUnitId, new UnityBannerSize(320, 50));
            viewGroup.removeAllViews();
            viewGroup.addView(bannerView);
            bannerView.load();
        } catch (Exception e) {
            System.err.println("Gagal memuat Banner: " + e.getMessage());
        }
    }

    // ==========================================
    // EVENTS (Blocks Kuning) - SEKARANG MURNI
    // ==========================================
    @SimpleEvent(description = "Terpanggil saat inisialisasi sukses")
    public void InitializationSuccess() {
        EventDispatcher.dispatchEvent(this, "InitializationSuccess");
    }

    @SimpleEvent(description = "Terpanggil saat inisialisasi gagal")
    public void InitializationFailed(String errorMessage) {
        EventDispatcher.dispatchEvent(this, "InitializationFailed", errorMessage);
    }

    @SimpleEvent(description = "Terpanggil saat iklan siap ditampilkan")
    public void AdLoaded(String adUnitId) {
        EventDispatcher.dispatchEvent(this, "AdLoaded", adUnitId);
    }

    @SimpleEvent(description = "Terpanggil saat iklan gagal di-load")
    public void AdFailedToLoad(String adUnitId, String errorMessage) {
        EventDispatcher.dispatchEvent(this, "AdFailedToLoad", adUnitId, errorMessage);
    }

    @SimpleEvent(description = "Terpanggil saat iklan gagal ditampilkan")
    public void AdFailedToShow(String adUnitId, String errorMessage) {
        EventDispatcher.dispatchEvent(this, "AdFailedToShow", adUnitId, errorMessage);
    }

    @SimpleEvent(description = "Terpanggil saat iklan mulai muncul di layar")
    public void AdStarted(String adUnitId) {
        EventDispatcher.dispatchEvent(this, "AdStarted", adUnitId);
    }

    @SimpleEvent(description = "Terpanggil saat user mengklik iklan")
    public void AdClicked(String adUnitId) {
        EventDispatcher.dispatchEvent(this, "AdClicked", adUnitId);
    }

    @SimpleEvent(description = "Terpanggil saat iklan selesai")
    public void AdCompleted(String adUnitId, boolean isCompleted) {
        EventDispatcher.dispatchEvent(this, "AdCompleted", adUnitId, isCompleted);
    }
}
