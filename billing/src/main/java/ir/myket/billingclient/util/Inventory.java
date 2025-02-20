/* Copyright (c) 2012 Google Inc.
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

package ir.myket.billingclient.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Inventory {
    protected static final String TAG = "[MyketIAB][Inventory]";
    Map<String, SkuDetails> mSkuMap = new HashMap<String, SkuDetails>();
    Map<String, Purchase> mPurchaseMap = new HashMap<String, Purchase>();

    public Inventory() {
    }

    /**
     * Returns the listing details for an in-app product.
     */
    public SkuDetails getSkuDetails(String sku) {
        return mSkuMap.get(sku);
    }

    /**
     * Returns purchase information for a given product, or null if there is no purchase.
     */
    public Purchase getPurchase(String sku) {
        return mPurchaseMap.get(sku);
    }

    /**
     * Returns whether or not there exists a purchase of the given product.
     */
    public boolean hasPurchase(String sku) {
        return mPurchaseMap.containsKey(sku);
    }

    /**
     * Return whether or not details about the given product are available.
     */
    public boolean hasDetails(String sku) {
        return mSkuMap.containsKey(sku);
    }

    /**
     * Erase a purchase (locally) from the inventory, given its product ID. This just
     * modifies the Inventory object locally and has no effect on the server! This is
     * useful when you have an existing Inventory object which you know to be up to date,
     * and you have just consumed an item successfully, which means that erasing its
     * purchase data from the Inventory you already have is quicker than querying for
     * a new Inventory.
     */
    public void erasePurchase(String sku) {
        if (mPurchaseMap.containsKey(sku)) mPurchaseMap.remove(sku);
    }

    /**
     * Returns a list of all owned product IDs.
     */
    public List<String> getAllOwnedSkus() {
        return new ArrayList<String>(mPurchaseMap.keySet());
    }

    /**
     * Returns a list of all owned product IDs of a given type
     */
    public List<String> getAllOwnedSkus(String itemType) {
        List<String> result = new ArrayList<String>();
        for (Purchase p : mPurchaseMap.values()) {
            if (p.getItemType().equals(itemType))
                result.add(p.getSku());
        }
        return result;
    }

    /**
     * Returns a list of all purchases.
     */
    public List<Purchase> getAllPurchases() {
        return new ArrayList<Purchase>(mPurchaseMap.values());
    }

    public List<SkuDetails> getAllSkuDetails() {
        return new ArrayList(mSkuMap.values());
    }

    public void addSkuDetails(SkuDetails d) {
        mSkuMap.put(d.getSku(), d);
    }

    public void addPurchase(Purchase p) {
        mPurchaseMap.put(p.getSku(), p);
    }

    public JSONArray getAllSkusAsJson() {
        try {
            JSONArray json = new JSONArray();
            for (SkuDetails skuDetails : mSkuMap.values()) {
                json.put(new JSONObject(skuDetails.toJson()));
            }
            return json;
        } catch (JSONException e) {
            Log.i(TAG, "Error creating JSON from skus " + e.getMessage());
        }
        return new JSONArray();
    }

    public JSONArray getAllPurchasesAsJson() {
        try {
            JSONArray json = new JSONArray();
            for (Purchase p : mPurchaseMap.values()) {
                json.put(new JSONObject(p.toJson()));
            }
            return json;
        } catch (JSONException e) {
            Log.i(TAG, "Error creating JSON from Purchases " + e.getMessage());
        }
        return new JSONArray();
    }

    public String getAllSkusAndPurchasesAsJson() {
        try {
            JSONObject json = new JSONObject();

            json.put("purchases", getAllPurchasesAsJson());
            json.put("skus", getAllSkusAsJson());

            return json.toString();
        } catch (JSONException e) {
            Log.i(TAG, "Error creating JSON from skus or Purchases " + e.getMessage());
        }
        return "{}";
    }
}
