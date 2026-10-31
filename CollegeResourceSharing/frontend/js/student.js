/**
 * COLLEGE RESOURCE SHARING PLATFORM — STUDENT DASHBOARD LOGIC
 * Manages equipment browsing, booking requests, waitlists, notifications, and profile.
 */

let currentStudent = null;
let allEquipment = [];
let categoriesSet = new Set();

document.addEventListener('DOMContentLoaded', async () => {
    // Enforce student role
    if (!Api.requireRole('STUDENT')) return;

    await loadStudentProfile();
    await loadDashboardSummary();
    await loadEquipmentList();
    await loadLaboratories();
    await loadStudentBookings();
    await loadStudentWaitlist();
    await loadNotifications();

    // Set today as minimum booking date
    const today = new Date().toISOString().split('T')[0];
    const dateInput = document.getElementById('bookingDate');
    if (dateInput) dateInput.min = today;
});

/**
 * Mobile sidebar toggle
 */
function toggleSidebar() {
    const sidebar = document.getElementById('appSidebar');
    if (sidebar) sidebar.classList.toggle('open');
}

/**
 * Section Switcher
 */
function switchSection(sectionId) {
    const sections = ['dashboardSection', 'browseSection', 'labsSection', 'bookingsSection', 'waitlistSection', 'notificationsSection', 'profileSection'];
    sections.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.style.display = (id === sectionId) ? 'block' : 'none';
    });

    // Update active nav link
    document.querySelectorAll('.sidebar-nav .nav-item').forEach(item => item.classList.remove('active'));
    const activeLink = document.querySelector(`.sidebar-nav .nav-item[onclick*="${sectionId}"]`);
    if (activeLink) activeLink.classList.add('active');

    // Close mobile menu if open
    const sidebar = document.getElementById('appSidebar');
    if (sidebar) sidebar.classList.remove('open');

    // Update page title
    const titles = {
        dashboardSection: 'Student Dashboard',
        browseSection: 'Browse Equipment Catalogue',
        labsSection: 'Laboratories',
        bookingsSection: 'My Bookings & History',
        waitlistSection: 'Waiting List Status',
        notificationsSection: 'Notifications & Alerts',
        profileSection: 'Student Profile'
    };
    document.getElementById('pageTitle').textContent = titles[sectionId] || 'Student Portal';
}

/**
 * Load student profile
 */
async function loadStudentProfile() {
    try {
        const res = await Api.student.getProfile();
        currentStudent = res.data;

        // Populate Sidebar
        document.getElementById('sidebarUserName').textContent = currentStudent.fullName || `${currentStudent.firstName} ${currentStudent.lastName}`;
        document.getElementById('sidebarUserReg').textContent = currentStudent.registrationNumber;
        document.getElementById('sidebarAvatar').textContent = `${currentStudent.firstName[0]}${currentStudent.lastName[0]}`;

        // Populate Profile Tab
        document.getElementById('profileContent').innerHTML = `
            <div style="display:grid; grid-template-columns:1fr 1fr; gap:1.25rem;">
                <div>
                    <label style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Full Name</label>
                    <div style="font-weight:600; font-size:1.05rem; margin-top:0.25rem;">${currentStudent.firstName} ${currentStudent.lastName}</div>
                </div>
                <div>
                    <label style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Registration Number</label>
                    <div style="font-weight:600; font-size:1.05rem; margin-top:0.25rem; font-family:monospace;">${currentStudent.registrationNumber}</div>
                </div>
                <div>
                    <label style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Academic Department</label>
                    <div style="font-weight:500; margin-top:0.25rem;">${currentStudent.department}</div>
                </div>
                <div>
                    <label style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Current Semester</label>
                    <div style="font-weight:500; margin-top:0.25rem;">Semester ${currentStudent.semester}</div>
                </div>
                <div>
                    <label style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Contact Phone</label>
                    <div style="font-weight:500; margin-top:0.25rem;">${currentStudent.phone || 'Not recorded'}</div>
                </div>
                <div>
                    <label style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Account Status</label>
                    <div style="margin-top:0.25rem;"><span class="badge badge-active">ACTIVE STUDENT</span></div>
                </div>
            </div>
        `;
    } catch (err) {
        console.error('Failed to load student profile:', err);
    }
}

/**
 * Load dashboard summary statistics
 */
async function loadDashboardSummary() {
    try {
        const bookingsRes = await Api.student.getBookings({ size: 100 });
        const bookings = bookingsRes.data.content || [];

        const active = bookings.filter(b => ['PENDING', 'APPROVED', 'COLLECTED'].includes(b.status)).length;
        const approved = bookings.filter(b => b.status === 'APPROVED').length;
        const completed = bookings.filter(b => b.status === 'RETURNED').length;
        const overdue = bookings.filter(b => b.status === 'OVERDUE').length;

        document.getElementById('statActiveBookings').textContent = active;
        document.getElementById('statApprovedBookings').textContent = approved;
        document.getElementById('statCompletedBookings').textContent = completed;

        // Nav badge
        const activeBadge = document.getElementById('activeBookingsBadge');
        if (active > 0) {
            activeBadge.textContent = active;
            activeBadge.style.display = 'inline-block';
        }

        // Overdue banner
        const overdueBanner = document.getElementById('overdueAlertBanner');
        if (overdueBanner) {
            overdueBanner.style.display = overdue > 0 ? 'flex' : 'none';
        }

        // Render Recent Table (first 5)
        const recentBody = document.getElementById('recentBookingsTableBody');
        const recent = bookings.slice(0, 5);
        if (recent.length === 0) {
            recentBody.innerHTML = `<tr><td colspan="6" class="empty-state">No bookings found. Browse equipment to make your first reservation.</td></tr>`;
        } else {
            recentBody.innerHTML = recent.map(b => renderBookingRow(b, false)).join('');
        }
    } catch (err) {
        console.error('Failed to load dashboard summary:', err);
    }
}

/**
 * Load Equipment catalogue with filters
 */
async function loadEquipmentList() {
    const container = document.getElementById('equipmentCardsContainer');
    container.innerHTML = '<div class="empty-state"><span class="spinner"></span><p>Loading equipment catalogue...</p></div>';

    const category = document.getElementById('categoryFilter').value;
    const available = document.getElementById('availabilityFilter').value;
    const keyword = document.getElementById('equipmentSearchInput').value.trim();

    const params = { size: 50 };
    if (category) params.category = category;
    if (available) params.available = available;
    if (keyword) params.keyword = keyword;

    try {
        const res = await Api.student.getEquipment(params);
        allEquipment = res.data.content || [];

        // Populate Category Filter dropdown once
        if (categoriesSet.size === 0) {
            allEquipment.forEach(eq => { if (eq.category) categoriesSet.add(eq.category); });
            const catSelect = document.getElementById('categoryFilter');
            categoriesSet.forEach(cat => {
                const opt = document.createElement('option');
                opt.value = cat;
                opt.textContent = cat;
                catSelect.appendChild(opt);
            });
        }

        if (allEquipment.length === 0) {
            container.innerHTML = `
                <div class="empty-state" style="grid-column: 1/-1;">
                    <i class="fa-solid fa-box-open empty-state-icon"></i>
                    <h4>No Equipment Found</h4>
                    <p>Try clearing your search query or adjusting your filters.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = allEquipment.map(eq => {
            const isAvail = eq.quantityAvailable > 0;
            const badgeClass = isAvail ? 'badge-available' : 'badge-unavailable';
            const badgeText = isAvail ? `${eq.quantityAvailable} of ${eq.quantityTotal} Available` : 'Fully Reserved';

            return `
                <div class="equipment-card">
                    <div class="equipment-card-img-wrap">
                        <i class="fa-solid fa-microchip equipment-placeholder-icon"></i>
                        <div class="equipment-badge-pos">
                            <span class="badge ${badgeClass}">${badgeText}</span>
                        </div>
                    </div>
                    <div class="equipment-card-content">
                        <div class="equipment-category-tag">${eq.category || 'General'}</div>
                        <h3 class="equipment-title">${eq.name}</h3>
                        <span class="equipment-code-tag">${eq.equipmentCode}</span>
                        <p class="equipment-desc">${eq.description || 'No description provided.'}</p>
                        
                        <div class="equipment-meta-row">
                            <span><i class="fa-solid fa-building"></i> ${eq.laboratory ? eq.laboratory.labName : 'Main Lab'}</span>
                            <span>Condition: <strong>${eq.conditionStatus}</strong></span>
                        </div>

                        <div style="display:flex; gap:0.5rem; margin-top:auto;">
                            <button class="btn btn-secondary btn-sm" style="flex:1;" onclick="viewEquipmentDetails(${eq.equipmentId})">
                                Details
                            </button>
                            <button class="btn btn-primary btn-sm" style="flex:1;" onclick="openBookingModal(${eq.equipmentId})" ${!isAvail ? 'title="Queue in Waiting List"' : ''}>
                                ${isAvail ? 'Book Now' : 'Join Waitlist'}
                            </button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');

    } catch (err) {
        container.innerHTML = `<div class="empty-state" style="grid-column:1/-1; color:red;">Failed to load equipment catalogue: ${err.message}</div>`;
    }
}

function handleEquipmentSearch() {
    clearTimeout(window._searchTimer);
    window._searchTimer = setTimeout(() => {
        loadEquipmentList();
    }, 300);
}

/**
 * Load Laboratories
 */
async function loadLaboratories() {
    const container = document.getElementById('laboratoriesContainer');
    try {
        const res = await Api.student.getLabs();
        const labs = res.data || [];
        if (labs.length === 0) {
            container.innerHTML = '<div class="empty-state">No active laboratories registered.</div>';
            return;
        }

        container.innerHTML = labs.map(lab => `
            <div class="equipment-card">
                <div class="equipment-card-content">
                    <div class="equipment-category-tag"><i class="fa-solid fa-door-open"></i> ${lab.labCode}</div>
                    <h3 class="equipment-title">${lab.labName}</h3>
                    <p class="equipment-desc">${lab.description || 'Dedicated academic laboratory facility.'}</p>
                    <div class="equipment-meta-row">
                        <span><i class="fa-solid fa-location-dot"></i> ${lab.location}</span>
                        <span>Capacity: <strong>${lab.capacity} Seats</strong></span>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (err) {
        container.innerHTML = `<div class="empty-state">Unable to load labs: ${err.message}</div>`;
    }
}

/**
 * Equipment Details Modal
 */
function viewEquipmentDetails(id) {
    const eq = allEquipment.find(e => e.equipmentId === id);
    if (!eq) return;

    document.getElementById('detailEquipName').textContent = eq.name;
    document.getElementById('detailModalBody').innerHTML = `
        <div style="display:grid; grid-template-columns:1fr 1fr; gap:1rem; margin-bottom:1rem;">
            <div><strong>Asset Code:</strong> <span style="font-family:monospace;">${eq.equipmentCode}</span></div>
            <div><strong>Laboratory:</strong> ${eq.laboratory ? eq.laboratory.labName : 'General'}</div>
            <div><strong>Category:</strong> ${eq.category}</div>
            <div><strong>Make / Model:</strong> ${eq.makeModel || 'N/A'}</div>
            <div><strong>Serial Number:</strong> ${eq.serialNumber || 'N/A'}</div>
            <div><strong>Condition Status:</strong> <span class="badge badge-approved">${eq.conditionStatus}</span></div>
            <div><strong>Total Stock:</strong> ${eq.quantityTotal} units</div>
            <div><strong>Available Now:</strong> ${eq.quantityAvailable} units</div>
        </div>
        <div style="border-top:1px solid var(--border-color); padding-top:0.75rem;">
            <strong>Technical Description:</strong>
            <p style="margin-top:0.25rem; color:var(--text-secondary); font-size:0.9rem;">${eq.description || 'No detailed specifications supplied.'}</p>
        </div>
    `;

    document.getElementById('detailBookBtn').onclick = () => {
        closeDetailModal();
        openBookingModal(id);
    };

    document.getElementById('equipmentDetailModal').classList.add('open');
}

function closeDetailModal() {
    document.getElementById('equipmentDetailModal').classList.remove('open');
}

/**
 * Open Booking Request Modal
 */
function openBookingModal(equipmentId) {
    const eq = allEquipment.find(e => e.equipmentId === equipmentId);
    if (!eq) return;

    document.getElementById('bookingEquipmentId').value = eq.equipmentId;
    document.getElementById('modalEquipmentName').textContent = `Book ${eq.name}`;
    document.getElementById('bookingEquipmentDetails').textContent = `${eq.equipmentCode} — ${eq.laboratory ? eq.laboratory.labName : 'Lab'} (${eq.quantityAvailable} available)`;
    document.getElementById('bookingQuantity').max = Math.max(eq.quantityAvailable, 1);
    document.getElementById('bookingModal').classList.add('open');
}

function closeBookingModal() {
    document.getElementById('bookingModal').classList.remove('open');
}

/**
 * Submit Booking Request
 */
async function submitBookingRequest(e) {
    e.preventDefault();
    const btn = document.getElementById('bookingSubmitBtn');
    btn.disabled = true;
    btn.innerHTML = `<span class="spinner"></span> Submitting...`;

    const payload = {
        equipmentId: parseInt(document.getElementById('bookingEquipmentId').value),
        bookingDate: document.getElementById('bookingDate').value,
        startTime: document.getElementById('bookingStartTime').value + ':00',
        endTime: document.getElementById('bookingEndTime').value + ':00',
        quantityBooked: parseInt(document.getElementById('bookingQuantity').value),
        purpose: document.getElementById('bookingPurpose').value.trim()
    };

    try {
        await Api.student.createBooking(payload);
        Toast.success('Reservation request submitted successfully!');
        closeBookingModal();
        await loadDashboardSummary();
        await loadEquipmentList();
        await loadStudentBookings();
        await loadStudentWaitlist();
    } catch (err) {
        Toast.error(err.message || 'Failed to submit booking request.');
    } finally {
        btn.disabled = false;
        btn.innerHTML = `Confirm Request`;
    }
}

/**
 * Load student bookings history
 */
async function loadStudentBookings() {
    const tableBody = document.getElementById('fullBookingsTableBody');
    const status = document.getElementById('bookingStatusFilter').value;
    const params = { size: 50 };
    if (status) params.status = status;

    try {
        const res = await Api.student.getBookings(params);
        const bookings = res.data.content || [];

        if (bookings.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="9" class="empty-state">No reservation records match your filter.</td></tr>`;
            return;
        }

        tableBody.innerHTML = bookings.map(b => renderBookingRow(b, true)).join('');
    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="9" style="color:red;" class="empty-state">Failed to load bookings: ${err.message}</td></tr>`;
    }
}

function renderBookingRow(b, showFullActions = true) {
    const statusBadges = {
        PENDING: 'badge-pending',
        APPROVED: 'badge-approved',
        COLLECTED: 'badge-collected',
        RETURNED: 'badge-returned',
        REJECTED: 'badge-rejected',
        CANCELLED: 'badge-cancelled',
        OVERDUE: 'badge-overdue'
    };
    const badgeClass = statusBadges[b.status] || 'badge-cancelled';
    const canCancel = ['PENDING', 'APPROVED'].includes(b.status);

    return `
        <tr>
            <td style="font-family:monospace; font-weight:600;">#${b.bookingId}</td>
            <td><strong>${b.equipment ? b.equipment.name : 'Equipment'}</strong></td>
            ${showFullActions ? `<td>${b.equipment && b.equipment.laboratory ? b.equipment.laboratory.labCode : '-'}</td>` : ''}
            <td>${b.bookingDate}</td>
            <td>${b.startTime.substring(0,5)} - ${b.endTime.substring(0,5)}</td>
            ${showFullActions ? `<td>${b.quantityBooked}</td>` : ''}
            <td><span class="badge ${badgeClass}">${b.status}</span></td>
            ${showFullActions ? `<td style="font-size:0.85rem; color:var(--text-secondary); max-width:200px;">${b.rejectionReason || b.purpose || '-'}</td>` : ''}
            <td>
                ${canCancel ? `
                    <button class="btn btn-secondary btn-sm" style="color:#b91c1c;" onclick="cancelBooking(${b.bookingId})">
                        <i class="fa-solid fa-ban"></i> Cancel
                    </button>
                ` : `<span style="color:var(--text-muted); font-size:0.8rem;">None</span>`}
            </td>
        </tr>
    `;
}

/**
 * Cancel booking
 */
async function cancelBooking(id) {
    if (!confirm(`Are you sure you want to cancel booking #${id}?`)) return;

    try {
        await Api.student.cancelBooking(id);
        Toast.success(`Booking #${id} has been cancelled.`);
        await loadDashboardSummary();
        await loadStudentBookings();
        await loadEquipmentList();
    } catch (err) {
        Toast.error(err.message || 'Failed to cancel booking.');
    }
}

/**
 * Load Waiting List
 */
async function loadStudentWaitlist() {
    const tableBody = document.getElementById('waitlistTableBody');
    try {
        const res = await Api.student.getWaitlist();
        const waitlist = res.data.content || [];

        document.getElementById('statWaitlistCount').textContent = waitlist.length;

        if (waitlist.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="empty-state">You are not currently queued on any equipment waiting lists.</td></tr>`;
            return;
        }

        tableBody.innerHTML = waitlist.map(w => `
            <tr>
                <td style="font-family:monospace; font-weight:600;">#WL-${w.waitlistId}</td>
                <td><strong>${w.equipment ? w.equipment.name : 'Equipment'}</strong></td>
                <td>${w.bookingDate}</td>
                <td>${w.startTime.substring(0,5)} - ${w.endTime.substring(0,5)}</td>
                <td><span class="badge badge-waitlisted"><i class="fa-solid fa-list-ol"></i> Position #${w.queuePosition}</span></td>
                <td><span class="badge badge-pending">${w.status}</span></td>
            </tr>
        `).join('');
    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="6" style="color:red;" class="empty-state">Failed to load waiting list.</td></tr>`;
    }
}

/**
 * Load Notifications
 */
async function loadNotifications() {
    const list = document.getElementById('notificationsList');
    try {
        const res = await Api.student.getNotifications();
        const notifications = res.data.content || [];
        const unread = notifications.filter(n => !n.isRead).length;

        const badge = document.getElementById('unreadNotifBadge');
        const dot = document.getElementById('topNotifDot');
        if (unread > 0) {
            badge.textContent = unread;
            badge.style.display = 'inline-block';
            dot.style.display = 'block';
        } else {
            badge.style.display = 'none';
            dot.style.display = 'none';
        }

        if (notifications.length === 0) {
            list.innerHTML = `<div class="empty-state">No notifications recorded.</div>`;
            return;
        }

        list.innerHTML = notifications.map(n => `
            <div style="background:${n.isRead ? '#ffffff' : '#f0fdf4'}; border:1px solid ${n.isRead ? 'var(--border-color)' : '#86efac'}; padding:1rem; border-radius:8px; display:flex; justify-content:space-between; align-items:flex-start;">
                <div>
                    <strong style="font-size:0.95rem; color:var(--text-primary); display:block;">${n.title}</strong>
                    <p style="font-size:0.875rem; color:var(--text-secondary); margin-top:0.2rem;">${n.message}</p>
                    <span style="font-size:0.75rem; color:var(--text-muted); display:inline-block; margin-top:0.4rem;">${new Date(n.createdAt).toLocaleString()}</span>
                </div>
                ${!n.isRead ? `
                    <button class="btn btn-secondary btn-sm" onclick="markNotificationRead(${n.notificationId})">
                        Mark Read
                    </button>
                ` : ''}
            </div>
        `).join('');
    } catch (err) {
        list.innerHTML = `<div class="empty-state">Failed to load notifications.</div>`;
    }
}

async function markNotificationRead(id) {
    try {
        await Api.student.markNotificationRead(id);
        await loadNotifications();
    } catch (err) {
        Toast.error('Failed to mark notification as read');
    }
}

async function markAllNotificationsRead() {
    try {
        await Api.student.markAllNotificationsRead();
        await loadNotifications();
        Toast.success('All notifications marked as read.');
    } catch (err) {
        Toast.error('Failed to mark all as read');
    }
}
