# Release signing

`ramesh-release.p12` is the key that identifies Ramesh to Android. Every future release must be
signed with the same key, otherwise the phone refuses the update.

| Item | Value |
| --- | --- |
| File | `keystore/ramesh-release.p12` (PKCS#12) |
| Alias | `ramesh` |
| Store / key password | see `keystore.properties` |
| Algorithm | RSA 4096, SHA-256, valid until 2054 |
| Subject | `CN=Ramesh, O=Ramesh, C=IR` |

`gradle` reads `keystore/keystore.properties` automatically (see `app/build.gradle.kts`), so
`./gradlew assembleRelease` produces a signed APK locally and in CI.

## ⚠️ This key is public

The keystore and its password are committed to a public repository, which means anyone can sign an
APK that Android recognises as an update of Ramesh. That is fine while the app is distributed only
through GitHub Releases by its owner, but it is **not** acceptable for Google Play, and it weakens
the trust story for F-Droid.

## Move the key to GitHub secrets (recommended)

1. Open **Settings → Secrets and variables → Actions** in the repository.
2. Add the base64 form of the keystore:
   ```bash
   base64 -w0 keystore/ramesh-release.p12
   ```
   as `SIGNING_KEYSTORE`, then add `SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS` (`ramesh`) and
   `SIGNING_KEY_PASSWORD` with the values from `keystore.properties`.
3. Delete the checked-in material and commit:
   ```bash
   git rm keystore/ramesh-release.p12 keystore/keystore.properties
   git commit -m "Move signing key to GitHub secrets"
   ```
   The release workflow prefers the secret when it exists and otherwise falls back to this folder,
   so removing the files only takes effect once the secrets are in place.
4. Keep an offline backup of the `.p12` file. Losing it means you can never update an installed
   copy of Ramesh.

## Rotating the key

A new key means a new identity: existing installs cannot be updated, they have to be uninstalled
first. If you rotate, bump `applicationId` (for example `com.ramesh.sms2`) or accept the manual
reinstall.
