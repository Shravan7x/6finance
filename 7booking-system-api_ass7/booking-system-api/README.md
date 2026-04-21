# Online Booking System

A simple REST API + website for booking, modifying, cancelling, and deleting reservations.

## Features
- Create reservation
- View all reservations
- Modify reservation using PATCH
- Cancel reservation without deleting record
- Delete reservation permanently
- Check available slots
- SQLite database included automatically

## Project Structure
- `app.py` - Flask backend and REST API
- `templates/index.html` - frontend page
- `static/app.js` - frontend logic
- `static/style.css` - styling
- `requirements.txt` - Python dependency

## REST API Endpoints
- `GET /api/health`
- `GET /api/availability?service=Hotel%20Room&date=2026-04-20`
- `GET /api/reservations`
- `GET /api/reservations/<id>`
- `POST /api/reservations`
- `PUT /api/reservations/<id>`
- `PATCH /api/reservations/<id>`
- `PATCH /api/reservations/<id>/cancel`
- `DELETE /api/reservations/<id>`

## Run Steps
1. Open terminal in the project folder.
2. Create virtual environment:
   - Windows: `python -m venv venv`
3. Activate it:
   - Windows CMD: `venv\Scripts\activate`
4. Install dependencies:
   - `pip install -r requirements.txt`
5. Run app:
   - `python app.py`
6. Open browser:
   - `http://127.0.0.1:5000`
