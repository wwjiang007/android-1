/**
 * ownCloud Android client application
 *
 * @author David González Verdugo
 * Copyright (C) 2020 ownCloud GmbH.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.data.authentication

const val SELECTED_ACCOUNT = "select_oc_account"

/**
 * OAuth2 user id
 */
const val KEY_USER_ID = "user_id"

/**
 * OAuth2 refresh token
 */
const val KEY_OAUTH2_REFRESH_TOKEN = "oc_oauth2_refresh_token"

/**
 * OAuth2 scope
 */
const val KEY_OAUTH2_SCOPE = "oc_oauth2_scope"
const val OAUTH2_OIDC_SCOPE = "openid offline_access email profile"