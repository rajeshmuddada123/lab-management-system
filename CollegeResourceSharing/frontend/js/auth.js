/**
 * COLLEGE RESOURCE SHARING PLATFORM — AUTHENTICATION CONTROLLER
 * Handles Student, Faculty, and Admin authentication workflows.
 */

document.addEventListener('DOMContentLoaded', () => {
    // If user is already authenticated, redirect to their role dashboard
    if (Api.isAuthenticated()) {
        const user = Api.getCurrentUser();
        if (user && user.role) {
            Api.redirectToRoleDashboard(user.role);
            return;
        }
    }

    initStudentLogin();
    initFacultyLogin();
    initAdminLogin();
});

/**
 * Toggle password field visibility
 */
function togglePasswordVisibility(inputId, btn) {
    const input = document.getElementById(inputId);
    if (!input) return;
    const isPassword = input.type === 'password';
    input.type = isPassword ? 'text' : 'password';
    btn.innerHTML = isPassword ? '<i class="fa-regular fa-eye-slash"></i>' : '<i class="fa-regular fa-eye"></i>';
}

/**
 * Open / Close Admin Modal
 */
function openAdminModal() {
    const modal = document.getElementById('adminModal');
    if (modal) modal.classList.add('open');
    clearFeedback('adminFeedback');
}

function closeAdminModal() {
    const modal = document.getElementById('adminModal');
    if (modal) modal.classList.remove('open');
}

/**
 * Clear feedback banner
 */
function clearFeedback(elementId) {
    const el = document.getElementById(elementId);
    if (el) {
        el.className = 'login-feedback';
        el.innerHTML = '';
        el.style.display = 'none';
    }
}

/**
 * Show feedback banner
 */
function showFeedback(elementId, message, type = 'error') {
    const el = document.getElementById(elementId);
    if (el) {
        el.className = `login-feedback ${type}`;
        el.innerHTML = `<i class="fa-solid fa-circle-exclamation"></i> ${message}`;
        el.style.display = 'block';
    }
}

/**
 * Generic login executor
 */
async function executeLogin({ identifier, password, expectedRole, feedbackId, buttonId }) {
    clearFeedback(feedbackId);
    const btn = document.getElementById(buttonId);
    const originalText = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = `<span class="spinner"></span> <span>Verifying...</span>`;

    try {
        const res = await Api.auth.login(identifier, password);
        const data = res.data;

        // Role verification guard: ensure user signed in via their designated portal
        if (expectedRole && data.role !== expectedRole) {
            throw new Error(`Authentication succeeded, but this account is not registered as a ${expectedRole.toLowerCase()}. Please use the appropriate portal.`);
        }

        // Store JWT token and session metadata
        Api.setSession(data.token, {
            userId: data.userId,
            username: data.username,
            email: data.email,
            role: data.role,
            status: data.status
        });

        showFeedback(feedbackId, 'Authentication successful! Redirecting...', 'success');

        setTimeout(() => {
            Api.redirectToRoleDashboard(data.role);
        }, 600);

    } catch (err) {
        showFeedback(feedbackId, err.message || 'Login failed. Please check your credentials.');
    } finally {
        btn.disabled = false;
        btn.innerHTML = originalText;
    }
}

/**
 * Student Login Setup
 */
function initStudentLogin() {
    const form = document.getElementById('studentLoginForm');
    if (!form) return;

    form.addEventListener('submit', (e) => {
        e.preventDefault();
        const regNo = document.getElementById('studentRegNo').value.trim();
        const password = document.getElementById('studentPassword').value;

        if (!regNo || !password) {
            showFeedback('studentFeedback', 'Please provide both Registration Number and Password.');
            return;
        }

        executeLogin({
            identifier: regNo,
            password: password,
            expectedRole: 'STUDENT',
            feedbackId: 'studentFeedback',
            buttonId: 'studentSubmitBtn'
        });
    });
}

/**
 * Faculty Login Setup
 */
function initFacultyLogin() {
    const form = document.getElementById('facultyLoginForm');
    if (!form) return;

    form.addEventListener('submit', (e) => {
        e.preventDefault();
        const facultyCode = document.getElementById('facultyCode').value.trim();
        const password = document.getElementById('facultyPassword').value;

        if (!facultyCode || !password) {
            showFeedback('facultyFeedback', 'Please provide both Faculty ID and Password.');
            return;
        }

        executeLogin({
            identifier: facultyCode,
            password: password,
            expectedRole: 'FACULTY',
            feedbackId: 'facultyFeedback',
            buttonId: 'facultySubmitBtn'
        });
    });
}

/**
 * Admin Login Setup
 */
function initAdminLogin() {
    const form = document.getElementById('adminLoginForm');
    if (!form) return;

    form.addEventListener('submit', (e) => {
        e.preventDefault();
        const username = document.getElementById('adminUsername').value.trim();
        const password = document.getElementById('adminPassword').value;

        if (!username || !password) {
            showFeedback('adminFeedback', 'Please enter your Admin username/email and password.');
            return;
        }

        executeLogin({
            identifier: username,
            password: password,
            expectedRole: 'ADMIN',
            feedbackId: 'adminFeedback',
            buttonId: 'adminSubmitBtn'
        });
    });
}
