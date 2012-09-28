/**
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;

/**
 * <p>
 * A base class for implementations of a {@link Session Session} token cache.
 * </p>
 * <p>
 * The Session constructor optionally takes a TokenCache, from which it will
 * attempt to load a cached token during construction. Also, whenever the
 * Session updates its token, it will also save the token and associated state
 * to the TokenCache.
 * </p>
 * <p>
 * This is the only mechanism supported for an Android service to use Session.
 * The service can create a custom TokenCache that returns the Session provided
 * by an Activity through which the user logged in to Facebook.
 * </p>
 */
public abstract class TokenCache {
    /**
     * The key used by Session to store the token value in the Bundle during
     * load and save.
     */
    public static final String TOKEN_KEY = "com.facebook.TokenCache.Token";

    /**
     * The key used by Session to store the expiration date value in the Bundle
     * during load and save.
     */
    public static final String EXPIRATION_DATE_KEY = "com.facebook.TokenCache.ExpirationDate";

    /**
     * The key used by Session to store the last refresh date value in the
     * Bundle during load and save.
     */
    public static final String LAST_REFRESH_DATE_KEY = "com.facebook.TokenCache.LastRefreshDate";

    /**
     * The key used by Session to store the user's id value in the Bundle during
     * load and save.
     */
    public static final String USER_FBID_KEY = "com.facebook.TokenCache.UserFBID";

    /**
     * The key used by Session to store a boolean indicating whether the token
     * was SSO in the Bundle during load and save.
     */
    public static final String IS_SSO_KEY = "com.facebook.TokenCache.IsSSO";

    /**
     * The key used by Session to store the list of permissions granted by the
     * token in the Bundle during load and save.
     */
    public static final String PERMISSIONS_KEY = "com.facebook.TokenCache.Permissions";

    private static final long INVALID_BUNDLE_MILLISECONDS = Long.MIN_VALUE;

    /**
     * Called during Session construction to get the token state. Typically this
     * is loaded from a persistent store that was previously initialized via
     * save.  The caller may choose to keep a reference to the returned Bundle
     * indefinitely.  Therefore the TokenCache should not store the returned Bundle
     * and should return a new Bundle on every call to this method.
     *
     * @return A Bundle that represents the token state that was loaded.
     */
    public abstract Bundle load();

    /**
     * Called when a Session updates its token. This is passed a Bundle of
     * values that should be stored durably for the purpose of being returned
     * from a later call to load.  Some implementations may choose to store
     * bundle beyond the scope of this call, so the caller should keep no
     * references to the bundle to ensure that it is not modified later.
     * 
     * @param bundle
     *            A Bundle that represents the token state to be saved.
     */
    public abstract void save(Bundle bundle);

    /**
     * Called when a Session learns its token is no longer valid or during a
     * call to {@link Session#closeAndClearTokenInformation
     * closeAndClearTokenInformation} to clear the durable state associated with
     * the token.
     */
    public abstract void clear();

    /**
     * Returns a boolean indicating whether a Bundle contains properties that
     * could be a valid saved token.
     * 
     * @param bundle
     *            A Bundle to check for token information.
     * @return a boolean indicating whether a Bundle contains properties that
     *         could be a valid saved token.
     */
    public static boolean hasTokenInformation(Bundle bundle) {
        if (bundle == null) {
            return false;
        }

        String token = bundle.getString(TOKEN_KEY);
        if ((token == null) || (token.length() == 0)) {
            return false;
        }

        long expiresMilliseconds = bundle.getLong(EXPIRATION_DATE_KEY, 0L);
        if (expiresMilliseconds == 0L) {
            return false;
        }

        return true;
    }

    /**
     * Gets the cached token value from a Bundle.
     * 
     * @param bundle
     *            A Bundle in which the token value was stored.
     * @return the cached token value, or null.
     */
    public static String getToken(Bundle bundle) {
        Validate.notNull(bundle, "bundle");
        return bundle.getString(TOKEN_KEY);
    }

    /**
     * Puts the token value into a Bundle.
     * 
     * @param bundle
     *            A Bundle in which the token value should be stored.
     * @param value
     *            The String representing the token value, or null.
     */
    public static void putToken(Bundle bundle, String value) {
        Validate.notNull(bundle, "bundle");
        Validate.notNull(value, "value");
        bundle.putString(TOKEN_KEY, value);
    }

    /**
     * Gets the cached expiration date from a Bundle.
     * 
     * @param bundle
     *            A Bundle in which the expiration date was stored.
     * @return the cached expiration date, or null.
     */
    public static Date getExpirationDate(Bundle bundle) {
        Validate.notNull(bundle, "bundle");
        return getDate(bundle, EXPIRATION_DATE_KEY);
    }

    /**
     * Puts the expiration date into a Bundle.
     * 
     * @param bundle
     *            A Bundle in which the expiration date should be stored.
     * @param value
     *            The Date representing the expiration date.
     */
    public static void putExpirationDate(Bundle bundle, Date value) {
        Validate.notNull(bundle, "bundle");
        Validate.notNull(value, "value");
        putDate(bundle, EXPIRATION_DATE_KEY, value);
    }

    /**
     * Gets the cached expiration date from a Bundle.
     * 
     * @param bundle
     *            A Bundle in which the expiration date was stored.
     * @return the long representing the cached expiration date in milliseconds
     *         since the epoch, or 0.
     */
    public static long getExpirationMilliseconds(Bundle bundle) {
        Validate.notNull(bundle, "bundle");
        return bundle.getLong(EXPIRATION_DATE_KEY);
    }

    /**
     * Puts the expiration date into a Bundle.
     * 
     * @param bundle
     *            A Bundle in which the expiration date should be stored.
     * @param value
     *            The long representing the expiration date in milliseconds
     *            since the epoch.
     */
    public static void putExpirationMilliseconds(Bundle bundle, long value) {
        Validate.notNull(bundle, "bundle");
        bundle.putLong(EXPIRATION_DATE_KEY, value);
    }

    /**
     * Gets the cached list of permissions from a Bundle.
     * 
     * @param bundle
     *            A Bundle in which the list of permissions was stored.
     * @return the cached list of permissions.
     */
    public static List<String> getPermissions(Bundle bundle) {
        Validate.notNull(bundle, "bundle");
        return bundle.getStringArrayList(PERMISSIONS_KEY);
    }

    /**
     * Puts the list of permissions into a Bundle.
     * 
     * @param bundle
     *            A Bundle in which the list of permissions should be stored.
     * @param value
     *            The List&lt;String&gt; representing the list of permissions,
     *            or null.
     */
    public static void putPermissions(Bundle bundle, List<String> value) {
        Validate.notNull(bundle, "bundle");
        Validate.notNull(value, "value");

        ArrayList<String> arrayList;
        if (value instanceof ArrayList<?>) {
            arrayList = (ArrayList<String>) value;
        } else {
            arrayList = new ArrayList<String>(value);
        }
        bundle.putStringArrayList(PERMISSIONS_KEY, arrayList);
    }

    /**
     * Gets the cached boolean indicating whether the token came from SSO from a
     * Bundle.
     * 
     * @param bundle
     *            A Bundle in which the boolean indicating whether the token
     *            came from SSO was stored.
     * @return the cached boolean indicating whether the token came from SSO, or
     *         null.
     */
    public static boolean getIsSSO(Bundle bundle) {
        Validate.notNull(bundle, "bundle");
        return bundle.getBoolean(IS_SSO_KEY);
    }

    /**
     * Puts the boolean indicating whether the token came from SSO into a
     * Bundle.
     * 
     * @param bundle
     *            A Bundle in which the boolean indicating whether the token
     *            came from SSO should be stored.
     * @param value
     *            The boolean indicating whether the token came from SSO, or
     *            null.
     */
    public static void putIsSSO(Bundle bundle, boolean value) {
        Validate.notNull(bundle, "bundle");
        bundle.putBoolean(IS_SSO_KEY, value);
    }

    /**
     * Gets the cached last refresh date from a Bundle.
     * 
     * @param bundle
     *            A Bundle in which the last refresh date was stored.
     * @return the cached last refresh Date, or null.
     */
    public static Date getLastRefreshDate(Bundle bundle) {
        Validate.notNull(bundle, "bundle");
        return getDate(bundle, LAST_REFRESH_DATE_KEY);
    }

    /**
     * Puts the last refresh date into a Bundle.
     * 
     * @param bundle
     *            A Bundle in which the last refresh date should be stored.
     * @param value
     *            The Date representing the last refresh date, or null.
     */
    public static void putLastRefreshDate(Bundle bundle, Date value) {
        Validate.notNull(bundle, "bundle");
        Validate.notNull(value, "value");
        putDate(bundle, LAST_REFRESH_DATE_KEY, value);
    }

    /**
     * Gets the cached last refresh date from a Bundle.
     * 
     * @param bundle
     *            A Bundle in which the last refresh date was stored.
     * @return the cached last refresh date in milliseconds since the epoch.
     */
    public static long getLastRefreshMilliseconds(Bundle bundle) {
        Validate.notNull(bundle, "bundle");
        return bundle.getLong(LAST_REFRESH_DATE_KEY);
    }

    /**
     * Puts the last refresh date into a Bundle.
     * 
     * @param bundle
     *            A Bundle in which the last refresh date should be stored.
     * @param value
     *            The long representing the last refresh date in milliseconds
     *            since the epoch.
     */
    public static void putLastRefreshMilliseconds(Bundle bundle, long value) {
        Validate.notNull(bundle, "bundle");
        bundle.putLong(LAST_REFRESH_DATE_KEY, value);
    }

    static Date getDate(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        }

        long n = bundle.getLong(key, INVALID_BUNDLE_MILLISECONDS);
        if (n == INVALID_BUNDLE_MILLISECONDS) {
            return null;
        }

        return new Date(n);
    }

    static void putDate(Bundle bundle, String key, Date date) {
        bundle.putLong(key, date.getTime());
    }
}
