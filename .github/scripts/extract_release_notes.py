#!/usr/bin/env python3
import re
import sys
from pathlib import Path


def main() -> int:
    if len(sys.argv) != 4:
        print("usage: extract_release_notes.py <tag> <changelog> <output>", file=sys.stderr)
        return 2

    tag = sys.argv[1]
    changelog_path = Path(sys.argv[2])
    output_path = Path(sys.argv[3])

    if not re.fullmatch(r"v\d+\.\d+\.\d+", tag):
        print(f"release tag must match vX.Y.Z: {tag}", file=sys.stderr)
        return 1

    text = changelog_path.read_text(encoding="utf-8")
    heading = re.compile(rf"^##\s+{re.escape(tag)}(?:\s+-\s+.+)?\s*$", re.MULTILINE)
    match = heading.search(text)
    if not match:
        print(f"CHANGELOG.md does not contain a release section for {tag}", file=sys.stderr)
        return 1

    next_heading = re.search(r"^##\s+", text[match.end() :], re.MULTILINE)
    end = match.end() + next_heading.start() if next_heading else len(text)
    notes = text[match.start() : end].strip()

    if "Full Changelog" in notes:
        print("release notes must be human-written, not GitHub auto-generated notes", file=sys.stderr)
        return 1
    if len(notes.splitlines()) < 4:
        print(f"release notes for {tag} are too short", file=sys.stderr)
        return 1

    output_path.write_text(notes + "\n", encoding="utf-8")
    print(f"wrote release notes for {tag} to {output_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
