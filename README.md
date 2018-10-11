GP Propo WiFi (Android版)
=========

## 概要
GPduino WiFiを使ったWiFiラジコンのためのプロポアプリです。（Android版）  
GPduino WiFiは、ESP8266搭載のWiFiラジコン制御ボードです。  
GPduino WiFiに関する詳細は、[GPduinoサポートページ](http://lipoyang.net/gpduino)をごらんください。

![概念図](image/Overview.png)

ラジコンは、GPduinoとRCサーボやDCモータを組み合わせて作ります。  
下図はミニ四駆を改造して作ったラジコンです。

![ラジコンの写真](image/Mini4WD.jpg)

## アプリの操作

![アプリの画面](image/MainUI.png)

<!--* BLEボタンを押すと、接続するデバイスを選択する画面になります。-->
<!--* ボタンの色は橙が未接続、黄色が接続中、青が接続済を示します。-->
* GPduino WiFiはWiFiのアクセスポイントになるので、まずAndroidをそちらに接続します。
* SSIDはesp8266-* で、パスワードは 12345678 です。
* 「このネットワークはインターネットに接続していません。接続を維持しますか？」 と表示されたら、「はい」 をタップします。
![警告画面](image/warning.png)
* 通信できる状態になると上部のWiFiインジケータが青になります。
* 通信できない場合は上部のWiFiインジケータが赤になります。
* 見てのとおり、ラジコンプロポの要領で2本のスティックを操作します。
* 設定ボタンを押すと、設定画面に遷移します。

![設定画面](image/SettingUI.png)

* RCサーボ CH0～2の、反転、トリム、ゲインを設定できます。
* REVのスイッチをONにすると、サーボの回転方向が反転します。
* TRIMの数値を上下すると、サーボのニュートラル位置を調整できます。
* GAINの数値を上下すると、サーボの回転幅を調整できます。
* SAVEボタンを押すと、設定をGPduinoの不揮発メモリに保存します。
* RELOADボタンを押すと、GPduinoの不揮発メモリから設定を読み出します。
* MODEは、車両モードを設定します。
    * CAR は、自動車モード (駆動輪＋ステアリング)
    * TANK は、戦車モード (左右独立駆動輪)
* 4WS MODE は、RCサーボ CH1(前輪)
とCH2(後輪)を使った四輪操舵のモードを設定します。
    * FRONT は、前輪のみのステアリング
    * REAR は、後輪のみのステアリング
    * NORMAL は、前輪と後輪が同相の四輪操舵
    * REVERSE は、前輪と後輪が逆相の四輪操舵
* BATTERYは、バッテリー電圧を表示します。

## 動作環境
### システム要件
* Android端末: Android 4.2 (API Level 17)以上
* 開発環境: Android Studio
* GPduino WiFi
* DCモータとRCサーボを有するラジコンカー または DCモータ2個を有するラジコン戦車

### 確認済み環境

* Android端末: Nexus7(2013), Android 5.1, xdpi 1920×1200 pixel

## ファイル一覧

* GPPropoWiFi/: プロポアプリのソース一式
* LICENSE: Apache Licence 2.0です
* README.md これ
