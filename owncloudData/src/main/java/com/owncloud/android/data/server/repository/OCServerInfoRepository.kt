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

package com.owncloud.android.data.server.repository

import com.owncloud.android.data.server.datasources.RemoteServerInfoDataSource
import com.owncloud.android.domain.server.ServerInfoRepository
import com.owncloud.android.domain.server.model.ServerInfo
import com.owncloud.android.lib.common.network.WebdavUtils.normalizeProtocolPrefix

class OCServerInfoRepository(
    private val remoteServerInfoDataSource: RemoteServerInfoDataSource
) : ServerInfoRepository {

    override fun getServerInfo(path: String): ServerInfo {
        // First step: check the status of the server (including its version)
        val pairRemoteStatus = remoteServerInfoDataSource.getRemoteStatus(path)

        // Second step: get authentication method required by the server
        val authenticationMethod = remoteServerInfoDataSource.getAuthenticationMethod(
            normalizeProtocolPrefix(
                path,
                pairRemoteStatus.second
            )
        )

        return ServerInfo(
            ownCloudVersion = pairRemoteStatus.first.version,
            baseUrl = normalizeProtocolPrefix(path, pairRemoteStatus.second),
            authenticationMethod = authenticationMethod,
            isSecureConnection = pairRemoteStatus.second
        )
    }
}
