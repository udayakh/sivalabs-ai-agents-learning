# Review Buddy - Requirements (use case specifications)

Generated from [prd.md](prd.md) with the `prd-to-requirements` skill, then refined use case by use
case. Implement with the `implement-use-case` skill; when clarifications come up during
implementation, sync this file with the `update-use-case` skill.

| ID     | Use case                         | Status          | Depends on     |
|--------|----------------------------------|-----------------|----------------|
| UC-000 | Register, login, logout          | Completed       | -              |
| UC-001 | Browse products                  | Completed       | UC-000         |
| UC-002 | Submit a product                 | Completed       | UC-000         |
| UC-003 | View product details             | Completed       | UC-001         |
| UC-004 | Review a product                 | Completed       | UC-003         |
| UC-005 | AI review summary                | Completed       | UC-004         |
| UC-006 | My products and my reviews page  | Not implemented | UC-002, UC-004 |
| UC-007 | Edit / delete my review          | Not implemented | UC-004         |

---

## UC-000 Register, login, logout
**Status:** Completed (provided by the scaffolding, like Progen does)

- Visitors register with name, email and password (min 4 chars). Emails are unique (case insensitive).
- Login uses email + password (form login). Passwords are stored as BCrypt hashes.
- The navbar shows the logged-in user's **name** (not email) with a logout action.

**Acceptance criteria**
- Duplicate email shows a field error on the registration form.
- Wrong password redirects to `/login?error`.

## UC-001 Browse products
**Status:** Completed

Anyone (also anonymous) can browse products at `/products`.
- Grid of product cards: image (fallback: first letter), name, tagline, category, star rating, review count.
- 9 products per page with previous/next pagination.
- Search `q` matches name or tagline, case insensitive.
- Filter by category (category is an entity with `name` and `slug`; URLs use the slug).
- Sort: `newest` (default), `top-rated` (avg rating desc), `most-reviewed` (review count desc).
- Search, filter, sort and page combine and are preserved in links.

**Acceptance criteria**
- Unknown sort codes fall back to newest; page < 1 is treated as page 1.

## UC-002 Submit a product
**Status:** Completed

Logged-in users submit products at `/products/new`.
- Fields: name (max 100), tagline (max 150), description (max 2000), website URL (http/https),
  optional image URL (http/https), category (required).
- A unique slug is generated from the name (`Pixel Press` -> `pixel-press`, duplicates get `-2`, `-3`...).
  Reserved slugs (`new`) are prefixed (`product-new`).
- After submitting, the user is redirected to the product details page.

**Acceptance criteria**
- Anonymous users are redirected to the login page.
- Validation errors are shown next to each field.

## UC-003 View product details
**Status:** Completed

`/products/{slug}` shows product info, the AI summary box and all reviews (newest first). Unknown slug -> 404 page.

## UC-004 Review a product
**Status:** Completed

Logged-in users review a product from its details page.
- Rating 1-5 (required), "what I like" (required, max 1000), "what to improve" (optional, max 1000).
- A user can review a product only **once**; makers **cannot review their own** product.
- The product's average rating (1 decimal) and review count are updated immediately.

**Acceptance criteria**
- Blocked users see the reason instead of the form.

## UC-005 AI review summary
**Status:** Completed

Clarifications captured during implementation (via `update-use-case`):
- Generate automatically after each new review, **asynchronously**, once the product has at least
  **3 reviews** (`app.ai.summary.min-reviews`).
- Use **Spring AI** with OpenAI; retry failed requests **twice**.
- If AI is not configured or fails, fall back to a deterministic rule based summary so the feature never breaks.
- Show who generated the summary (`spring-ai`, `rule-based`) and how many reviews it is based on.
- Review text is untrusted input: the system prompt instructs the model to ignore instructions inside reviews.

## UC-006 My products and my reviews page
**Status:** Not implemented (practice exercise, see learning/day-08)

A logged-in user sees `/me` with two lists: products they submitted and reviews they wrote (with product links).

## UC-007 Edit / delete my review
**Status:** Not implemented (practice exercise)

The author of a review can edit or delete it; rating stats and the AI summary are refreshed afterwards.
