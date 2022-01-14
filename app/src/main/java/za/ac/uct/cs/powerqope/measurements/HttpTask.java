  /* Copyright 2012 Google Inc.
   *
   * Licensed under the Apache License, Version 2.0 (the "License");
   * you may not use this file except in compliance with the License.
   * You may obtain a copy of the License at
   *
   *     http://www.apache.org/licenses/LICENSE-2.0
   *
   * Unless required by applicable law or agreed to in writing, software
   * distributed under the License is distributed on an "AS IS" BASIS,
   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   * See the License for the specific language governing permissions and
   * limitations under the License.
   */

  package za.ac.uct.cs.powerqope.measurements;

  import android.content.Context;
  import android.util.Log;

  import java.io.IOException;
  import java.io.InputStream;
  import java.io.InvalidClassException;
  import java.net.MalformedURLException;
  import java.security.InvalidParameterException;
  import java.util.ArrayList;
  import java.util.Arrays;
  import java.util.Collection;
  import java.util.Collections;
  import java.util.Date;
  import java.util.HashMap;
  import java.util.List;
  import java.util.Map;

  import okhttp3.CipherSuite;
  import okhttp3.ConnectionSpec;
  import okhttp3.Headers;
  import okhttp3.OkHttpClient;
  import okhttp3.Request;
  import okhttp3.Response;
  import okhttp3.TlsVersion;
  import za.ac.uct.cs.powerqope.Config;
  import za.ac.uct.cs.powerqope.Logger;
  import za.ac.uct.cs.powerqope.MeasurementDesc;
  import za.ac.uct.cs.powerqope.MeasurementError;
  import za.ac.uct.cs.powerqope.MeasurementResult;
  import za.ac.uct.cs.powerqope.MeasurementTask;
  import za.ac.uct.cs.powerqope.util.CustomHTTPEventListener;
  import za.ac.uct.cs.powerqope.util.MeasurementJsonConvertor;
  import za.ac.uct.cs.powerqope.util.PhoneUtils;
  import za.ac.uct.cs.powerqope.util.WebSocketConnector;

  /**
   * A Callable class that performs download throughput test using HTTP get
   */
  public class HttpTask extends MeasurementTask {

      private static final String TAG = "HttpTask";

      // Type name for internal use
      public static final String TYPE = "http";
      // Human readable name for the task
      public static final String DESCRIPTOR = "HTTP";
      /* TODO(Wenjie): Depending on state machine configuration of cell tower's radio,
       * the size to find the 'real' bandwidth of the phone may be network dependent.
       */
      // The maximum number of bytes we will read from requested URL. Set to 1Mb.
      public static final long MAX_HTTP_RESPONSE_SIZE = 1024 * 1024;
      // The size of the response body we will report to the service.
      // If the response is larger than MAX_BODY_SIZE_TO_UPLOAD bytes, we will
      // only report the first MAX_BODY_SIZE_TO_UPLOAD bytes of the body.
      public static final int MAX_BODY_SIZE_TO_UPLOAD = 1024;
      // The buffer size we use to read from the HTTP response stream
      public static final int READ_BUFFER_SIZE = 1024;
      // Not used by the HTTP protocol. Just in case we do not receive a status line from the response
      public static final int DEFAULT_STATUS_CODE = 0;

      private OkHttpClient httpClient = null;

      // Track data consumption for this task to avoid exceeding user's limit
      private long dataConsumed;

      public HttpTask(MeasurementDesc desc, Context parent) {
          super(new HttpDesc(desc.key, desc.startTime, desc.endTime, desc.intervalSec,
                  desc.count, desc.priority, desc.parameters, desc.instanceNumber, desc.cipherLevel), parent);
          dataConsumed = 0;
      }

      /**
       * The description of a HTTP measurement
       */
      public static class HttpDesc extends MeasurementDesc {
          public String url;
          private String method;
          private String headers;
          private String body;

          public HttpDesc(String key, Date startTime, Date endTime,
                          double intervalSec, long count, long priority, Map<String, String> params, int instanceNumber, String cipherLevel)
                  throws InvalidParameterException {
              super(HttpTask.TYPE, key, startTime, endTime, intervalSec, count, priority, params, instanceNumber, cipherLevel);
              initializeParams(params);
              if (this.url == null || this.url.length() == 0) {
                  throw new InvalidParameterException("URL for http task is null");
              }
          }

          @Override
          protected void initializeParams(Map<String, String> params) {

              if (params == null) {
                  return;
              }

              this.url = (params.get("target") == null) ? params.get("url") : params.get("target");
              if (!this.url.startsWith("http://") && !this.url.startsWith("https://")) {
                  this.url = "http://" + this.url;
              }

              this.method = params.get("method");
              if (this.method == null || this.method.isEmpty()) {
                  this.method = "get";
              }
              this.headers = params.get("headers");
              this.body = params.get("body");
          }

          @Override
          public String getType() {
              return HttpTask.TYPE;
          }

      }

      /**
       * Returns a copy of the HttpTask
       */
      @Override
      public MeasurementTask clone() {
          MeasurementDesc desc = this.measurementDesc;
          HttpDesc newDesc = new HttpDesc(desc.key, desc.startTime, desc.endTime,
                  desc.intervalSec, desc.count, desc.priority, desc.parameters, desc.instanceNumber, desc.cipherLevel);
          return new HttpTask(newDesc, parent);
      }

      @Override
      public void stop() {

      }

      /**
       * Runs the HTTP measurement task. Will acquire power lock to ensure wifi is not turned off
       */
      @Override
      public MeasurementResult call() throws MeasurementError {
          long duration = 0;
          long originalHeadersLen = 0;
          boolean success = false;
          String errorMsg = "";
          InputStream inputStream = null;
          String responseBody = null;
          String responseHeaders = null;
          Map<String, Double> eventDurationMap = new HashMap<>();
          ConnectionSpec spec = null;
          try {
              // set the download URL, a URL that points to a file on the Internet
              // this is the file to be downloaded
              HttpDesc task = (HttpDesc) this.measurementDesc;
              switch (task.cipherLevel){
                  case "high":
                      spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                              .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
                              .cipherSuites(CipherSuite.TLS_AES_256_GCM_SHA384,
                                      CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
                                      CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
                                      CipherSuite.TLS_DHE_DSS_WITH_AES_256_GCM_SHA384,
                                      CipherSuite.TLS_DHE_RSA_WITH_AES_256_GCM_SHA384)
                              .build();
                      break;
                  case "medium":
                      spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                              .tlsVersions(TlsVersion.TLS_1_2)
                              .cipherSuites(CipherSuite.TLS_AES_128_GCM_SHA256,
                                      CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384)
                              .build();
                      break;
                  case "low":
                      spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                              .tlsVersions(TlsVersion.TLS_1_2)
                              .cipherSuites(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                                      CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                                      CipherSuite.TLS_DHE_DSS_WITH_AES_128_GCM_SHA256,
                                      CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                                      CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256)
                              .build();
                      break;
                  default:
                      spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                              .build();
                      break;
              }
              String urlStr = task.url;
              Log.i(TAG, urlStr);
              httpClient = new OkHttpClient.Builder()
                      .eventListener(new CustomHTTPEventListener(eventDurationMap))
                      .connectionSpecs(Arrays.asList(spec, ConnectionSpec.CLEARTEXT))
                      .cache(null)
                      .build();
              Map<String, String> httpHeaders = new HashMap<>();

              if (task.headers != null && task.headers.trim().length() > 0) {
                  for (String headerLine : task.headers.split("\r\n")) {
                      String tokens[] = headerLine.split(":");
                      if (tokens.length == 2) {
                          httpHeaders.put(tokens[0], tokens[1]);
                      } else {
                          throw new MeasurementError("Incorrect header line: " + headerLine);
                      }
                  }
              }

              Request request = new Request.Builder()
                      .url(urlStr)
                      .headers(Headers.of(httpHeaders))
                      .build();
              duration += System.currentTimeMillis();
              try (Response response = httpClient.newCall(request).execute()) {
                  duration = System.currentTimeMillis() - duration;
                  if (!response.isSuccessful()) {
                      success = false;
                  } else {
                      success = true;
                      responseBody = response.body().string();
                      Headers rHeaders = response.headers();
                      for (int i = 0; i < rHeaders.size(); i++) {
                          responseHeaders = rHeaders.name(i) + ":" + rHeaders.value(i);
                      }
                  }
              }

              PhoneUtils phoneUtils = PhoneUtils.getPhoneUtils();

              MeasurementResult result = new MeasurementResult(phoneUtils.getDeviceInfo().deviceId,
                      null, HttpTask.TYPE, System.currentTimeMillis() * 1000,
                      success, this.measurementDesc, duration);

              if (success) {
                  result.addResult("code", 200);
                  result.addResult("time_ms", duration);
                  result.addResult("headers_len", originalHeadersLen);
                  result.addResult("body_len", responseBody.length());
                  result.addResult("headers", responseHeaders);
                  for(Map.Entry<String, Double> event : eventDurationMap.entrySet()){
                      result.addResult(event.getKey(), event.getValue());
                  }
              }
              String resultJsonString = MeasurementJsonConvertor.toJsonString(result);
              Logger.i(resultJsonString);
              Logger.d("HttpTask Results Sending initiated");
              return result;
          } catch (MalformedURLException e) {
              errorMsg += e.getMessage() + "\n";
              Logger.e(e.getMessage());
          } catch (IOException e) {
              errorMsg += e.getMessage() + "\n";
              Logger.e(e.getMessage());
          } finally {
              if (inputStream != null) {
                  try {
                      inputStream.close();
                  } catch (IOException e) {
                      Logger.e("Fails to close the input stream from the HTTP response");
                  }
              }
          }
          throw new MeasurementError("Cannot get result from HTTP measurement because " +
                  errorMsg);
      }

      @SuppressWarnings("rawtypes")
      public static Class getDescClass() throws InvalidClassException {
          return HttpDesc.class;
      }

      @Override
      public String getType() {
          return HttpTask.TYPE;
      }

      @Override
      public String getDescriptor() {
          return DESCRIPTOR;
      }

      @Override
      public String toString() {
          HttpDesc desc = (HttpDesc) measurementDesc;
          return "[HTTP " + desc.method + "]\n  Target: " + desc.url + "\n  Interval (sec): " +
                  desc.intervalSec + "\n  Next run: " + desc.startTime;
      }

      /**
       * Data used so far by the task.
       * <p>
       * To calculate this, we measure <i>all</i> data sent while the task
       * is running. This will tend to overestimate the data consumed, but due
       * to retransmissions, etc, especially when signal strength is poor, attempting
       * to calculate the size directly will tend to greatly underestimate the data
       * consumed.
       */
      @Override
      public long getDataConsumed() {
          return dataConsumed;
      }
  }