<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Device Compatibility

FaceAccess is designed for Android devices that support CameraX, MediaPipe Face Landmarker, and Android Accessibility Service.

This document records the devices and OEM environments that have been manually tested with the current FaceAccess build.

Compatibility Matrix

Device

Model

Android

OEM UI / ROM

RAM / Storage

Navigation

Support / Calling

Dual SIM

Cursor

Stop / Restart

Status

Samsung Galaxy Note20 Ultra

Not recorded

Not recorded

Samsung One UI

Not recorded

PASS

PASS

PASS where SIM selection is shown

PASS

PASS

Tested

MEIZU Lucky 08

M431Q

Android 14

Flyme 11.0.6.4G

12 GB / 256 GB

PASS

PASS

PASS

PASS

PASS

Tested

Samsung Galaxy Note20 Ultra

The Samsung Galaxy Note20 Ultra is used as one of the main reference devices during FaceAccess development and regression testing.

Verified behavior includes:

Real-time face tracking with MediaPipe Face Landmarker.

YAW navigation between accessible interface items.

PITCH actions in supported modes.

Intentional blink confirmation.

Mouth gesture handling.

Cursor movement, locking and unlocking.

Cursor cleanup after stopping and restarting tracking.

Support-mode contact selection and call flow.

Dual-SIM selection when the system dialer displays a SIM-selection interface.

Regression testing after OEM-specific compatibility fixes.

The exact Android version, One UI version and hardware model code should be added later if they are recorded from the test device.

MEIZU Lucky 08

The second confirmed physical test device is:

Device: MEIZU Lucky 08
Model: M431Q
Android version: 14
Flyme version: 11.0.6.4G
RAM: 12 GB
Storage: 256 GB
Display size: 6.78 inches
Display resolution: 2780 × 1264

These hardware and software details were recorded directly from the device information screen used during testing.

Verified FaceAccess behavior on MEIZU Lucky 08

The following behaviors were manually verified on the MEIZU Lucky 08:

Face tracking operates normally.

YAW navigation works in Flyme system Settings after the Accessibility compatibility fallback.

Intentional blink can confirm the currently focused item.

Support mode can move between contacts using head gestures.

PITCH UP can open the selected phone number in the system dialer.

Intentional blink can trigger the call action.

The Flyme SIM-selection dialog can be detected.

YAW left/right can move between available SIM choices.

Intentional blink confirms the selected SIM.

Devices that do not show a SIM-selection dialog continue through the normal call flow.

Cursor mode remains functional.

Stopping and restarting tracking does not leave duplicate cursor overlays.

Dual-SIM Behavior

FaceAccess supports two call flows.

Normal call flow

If the system does not display a SIM-selection interface, FaceAccess continues using the normal call flow without activating SIM-selection control.

SIM-selection flow

If a SIM-selection interface is detected:

YAW left/right changes the currently selected SIM.

FaceAccess keeps a visible Accessibility focus on the selected option where supported.

Intentional blink confirms the selected SIM.

The normal Support-mode PITCH behavior is preserved outside the SIM-selection interface.

This behavior was manually tested on the MEIZU Lucky 08.

OEM Compatibility Strategy

FaceAccess keeps the standard Android Accessibility path as the primary implementation.

OEM-specific fallback logic is used only when the standard Android behavior is insufficient. This reduces the risk of breaking devices that already work correctly while improving compatibility with customized Android interfaces such as Flyme OS.

Current compatibility work includes:

Semantic Accessibility navigation fallback for Flyme system screens.

Selecting the Accessibility window/root that exposes the most useful navigation targets.

Generic dialer fallback when an OEM dialer does not respond correctly to a package-specific intent.

Accessibility-based SIM-selection handling when the system requires explicit SIM confirmation.

Preservation of the standard Samsung navigation path during Meizu/Flyme fixes.

Test Scope

A PASS entry means the feature was manually verified on the listed physical test device during the current development cycle.

It does not mean that every Android version, every firmware version or every device from the same manufacturer is guaranteed to work.

Before adding a new device to this document, record:

Manufacturer and exact device name.

Model code.

Android version.

OEM UI / ROM version.

FaceAccess version or commit SHA.

Features tested.

Actual test result.

Known limitations, if any.

Devices Not Yet Verified

Manufacturer

Device / UI

Status

Xiaomi

HyperOS / MIUI devices

Not yet tested

OPPO

ColorOS devices

Not yet tested

vivo

Funtouch OS / OriginOS devices

Not yet tested

Google

Pixel devices

Not yet tested

Reporting Compatibility Issues

When reporting a device-specific problem, include:

Device manufacturer and model.

Android version.

OEM UI / ROM version.

FaceAccess version or commit SHA.

Active FaceAccess mode.

Gesture used.

Expected behavior.

Actual behavior.

Relevant Logcat output when available.