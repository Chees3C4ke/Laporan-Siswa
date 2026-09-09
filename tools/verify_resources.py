#!/usr/bin/env python3
"""Dev tool: pemeriksaan statis cepat untuk project Laporan Siswa.

Menjalankan 3 pemeriksaan tanpa butuh Android SDK:
  1. Semua file XML (manifest + res) well-formed.
  2. Semua referensi resource (@drawable/x, R.string.y, dst.) benar-benar ada.
  3. Sanity file Java: kurung seimbang & nama class == nama file,
     plus nama activity di Manifest punya file class-nya.

Pakai:  python3 tools/verify_resources.py
"""
import os
import re
import sys
import xml.etree.ElementTree as ET

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES = os.path.join(ROOT, "app", "src", "main", "res")
JAVA = os.path.join(ROOT, "app", "src", "main", "java")
MANIFEST = os.path.join(ROOT, "app", "src", "main", "AndroidManifest.xml")

LIB_RESOURCE = ("appbar_scrolling_view_behavior",)
LIB_PREFIX = ("Widget.", "TextAppearance.", "Theme.", "Base.", "android:")

errors = []

# ---------------------------------------------------------------- 1) XML parse
xml_files = [MANIFEST]
for d, _, fs in os.walk(RES):
    for f in fs:
        if f.endswith(".xml"):
            xml_files.append(os.path.join(d, f))
for p in xml_files:
    try:
        ET.parse(p)
    except Exception as e:  # noqa: BLE001
        errors.append(f"XML PARSE {os.path.relpath(p, ROOT)}: {e}")

# ------------------------------------- 1b) tidak boleh ada file lepas di res/
# AAPT2 menolak file yang berada langsung di root res/ (harus di dalam
# folder berekstensi konfigurasi seperti drawable/, layout/, values/ ...).
for f in os.listdir(RES):
    if os.path.isfile(os.path.join(RES, f)):
        errors.append(f"STRAY FILE di root res/: {f}")

# ------------------------------------------------------- 2) inventaris resource
def res_names(kind_dir, ext=".xml"):
    out = set()
    d = os.path.join(RES, kind_dir)
    if os.path.isdir(d):
        for f in os.listdir(d):
            if f.endswith(ext):
                out.add(f[: -len(ext)])
    return out


drawables = set()
for sub in os.listdir(RES):
    if sub.startswith("drawable"):
        for f in os.listdir(os.path.join(RES, sub)):
            drawables.add(os.path.splitext(f)[0])
mipmaps = set()
for sub in os.listdir(RES):
    if sub.startswith("mipmap"):
        for f in os.listdir(os.path.join(RES, sub)):
            mipmaps.add(os.path.splitext(f)[0])
layouts = res_names("layout")
menus = res_names("menu")
xmls = res_names("xml")

values = {}
for f in os.listdir(os.path.join(RES, "values")):
    if f.endswith(".xml"):
        t = ET.parse(os.path.join(RES, "values", f)).getroot()
        for child in t:
            if child.get("name"):
                values.setdefault(child.tag, set()).add(child.get("name"))
strings = values.get("string", set())
colors = values.get("color", set())
styles = values.get("style", set())
color_dir = os.path.join(RES, "color")
if os.path.isdir(color_dir):
    for f in os.listdir(color_dir):
        if f.endswith(".xml"):
            colors.add(f[:-4])

POOLS = {"drawable": drawables, "string": strings, "color": colors,
         "layout": layouts, "menu": menus, "xml": xmls,
         "mipmap": mipmaps, "style": styles}

# ------------------------------------------- 3) referensi @type/name di semua XML
ref_re = re.compile(r"@([a-zA-Z]+)/([A-Za-z0-9_.]+)")
for p in xml_files:
    txt = open(p, encoding="utf-8").read()
    for kind, name in ref_re.findall(txt):
        if kind not in POOLS:
            continue
        if name in LIB_RESOURCE or name.startswith(LIB_PREFIX):
            continue
        if name not in POOLS[kind]:
            errors.append(f"MISSING @{kind}/{name}  (di {os.path.relpath(p, ROOT)})")

# ------------------------------------------------- 4) referensi R.* dari Java
java_files = []
for d, _, fs in os.walk(JAVA):
    for f in fs:
        if f.endswith(".java"):
            java_files.append(os.path.join(d, f))

ids_in_layout = set()
for p in xml_files:
    txt = open(p, encoding="utf-8").read()
    for m in re.finditer(r"@\+id/([A-Za-z0-9_]+)", txt):
        ids_in_layout.add(m.group(1))

r_re = re.compile(r"\bR\.(drawable|string|color|layout|menu|xml|mipmap|id|style)\.([A-Za-z0-9_]+)")
for p in java_files:
    txt = open(p, encoding="utf-8").read()
    for kind, name in r_re.findall(txt):
        if kind == "id" and name == "home" and "android.R.id.home" in txt:
            continue
        pool = POOLS[kind] if kind != "id" else ids_in_layout
        if name not in pool:
            errors.append(f"MISSING R.{kind}.{name}  (di {os.path.relpath(p, ROOT)})")

# ------------------------------------------------------ 5) sanity file Java
for p in java_files:
    t = open(p, encoding="utf-8").read()
    t2 = re.sub(r'"(\\.|[^"\\])*"', '""', t)
    t2 = re.sub(r"'(\\.|[^'\\])*'", "''", t2)
    t2 = re.sub(r"//.*", "", t2)
    t2 = re.sub(r"/\*.*?\*/", "", t2, flags=re.S)
    for a, b in [("{", "}"), ("(", ")"), ("[", "]")]:
        if t2.count(a) != t2.count(b):
            errors.append(f"JAVA BRACE {os.path.relpath(p, ROOT)}: {a}{b} "
                          f"{t2.count(a)} vs {t2.count(b)}")
    cls = os.path.splitext(os.path.basename(p))[0]
    if not re.search(rf"\b(class|interface|enum)\s+{cls}\b", t):
        errors.append(f"JAVA CLASS NAME {os.path.relpath(p, ROOT)} != {cls}")

# --------------------------------------------- 6) activity di Manifest ada file
man = open(MANIFEST, encoding="utf-8").read()
for m in re.finditer(r'android:name="\.([A-Za-z0-9_.]+)"', man):
    rel = m.group(1).replace(".", "/")
    if not os.path.exists(os.path.join(JAVA, "com", "laporansiswa", "app", rel + ".java")):
        errors.append(f"MANIFEST MISSING class .{m.group(1)}")

print("=== HASIL ===")
if errors:
    for e in sorted(set(errors)):
        print(" *", e)
    print(f"\n{len(set(errors))} masalah")
    sys.exit(1)
print("SEMUA REFERENSI RESOURCE OK")
print(f"drawable={len(drawables)} layout={len(layouts)} string={len(strings)} "
      f"color={len(colors)} style={len(styles)} id={len(ids_in_layout)}")
