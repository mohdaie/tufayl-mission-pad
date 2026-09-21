# 🚀 Tufayl's Mission Pad v2.0

A dyslexia-friendly, gamified learning web app for handwriting, spelling, focus and creative reward time. The project is intentionally lightweight: one main HTML app, vanilla JavaScript, browser storage and a small PWA layer.

## ✨ What v2.0 includes

### ✍️ Trace Mission
- Uppercase, lowercase and numbers.
- Sky / Plane / Grass handwriting guide lines.
- Improved trace scoring using a tolerance mask, stroke precision, target coverage and minimum path length.
- 50%+ score advances to the next target.
- Three unsuccessful attempts can deduct one star without allowing the total to fall below zero.

### 🔤 Spell Mission
A child-safe curriculum now replaces the old unrestricted word pool:

- **🌱 Foundation** — short phonics and early-reader words.
- **🌿 Growing** — common everyday words.
- **🚀 Explorer** — longer, age-appropriate challenge words.

The active spelling level can be changed in Settings. Text-to-speech, replay, Show Word, reset and skip controls remain available.

### 🎨 Art Studio Reward
Art Studio unlocks when the daily Trace and Spell targets are complete. Once earned, it stays unlocked for the rest of that day.

Tools include:
- Pen, pencil, brush, eraser and ruler.
- 48 colours.
- Stickers.
- Undo / redo.
- Zoom controls.
- Saved-art reward tracking.

### ⭐ Daily progress and streaks
- Daily Trace / Spell / Art progress persists across refreshes and app restarts.
- Completion history is stored per learner profile.
- Streaks are calculated from real consecutive calendar days.
- The weekly tracker represents the actual Monday–Sunday calendar week.
- Existing streak data is migrated automatically where possible.
- Multiple learner profiles remain supported.

### ⏱️ Focus support
- Adjustable focus timer.
- Large touch targets and low-distraction navigation.
- Dyslexia-friendly layout and clear visual separation between missions.

## 📱 Progressive Web App

v2.0 adds:
- `manifest.webmanifest`
- `sw.js` service worker
- `icon.svg`
- Standalone app metadata
- Offline caching for the core app
- Network-first navigation so new releases replace cached versions when online

The app can be added to the Home Screen on supported mobile/tablet browsers.

## 🌐 GitHub Pages

Repository:

**https://github.com/mohdaie/tufayl-mission-pad**

Expected GitHub Pages address when Pages is enabled for `main` / repository root:

**https://mohdaie.github.io/tufayl-mission-pad/**

If Pages is not enabled yet, open **Repository Settings → Pages**, publish from the `main` branch and repository root.

## 🛠️ Stack

- HTML5 / CSS3
- Vanilla JavaScript
- HTML5 Canvas API
- Web Speech API
- Local Storage
- Service Worker + Web App Manifest
- Google Fonts with browser fallback when offline

No framework, package manager, build process or backend is required.

## 💻 Local development

1. Clone or download the repository.
2. Open `index.html` for normal UI development.
3. PWA/service-worker functionality requires HTTP/HTTPS, so use a local web server when testing install/offline behaviour.

---

Created by Zuhairi.
