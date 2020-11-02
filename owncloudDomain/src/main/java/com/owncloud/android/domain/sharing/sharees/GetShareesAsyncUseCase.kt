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

package com.owncloud.android.domain.sharing.sharees

import com.owncloud.android.domain.BaseUseCaseWithResult
import com.owncloud.android.domain.sharing.sharees.model.OCSharee

class GetShareesAsyncUseCase(
    private val shareeRepository: ShareeRepository
) : BaseUseCaseWithResult<List<OCSharee>, GetShareesAsyncUseCase.Params>() {
    override fun run(params: Params): List<OCSharee> =
        shareeRepository.getSharees(
            params.searchString,
            params.page,
            params.perPage
        )

    data class Params(
        val searchString: String,
        val page: Int,
        val perPage: Int
    )
}
