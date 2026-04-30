# Design System

## Design Direction

MarkScene should feel fast, calm, modern, and trustworthy.

Use Material 3 and a card/chip-based UI.

## Visual Principles

- Large photo previews.
- Clean cards.
- Rounded corners.
- Clear spacing.
- Minimal clutter.
- Fast state transitions.
- Strong empty states.
- Bottom-friendly actions.

## Theme

Support:

- Light mode.
- Dark mode.
- Dynamic color when available.

Avoid custom complex theming in MVP.

## Components

### Record Card

Contains:

- Thumbnail.
- Title or auto-generated fallback.
- Top 3 to 5 tags.
- Date.

### Tag Chip

States:

- Suggested.
- User-confirmed.
- User-created.
- Advanced AI.

MVP may use the same visual style for all states, but the data model should preserve source and confirmation status.

### Primary Capture Button

The capture/import actions should be visually prominent.

### Analysis Status

Use inline status cards instead of blocking full-screen loading where possible.

Examples:

```text
Generating local tags...
Advanced analysis failed. Local tags are still available.
No tags found. Add one manually.
```

## Suggested Navigation

MVP can use simple navigation:

- Home.
- Search.
- Settings.
- Record detail.
- Create/edit record.

Bottom navigation is optional. A simple top-level home with action buttons is acceptable for the first version.

## Copywriting Rules

Use cautious wording:

- "Detected tags"
- "Suggested tags"
- "May contain"
- "Appears to be"

Avoid overpromising:

- Do not say "all objects".
- Do not say "exact count".
- Do not say "guaranteed location".

## Accessibility

- Provide content descriptions for images and buttons.
- Ensure text contrast is acceptable.
- Support larger font sizes where practical.
- Do not rely on color alone for important state.

