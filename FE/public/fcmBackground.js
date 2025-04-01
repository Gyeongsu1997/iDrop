importScripts(
    "https://www.gstatic.com/firebasejs/10.8.0/firebase-app-compat.js"
);
importScripts(
    "https://www.gstatic.com/firebasejs/10.8.0/firebase-messaging-compat.js"
);

self.addEventListener("install", function (e) {
    self.skipWaiting();
});

self.addEventListener("activate", function (e) {
    console.log("fcm service worker가 실행되었습니다.");
});

const firebaseConfig = {
    apiKey: "AIzaSyCno-ctJi4gVtKAv0NzmRrXywI8lnBv8PU",
    authDomain: "idrop-f372c.firebaseapp.com",
    projectId: "idrop-f372c",
    storageBucket: "idrop-f372c.firebasestorage.app",
    messagingSenderId: "831904969629",
    appId: "1:831904969629:web:87699928a00060d748f574",
    measurementId: "G-8J0TX0HV2T"
};

firebase.initializeApp(firebaseConfig);

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
    console.log("background", payload);
    const notificationTitle = payload.title;
    const notificationOptions = {
        body: payload.body
        // icon: payload.icon
    };
    self.registration.showNotification(notificationTitle, notificationOptions);
});
