/**
 * ownCloud Android client application
 *
 * @author Christian Schabesberger
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

package com.owncloud.android.domain.server.model

enum class AuthenticationMethod {
    BASIC_HTTP_AUTH {
        override fun toString(): String = "basic"
    },
    BEARER_TOKEN {
        override fun toString(): String = "bearer"
    },
    NONE {
        override fun toString(): String = "none"
    };
}
