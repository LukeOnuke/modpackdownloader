# modpackdownloader

> **Warning**
> 
> I have written a **better** version of this [link here (LukeOnuke/mpdl)](https://github.com/lukeonuke/mpdl), using the newer API's and made it compatable with the new curseforge DRM
> 
> This program is no longer supported and probably doesnt work anymore.

Simple minecraft downloader without the bloat of overwolf.

***Works with java 11 and up***

# Usage
1. Download jar from releases
2. Navigate to it in your console of choice
3. Download your minecraft modpack from curseforge, and 
   unzip it. After unzipping copy over the jar to the unzipped folder.
4. Execute modpackdownloader with the command below
   ```bash
    java -jar modpackdownloader-1.0.0.jar
   ```
5. It will ask you for the path to manifest.json, enter `manifest.json` and press enter (⚠***IT CAN NOT CONTAIN SPACES***⚠)
6. After its done downloading all the files it will display
   a report, there you can see all the needed info to 
   install the modpack. Assembled modpack is found in
   `<root-of-manifest.json>/modpack`
   Mods are found in the 
   `<root-of-manifest.json>/mods`.
    
    ### example report
    ```txt
        ============> Report <============
        Modpack info :
            - Modpack name : "All the Mods 6"
            - Modpack author : "LadyLexxie"
            - Modpack version : "1.7.8"
            - Ammount of mods : 321
        Modloader acceptable versions :
            - forge-36.2.2 | recommended : true

        Minecraft version : "1.16.5"
    ```
    
