from __future__ import annotations

import sqlite3
from contextlib import closing
from pathlib import Path
from typing import Any

from flask import Flask, jsonify, render_template, request

BASE_DIR = Path(__file__).resolve().parent
DB_PATH = BASE_DIR / "booking.db"

app = Flask(__name__)


def get_db() -> sqlite3.Connection:
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn


def init_db() -> None:
    with closing(get_db()) as conn:
        conn.execute(
            """
            CREATE TABLE IF NOT EXISTS reservations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_name TEXT NOT NULL,
                email TEXT NOT NULL,
                service TEXT NOT NULL,
                reservation_date TEXT NOT NULL,
                start_time TEXT NOT NULL,
                guests INTEGER NOT NULL CHECK(guests > 0),
                status TEXT NOT NULL DEFAULT 'confirmed',
                notes TEXT DEFAULT '',
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """
        )
        conn.commit()


def row_to_dict(row: sqlite3.Row) -> dict[str, Any]:
    return {
        "id": row["id"],
        "customerName": row["customer_name"],
        "email": row["email"],
        "service": row["service"],
        "reservationDate": row["reservation_date"],
        "startTime": row["start_time"],
        "guests": row["guests"],
        "status": row["status"],
        "notes": row["notes"],
        "createdAt": row["created_at"],
        "updatedAt": row["updated_at"],
    }


def validate_reservation_input(data: dict[str, Any], partial: bool = False) -> tuple[bool, str | None]:
    required = [
        "customerName",
        "email",
        "service",
        "reservationDate",
        "startTime",
        "guests",
    ]

    if not partial:
        for field in required:
            if field not in data or str(data[field]).strip() == "":
                return False, f"'{field}' is required"

    if "guests" in data:
        try:
            guests = int(data["guests"])
            if guests <= 0:
                return False, "'guests' must be greater than 0"
        except (TypeError, ValueError):
            return False, "'guests' must be a number"

    return True, None


def slot_conflict(
    reservation_date: str,
    start_time: str,
    service: str,
    exclude_id: int | None = None,
) -> bool:
    query = (
        "SELECT id FROM reservations WHERE reservation_date = ? AND start_time = ? "
        "AND service = ? AND status = 'confirmed'"
    )
    params: list[Any] = [reservation_date, start_time, service]
    if exclude_id is not None:
        query += " AND id != ?"
        params.append(exclude_id)

    with closing(get_db()) as conn:
        existing = conn.execute(query, params).fetchone()
        return existing is not None


@app.route("/")
def index() -> str:
    return render_template("index.html")


@app.get("/api/health")
def health() -> tuple[dict[str, str], int]:
    return {"status": "ok", "message": "Booking API is running"}, 200


@app.get("/api/availability")
def availability() -> tuple[Any, int]:
    reservation_date = request.args.get("date")
    service = request.args.get("service")

    if not reservation_date or not service:
        return jsonify({"error": "'date' and 'service' query parameters are required"}), 400

    all_slots = [
        "09:00", "10:00", "11:00", "12:00",
        "14:00", "15:00", "16:00", "17:00"
    ]

    with closing(get_db()) as conn:
        rows = conn.execute(
            """
            SELECT start_time FROM reservations
            WHERE reservation_date = ? AND service = ? AND status = 'confirmed'
            """,
            (reservation_date, service),
        ).fetchall()

    booked_slots = {row["start_time"] for row in rows}
    available_slots = [slot for slot in all_slots if slot not in booked_slots]

    return jsonify(
        {
            "service": service,
            "date": reservation_date,
            "availableSlots": available_slots,
        }
    ), 200


@app.get("/api/reservations")
def list_reservations() -> tuple[Any, int]:
    status = request.args.get("status")
    email = request.args.get("email")

    query = "SELECT * FROM reservations"
    conditions: list[str] = []
    params: list[Any] = []

    if status:
        conditions.append("status = ?")
        params.append(status)
    if email:
        conditions.append("email = ?")
        params.append(email)

    if conditions:
        query += " WHERE " + " AND ".join(conditions)
    query += " ORDER BY reservation_date, start_time"

    with closing(get_db()) as conn:
        rows = conn.execute(query, params).fetchall()

    return jsonify([row_to_dict(row) for row in rows]), 200


@app.get("/api/reservations/<int:reservation_id>")
def get_reservation(reservation_id: int) -> tuple[Any, int]:
    with closing(get_db()) as conn:
        row = conn.execute("SELECT * FROM reservations WHERE id = ?", (reservation_id,)).fetchone()

    if row is None:
        return jsonify({"error": "Reservation not found"}), 404

    return jsonify(row_to_dict(row)), 200


@app.post("/api/reservations")
def create_reservation() -> tuple[Any, int]:
    data = request.get_json(silent=True) or {}
    valid, error = validate_reservation_input(data)
    if not valid:
        return jsonify({"error": error}), 400

    if slot_conflict(data["reservationDate"], data["startTime"], data["service"]):
        return jsonify({"error": "Selected slot is already booked for this service"}), 409

    with closing(get_db()) as conn:
        cursor = conn.execute(
            """
            INSERT INTO reservations (
                customer_name, email, service, reservation_date, start_time, guests, notes, status,
                created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, 'confirmed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """,
            (
                data["customerName"].strip(),
                data["email"].strip(),
                data["service"].strip(),
                data["reservationDate"].strip(),
                data["startTime"].strip(),
                int(data["guests"]),
                str(data.get("notes", "")).strip(),
            ),
        )
        conn.commit()
        reservation_id = cursor.lastrowid

    with closing(get_db()) as conn:
        row = conn.execute("SELECT * FROM reservations WHERE id = ?", (reservation_id,)).fetchone()

    return jsonify(row_to_dict(row)), 201


@app.put("/api/reservations/<int:reservation_id>")
@app.patch("/api/reservations/<int:reservation_id>")
def update_reservation(reservation_id: int) -> tuple[Any, int]:
    data = request.get_json(silent=True) or {}
    partial = request.method == "PATCH"
    valid, error = validate_reservation_input(data, partial=partial)
    if not valid:
        return jsonify({"error": error}), 400

    with closing(get_db()) as conn:
        existing = conn.execute("SELECT * FROM reservations WHERE id = ?", (reservation_id,)).fetchone()
        if existing is None:
            return jsonify({"error": "Reservation not found"}), 404

        current = row_to_dict(existing)
        updated = {
            "customerName": data.get("customerName", current["customerName"]),
            "email": data.get("email", current["email"]),
            "service": data.get("service", current["service"]),
            "reservationDate": data.get("reservationDate", current["reservationDate"]),
            "startTime": data.get("startTime", current["startTime"]),
            "guests": int(data.get("guests", current["guests"])),
            "status": data.get("status", current["status"]),
            "notes": data.get("notes", current["notes"]),
        }

        if updated["status"] == "confirmed" and slot_conflict(
            updated["reservationDate"], updated["startTime"], updated["service"], exclude_id=reservation_id
        ):
            return jsonify({"error": "Selected slot is already booked for this service"}), 409

        conn.execute(
            """
            UPDATE reservations
            SET customer_name = ?, email = ?, service = ?, reservation_date = ?,
                start_time = ?, guests = ?, status = ?, notes = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """,
            (
                str(updated["customerName"]).strip(),
                str(updated["email"]).strip(),
                str(updated["service"]).strip(),
                str(updated["reservationDate"]).strip(),
                str(updated["startTime"]).strip(),
                int(updated["guests"]),
                str(updated["status"]).strip(),
                str(updated["notes"]).strip(),
                reservation_id,
            ),
        )
        conn.commit()
        row = conn.execute("SELECT * FROM reservations WHERE id = ?", (reservation_id,)).fetchone()

    return jsonify(row_to_dict(row)), 200


@app.delete("/api/reservations/<int:reservation_id>")
def delete_reservation(reservation_id: int) -> tuple[str, int]:
    with closing(get_db()) as conn:
        existing = conn.execute("SELECT id FROM reservations WHERE id = ?", (reservation_id,)).fetchone()
        if existing is None:
            return jsonify({"error": "Reservation not found"}), 404

        conn.execute("DELETE FROM reservations WHERE id = ?", (reservation_id,))
        conn.commit()

    return "", 204


@app.patch("/api/reservations/<int:reservation_id>/cancel")
def cancel_reservation(reservation_id: int) -> tuple[Any, int]:
    with closing(get_db()) as conn:
        existing = conn.execute("SELECT * FROM reservations WHERE id = ?", (reservation_id,)).fetchone()
        if existing is None:
            return jsonify({"error": "Reservation not found"}), 404

        conn.execute(
            "UPDATE reservations SET status = 'cancelled', updated_at = CURRENT_TIMESTAMP WHERE id = ?",
            (reservation_id,),
        )
        conn.commit()
        row = conn.execute("SELECT * FROM reservations WHERE id = ?", (reservation_id,)).fetchone()

    return jsonify(row_to_dict(row)), 200


if __name__ == "__main__":
    init_db()
    app.run(debug=True)
