# DoggyDuty: An Android Graph Library

## Running project
1. [Download Android Studio](https://developer.android.com/studio/index.html).
2. Get [Google Maps API key](https://developers.google.com/maps/documentation/android-api/signup). Record.
3. Get [Google Directions API key](https://developers.google.com/maps/documentation/directions/get-api-key). Record.
4. Clone project onto local file system; open in Android Studio.
5. Go to app -> res --> values --> "google_maps_api.xml"
    - Replace "XXXXXXXX" with your API key from Step 2.
    - [Google Maps API Key]: https://imgur.com/LwCQuMu "Google Maps API Key"
6. Open up the Java class "DirectionsRequestBuilder".
    - Replace the value of the final String property named KEY with your API key from Step 3.
    - [Google Directions API Key]: https://imgur.com/wUvMNf0 "Google Directions API Key"
7. Run/debug project in Android Studio.
