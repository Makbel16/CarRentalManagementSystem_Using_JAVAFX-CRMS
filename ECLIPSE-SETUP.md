# Eclipse Configuration for FXML Resources

## Problem: "Location is required" / NullPointerException

This error occurs when JavaFX can't find the FXML files because the `resources` folder is not in the classpath.

## Solution: Configure Resources Folder in Eclipse

### Method 1: Add Resources as Source Folder (Recommended)

1. **Right-click** on your project → **Properties**
2. Go to **Java Build Path** → **Source** tab
3. Click **Add Folder...**
4. Select the `resources` folder
5. Click **OK** → **Apply and Close**

### Method 2: Configure Build Path

1. **Right-click** on `resources` folder → **Build Path** → **Use as Source Folder**
2. The folder icon should change to show it's a source folder

### Method 3: Verify Output Folder

1. **Right-click** project → **Properties**
2. Go to **Java Build Path** → **Source** tab
3. Make sure **Default output folder** is set to: `CarRentalManagementSystem/bin`
4. The `resources` folder should be copied to `bin` when you build

## Verify Setup

After configuration:

1. **Clean and Build** the project:
   - Project → Clean → Clean all projects
   - Project → Build Project

2. Check that `bin/fxml/Login.fxml` exists after building

3. Run the application - it should load without errors

## Alternative: Use Absolute Paths (Not Recommended)

If the above doesn't work, you can temporarily use:

```java
File fxmlFile = new File("resources/fxml/Login.fxml");
FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
```

But this is not recommended for production.

## Quick Fix

If you're still having issues:

1. Make sure `resources` folder is at the **root** of your project (same level as `src`)
2. In Eclipse: Right-click `resources` → **Build Path** → **Use as Source Folder**
3. Clean and rebuild project
4. Run again

## Project Structure Should Be:

```
CarRentalManagementSystem/
├── src/
│   └── application/
│       └── Main.java
├── resources/          ← Must be source folder
│   └── fxml/
│       └── Login.fxml
└── bin/                ← Output folder
    └── fxml/           ← Should contain copied FXML files
        └── Login.fxml
```



