package com.zpi.infrastructure.rest.analysis;

import com.zpi.api.authCode.authenticationRequest.audit.DeviceInfoDTO;
import com.zpi.api.authCode.authenticationRequest.audit.IpInfoDTO;
import com.zpi.domain.rest.analysis.afterLogin.AnalysisRequest;
import com.zpi.domain.rest.analysis.afterLogin.AuditUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnalysisRequestDTO {
    private final IpInfoDTO ipInfo;
    private final DeviceInfoDTO deviceInfo;
    private final AuditUserDTO user;

    public AnalysisRequestDTO(AnalysisRequest request) {
        var ipInfo = request.getIpInfo();
        this.ipInfo = new IpInfoDTO(ipInfo.getCity(), ipInfo.getContinentCode(), ipInfo.getContinentName(), ipInfo.getCountryCode(), ipInfo.getCountryName(), ipInfo.getIpAddress(), ipInfo.getStateProv());

        var deviceInfo = request.getDeviceInfo();
        this.deviceInfo = new DeviceInfoDTO(deviceInfo.getFingerprint(), deviceInfo.getUserAgent(), deviceInfo.getBrowser(), deviceInfo.getEngine(), deviceInfo.getEngineVersion(), deviceInfo.getOS(), deviceInfo.getOSVersion(), deviceInfo.getDevice(), deviceInfo.getDeviceType(), deviceInfo.getDeviceVendor(), deviceInfo.getCPU(), deviceInfo.getScreenPrint(), deviceInfo.getColorDepth(), deviceInfo.getCurrentResolution(), deviceInfo.getAvailableResolution(), deviceInfo.getDeviceXDPI(), deviceInfo.getDeviceYDPI(), deviceInfo.getMimeTypes(), deviceInfo.isFont(), deviceInfo.getFonts(), deviceInfo.isLocalStorage(), deviceInfo.isSessionStorage(), deviceInfo.isCookie(), deviceInfo.getTimeZone(), deviceInfo.getLanguage(), deviceInfo.getSystemLanguage(), deviceInfo.isCanvas(), deviceInfo.getCanvasPrint());

        this.user = new AuditUserDTO(request.getUser().getLogin());
    }

    public AnalysisRequest toDomain() {
        return new AnalysisRequest(deviceInfo.toDomain(), ipInfo.toDomain(), new AuditUser(user.getLogin()));
    }
}
