/**
 * ownCloud Android client application
 *
 * @author Abel García de Prada
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
package com.owncloud.android.domain.user.model

data class UserAvatar(
    val avatarData: ByteArray = byteArrayOf(),
    val mimeType: String = "",
    val eTag: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserAvatar

        if (!avatarData.contentEquals(other.avatarData)) return false
        if (mimeType != other.mimeType) return false
        if (eTag != other.eTag) return false

        return true
    }

    override fun hashCode(): Int {
        var result = avatarData.contentHashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + eTag.hashCode()
        return result
    }
}
