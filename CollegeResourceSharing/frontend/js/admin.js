/**
 * COLLEGE RESOURCE SHARING PLATFORM — ADMINISTRATOR CONTROLLER
 * Full system controls: User management, CSV import, Labs, Inventory, Booking oversight, Damage resolutions, and Audit logs.
 */

let cachedLabs = [];
let cachedFaculty = [];

document.addEventListener('DOMContentLoaded', async () => {
    if (!Api.requireRole('ADMIN')) return;

    // Set today as default assignment date
    const today = new Date().toISOString().split('T')[0];
    const assignDate = document.getElementById('assignDate');
    if (assignDate) assignDate.value = today;

    await loadAdminDashboardStats();
    await loadStudents();
    await loadFaculty();
    await loadLaboratories();
    await loadEquipment();
    await loadAdminBookings();
    await loadWaitingLists();
    await loadDamageReports();
    await loadActivityLogs();
});

function toggleSidebar() {
    const sidebar = document.getElementById('appSidebar');
    if (sidebar) sidebar.classList.toggle('open');
}

function switchAdminSection(sectionId) {
    const sections = ['dashboardSection', 'studentsSection', 'facultySection', 'labsSection', 'equipmentSection', 'assignmentsSection', 'bookingsSection', 'waitlistSection', 'damageSection', 'rulesSection', 'logsSection'];
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
        dashboardSection: 'System Overview & Analytics',
        studentsSection: 'Student Registry & Access',
        facultySection: 'Faculty Accounts & Departments',
        labsSection: 'Institutional Laboratories',
        equipmentSection: 'Equipment Catalogue & Inventory',
        assignmentsSection: 'Faculty-Laboratory Assignments',
        bookingsSection: 'Master Booking Management',
        waitlistSection: 'Equipment Waiting Lists',
        damageSection: 'Equipment Damage Incidents',
        rulesSection: 'Fair Usage Policies',
        logsSection: 'Activity Audit Trail'
    };
    document.getElementById('pageTitle').textContent = titles[sectionId] || 'Administration';
}

/**
 * 1. Dashboard Metrics
 */
async function loadAdminDashboardStats() {
    try {
        const res = await Api.admin.getStats();
        const s = res.data;

        document.getElementById('statTotalStudents').textContent = s.totalStudents || 0;
        document.getElementById('statTotalFaculty').textContent = s.totalFaculty || 0;
        document.getElementById('statTotalLabs').textContent = s.totalLabs || 0;
        document.getElementById('statTotalEquipment').textContent = s.totalEquipment || 0;
        document.getElementById('statTotalBookings').textContent = s.totalBookings || 0;
        document.getElementById('statPendingBookings').textContent = s.pendingBookings || 0;
        document.getElementById('statDamageReports').textContent = s.activeDamageReports || 0;
        document.getElementById('statOverdueBookings').textContent = s.overdueBookings || 0;
    } catch (err) {
        console.error('Failed to load admin stats:', err);
    }
}

/**
 * 2. Student Management
 */
async function loadStudents() {
    const tableBody = document.getElementById('studentsTableBody');
    const keyword = document.getElementById('studentSearchInput').value.trim();
    const params = { size: 100 };
    if (keyword) params.keyword = keyword;

    try {
        const res = await Api.admin.getStudents(params);
        const students = res.data.content || [];

        if (students.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="7" class="empty-state">No students found.</td></tr>`;
            return;
        }

        tableBody.innerHTML = students.map(s => `
            <tr>
                <td style="font-family:monospace;">#${s.studentId}</td>
                <td><strong style="font-family:monospace;">${s.registrationNumber}</strong></td>
                <td>${s.firstName} ${s.lastName}</td>
                <td>${s.department}</td>
                <td>Semester ${s.semester}</td>
                <td><span class="badge ${s.user && s.user.status === 'ACTIVE' ? 'badge-active' : 'badge-inactive'}">${s.user ? s.user.status : 'ACTIVE'}</span></td>
                <td>
                    <div class="table-actions">
                        <button class="btn btn-secondary btn-sm" onclick='editStudent(${JSON.stringify(s)})'>
                            <i class="fa-solid fa-pen-to-square"></i>
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="deactivateStudent(${s.studentId})">
                            <i class="fa-solid fa-user-xmark"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="7" style="color:red;" class="empty-state">Failed to load students: ${err.message}</td></tr>`;
    }
}

function handleStudentSearch() {
    clearTimeout(window._studentSearchTimer);
    window._studentSearchTimer = setTimeout(loadStudents, 300);
}

function openStudentModal() {
    document.getElementById('studentModalTitle').textContent = 'Add New Student';
    document.getElementById('studentEditId').value = '';
    document.getElementById('studentUsername').value = '';
    document.getElementById('studentEmail').value = '';
    document.getElementById('studentPassword').value = '';
    document.getElementById('studentRegNo').value = '';
    document.getElementById('studentFirstName').value = '';
    document.getElementById('studentLastName').value = '';
    document.getElementById('studentDept').value = '';
    document.getElementById('studentSem').value = '1';
    document.getElementById('studentPasswordGroup').style.display = 'block';
    document.getElementById('studentPassword').required = true;
    document.getElementById('studentModal').classList.add('open');
}

function editStudent(s) {
    document.getElementById('studentModalTitle').textContent = 'Edit Student Details';
    document.getElementById('studentEditId').value = s.studentId;
    document.getElementById('studentUsername').value = s.user ? s.user.username : '';
    document.getElementById('studentEmail').value = s.user ? s.user.email : '';
    document.getElementById('studentPassword').value = '';
    document.getElementById('studentRegNo').value = s.registrationNumber;
    document.getElementById('studentFirstName').value = s.firstName;
    document.getElementById('studentLastName').value = s.lastName;
    document.getElementById('studentDept').value = s.department;
    document.getElementById('studentSem').value = s.semester;
    document.getElementById('studentPassword').required = false;
    document.getElementById('studentModal').classList.add('open');
}

function closeStudentModal() {
    document.getElementById('studentModal').classList.remove('open');
}

async function submitStudentForm(e) {
    e.preventDefault();
    const editId = document.getElementById('studentEditId').value;
    const payload = {
        username: document.getElementById('studentUsername').value.trim(),
        email: document.getElementById('studentEmail').value.trim(),
        registrationNumber: document.getElementById('studentRegNo').value.trim(),
        firstName: document.getElementById('studentFirstName').value.trim(),
        lastName: document.getElementById('studentLastName').value.trim(),
        department: document.getElementById('studentDept').value.trim(),
        semester: parseInt(document.getElementById('studentSem').value)
    };
    const pwd = document.getElementById('studentPassword').value;
    if (pwd) payload.password = pwd;

    try {
        if (editId) {
            await Api.admin.updateStudent(editId, payload);
            Toast.success('Student details updated successfully!');
        } else {
            await Api.admin.createStudent(payload);
            Toast.success('Student account created successfully!');
        }
        closeStudentModal();
        await loadStudents();
        await loadAdminDashboardStats();
    } catch (err) {
        Toast.error(err.message || 'Failed to save student.');
    }
}

async function deactivateStudent(id) {
    if (!confirm(`Are you sure you want to deactivate student account #${id}?`)) return;
    try {
        await Api.admin.deactivateStudent(id);
        Toast.success('Student account deactivated.');
        await loadStudents();
    } catch (err) {
        Toast.error(err.message || 'Failed to deactivate student.');
    }
}

// CSV Batch Import
function openCsvImportModal() {
    document.getElementById('csvFile').value = '';
    document.getElementById('csvImportModal').classList.add('open');
}

function closeCsvImportModal() {
    document.getElementById('csvImportModal').classList.remove('open');
}

async function submitCsvImport(e) {
    e.preventDefault();
    const file = document.getElementById('csvFile').files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
        const res = await Api.admin.importStudentsCsv(formData);
        const data = res.data;
        Toast.success(`CSV Import complete: ${data.imported} enrolled, ${data.failed} skipped.`);
        closeCsvImportModal();
        await loadStudents();
        await loadAdminDashboardStats();
    } catch (err) {
        Toast.error(err.message || 'Failed to import CSV file.');
    }
}

/**
 * 3. Faculty Management
 */
async function loadFaculty() {
    const tableBody = document.getElementById('facultyTableBody');
    const keyword = document.getElementById('facultySearchInput').value.trim();
    const params = { size: 100 };
    if (keyword) params.keyword = keyword;

    try {
        const res = await Api.admin.getFaculty(params);
        cachedFaculty = res.data.content || [];

        populateFacultySelects(cachedFaculty);

        if (cachedFaculty.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="empty-state">No faculty accounts found.</td></tr>`;
            return;
        }

        tableBody.innerHTML = cachedFaculty.map(f => `
            <tr>
                <td style="font-family:monospace;">#${f.facultyId}</td>
                <td><strong style="font-family:monospace;">${f.facultyCode}</strong></td>
                <td>${f.firstName} ${f.lastName}</td>
                <td>${f.department}</td>
                <td>${f.designation}</td>
                <td>
                    <button class="btn btn-danger btn-sm" onclick="deactivateFaculty(${f.facultyId})">
                        <i class="fa-solid fa-user-xmark"></i> Deactivate
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="6" style="color:red;" class="empty-state">Failed to load faculty: ${err.message}</td></tr>`;
    }
}

function handleFacultySearch() {
    clearTimeout(window._facSearchTimer);
    window._facSearchTimer = setTimeout(loadFaculty, 300);
}

function openFacultyModal() {
    document.getElementById('facUsername').value = '';
    document.getElementById('facEmail').value = '';
    document.getElementById('facPassword').value = '';
    document.getElementById('facCode').value = '';
    document.getElementById('facFirstName').value = '';
    document.getElementById('facLastName').value = '';
    document.getElementById('facDept').value = '';
    document.getElementById('facDesignation').value = '';
    document.getElementById('facultyModal').classList.add('open');
}

function closeFacultyModal() {
    document.getElementById('facultyModal').classList.remove('open');
}

async function submitFacultyForm(e) {
    e.preventDefault();
    const payload = {
        username: document.getElementById('facUsername').value.trim(),
        email: document.getElementById('facEmail').value.trim(),
        password: document.getElementById('facPassword').value,
        facultyCode: document.getElementById('facCode').value.trim(),
        firstName: document.getElementById('facFirstName').value.trim(),
        lastName: document.getElementById('facLastName').value.trim(),
        department: document.getElementById('facDept').value.trim(),
        designation: document.getElementById('facDesignation').value.trim()
    };

    try {
        await Api.admin.createFaculty(payload);
        Toast.success('Faculty account created successfully!');
        closeFacultyModal();
        await loadFaculty();
        await loadAdminDashboardStats();
    } catch (err) {
        Toast.error(err.message || 'Failed to create faculty account.');
    }
}

async function deactivateFaculty(id) {
    if (!confirm(`Are you sure you want to deactivate faculty account #${id}?`)) return;
    try {
        await Api.admin.deactivateFaculty(id);
        Toast.success('Faculty account deactivated.');
        await loadFaculty();
    } catch (err) {
        Toast.error(err.message || 'Failed to deactivate faculty.');
    }
}

/**
 * 4. Laboratories
 */
async function loadLaboratories() {
    const tableBody = document.getElementById('adminLabsTableBody');
    try {
        const res = await Api.admin.getLabs();
        cachedLabs = res.data || [];

        populateLabSelects(cachedLabs);

        if (cachedLabs.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="empty-state">No laboratories defined.</td></tr>`;
            return;
        }

        tableBody.innerHTML = cachedLabs.map(l => `
            <tr>
                <td style="font-family:monospace; font-weight:600;">${l.labCode}</td>
                <td><strong>${l.labName}</strong></td>
                <td>${l.location}</td>
                <td>${l.capacity} Seats</td>
                <td><span class="badge badge-active">${l.status}</span></td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick='editLab(${JSON.stringify(l)})'>
                        <i class="fa-solid fa-pen"></i> Edit
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="6" style="color:red;" class="empty-state">Failed to load labs.</td></tr>`;
    }
}

function populateLabSelects(labs) {
    const equipSelect = document.getElementById('equipLabSelect');
    const assignSelect = document.getElementById('assignLabSelect');
    const ruleSelect = document.getElementById('ruleLabSelect');

    const opts = labs.map(l => `<option value="${l.labId}">${l.labCode} — ${l.labName}</option>`).join('');
    if (equipSelect) equipSelect.innerHTML = opts;
    if (assignSelect) assignSelect.innerHTML = opts;
    if (ruleSelect) {
        ruleSelect.innerHTML = opts;
        fetchUsageRuleForLab();
    }
}

function populateFacultySelects(faculty) {
    const assignSelect = document.getElementById('assignFacultySelect');
    if (assignSelect) {
        assignSelect.innerHTML = faculty.map(f => `<option value="${f.facultyId}">${f.facultyCode} — ${f.firstName} ${f.lastName}</option>`).join('');
    }
}

function openLabModal() {
    document.getElementById('labModalTitle').textContent = 'Add Laboratory';
    document.getElementById('labEditId').value = '';
    document.getElementById('labCodeInput').value = '';
    document.getElementById('labNameInput').value = '';
    document.getElementById('labLocationInput').value = '';
    document.getElementById('labCapacityInput').value = '30';
    document.getElementById('labModal').classList.add('open');
}

function editLab(l) {
    document.getElementById('labModalTitle').textContent = 'Edit Laboratory';
    document.getElementById('labEditId').value = l.labId;
    document.getElementById('labCodeInput').value = l.labCode;
    document.getElementById('labNameInput').value = l.labName;
    document.getElementById('labLocationInput').value = l.location;
    document.getElementById('labCapacityInput').value = l.capacity;
    document.getElementById('labModal').classList.add('open');
}

function closeLabModal() {
    document.getElementById('labModal').classList.remove('open');
}

async function submitLabForm(e) {
    e.preventDefault();
    const editId = document.getElementById('labEditId').value;
    const payload = {
        labCode: document.getElementById('labCodeInput').value.trim(),
        labName: document.getElementById('labNameInput').value.trim(),
        location: document.getElementById('labLocationInput').value.trim(),
        capacity: parseInt(document.getElementById('labCapacityInput').value)
    };

    try {
        if (editId) {
            await Api.admin.updateLab(editId, payload);
            Toast.success('Laboratory updated successfully!');
        } else {
            await Api.admin.createLab(payload);
            Toast.success('Laboratory added successfully!');
        }
        closeLabModal();
        await loadLaboratories();
    } catch (err) {
        Toast.error(err.message || 'Failed to save laboratory.');
    }
}

/**
 * 5. Equipment Management
 */
async function loadEquipment() {
    const tableBody = document.getElementById('adminEquipmentTableBody');
    const keyword = document.getElementById('adminEquipSearchInput').value.trim();
    const params = { size: 100 };
    if (keyword) params.keyword = keyword;

    try {
        const res = await Api.admin.getEquipment(params);
        const equip = res.data.content || [];

        if (equip.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="8" class="empty-state">No equipment records found.</td></tr>`;
            return;
        }

        tableBody.innerHTML = equip.map(e => `
            <tr>
                <td style="font-family:monospace; font-weight:600;">${e.equipmentCode}</td>
                <td><strong>${e.name}</strong></td>
                <td>${e.laboratory ? e.laboratory.labCode : '-'}</td>
                <td>${e.category}</td>
                <td>${e.quantityTotal}</td>
                <td><span class="badge ${e.quantityAvailable > 0 ? 'badge-available' : 'badge-unavailable'}">${e.quantityAvailable}</span></td>
                <td><span class="badge badge-approved">${e.conditionStatus}</span></td>
                <td>
                    <div class="table-actions">
                        <button class="btn btn-secondary btn-sm" onclick='editEquipment(${JSON.stringify(e)})'>
                            <i class="fa-solid fa-pen"></i>
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="deleteEquipment(${e.equipmentId})">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="8" style="color:red;" class="empty-state">Failed to load equipment.</td></tr>`;
    }
}

function handleAdminEquipmentSearch() {
    clearTimeout(window._adminEquipTimer);
    window._adminEquipTimer = setTimeout(loadEquipment, 300);
}

function openEquipmentModal() {
    document.getElementById('equipmentModalTitle').textContent = 'Register Equipment';
    document.getElementById('equipEditId').value = '';
    document.getElementById('equipCode').value = '';
    document.getElementById('equipName').value = '';
    document.getElementById('equipCategory').value = '';
    document.getElementById('equipModel').value = '';
    document.getElementById('equipTotalQty').value = '1';
    document.getElementById('equipAvailQty').value = '1';
    document.getElementById('equipDesc').value = '';
    document.getElementById('equipmentModal').classList.add('open');
}

function editEquipment(e) {
    document.getElementById('equipmentModalTitle').textContent = 'Edit Equipment';
    document.getElementById('equipEditId').value = e.equipmentId;
    document.getElementById('equipLabSelect').value = e.laboratory ? e.laboratory.labId : '';
    document.getElementById('equipCode').value = e.equipmentCode;
    document.getElementById('equipName').value = e.name;
    document.getElementById('equipCategory').value = e.category;
    document.getElementById('equipModel').value = e.makeModel || '';
    document.getElementById('equipTotalQty').value = e.quantityTotal;
    document.getElementById('equipAvailQty').value = e.quantityAvailable;
    document.getElementById('equipCondition').value = e.conditionStatus;
    document.getElementById('equipDesc').value = e.description || '';
    document.getElementById('equipmentModal').classList.add('open');
}

function closeEquipmentModal() {
    document.getElementById('equipmentModal').classList.remove('open');
}

async function submitEquipmentForm(e) {
    e.preventDefault();
    const editId = document.getElementById('equipEditId').value;
    const payload = {
        labId: parseInt(document.getElementById('equipLabSelect').value),
        equipmentCode: document.getElementById('equipCode').value.trim(),
        name: document.getElementById('equipName').value.trim(),
        category: document.getElementById('equipCategory').value.trim(),
        makeModel: document.getElementById('equipModel').value.trim(),
        quantityTotal: parseInt(document.getElementById('equipTotalQty').value),
        quantityAvailable: parseInt(document.getElementById('equipAvailQty').value),
        conditionStatus: document.getElementById('equipCondition').value,
        description: document.getElementById('equipDesc').value.trim()
    };

    try {
        if (editId) {
            await Api.admin.updateEquipment(editId, payload);
            Toast.success('Equipment updated successfully!');
        } else {
            await Api.admin.createEquipment(payload);
            Toast.success('Equipment registered successfully!');
        }
        closeEquipmentModal();
        await loadEquipment();
    } catch (err) {
        Toast.error(err.message || 'Failed to save equipment.');
    }
}

async function deleteEquipment(id) {
    if (!confirm(`Are you sure you want to deactivate equipment #${id}?`)) return;
    try {
        await Api.admin.deleteEquipment(id);
        Toast.success('Equipment deleted from active catalogue.');
        await loadEquipment();
    } catch (err) {
        Toast.error(err.message || 'Failed to delete equipment.');
    }
}

/**
 * 6. Faculty-Lab Assignments
 */
function openAssignmentModal() {
    document.getElementById('assignmentModal').classList.add('open');
}

function closeAssignmentModal() {
    document.getElementById('assignmentModal').classList.remove('open');
}

async function submitAssignmentForm(e) {
    e.preventDefault();
    const payload = {
        facultyId: parseInt(document.getElementById('assignFacultySelect').value),
        labId: parseInt(document.getElementById('assignLabSelect').value),
        assignmentRole: document.getElementById('assignRoleSelect').value,
        assignedDate: document.getElementById('assignDate').value
    };

    try {
        await Api.admin.assignFacultyToLab(payload);
        Toast.success('Faculty successfully assigned to laboratory!');
        closeAssignmentModal();
    } catch (err) {
        Toast.error(err.message || 'Failed to create faculty-lab link.');
    }
}

/**
 * 7. Master Bookings
 */
async function loadAdminBookings() {
    const tableBody = document.getElementById('adminBookingsTableBody');
    const status = document.getElementById('adminBookingStatusFilter').value;
    const params = { size: 100 };
    if (status) params.status = status;

    try {
        const res = await Api.admin.getBookings(params);
        const bookings = res.data.content || [];

        if (bookings.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="7" class="empty-state">No reservations match criteria.</td></tr>`;
            return;
        }

        tableBody.innerHTML = bookings.map(b => `
            <tr>
                <td style="font-family:monospace; font-weight:600;">#${b.bookingId}</td>
                <td>${b.student ? b.student.fullName : 'Student'}</td>
                <td><strong>${b.equipment ? b.equipment.name : 'Equipment'}</strong></td>
                <td>${b.bookingDate}</td>
                <td>${b.startTime.substring(0,5)} - ${b.endTime.substring(0,5)}</td>
                <td><span class="badge badge-${b.status.toLowerCase()}">${b.status}</span></td>
                <td>
                    ${b.status === 'PENDING' ? `
                        <div class="table-actions">
                            <button class="btn btn-success btn-sm" onclick="adminApproveBooking(${b.bookingId})">Approve</button>
                            <button class="btn btn-danger btn-sm" onclick="adminRejectBooking(${b.bookingId})">Reject</button>
                        </div>
                    ` : '<span style="color:var(--text-muted); font-size:0.8rem;">Processed</span>'}
                </td>
            </tr>
        `).join('');
    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="7" style="color:red;" class="empty-state">Failed to load bookings: ${err.message}</td></tr>`;
    }
}

async function adminApproveBooking(id) {
    try {
        await Api.admin.approveRejectBooking(id, 'APPROVED', 'Approved by institutional administrator.');
        Toast.success(`Booking #${id} approved.`);
        await loadAdminBookings();
    } catch (err) {
        Toast.error(err.message || 'Failed to approve booking.');
    }
}

async function adminRejectBooking(id) {
    const reason = prompt('Specify rejection reason:');
    if (!reason) return;

    try {
        await Api.admin.approveRejectBooking(id, 'REJECTED', reason);
        Toast.success(`Booking #${id} rejected.`);
        await loadAdminBookings();
    } catch (err) {
        Toast.error(err.message || 'Failed to reject booking.');
    }
}

/**
 * 8. Waiting Lists
 */
async function loadWaitingLists() {
    const tableBody = document.getElementById('adminWaitlistTableBody');
    try {
        const res = await Api.admin.getWaitingLists();
        const lists = res.data || [];

        if (lists.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="7" class="empty-state">No equipment waiting queues currently active.</td></tr>`;
            return;
        }

        tableBody.innerHTML = lists.map(w => `
            <tr>
                <td style="font-family:monospace;">#WL-${w.waitlistId}</td>
                <td><strong>${w.equipment ? w.equipment.name : 'Equipment'}</strong></td>
                <td>${w.student ? w.student.fullName : 'Student'}</td>
                <td>${w.bookingDate}</td>
                <td>${w.startTime.substring(0,5)} - ${w.endTime.substring(0,5)}</td>
                <td><span class="badge badge-waitlisted">#${w.queuePosition}</span></td>
                <td><span class="badge badge-pending">${w.status}</span></td>
            </tr>
        `).join('');
    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="7" style="color:red;" class="empty-state">Failed to load waiting lists.</td></tr>`;
    }
}

/**
 * 9. Damage Reports
 */
async function loadDamageReports() {
    const tableBody = document.getElementById('adminDamageTableBody');
    try {
        const res = await Api.admin.getDamageReports({ size: 100 });
        const reports = res.data.content || [];

        if (reports.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="7" class="empty-state">No damage incidents recorded.</td></tr>`;
            return;
        }

        tableBody.innerHTML = reports.map(r => `
            <tr>
                <td style="font-family:monospace; font-weight:600;">#DM-${r.damageId}</td>
                <td><strong>${r.equipment ? r.equipment.name : 'Equipment'}</strong></td>
                <td>${r.reportedBy ? r.reportedBy.username : 'Staff'}</td>
                <td><span class="badge badge-rejected">${r.severity}</span></td>
                <td style="max-width:250px; font-size:0.85rem;">${r.description}</td>
                <td><span class="badge badge-pending">${r.status}</span></td>
                <td>
                    <select onchange="updateDamageReportStatus(${r.damageId}, this.value)" style="font-size:0.8rem; padding:0.2rem 0.4rem; border-radius:4px; border:1px solid var(--border-color);">
                        <option value="REPORTED" ${r.status==='REPORTED'?'selected':''}>REPORTED</option>
                        <option value="UNDER_REVIEW" ${r.status==='UNDER_REVIEW'?'selected':''}>UNDER REVIEW</option>
                        <option value="REPAIR_IN_PROGRESS" ${r.status==='REPAIR_IN_PROGRESS'?'selected':''}>IN REPAIR</option>
                        <option value="RESOLVED" ${r.status==='RESOLVED'?'selected':''}>RESOLVED</option>
                        <option value="WRITTEN_OFF" ${r.status==='WRITTEN_OFF'?'selected':''}>WRITTEN OFF</option>
                    </select>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="7" style="color:red;" class="empty-state">Failed to load damage reports.</td></tr>`;
    }
}

async function updateDamageReportStatus(id, newStatus) {
    try {
        await Api.admin.updateDamageStatus(id, newStatus);
        Toast.success(`Damage report #${id} marked as ${newStatus}.`);
        await loadAdminDashboardStats();
    } catch (err) {
        Toast.error('Failed to update damage report status');
    }
}

/**
 * 10. Usage Rules
 */
async function fetchUsageRuleForLab() {
    const labId = document.getElementById('ruleLabSelect').value;
    if (!labId) return;

    try {
        const res = await Api.admin.getUsageRule(labId);
        const rule = res.data;
        if (rule) {
            document.getElementById('ruleMaxDuration').value = rule.maxBookingDurationHours;
            document.getElementById('ruleMaxAdvance').value = rule.maxAdvanceBookingDays;
            document.getElementById('ruleMaxConcurrent').value = rule.maxConcurrentBookingsPerStudent;
            document.getElementById('ruleAllowedDays').value = rule.allowedDays;
            document.getElementById('ruleOpenTime').value = rule.openTime.substring(0,5);
            document.getElementById('ruleCloseTime').value = rule.closeTime.substring(0,5);
        }
    } catch (err) {
        console.error('Usage rule fetch error:', err);
    }
}

async function saveUsageRule(e) {
    e.preventDefault();
    const payload = {
        labId: parseInt(document.getElementById('ruleLabSelect').value),
        maxBookingDurationHours: parseInt(document.getElementById('ruleMaxDuration').value),
        maxAdvanceBookingDays: parseInt(document.getElementById('ruleMaxAdvance').value),
        maxConcurrentBookingsPerStudent: parseInt(document.getElementById('ruleMaxConcurrent').value),
        allowedDays: document.getElementById('ruleAllowedDays').value.trim(),
        openTime: document.getElementById('ruleOpenTime').value + ':00',
        closeTime: document.getElementById('ruleCloseTime').value + ':00'
    };

    try {
        await Api.admin.setUsageRule(payload);
        Toast.success('Fair usage policy updated for this laboratory!');
    } catch (err) {
        Toast.error(err.message || 'Failed to update usage rules.');
    }
}

/**
 * 11. Activity Logs
 */
async function loadActivityLogs() {
    const tableBody = document.getElementById('fullAuditTableBody');
    const quickBody = document.getElementById('quickAuditTableBody');

    try {
        const res = await Api.admin.getActivityLogs({ size: 50 });
        const logs = res.data.content || [];

        if (logs.length === 0) {
            const empty = `<tr><td colspan="7" class="empty-state">No audit logs recorded yet.</td></tr>`;
            if (tableBody) tableBody.innerHTML = empty;
            if (quickBody) quickBody.innerHTML = empty;
            return;
        }

        const rows = logs.map(l => `
            <tr>
                <td style="font-size:0.8rem; color:var(--text-muted);">${new Date(l.createdAt).toLocaleString()}</td>
                <td><span class="badge badge-active">${l.actionType}</span></td>
                <td>${l.entityType}</td>
                <td style="font-size:0.85rem;">${l.description || '-'}</td>
                <td style="font-family:monospace; font-size:0.8rem;">${l.ipAddress || '-'}</td>
            </tr>
        `).join('');

        if (tableBody) {
            tableBody.innerHTML = logs.map(l => `
                <tr>
                    <td style="font-family:monospace;">#${l.logId}</td>
                    <td style="font-size:0.8rem; color:var(--text-muted);">${new Date(l.createdAt).toLocaleString()}</td>
                    <td><strong>${l.user ? l.user.username : 'SYSTEM'}</strong></td>
                    <td><span class="badge badge-active">${l.actionType}</span></td>
                    <td>${l.entityType}</td>
                    <td style="font-size:0.85rem;">${l.description || '-'}</td>
                    <td style="font-family:monospace; font-size:0.8rem;">${l.ipAddress || '-'}</td>
                </tr>
            `).join('');
        }

        if (quickBody) {
            quickBody.innerHTML = rows;
        }
    } catch (err) {
        console.error('Failed to load audit logs:', err);
    }
}
