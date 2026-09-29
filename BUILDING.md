# Build ParCool Fabric for Minecraft 26.1.2

This project is already configured for:

- Minecraft: 26.1.2
- Fabric Loader: 0.19.5 or newer
- Fabric API: 0.155.3+26.1.2
- Java/JDK: 25

## macOS

1. Install a JDK 25 distribution if you do not already have one.
2. Double-click `build-jar.command` (you may need to allow it in macOS security settings), or run:
   `./build-jar.command`
3. When the build succeeds, use the non-sources JAR in `build/libs/`.

Expected file: `build/libs/parcool-fabric-1.0.0.jar`

## Windows

1. Install JDK 25 and make sure `java -version` reports version 25.
2. Double-click `build-jar.bat`.
3. When the build succeeds, use `build\libs\parcool-fabric-1.0.0.jar`.

## GitHub Actions (no local Java setup)

1. Put this project in a GitHub repository.
2. Open the repository's **Actions** tab.
3. Run the **Build Fabric JAR** workflow, or push a commit to trigger it automatically.
4. Download the `ParCool-Fabric-26.1.2` artifact from the successful workflow run.
5. Extract the artifact and place the normal JAR (not a sources JAR) in your Minecraft `mods` folder.

## Minecraft installation

Your Fabric 26.1.2 instance also needs Fabric API for 26.1.2 installed in the `mods` folder.

Do not put the source ZIP itself in `mods`; only the built `.jar` belongs there.

## Publish this project to GitHub on macOS

1. On GitHub, create an empty repository named `parcool-fabric-26.1.2` (do not add a README, license, or .gitignore).
2. Copy the repository HTTPS URL.
3. Double-click `publish-to-github.command` and paste the URL when prompted.
4. After the push, open the repository's **Actions** tab and run **Build Fabric JAR**.
5. Download the `ParCool-Fabric-26.1.2` artifact from the completed workflow run.
