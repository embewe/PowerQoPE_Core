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
package za.ac.uct.cs.powerqope;

import android.util.StringBuilderPrinter;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;


import za.ac.uct.cs.powerqope.measurements.HttpTask;
import za.ac.uct.cs.powerqope.measurements.HttpTask.HttpDesc;
import za.ac.uct.cs.powerqope.util.MeasurementJsonConvertor;
import za.ac.uct.cs.powerqope.util.Util;

/**
 * POJO that represents the result of a measurement
 *
 * @see MeasurementDesc
 */
public class MeasurementResult {

    private String deviceId;
    private String accountName;
    private DeviceProperty properties;
    private long timestamp;
    private boolean success;
    private String taskKey;
    private String type;
    private MeasurementDesc parameters;
    private HashMap<String, String> values;
    private boolean isExperiment;
    private long executionTime;
    /**
     * @param deviceProperty
     * @param type
     * @param timeStamp
     * @param success
     * @param measurementDesc
     */
    public MeasurementResult(String id, DeviceProperty deviceProperty, String type,
                             long timeStamp, boolean success, MeasurementDesc measurementDesc, long executionTime) {
        super();
        this.taskKey = measurementDesc.key;
        this.deviceId = id;
        this.type = type;
        this.properties = deviceProperty;
        this.timestamp = timeStamp;
        this.success = success;
        this.parameters = measurementDesc;
        this.isExperiment=hasExperimentTag(parameters.parameters);
        this.parameters.parameters = null;
        this.accountName ="Anonymous";
        this.values = new HashMap<>();
        this.executionTime = executionTime;
    }

    public HashMap<String, String> getValues() {
        return values;
    }

    private boolean hasExperimentTag(Map<String, String> map){
        boolean experiment=false;
        if(map.containsKey("isExperiment")){
            experiment=true;
        }
        return experiment;
    }

    /* Returns the type of this result */
    public String getType() {
        return parameters.getType();
    }

    /* Add the measurement results of type String into the class */
    public void addResult(String resultType, Object resultVal) {
        this.values.put(resultType, MeasurementJsonConvertor.toJsonString(resultVal));
    }

    /* Returns a string representation of the result */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        StringBuilderPrinter printer = new StringBuilderPrinter(builder);
        Formatter format = new Formatter();
        try {
            switch (type) {
                case HttpTask.TYPE:
                    getHttpResult(printer, values);
                    break;
                default:
                    Logger.e("Failed to get results for unknown measurement type " + type);
                    break;
            }
            return builder.toString();
        } catch (NumberFormatException e) {
            Logger.e("Exception occurs during constructing result string for user", e);
        } catch (ClassCastException e) {
            Logger.e("Exception occurs during constructing result string for user", e);
        } catch (Exception e) {
            Logger.e("Exception occurs during constructing result string for user", e);
        }
        return "Measurement has failed";
    }

    private void getHttpResult(StringBuilderPrinter printer, HashMap<String, String> values) {
        HttpDesc desc = (HttpDesc) parameters;
        printer.println("[HTTP]");
        printer.println("URL: " + desc.url);
        printer.println("Timestamp: " + Util.getTimeStringFromMicrosecond(properties.timestamp));
        printIPTestResult(printer);

        if (success) {
            int headerLen = Integer.parseInt(values.get("headers_len"));
            int bodyLen = Integer.parseInt(values.get("body_len"));
            int time = Integer.parseInt(values.get("time_ms"));
            printer.println("");
            printer.println("Downloaded " + (headerLen + bodyLen) + " bytes in " + time + " ms");
            printer.println("Bandwidth: " + (headerLen + bodyLen) * 8 / time + " Kbps");
        } else {
            printer.println("Download failed, status code " + values.get("code"));
        }
    }

    /**
     * Print ip connectivity and hostname resolvability result
     */
    private void printIPTestResult(StringBuilderPrinter printer) {
        printer.println("IPv4/IPv6 Connectivity: " + properties.ipConnectivity);
        printer.println("IPv4/IPv6 Domain Name Resolvability: "
                + properties.dnResolvability);
    }
}
