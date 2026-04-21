const form = document.getElementById('bookingForm');
const tableBody = document.getElementById('reservationTableBody');
const messageDiv = document.getElementById('message');
const availabilityDiv = document.getElementById('availabilityResult');
const refreshBtn = document.getElementById('refreshBtn');
const checkAvailabilityBtn = document.getElementById('checkAvailabilityBtn');

async function fetchReservations() {
  const response = await fetch('/api/reservations');
  const reservations = await response.json();
  tableBody.innerHTML = '';

  reservations.forEach((reservation) => {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${reservation.id}</td>
      <td>${reservation.customerName}</td>
      <td>${reservation.service}</td>
      <td>${reservation.reservationDate}</td>
      <td>${reservation.startTime}</td>
      <td>${reservation.guests}</td>
      <td class="status-${reservation.status}">${reservation.status}</td>
      <td>
        <button class="action-btn edit" onclick="modifyReservation(${reservation.id})">Modify</button>
        <button class="action-btn cancel" onclick="cancelReservation(${reservation.id})">Cancel</button>
        <button class="action-btn delete" onclick="deleteReservation(${reservation.id})">Delete</button>
      </td>
    `;
    tableBody.appendChild(row);
  });
}

function getFormData() {
  return {
    customerName: document.getElementById('customerName').value,
    email: document.getElementById('email').value,
    service: document.getElementById('service').value,
    reservationDate: document.getElementById('reservationDate').value,
    startTime: document.getElementById('startTime').value,
    guests: Number(document.getElementById('guests').value),
    notes: document.getElementById('notes').value,
  };
}

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  const payload = getFormData();

  const response = await fetch('/api/reservations', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  const data = await response.json().catch(() => ({}));
  messageDiv.textContent = response.ok
    ? `Reservation created successfully. ID: ${data.id}`
    : `Error: ${data.error || 'Something went wrong'}`;

  if (response.ok) {
    form.reset();
    fetchReservations();
  }
});

checkAvailabilityBtn.addEventListener('click', async () => {
  const service = document.getElementById('service').value;
  const date = document.getElementById('reservationDate').value;

  if (!service || !date) {
    availabilityDiv.textContent = 'Please select service and date first.';
    return;
  }

  const response = await fetch(`/api/availability?service=${encodeURIComponent(service)}&date=${encodeURIComponent(date)}`);
  const data = await response.json();
  availabilityDiv.textContent = `Available slots: ${data.availableSlots.join(', ') || 'No slots available'}`;
});

async function cancelReservation(id) {
  const response = await fetch(`/api/reservations/${id}/cancel`, { method: 'PATCH' });
  if (response.ok) {
    messageDiv.textContent = `Reservation ${id} cancelled.`;
    fetchReservations();
  }
}

async function deleteReservation(id) {
  const response = await fetch(`/api/reservations/${id}`, { method: 'DELETE' });
  if (response.ok) {
    messageDiv.textContent = `Reservation ${id} deleted.`;
    fetchReservations();
  }
}

async function modifyReservation(id) {
  const newGuests = prompt('Enter new number of guests:');
  if (!newGuests) return;

  const response = await fetch(`/api/reservations/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ guests: Number(newGuests) })
  });

  const data = await response.json().catch(() => ({}));
  messageDiv.textContent = response.ok
    ? `Reservation ${id} modified successfully.`
    : `Error: ${data.error || 'Unable to modify reservation'}`;

  if (response.ok) fetchReservations();
}

refreshBtn.addEventListener('click', fetchReservations);
window.modifyReservation = modifyReservation;
window.cancelReservation = cancelReservation;
window.deleteReservation = deleteReservation;

fetchReservations();
