package com.astromyllc.shootingstar.adminpta.dto.request.alien;

import com.astromyllc.shootingstar.adminpta.dto.request.DynamicStringRequest;

import java.util.*;
import java.util.stream.Collectors;

    public class DynamicStringRequestUtil {

        // Get first matching value by key
        public static String getValue(DynamicStringRequest request, String searchKey) {
            if (request == null || request.getKey() == null || request.getVal() == null) {
                return null;
            }

            for (int i = 0; i < request.getKey().size(); i++) {
                if (i < request.getVal().size() && Objects.equals(request.getKey().get(i), searchKey)) {
                    return request.getVal().get(i);
                }
            }
            return null;
        }

        // Get all values for a key
        public static List<String> getAllValues(DynamicStringRequest request, String searchKey) {
            List<String> results = new ArrayList<>();
            if (request == null || request.getKey() == null || request.getVal() == null) {
                return results;
            }

            for (int i = 0; i < request.getKey().size(); i++) {
                if (i < request.getVal().size() && Objects.equals(request.getKey().get(i), searchKey)) {
                    results.add(request.getVal().get(i));
                }
            }
            return results;
        }

        // Convert from Map to DynamicStringRequest
        public static DynamicStringRequest fromMap(Map<String, String> map) {
            return DynamicStringRequest.builder()
                    .key(new ArrayList<>(map.keySet()))
                    .val(new ArrayList<>(map.values()))
                    .build();
        }

        // Convert to Map
        public static Map<String, String> toMap(DynamicStringRequest request) {
            if (request == null || request.getKey() == null || request.getVal() == null) {
                return new HashMap<>();
            }

            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < request.getKey().size(); i++) {
                if (i < request.getVal().size()) {
                    map.put(request.getKey().get(i), request.getVal().get(i));
                }
            }
            return map;
        }
    }