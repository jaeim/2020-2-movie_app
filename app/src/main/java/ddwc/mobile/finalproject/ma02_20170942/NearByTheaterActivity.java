package ddwc.mobile.finalproject.ma02_20170942;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class NearByTheaterActivity extends AppCompatActivity implements OnMapReadyCallback {

    final static String TAG = "NearByTheaterActivity";
    private final static int MY_PERMISSIONS_REQ_LOC = 200;
    final static int PERMISSION_REQ_CODE = 100;

    private DrawerLayout mDrawerLayout;
    private Context context = this;

    private GoogleMap mGoogleMap;
    private MarkerOptions markerOptions;
    private MarkerOptions myLocationMarkerOptions;
    private PlacesClient placesClient;

    Button find_btn;
    private LocationManager locationManager;
    private String bestProvider;
    private Marker currentMarker;
    Bitmap smallMarker;
    Bitmap currentIcon;


    double currentLat;
    double currentLng;

    AlertDialog.Builder builder;
    AlertDialog theater_dialog;
    View custom_theater_dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater);

        find_btn = findViewById(R.id.theater_findbtn);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        /*Passive(fake GPS) 가 아닌 GPS 또는 Network provider 중 선택이 필요할 경우 기준 설정*/
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        criteria.setAltitudeRequired(false);
        criteria.setCostAllowed(false);
        bestProvider = locationManager.getBestProvider(criteria, true);

//        bestProvider = LocationManager.GPS_PROVIDER;



        Drawable drawble = getResources().getDrawable(R.drawable.menu);
        Bitmap bitmap = ((BitmapDrawable)drawble).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 40, 50, true));

        Toolbar toolbar = (Toolbar) findViewById(R.id.theater_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(newdrawable); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = (DrawerLayout) findViewById(R.id.theaterLayout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.theater_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Navigation 메뉴의 이벤트
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();

                if (id == R.id.menu_box) {
                    Toast.makeText(context, "박스오피스", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NearByTheaterActivity.this, BoxOfficeActivity.class);
                    startActivity(intent);
                    NearByTheaterActivity.this.finish();
                } else if (id == R.id.menu_movies) {
                    Toast.makeText(context, "나의 감상 목록", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NearByTheaterActivity.this, WatchedMovieActivity.class);
                    startActivity(intent);
                    NearByTheaterActivity.this.finish();
                } else if (id == R.id.menu_search) {
                    Toast.makeText(context, "검색", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NearByTheaterActivity.this, MainActivity.class);
                    startActivity(intent);
                    NearByTheaterActivity.this.finish();
                } else if (id == R.id.menu_favorite) {
                    Toast.makeText(context, "즐겨찾기", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NearByTheaterActivity.this, FavoriteMovieActivity.class);
                    startActivity(intent);
                    NearByTheaterActivity.this.finish();
                } else if (id == R.id.menu_theater) {
                    Toast.makeText(context, "주변 영화관 찾기", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.location_pin);
        Bitmap b = bitmapDrawable.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, 100, 110, false);

        BitmapDrawable btDrawble = (BitmapDrawable)getResources().getDrawable(R.drawable.group_6827);
        Bitmap b2 = btDrawble.getBitmap();
        currentIcon = Bitmap.createScaledBitmap(b2, 150, 150, false);

        mapLoad();
        // places에 대한 기본적인 설정 정보 설정
        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        // 특정 장소에 대한 정보를 요청할 수 있는 구글 api 사용객체 생성
        placesClient = Places.createClient(this);

        if(checkPermission()) {
            locationUpdate();
        }

    }
    // 툴바 버튼의 클릭 이벤트를 정의
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.theater_findbtn:
                searchStart(PlaceType.MOVIE_THEATER);
                break;
        }
    }

    private void searchStart(String type) {
        new NRPlaces.Builder().listener(placesListener)
                .key(getResources().getString(R.string.api_key))
                .latlng(currentLat,
                        currentLng)
                .radius(2000)
                .type(PlaceType.MOVIE_THEATER)
                .build()
                .execute();
    }

    /*Place ID 의 장소에 대한 세부정보 획득*/
    private void getPlaceDetail(String placeId) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.PHONE_NUMBER, Place.Field.ADDRESS);
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                // 마커 클릭시 다이얼로그로 정보 띄우도록..null처리도 해야할듯 ex)정보 없음음
//                Log.d(TAG, "place found : " + place.getName());
//                Log.d(TAG, "phone : " + place.getPhoneNumber());
//                Log.d(TAG, "address : " + place.getAddress());
//                Log.d(TAG, "url : " + place.getWebsiteUri());

                builder = new AlertDialog.Builder(NearByTheaterActivity.this);
                custom_theater_dialog = View.inflate(NearByTheaterActivity.this, R.layout.dialog_theater_info, null);

                TextView tv_name = custom_theater_dialog.findViewById(R.id.theater_name);
                TextView tv_phone = custom_theater_dialog.findViewById(R.id.theater_phone);
                TextView tv_address = custom_theater_dialog.findViewById(R.id.theater_address);
                Button btn_close = custom_theater_dialog.findViewById(R.id.theater_closebtn);

                if (place.getName() == null || place.getName().equals("")) {
                    tv_name.setText("영화관 이름 정보 없음");
                } else {
                    tv_name.setText(place.getName());
                }

                if(place.getPhoneNumber() == null || place.getPhoneNumber().equals("")) {
                    tv_phone.setText("전화번호 정보 없음");
                } else {
                    tv_phone.setText("전화번호 : " + place.getPhoneNumber());
                }

                if(place.getAddress() == null || place.getAddress().equals("")) {
                    tv_address.setText("주소 정보 없음");
                } else {
                    tv_address.setText("주소 : " + place.getAddress());
                }

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        theater_dialog.dismiss();
                    }
                });
                builder.setView(custom_theater_dialog);
                theater_dialog = builder.create();
                theater_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                theater_dialog.show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    int statusCode = apiException.getStatusCode();
                    Log.e(TAG, "Place not found : " + statusCode + " " + e.getMessage());
                }
            }
        });

    }


    PlacesListener placesListener = new PlacesListener() {
        // 주변 장소 결과를 List 형태로 전달 받음
        @Override
        public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
//            Log.d(TAG, "Adding Markers");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (noman.googleplaces.Place place : places) {
                        markerOptions.title(place.getName());
                        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        // 마커 찍기
                        Marker newMarker = mGoogleMap.addMarker(markerOptions);
                        // 가장 중요한 정보의 PlcaeId를 마커에 보관 -> 해당 placeId와 안드로이드용 Place Api를 이용하여 해당 장소의 세부정보 get
                        newMarker.setTag(place.getPlaceId());
//                        Log.d(TAG, place.getName() + " : " + place.getPlaceId());
                    }//mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLng), 10));
                }
            });
        }

        @Override
        public void onPlacesFailure(PlacesException e) {

        }

        @Override
        public void onPlacesStart() {

        }

        @Override
        public void onPlacesFinished() {

        }
    };
    private Location getLastLocation() {
        Location lastLocation = null;
        if (checkPermission()) {
            // 퍼미션 확인 코드 반드시 필요
            lastLocation = locationManager.getLastKnownLocation(bestProvider);
        } return lastLocation;
    }

    private void locationUpdate() {
        // 맨처음 앱 사용시 사용권한을 물어봄 -> 사용자가 승인시 -> 다시 해당 메소드 호출 -> 앞서 사용자가 승인했으므로 바로 리스너 등록 ok
        if (checkPermission()) {
            // 어느 종류의 프로바이더이던지 리스너가 작동하도록 함
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locationListener);
            locationManager.requestLocationUpdates(bestProvider, 3000, 0, locationListener);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));
            currentMarker.setPosition(currentLoc);

            currentLat = location.getLatitude();
            currentLng = location.getLongitude();
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // 준비된 맵 보관
        mGoogleMap = googleMap;
        // 마커 옵션 준비
        markerOptions = new MarkerOptions();
        myLocationMarkerOptions = new MarkerOptions();
        // 맵 로딩 후 내 위치 표시 버튼을 활성화 => 퍼미션 필요!
//        if(checkPermission()) {
//            mGoogleMap.setMyLocationEnabled(true);
//        }
        // 기본 위치 설정
        LatLng latLng = null;
        double latitude = Double.parseDouble(getResources().getString(R.string.init_lat));
        double longitude = Double.parseDouble(getResources().getString(R.string.init_lng));

        // 마지막으로 provider가 수신한 위치로 지도를 이동하여 마커 표시
        Location location = getLastLocation();
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            latLng = new LatLng(latitude, longitude);
        } else { // 마지막으로 수신한 위치가 없으면 강남역을 기본 위치로
            latLng = new LatLng(latitude, longitude);
        }

        currentLat = latitude;
        currentLng = longitude;

        if(checkPermission()) {
            locationUpdate();
        }

//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        myLocationMarkerOptions.position(latLng);
        myLocationMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(currentIcon));

        currentMarker = mGoogleMap.addMarker(myLocationMarkerOptions);
        // 현재 위치로 가는 버튼 클릭시
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
//                Toast.makeText(NearByTheaterActivity.this, "clicked!", Toast.LENGTH_SHORT).show();
                // true로 한다면 부모 기능을 수행하지 않게된다! (= 토스트만 뜨고 지도의 중심이 현재위치로 이동x)
                return false;
            }
        });

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // 클릭한 마커안에 TAG형태로 PlaceId가 저장되어 있음
                String placeId = marker.getTag().toString();
                getPlaceDetail(placeId);
            }
        });

    }
    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(NearByTheaterActivity.this);
    }




    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // FINE을 포함할 경우 COARSE는 기본적으로 허용됨
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없을 경우 권한 요청 메소드 실행(다이얼로그)
                // '나의 요청 코드'와 함께 사용자에게 승인 요청을 하는 다이얼로그를 띄우도록 함
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQ_LOC);
                return false;
            } else
                // 두개의 권한 모두 있을 때
                return true;
        }
        return false;
    }

    /*권한승인 요청에 대한 사용자의 응답 결과에 따른 수행*/
    // 액티비티 상속 메소드
    // 사용자가 승인 또는 거절할 경우 자동으로 호출 됨/요청시 사용했던 req.code 상수값을 전달 받음
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQ_LOC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*권한을 승인받았을 때 수행하여야 하는 동작 지정*/
//                    locationUpdate();
                    mapLoad();
                } else {
                    /*사용자에게 권한 제약에 따른 안내*/
                    Toast.makeText(this, "Permissions are not granted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}