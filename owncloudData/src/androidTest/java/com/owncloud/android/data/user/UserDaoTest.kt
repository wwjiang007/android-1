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
package com.owncloud.android.data.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.owncloud.android.data.user.db.UserDao
import com.owncloud.android.data.OwncloudDatabase
import com.owncloud.android.data.user.datasources.mapper.UserQuotaMapper
import com.owncloud.android.testutil.OC_ACCOUNT_NAME
import com.owncloud.android.testutil.OC_USER_QUOTA
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
class UserDaoTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var userDao: UserDao
    private val userQuotaMapper = UserQuotaMapper()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        OwncloudDatabase.switchToInMemory(context)
        val db: OwncloudDatabase = OwncloudDatabase.getDatabase(context)
        userDao = db.userDao()
    }

    /******************************************************************************************************
     ******************************************** User Quota **********************************************
     ******************************************************************************************************/

    @Test
    fun insertQuotaForAccount() {
        userDao.insert(userQuotaMapper.toEntity(OC_USER_QUOTA)!!.copy(accountName = OC_ACCOUNT_NAME))

        val userQuotaEntity = userDao.getQuotaForAccount(OC_ACCOUNT_NAME)

        assertNotNull(userQuotaEntity)
        assertEquals(OC_USER_QUOTA, userQuotaMapper.toModel(userQuotaEntity))
    }

    @Test
    fun replaceQuotaForAccount() {
        userDao.insert(userQuotaMapper.toEntity(OC_USER_QUOTA)!!.copy(accountName = OC_ACCOUNT_NAME))
        userDao.insert(userQuotaMapper.toEntity(OC_USER_QUOTA.copy(available = -3))!!.copy(accountName = OC_ACCOUNT_NAME))

        val userQuotaEntity = userDao.getQuotaForAccount(OC_ACCOUNT_NAME)

        assertNotNull(userQuotaEntity)
        assertEquals(OC_USER_QUOTA.copy(available = -3), userQuotaMapper.toModel(userQuotaEntity))
    }

    @Test
    fun getQuotaForAccountNull() {
        val userQuotaEntity = userDao.getQuotaForAccount(OC_ACCOUNT_NAME)

        assertNull(userQuotaEntity)
    }
}
