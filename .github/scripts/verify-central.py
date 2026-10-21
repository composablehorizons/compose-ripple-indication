"""Wait for every Gradle publication to become downloadable from Maven Central."""
import time
import urllib.error
import urllib.request
import xml.etree.ElementTree as ET
from pathlib import Path

namespace = {"m": "http://maven.apache.org/POM/4.0.0"}
urls = []
for pom in sorted(Path("ripple-indication/build/publications").glob("*/pom-default.xml")):
    root = ET.parse(pom).getroot()
    group = root.findtext("m:groupId", namespaces=namespace)
    artifact = root.findtext("m:artifactId", namespaces=namespace)
    version = root.findtext("m:version", namespaces=namespace)
    if not all((group, artifact, version)):
        raise SystemExit(f"Missing Maven coordinates in {pom}")
    base = f"https://repo.maven.apache.org/maven2/{group.replace('.', '/')}/{artifact}/{version}/{artifact}-{version}"
    urls.append(base + ".pom")
    if root.findtext("m:packaging", default="jar", namespaces=namespace) != "pom":
        packaging = root.findtext("m:packaging", default="jar", namespaces=namespace)
        urls.append(base + "." + packaging)
if not urls:
    raise SystemExit("No publication POMs found")

pending = set(urls)
deadline = time.monotonic() + 1800
while pending:
    for url in sorted(pending):
        try:
            with urllib.request.urlopen(urllib.request.Request(url, method="HEAD"), timeout=20):
                pending.remove(url)
                print(f"Available: {url}", flush=True)
        except urllib.error.HTTPError as error:
            if error.code not in (404, 429, 500, 502, 503, 504):
                raise
        except (urllib.error.URLError, TimeoutError):
            pass
    if pending:
        if time.monotonic() >= deadline:
            raise SystemExit("Timed out waiting for Maven Central: " + ", ".join(sorted(pending)))
        print(f"Waiting for {len(pending)} Maven Central artifacts...", flush=True)
        time.sleep(30)
print("All platform publications are available on Maven Central.")
