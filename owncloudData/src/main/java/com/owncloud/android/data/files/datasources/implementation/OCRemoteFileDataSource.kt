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

package com.owncloud.android.data.files.datasources.implementation

import com.owncloud.android.data.files.datasources.RemoteFileDataSource
import com.owncloud.android.lib.resources.files.services.FileService

class OCRemoteFileDataSource(
    private val fileService: FileService
) : RemoteFileDataSource {
    override fun checkPathExistence(path: String, checkUserCredentials: Boolean): Boolean =
        fileService.checkPathExistence(path = path, isUserLogged = checkUserCredentials).data

}
