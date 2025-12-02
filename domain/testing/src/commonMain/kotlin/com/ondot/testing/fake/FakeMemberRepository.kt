package com.ondot.testing.fake

import com.ondot.domain.model.auth.AuthTokens
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.member.HomeAddressInfo
import com.ondot.domain.model.member.PreparationTime
import com.ondot.domain.model.request.DeleteAccountRequest
import com.ondot.domain.model.request.MapProviderRequest
import com.ondot.domain.model.request.OnboardingRequest
import com.ondot.domain.model.request.settings.home_address.HomeAddressRequest
import com.ondot.domain.model.request.settings.preparation_time.PreparationTimeRequest
import com.ondot.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakeMemberRepository: MemberRepository {

    /**------------ 인메모리 상태 -------------*/

    private val mapProviderFlow = MutableStateFlow(MapProvider.KAKAO)
    private val needsChooseProviderFlow = MutableStateFlow(false)

    private var homeAddress: HomeAddressInfo? = null
    private var preparationTime: PreparationTime? = null
    private var authTokens: AuthTokens? = null

    /**------------ 테스트에 쓸 플래그 & 기록 -------------*/

    var shouldFailOnboarding = false
    var shouldFailHomeAddress = false
    var shouldFailPreparationTime = false
    var shouldFailWithdraw = false
    var shouldFailUpdateMapProvider = false
    var shouldFailUpdateHomeAddress = false
    var shouldFailUpdatePreparationTime = false

    var lastOnboardingRequest: OnboardingRequest? = null
    var lastDeleteAccountRequest: DeleteAccountRequest? = null
    var lastMapProviderRequest: MapProviderRequest? = null
    var lastHomeAddressRequest: HomeAddressRequest? = null
    var lastPreparationTimeRequest: PreparationTimeRequest? = null

    /**------------ MemberRepository 구현 -------------*/

    override suspend fun completeOnboarding(request: OnboardingRequest): Flow<Result<AuthTokens>> = flow {
        lastOnboardingRequest = request
        if (shouldFailOnboarding) {
            emit(Result.failure(Exception("onboarding 실패")))
        } else {
            val tokens = authTokens ?: AuthTokens(
                accessToken = "fake_access",
                refreshToken = "fake_refresh"
            ).also { authTokens = it }

            emit(Result.success(tokens))
        }
    }

    override suspend fun getHomeAddress(): Flow<Result<HomeAddressInfo>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPreparationTime(): Flow<Result<PreparationTime>> {
        TODO("Not yet implemented")
    }

    override suspend fun withdrawUser(request: DeleteAccountRequest): Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMapProvider(request: MapProviderRequest): Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateHomeAddress(request: HomeAddressRequest): Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePreparationTime(request: PreparationTimeRequest): Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun setLocalMapProvider(mapProvider: MapProvider) {
        TODO("Not yet implemented")
    }

    override fun getLocalMapProvider(): Flow<MapProvider> {
        TODO("Not yet implemented")
    }

    override fun needsChooseProvider(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    /** ------------ 테스트 편의용 헬퍼 ------------- */

    fun setNeedsChooseProvider(value: Boolean) {
        needsChooseProviderFlow.value = value
    }

    fun setInitialMapProvider(provider: MapProvider) {
        mapProviderFlow.value = provider
    }

    fun setInitialHomeAddress(info: HomeAddressInfo) {
        homeAddress = info
    }

    fun setInitialPreparationTime(info: PreparationTime) {
        preparationTime = info
    }
}