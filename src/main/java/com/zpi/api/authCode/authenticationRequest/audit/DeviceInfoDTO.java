package com.zpi.api.authCode.authenticationRequest.audit;

import com.zpi.domain.rest.analysis.afterLogin.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeviceInfoDTO {
    private final String fingerprint;
    private final String userAgent;
    private final String browser;
    private final String engine;
    private final String engineVersion;
    private final String OS;
    private final String OSVersion;
    private final String device;
    private final String deviceType;
    private final String deviceVendor;
    private final String CPU;
    private final String screenPrint;
    private final String colorDepth;
    private final String currentResolution;
    private final String availableResolution;
    private final String deviceXDPI;
    private final String deviceYDPI;
    private final String mimeTypes;
    private final boolean isFont;
    private final String fonts;
    private final boolean isLocalStorage;
    private final boolean isSessionStorage;
    private final boolean isCookie;
    private final String timeZone;
    private final String language;
    private final String systemLanguage;
    private final boolean isCanvas;
    private final String canvasPrint;

    public DeviceInfo toDomain() {
        return new DeviceInfo(
                fingerprint,
                userAgent,
                browser,
                engine,
                engineVersion,
                OS,
                OSVersion,
                device,
                deviceType,
                deviceVendor,
                CPU,
                screenPrint,
                colorDepth,
                currentResolution,
                availableResolution,
                deviceXDPI,
                deviceYDPI,
                mimeTypes,
                isFont,
                fonts,
                isLocalStorage,
                isSessionStorage,
                isCookie,
                timeZone,
                language,
                systemLanguage,
                isCanvas,
                canvasPrint
        );
    }
}