/**
 * COLLEGE RESOURCE SHARING PLATFORM — FACULTY DASHBOARD CONTROLLER
 * Manages assigned laboratory equipment, approvals, collections, returns, damage reporting, and overdue tracking.
 */

let currentFaculty = null;
let assignedLabs = [];
let currentLabEquipment = [];

document.addEventListener('DOMContentLoaded', async () => {
    if (!Api.requireRole('FACULTY')) return;

    await loadFacultyProfile();
    await loadAssignedLabs();
    await loadFacultyOverview();
    await loadPendingApprovals();
    await loadCollectionAndReturns();
    await loadOverdueEquipment();
    await loadFacultyHistory();
});

function toggleSidebar() {
    const sidebar = document.getElementById('appSidebar');
    if (sidebar) sidebar.classList.toggle('open');
}

function switchFacultySection(sectionId) {
    const sections = ['dashboardSection', 'assignedLabsSection', 'equipmentSection', 'approvalsSection', 'handoverSection', 'damageSection', 'overdueSection', 'historySection', 'profileSection'];
    sections.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.style.display = (id === sectionId) ? 'block' : 'none';
    });

    document.querySelectorAll('.sidebar-nav .nav-item').forEach(item => item.classList.remove('active'));
    const activeLink = document.querySelector(`.sidebar-nav .nav-item[onclick*="${sectionId}"]`);
    if (activeLink) activeLink.classList.add('active');

    const sidebar = document.getElementById('appSidebar');
    if (sidebar) sidebar.classList.remove('open');

    const titles = {
        dashboardSection: 'Faculty Laboratory Oversight',
        assignedLabsSection: 'Assigned Laboratories',
        equipmentSection: 'Equipment Availability & Inventory',
        approvalsSection: 'Booking Approvals & Rejections',
        handoverSection: 'Equipment Handover & Inspections',
        damageSection: 'Damage Incident Tracking',
        overdueSection: 'Overdue Equipment Monitor',
        historySection: 'Complete Laboratory Booking History',
        profileSection: 'Faculty Academic Profile'
    };
    document.getElementById('pageTitle').textContent = titles[sectionId] || 'Faculty Portal';
}

function switchHandoverTab(tabId, btn) {
    document.getElementById('collectionTab').style.display = tabId === 'collectionTab' ? 'block' : 'none';
    document.getElementById('returnTab').style.display = tabId === 'returnTab' ? 'block' : 'none';
    document.querySelectorAll('.tab-nav .tab-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
}

/**
 * Load faculty profile
 */
async function loadFacultyProfile() {
    try {
        const res = await Api.faculty.getProfile();
        currentFaculty = res.data;

        document.getElementById('sidebarUserName').textContent = currentFaculty.fullName || `${currentFaculty.firstName} ${currentFaculty.lastName}`;
        document.getElementById('sidebarUserCode').textContent = currentFaculty.facultyCode;
        document.getElementById('sidebarAvatar').textContent = `${currentFaculty.firstName[0]}${currentFaculty.lastName[0]}`;

        document.getElementById('facultyProfileContent').innerHTML = `
            <div style="display:grid; grid-template-columns:1fr 1fr; gap:1.25rem;">
                <div>
                    <label style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Full Name</label>
                    <div style="font-weight:600; font-size:1.05rem; margin-top:0.25rem;">${currentFaculty.firstName} ${currentFaculty.lastName}</div>
                </div>
                <div>
                    <label style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Faculty ID / Code</label>
                    <div style="font-weight:600; font-size:1.05rem; margin-top:0.25rem; font-family:monospace;">${currentFaculty.facultyCode}</div>
                </div>
                <div>
                    <label style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Department</label>
                    <div style="font-weight:500; margin-top:0.25rem;">${currentFaculty.department}</div>
                </div>
                <div>
                    <label style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Academic Designation</label>
                    <div style="font-weight:500; margin-top:0.25rem;">${currentFaculty.designation}</div>
                </div>
                <div>
                    <label style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Phone</label>
                    <div style="font-weight:500; margin-top:0.25rem;">${currentFaculty.phone || 'Not recorded'}</div>
                </div>
                <div>
                    <label style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Authorization Status</label>
                    <div style="margin-top:0.25rem;"><span class="badge badge-active">AUTHORIZED FACULTY</span></div>
                </div>
            </div>
        `;
    } catch (err) {
        console.error('Failed to load faculty profile:', err);
    }
}

/**
 * Load assigned laboratories
 */
async function loadAssignedLabs() {
    try {
        const res = await Api.faculty.getAssignedLabs();
        assignedLabs = res.data || [];

        const container = document.getElementById('assignedLabsContainer');
        const labSelect = document.getElementById('facultyLabSelect');

        if (assignedLabs.length === 0) {
            container.innerHTML = '<div class="empty-state">No laboratories currently assigned to your account. Please contact the administrator.</div>';
            labSelect.innerHTML = '<option value="">No Labs Assigned</option>';
            return;
        }

        container.innerHTML = assignedLabs.map(lab => `
            <div class="equipment-card">
                <div class="equipment-card-content">
                    <div class="equipment-category-tag"><i class="fa-solid fa-door-open"></i> ${lab.labCode}</div>
                    <h3 class="equipment-title">${lab.labName}</h3>
                    <p class="equipment-desc">${lab.description || 'Specialized college laboratory facility.'}</p>
                    <div class="equipment-meta-row">
                        <span><i class="fa-solid fa-location-dot"></i> ${lab.location}</span>
                        <span>Capacity: <strong>${lab.capacity}</strong></span>
                    </div>
                    <button class="btn btn-primary btn-sm btn-block" onclick="viewLabEquipment(${lab.labId})">
                        Manage Equipment
                    </button>
                </div>
            </div>
        `).join('');

        labSelect.innerHTML = assignedLabs.map(lab => `
            <option value="${lab.labId}">${lab.labCode} — ${lab.labName}</option>
        `).join('');

        // Load equipment for the first lab
        if (assignedLabs.length > 0) {
            await loadFacultyEquipment();
        }
    } catch (err) {
        console.error('Failed to load assigned labs:', err);
    }
}

function viewLabEquipment(labId) {
    document.getElementById('facultyLabSelect').value = labId;
    switchFacultySection('equipmentSection');
    loadFacultyEquipment();
}

/**
 * Load Equipment for selected lab
 */
async function loadFacultyEquipment() {
    const labId = document.getElementById('facultyLabSelect').value;
    const tableBody = document.getElementById('facultyEquipmentTableBody');
    if (!labId) return;

    tableBody.innerHTML = `<tr><td colspan="7" class="empty-state"><span class="spinner"></span> Loading inventory...</td></tr>`;

    try {
        const res = await Api.faculty.getEquipmentForLab(labId);
        currentLabEquipment = res.data.content || [];

        // Also populate damage & condition equipment selects
        populateEquipmentSelects(currentLabEquipment);

        if (currentLabEquipment.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="7" class="empty-state">No equipment items found in this laboratory.</td></tr>`;
            return;
        }

        document.getElementById('statTotalLabEquipment').textContent = currentLabEquipment.length;

        tableBody.innerHTML = currentLabEquipment.map(eq => `
            <tr>
                <td style="font-family:monospace; font-weight:600;">${eq.equipmentCode}</td>
                <td><strong>${eq.name}</strong></td>
                <td>${eq.category}</td>
                <td>${eq.quantityTotal}</td>
                <td>
                    <input type="number" id="qtyAvail_${eq.equipmentId}" value="${eq.quantityAvailable}" min="0" max="${eq.quantityTotal}" style="width:70px; padding:0.25rem 0.5rem; border:1px solid var(--border-color); border-radius:4px;">
                </td>
                <td>
                    <select id="condStatus_${eq.equipmentId}" style="padding:0.25rem 0.5rem; border:1px solid var(--border-color); border-radius:4px;">
                        <option value="GOOD" ${eq.conditionStatus==='GOOD'?'selected':''}>GOOD</option>
                        <option value="FAIR" ${eq.conditionStatus==='FAIR'?'selected':''}>FAIR</option>
                        <option value="POOR" ${eq.conditionStatus==='POOR'?'selected':''}>POOR</option>
                        <option value="UNDER_REPAIR" ${eq.conditionStatus==='UNDER_REPAIR'?'selected':''}>UNDER REPAIR</option>
                        <option value="DECOMMISSIONED" ${eq.conditionStatus==='DECOMMISSIONED'?'selected':''}>DECOMMISSIONED</option>
                    </select>
                </td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="saveEquipmentAvailability(${eq.equipmentId})">
                        <i class="fa-solid fa-floppy-disk"></i> Update
                    </button>
                </td>
            </tr>
        `).join('');

    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="7" style="color:red;" class="empty-state">Failed to load equipment: ${err.message}</td></tr>`;
    }
}

function populateEquipmentSelects(equipmentList) {
    const damageSelect = document.getElementById('damageEquipmentSelect');
    const condSelect = document.getElementById('conditionEquipSelect');
    if (!damageSelect || !condSelect) return;

    const opts = equipmentList.map(eq => `<option value="${eq.equipmentId}">${eq.equipmentCode} — ${eq.name}</option>`).join('');
    damageSelect.innerHTML = opts || '<option value="">No equipment available</option>';
    condSelect.innerHTML = opts || '<option value="">No equipment available</option>';
}

/**
 * Save equipment availability updates
 */
async function saveEquipmentAvailability(id) {
    const qtyInput = document.getElementById(`qtyAvail_${id}`);
    const condSelect = document.getElementById(`condStatus_${id}`);

    const availableQty = parseInt(qtyInput.value);
    const condition = condSelect.value;

    try {
        await Api.faculty.updateEquipmentAvailability(id, availableQty, condition);
        Toast.success('Equipment status updated successfully');
    } catch (err) {
        Toast.error(err.message || 'Failed to update equipment availability');
    }
}

/**
 * Load Overview Stats
 */
async function loadFacultyOverview() {
    try {
        const pendingRes = await Api.faculty.getPendingBookings({ size: 100 });
        const pending = pendingRes.data.content || [];
        document.getElementById('statPendingApprovals').textContent = pending.length;

        const badge = document.getElementById('pendingApprovalsBadge');
        if (pending.length > 0) {
            badge.textContent = pending.length;
            badge.style.display = 'inline-block';
        }

        // Quick pending approvals table (first 5)
        const quickBody = document.getElementById('quickPendingApprovalsBody');
        const slice = pending.slice(0, 5);
        if (slice.length === 0) {
            quickBody.innerHTML = `<tr><td colspan="7" class="empty-state">No pending approvals at this time.</td></tr>`;
        } else {
            quickBody.innerHTML = slice.map(b => renderApprovalRow(b)).join('');
        }
    } catch (err) {
        console.error('Failed to load faculty overview:', err);
    }
}

/**
 * Load full Pending Approvals
 */
async function loadPendingApprovals() {
    const tableBody = document.getElementById('fullApprovalsTableBody');
    try {
        const res = await Api.faculty.getPendingBookings({ size: 100 });
        const bookings = res.data.content || [];

        if (bookings.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="8" class="empty-state">No pending booking requests awaiting review.</td></tr>`;
            return;
        }

        tableBody.innerHTML = bookings.map(b => renderApprovalRow(b)).join('');
    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="8" style="color:red;" class="empty-state">Failed to load booking approvals: ${err.message}</td></tr>`;
    }
}

function renderApprovalRow(b) {
    const student = b.student || {};
    const equipment = b.equipment || {};

    return `
        <tr>
            <td style="font-family:monospace; font-weight:600;">#${b.bookingId}</td>
            <td>
                <strong>${student.firstName || ''} ${student.lastName || ''}</strong><br>
                <small style="color:var(--text-muted); font-family:monospace;">${student.registrationNumber || '-'}</small>
            </td>
            <td>${equipment.name || 'Equipment'}</td>
            <td>${b.bookingDate}</td>
            <td>${b.startTime.substring(0,5)} - ${b.endTime.substring(0,5)}</td>
            <td>${b.quantityBooked}</td>
            <td style="font-size:0.85rem; max-width:220px;">${b.purpose || '-'}</td>
            <td>
                <div class="table-actions">
                    <button class="btn btn-success btn-sm" onclick="approveBooking(${b.bookingId})">
                        <i class="fa-solid fa-check"></i> Approve
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="openRejectionModal(${b.bookingId})">
                        <i class="fa-solid fa-xmark"></i> Reject
                    </button>
                </div>
            </td>
        </tr>
    `;
}

/**
 * Approve Booking
 */
async function approveBooking(id) {
    if (!confirm(`Approve reservation #${id}?`)) return;

    try {
        await Api.faculty.approveRejectBooking(id, 'APPROVED', 'Approved by assigned laboratory faculty.');
        Toast.success(`Booking #${id} has been APPROVED.`);
        await loadFacultyOverview();
        await loadPendingApprovals();
        await loadCollectionAndReturns();
    } catch (err) {
        Toast.error(err.message || 'Failed to approve booking.');
    }
}

/**
 * Rejection Modal
 */
function openRejectionModal(id) {
    document.getElementById('rejectBookingId').value = id;
    document.getElementById('rejectReason').value = '';
    document.getElementById('rejectionModal').classList.add('open');
}

function closeRejectionModal() {
    document.getElementById('rejectionModal').classList.remove('open');
}

async function submitRejection(e) {
    e.preventDefault();
    const id = document.getElementById('rejectBookingId').value;
    const reason = document.getElementById('rejectReason').value.trim();

    try {
        await Api.faculty.approveRejectBooking(id, 'REJECTED', reason);
        Toast.success(`Booking #${id} has been REJECTED.`);
        closeRejectionModal();
        await loadFacultyOverview();
        await loadPendingApprovals();
    } catch (err) {
        Toast.error(err.message || 'Failed to reject booking.');
    }
}

/**
 * Load Collection & Return queues
 */
async function loadCollectionAndReturns() {
    const colBody = document.getElementById('collectionTableBody');
    const retBody = document.getElementById('returnTableBody');

    try {
        const res = await Api.faculty.getBookings({ size: 100 });
        const bookings = res.data.content || [];

        const approved = bookings.filter(b => b.status === 'APPROVED');
        const collected = bookings.filter(b => b.status === 'COLLECTED' || b.status === 'OVERDUE');

        document.getElementById('statActiveHandovers').textContent = collected.length;

        // Collection Table
        if (approved.length === 0) {
            colBody.innerHTML = `<tr><td colspan="5" class="empty-state">No equipment currently awaiting pickup.</td></tr>`;
        } else {
            colBody.innerHTML = approved.map(b => `
                <tr>
                    <td style="font-family:monospace; font-weight:600;">#${b.bookingId}</td>
                    <td><strong>${b.student ? b.student.fullName : 'Student'}</strong> (${b.student ? b.student.registrationNumber : '-'})</td>
                    <td>${b.equipment ? b.equipment.name : 'Equipment'}</td>
                    <td>${b.bookingDate} (${b.startTime.substring(0,5)} - ${b.endTime.substring(0,5)})</td>
                    <td>
                        <button class="btn btn-primary btn-sm" onclick="openCollectionModal(${b.bookingId})">
                            <i class="fa-solid fa-hand-holding"></i> Verify Collection
                        </button>
                    </td>
                </tr>
            `).join('');
        }

        // Return Table
        if (collected.length === 0) {
            retBody.innerHTML = `<tr><td colspan="6" class="empty-state">No equipment currently issued and awaiting return.</td></tr>`;
        } else {
            retBody.innerHTML = collected.map(b => `
                <tr>
                    <td style="font-family:monospace; font-weight:600;">#${b.bookingId}</td>
                    <td><strong>${b.student ? b.student.fullName : 'Student'}</strong></td>
                    <td>${b.equipment ? b.equipment.name : 'Equipment'}</td>
                    <td>${b.bookingDate}</td>
                    <td><span class="badge ${b.status==='OVERDUE'?'badge-overdue':'badge-collected'}">${b.status}</span></td>
                    <td>
                        <button class="btn btn-success btn-sm" onclick="openReturnModal(${b.bookingId})">
                            <i class="fa-solid fa-box-check"></i> Verify Return
                        </button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (err) {
        console.error('Failed to load collections/returns:', err);
    }
}

/**
 * Collection Modal & Verification
 */
function openCollectionModal(id) {
    document.getElementById('collectionBookingId').value = id;
    document.getElementById('collectionNotes').value = '';
    document.getElementById('collectionPhoto').value = '';
    document.getElementById('collectionVerifyModal').classList.add('open');
}

function closeCollectionModal() {
    document.getElementById('collectionVerifyModal').classList.remove('open');
}

async function submitCollectionVerification(e) {
    e.preventDefault();
    const id = document.getElementById('collectionBookingId').value;
    const condition = document.getElementById('collectionCondition').value;
    const notes = document.getElementById('collectionNotes').value;
    const photoFile = document.getElementById('collectionPhoto').files[0];

    const formData = new FormData();
    formData.append('condition', condition);
    formData.append('notes', notes);
    if (photoFile) formData.append('photo', photoFile);

    try {
        await Api.faculty.verifyCollection(id, formData);
        Toast.success('Equipment collection recorded successfully!');
        closeCollectionModal();
        await loadCollectionAndReturns();
        await loadFacultyOverview();
    } catch (err) {
        Toast.error(err.message || 'Failed to verify collection.');
    }
}

/**
 * Return Modal & Verification
 */
function openReturnModal(id) {
    document.getElementById('returnBookingId').value = id;
    document.getElementById('returnNotes').value = '';
    document.getElementById('returnPhoto').value = '';
    document.getElementById('returnVerifyModal').classList.add('open');
}

function closeReturnModal() {
    document.getElementById('returnVerifyModal').classList.remove('open');
}

async function submitReturnVerification(e) {
    e.preventDefault();
    const id = document.getElementById('returnBookingId').value;
    const condition = document.getElementById('returnCondition').value;
    const notes = document.getElementById('returnNotes').value;
    const photoFile = document.getElementById('returnPhoto').files[0];

    const formData = new FormData();
    formData.append('condition', condition);
    formData.append('notes', notes);
    if (photoFile) formData.append('photo', photoFile);

    try {
        await Api.faculty.verifyReturn(id, formData);
        Toast.success('Equipment returned and inventory restored!');
        closeReturnModal();
        await loadCollectionAndReturns();
        await loadFacultyOverview();
        await loadFacultyEquipment();
    } catch (err) {
        Toast.error(err.message || 'Failed to verify return.');
    }
}

/**
 * Damage Modal & Submission
 */
function openDamageModal() {
    document.getElementById('damageDesc').value = '';
    document.getElementById('damageCost').value = '';
    document.getElementById('damagePhoto').value = '';
    document.getElementById('damageReportModal').classList.add('open');
}

function closeDamageModal() {
    document.getElementById('damageReportModal').classList.remove('open');
}

async function submitDamageReport(e) {
    e.preventDefault();
    const equipId = document.getElementById('damageEquipmentSelect').value;
    const severity = document.getElementById('damageSeverity').value;
    const desc = document.getElementById('damageDesc').value;
    const cost = document.getElementById('damageCost').value;
    const photo = document.getElementById('damagePhoto').files[0];

    const formData = new FormData();
    formData.append('equipmentId', equipId);
    formData.append('severity', severity);
    formData.append('description', desc);
    if (cost) formData.append('repairCost', cost);
    if (photo) formData.append('photo', photo);

    try {
        await Api.faculty.reportDamage(formData);
        Toast.success('Damage report registered with laboratory administration.');
        closeDamageModal();
    } catch (err) {
        Toast.error(err.message || 'Failed to register damage report.');
    }
}

/**
 * Condition Check Modal
 */
function openConditionReportModal() {
    document.getElementById('conditionDesc').value = '';
    document.getElementById('conditionReportModal').classList.add('open');
}

function closeConditionReportModal() {
    document.getElementById('conditionReportModal').classList.remove('open');
}

async function submitConditionCheck(e) {
    e.preventDefault();
    const equipId = document.getElementById('conditionEquipSelect').value;
    const status = document.getElementById('conditionCheckStatus').value;
    const desc = document.getElementById('conditionDesc').value;

    const formData = new FormData();
    formData.append('equipmentId', equipId);
    formData.append('conditionStatus', status);
    formData.append('description', desc);

    try {
        await Api.faculty.reportCondition(formData);
        Toast.success('Condition inspection saved successfully.');
        closeConditionReportModal();
    } catch (err) {
        Toast.error(err.message || 'Failed to submit condition report.');
    }
}

/**
 * Load Overdue Equipment
 */
async function loadOverdueEquipment() {
    const tableBody = document.getElementById('overdueTableBody');
    try {
        const res = await Api.faculty.getOverdue();
        const overdue = res.data || [];

        document.getElementById('statOverdueCount').textContent = overdue.length;
        const badge = document.getElementById('overdueBadge');
        if (overdue.length > 0) {
            badge.textContent = overdue.length;
            badge.style.display = 'inline-block';
        }

        if (overdue.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="empty-state">No equipment currently flagged as overdue. All items returned on schedule.</td></tr>`;
            return;
        }

        tableBody.innerHTML = overdue.map(b => `
            <tr>
                <td style="font-family:monospace; font-weight:600;">#${b.bookingId}</td>
                <td><strong>${b.student ? b.student.fullName : 'Student'}</strong> (${b.student ? b.student.registrationNumber : '-'})</td>
                <td>${b.equipment ? b.equipment.name : 'Equipment'}</td>
                <td>${b.bookingDate}</td>
                <td><span class="badge badge-overdue">OVERDUE</span></td>
                <td>
                    <button class="btn btn-success btn-sm" onclick="openReturnModal(${b.bookingId})">
                        Force Return Check
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="6" style="color:red;" class="empty-state">Failed to load overdue items.</td></tr>`;
    }
}

/**
 * Load Faculty All History
 */
async function loadFacultyHistory() {
    const tableBody = document.getElementById('facultyHistoryTableBody');
    try {
        const res = await Api.faculty.getBookings({ size: 100 });
        const bookings = res.data.content || [];

        if (bookings.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="7" class="empty-state">No booking history recorded for your assigned laboratories.</td></tr>`;
            return;
        }

        tableBody.innerHTML = bookings.map(b => `
            <tr>
                <td style="font-family:monospace;">#${b.bookingId}</td>
                <td>${b.student ? b.student.fullName : '-'}</td>
                <td><strong>${b.equipment ? b.equipment.name : '-'}</strong></td>
                <td>${b.equipment && b.equipment.laboratory ? b.equipment.laboratory.labCode : '-'}</td>
                <td>${b.bookingDate}</td>
                <td>${b.startTime.substring(0,5)} - ${b.endTime.substring(0,5)}</td>
                <td><span class="badge badge-${b.status.toLowerCase()}">${b.status}</span></td>
            </tr>
        `).join('');
    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="7" style="color:red;" class="empty-state">Failed to load history.</td></tr>`;
    }
}
