# Hospital Management System

A Java Swing + JDBC + MySQL desktop application that manages three sets of hospital
records — **Patients**, **Doctors**, and **Administration** — with full
Insert / Update / Delete / View (CRUD) operations on each.

Runs entirely in the browser through GitHub Codespaces. **Nothing to install.**

---

## What the app does

When you launch it, you first choose which records you want to manage:

```
              Hospital Management System
         Select the records you want to manage

                  [   Patients   ]
                  [   Doctors    ]
                  [ Administration ]
              [ Test Database Connection ]
```

Each module opens a form + a live table, with buttons for **Insert**, **Update**,
**Delete**, **View All**, and **Clear Form**.

### Database tables

| Table | Columns |
|---|---|
| `patients` | patient_id, name, age, address, disease, allergies, insurance, doctor_incharge, department |
| `doctors` | doctor_id, name, age, experience_years, department, cases_handled, salary |
| `administrators` | admin_id, name, designation, department, shift, contact_number |

---

## Part 1 — Put this project on GitHub

Do this **once**, on any machine with internet.

### Step 1: Create a GitHub account
Go to <https://github.com> and sign up (free). Skip if you already have one.

### Step 2: Create a new repository
1. Click the **+** icon (top right) → **New repository**
2. Repository name: `hospital-management`
3. Choose **Public** (Codespaces free tier works on public repos)
4. Do **NOT** tick "Add a README" — we already have one
5. Click **Create repository**

### Step 3: Upload the files
The easiest way, no Git commands needed:

1. On the empty repo page, click **uploading an existing file**
2. Drag the entire `hospital-management` folder contents into the box
3. Scroll down, click **Commit changes**

> **Important:** the `.devcontainer` folder starts with a dot, so it may be hidden
> in your file explorer. On Windows press `Ctrl + H` (or View → Hidden items) to
> show it. If drag-and-drop skips it, use "Add file → Create new file" and type
> `.devcontainer/devcontainer.json` as the filename, then paste the contents.
> Repeat for `docker-compose.yml` and `setup.sh`.

**If you prefer the command line** (needs Git installed):
```bash
cd hospital-management
git init
git add .
git commit -m "Hospital Management System - Java Swing + JDBC + MySQL"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/hospital-management.git
git push -u origin main
```

### Step 4: Add your teammates
Repo → **Settings** → **Collaborators** → **Add people** → enter their GitHub usernames.

---

## Part 2 — Run the project (this is your demo)

### Step 1: Open a Codespace
On the repo page: **Code** (green button) → **Codespaces** tab → **Create codespace on main**

The first build takes **2–4 minutes** (it is installing Java, MySQL, and a desktop
for you). After that it resumes in seconds.

> **Do this once at home before the exam** so it's already built.

Watch the terminal. When you see this, it's ready:
```
=======================================================
 Setup complete.
 Run the app with:   bash run.sh
=======================================================
```

### Step 2: Open the virtual desktop
1. Click the **PORTS** tab (next to TERMINAL, at the bottom)
2. Find port **6080** — labelled *"Desktop (open this to see the Swing app)"*
3. Click the **globe icon** 🌐 to open it in a new browser tab
4. Click **Connect**, password: `vscode`

You now have a Linux desktop inside your browser tab. Leave it open.

### Step 3: Run the app
Back in the Codespace terminal:
```bash
bash run.sh
```
Switch to the desktop tab — **your Swing window is there.**

---

## Part 3 — The demo script (what to do in front of your professor)

Keep **two browser tabs** open: the **desktop** tab and the **Codespace** tab.

**1. Prove the database is connected**
On the home screen, click **Test Database Connection** → "Connected to MySQL successfully."

**2. Show the module selection**
Click **Patients**. The existing sample records load automatically.

**3. Insert a record**
Fill the form:
- Name: `Test Patient`
- Age: `30`
- Address: `123 Demo Street`
- Disease: `Fever`
- Allergies: `None`
- Insurance: `Yes`
- Doctor In-charge: `Dr. Anita Rao`
- Department: `General Medicine`

Click **Insert**. The table refreshes with the new row.

**4. When the prof says "prove it's in the MySQL table"** ← *the important bit*

Switch to the **Codespace tab**, open a terminal, and run:
```bash
mysql -u root -proot hospital
```
Then at the `mysql>` prompt:
```sql
SELECT * FROM patients;
```
The row is right there, straight from the database. Nothing faked.

Other queries worth having ready:
```sql
SHOW TABLES;
DESCRIBE patients;
SELECT * FROM doctors;
SELECT * FROM administrators;
SELECT name, department FROM patients WHERE insurance = 'Yes';
```
Type `exit;` to leave the MySQL prompt.

**5. Update and Delete**
Back in the app: click the new row in the table (it auto-fills the form), change the
disease, click **Update**. Then click **Delete** and confirm. Re-run the `SELECT`
in MySQL to show the change took effect.

**6. Show the other modules**
Click **< Back** → **Doctors** → insert one. → **Back** → **Administration** → insert one.

---

## Backup plan (in case the wifi dies)

**Option A — record it.** The night before, screen-record the full demo above.
Keep the video on your laptop. Not ideal, but better than nothing.

**Option B — run it locally.** Works offline:
1. Install a **JDK** (17 or later) and **XAMPP**
2. Start MySQL from the XAMPP control panel
3. Open phpMyAdmin → SQL tab → paste `db/schema.sql` → Go
4. Download `mysql-connector-j-8.4.0.jar` from the MySQL website
5. From the project folder:
   ```
   javac -cp ".;mysql-connector-j-8.4.0.jar" -d out src/main/java/com/hospital/*.java
   java  -cp "out;mysql-connector-j-8.4.0.jar" com.hospital.HospitalApp
   ```
   (On Mac/Linux use `:` instead of `;`)

XAMPP's default MySQL root password is blank. If so, either set it to `root`, or set
an environment variable before running: `set DB_PASSWORD=` (Windows) / `export DB_PASSWORD=` (Mac/Linux).

---

## Viva questions you should be ready for

**What is JDBC?**
Java Database Connectivity — a standard Java API that lets a Java program connect to
and run SQL queries against a relational database like MySQL.

**How does your program connect to MySQL?**
Through the `DBConnection` class. It calls `DriverManager.getConnection(url, user, password)`
with the JDBC URL `jdbc:mysql://127.0.0.1:3306/hospital`. Every CRUD method opens a
connection through this one class.

**What is the MySQL Connector/J?**
The JDBC *driver* for MySQL — the jar that actually implements the JDBC interfaces for
MySQL. It's declared as a Maven dependency in `pom.xml`, so Maven downloads it automatically.

**Statement vs PreparedStatement — why did you use PreparedStatement?**
`PreparedStatement` is pre-compiled and takes parameters with `?` placeholders. It's faster
for repeated queries and, more importantly, it **prevents SQL injection** because user input
is never concatenated into the SQL string. I used `Statement` only for the fixed `SELECT *`
queries which take no user input.

**executeQuery() vs executeUpdate()?**
`executeQuery()` is used for `SELECT` and returns a `ResultSet`. `executeUpdate()` is used
for `INSERT`, `UPDATE`, `DELETE` and returns an `int` — the number of rows affected.

**What is a ResultSet?**
An object holding the rows returned by a query. You call `rs.next()` to move through rows
one at a time, and `rs.getString("column")` / `rs.getInt("column")` to read values.

**Why try-with-resources?**
`try (Connection conn = ...)` automatically closes the connection when the block ends, even
if an exception is thrown. This prevents connection leaks.

**What Swing components did you use?**
`JFrame` (window), `JPanel` (containers), `CardLayout` (to switch between the home screen and
the three modules), `JTextField`, `JComboBox`, `JButton`, `JTable` with `DefaultTableModel`
(to display records), `JOptionPane` (dialogs), and `GridBagLayout`/`BorderLayout` for layout.

**Why is `SwingUtilities.invokeLater` in main?**
Swing is not thread-safe. All UI components must be created and updated on the Event
Dispatch Thread, and `invokeLater` guarantees that.

---

## 20-second project summary

> The Hospital Management System is a Java Swing application connected to a MySQL database
> using JDBC. It manages three tables — patients, doctors, and administrative staff — and
> supports insert, update, delete, and view operations on each. The user first selects which
> records to manage, then performs CRUD operations through a form-based GUI. The project
> demonstrates JDBC connectivity, PreparedStatements, and GUI development in Java.
