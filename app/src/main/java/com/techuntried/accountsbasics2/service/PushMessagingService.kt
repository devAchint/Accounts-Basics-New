package com.techuntried.accountsbasics2.service

//@AndroidEntryPoint
//class PushMessagingService : FirebaseMessagingService() {
//
//    companion object{
//        const val NOTIFICATION_PROMO_CHANNEL_ID = "PROMO_CHANNEL_ID"
//        const val NOTIFICATION_PROMO_CHANNEL_NAME = "PROMOTIONS"
//        const val NOTIFICATION_ID = 2
//        const val NOTIFICATION_RC = 1
//    }
//    @Inject
//    lateinit var updateFcmTokenToServerUseCase: UpdateFcmTokenToServerUseCase
//
//    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        Log.d("MYDEBUG", "new token $token")
//        serviceScope.launch {
//            updateFcmTokenToServerUseCase(token)
//        }
//    }
//
//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//        sendNotification(message)
//    }
//
//    private fun sendNotification(message: RemoteMessage) {
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                NOTIFICATION_PROMO_CHANNEL_ID,
//                NOTIFICATION_PROMO_CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//        val pendingIntent =  appOpenPendingIntent()
//        val notification = NotificationCompat.Builder(this, NOTIFICATION_PROMO_CHANNEL_ID)
//            .apply {
//                setContentTitle(message.notification?.title ?: "")
//                setContentText(message.notification?.body)
//                setSmallIcon(R.drawable.notification_icon)
//                setAutoCancel(true)
//                setContentIntent(pendingIntent)
//            }.build()
//
//        notificationManager.notify(NOTIFICATION_ID, notification)
//    }
//
//    private fun appOpenPendingIntent(): PendingIntent {
//        val intent = Intent(this, MainActivity::class.java)
//        return PendingIntent.getActivity(
//            this,
//            NOTIFICATION_RC,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        serviceScope.cancel()
//    }
//
//}