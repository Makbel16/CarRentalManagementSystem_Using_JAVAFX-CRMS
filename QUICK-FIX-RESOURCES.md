# Quick Fix: "Location is not set" Error

## The Problem
Eclipse can't find your FXML files because the `resources` folder is not on the classpath.

## Solution 1: Configure Resources as Source Folder (RECOMMENDED)

### Step-by-Step:

1. **In Eclipse Package Explorer:**
   - Right-click on the `resources` folder
   - Select: **Build Path** → **Use as Source Folder**

2. **Verify:**
   - The `resources` folder icon should change (shows it's a source folder)
   - It should appear under "src" in the project structure

3. **Clean and Build:**
   - **Project** → **Clean...**
   - Select your project
   - Click **Clean**
   - Then **Project** → **Build Project**

4. **Verify Output:**
   - Check that `bin/fxml/Login.fxml` exists after building
   - If it exists, the configuration is correct

5. **Run the application again**

## Solution 2: Alternative - Use File Path (Temporary)

If Solution 1 doesn't work, the updated `Main.java` will try to find the file using file system paths as a fallback. But this is not recommended for production.

## Solution 3: Manual Classpath Configuration

1. **Right-click project** → **Properties**
2. Go to **Java Build Path** → **Source** tab
3. Click **Add Folder...**
4. Browse and select the `resources` folder
5. Click **OK** → **Apply and Close**
6. Clean and rebuild

## Verify It's Working

After configuration, when you run the app, you should see in console:
```
Found FXML via classpath: file:/path/to/bin/fxml/Login.fxml
```

If you see:
```
Could not find FXML file: /fxml/Login.fxml
```

Then the resources folder is still not configured correctly.

## Project Structure Should Be:

```
CarRentalManagementSystem/
├── src/                    (Source folder)
│   └── application/
│       └── Main.java
├── resources/              (MUST be source folder too!)
│   └── fxml/
│       └── Login.fxml
└── bin/                    (Output folder)
    └── fxml/               (Should contain copied FXML files)
        └── Login.fxml
```

## Still Not Working?

1. Check that `resources/fxml/Login.fxml` actually exists
2. Make sure there are no typos in the path
3. Try restarting Eclipse
4. Check Project → Properties → Java Build Path → Source tab
5. Ensure `resources` appears in the list of source folders



