# Vanguard Insurance Frontend

This is a React + Vite frontend application for the Vanguard Automated Commercial Insurance Claim Verification & Settlement Engine.

## Prerequisites
- Node.js (v16+)
- npm

## Setup & Run
1. Navigate to this directory (`D:\SOA\front`).
2. Run `npm install` to install dependencies.
3. Run `npm run dev` to start the development server on `http://localhost:3000`.

## Architecture Details
- The frontend ONLY communicates with the API Gateway at `http://localhost:8080`.
- All credentials and secrets are managed by the backend.
- The UI adapts based on the JWT `role` assigned (`POLICYHOLDER` or `ADJUSTER`).

## Deployment
Run `npm run build` to generate static assets in the `dist` folder.
