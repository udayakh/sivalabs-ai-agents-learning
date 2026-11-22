# Review Buddy - Product Requirements Document

## Overview
Review Buddy is a community website where makers share products they have built and users give
honest, structured feedback: a star rating, what they like, and what could be improved. An AI
generated summary tells visitors at a glance what people are saying about each product.

## Problem Statement
- Indie makers struggle to get structured, actionable feedback on early products.
- Visitors have to read dozens of reviews to understand overall sentiment.
- Generic review sites mix praise and criticism in free text, which is hard to act on.

## Vision
The friendliest place for makers to launch small products and learn what to improve next -
feedback that is honest, structured and summarized.

## Target Users
- **Makers**: solo developers and small teams launching side projects and tools.
- **Early adopters**: people who like discovering and trying new products.
- **Visitors**: anyone deciding whether a product is worth trying.

## Key Features (MVP)
1. Sign up / log in.
2. Browse products with search, category filter, sorting (newest, top rated, most reviewed) and pagination.
3. Submit a product (name, tagline, description, website, image, category).
4. Product details page with all reviews.
5. Write a review: rating 1-5, "what I like", "what to improve". One review per user per product;
   makers cannot review their own products.
6. AI summary of reviews once a product has enough reviews.

## Out of Scope (for now)
Comments and replies, up-votes, maker profiles, notifications/emails, moderation tools,
payments, public API, mobile apps.

## Risks & Assumptions
- Assumes makers want critical feedback, not only praise.
- Fake or spam reviews could reduce trust (mitigation later: moderation, reputation).
- AI summaries can be wrong or manipulated by review text; summaries must be clearly labelled.
- Low traffic at launch: the product must be useful even with few reviews.
