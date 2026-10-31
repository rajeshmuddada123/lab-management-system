/**
 * COLLEGE RESOURCE SHARING PLATFORM — CENTRAL API CLIENT
 * Centralized Fetch API wrapper, authentication, and role services.
 */

const API_CONFIG = {
    BASE_URL: 'https://lab-management-system-aj3p.onrender.com/api',
    TOKEN_KEY: 'crs_jwt_token',
    USER_KEY: 'crs_user_data'
};

const Api = {
    /**
     * Get stored JWT token
     */
    getToken() {
        return localStorage.getItem(API_CONFIG.TOKEN_KEY);
    },

    /**
     * Store authentication payload
     */
    setSession(token, user) {
        localStorage.setItem(API_CONFIG.TOKEN_KEY, token);
        localStorage.setItem(API_CONFIG.USER_KEY, JSON.stringify(user));
    },

    /**
     * Clear session data
     */
    clearSession() {
        localStorage.removeItem(API_CONFIG.TOKEN_KEY);
        localStorage.removeItem(API_CONFIG.USER_KEY);
    },

    /**
     * Retrieve stored user
     */
    getCurrentUser() {
        const data = localStorage.getItem(API_CONFIG.USER_KEY);
        try {
            return data ? JSON.parse(data) : null;
        } catch (e) {
            return null;
        }
    },

    /**
     * Check if user is logged in
     */
    isAuthenticated() {
        return !!this.getToken();
    },

    /**
     * Enforce role check or redirect to login
     */
    requireRole(expectedRole) {
        const user = this.getCurrentUser();
        if (!this.isAuthenticated() || !user) {
            window.location.href = 'index.html';
            return false;
        }
        if (user.role !== expectedRole) {
            alert(`Access restricted. You need ${expectedRole} privileges.`);
            this.redirectToRoleDashboard(user.role);
            return false;
        }
        return true;
    },

    /**
     * Role-based redirection helper
     */
    redirectToRoleDashboard(role) {
        switch (role) {
            case 'STUDENT':
                window.location.href = 'student.html';
                break;
            case 'FACULTY':
                window.location.href = 'faculty.html';
                break;
            case 'ADMIN':
                window.location.href = 'admin.html';
                break;
            default:
                window.location.href = 'index.html';
        }
    },

    /**
     * Logout and redirect
     */
    logout() {
        this.clearSession();
        window.location.href = 'index.html';
    },

    /**
     * Centralized HTTP Request Handler
     */
    async request(endpoint, options = {}) {
        const url = `${API_CONFIG.BASE_URL}${endpoint}`;
        const headers = options.headers || {};
        const token = this.getToken();

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        // Only set Content-Type to JSON if body is NOT FormData
        if (!(options.body instanceof FormData) && !headers['Content-Type']) {
            headers['Content-Type'] = 'application/json';
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(url, config);

            // Handle 401 Unauthorized
            if (response.status === 401) {
                this.clearSession();
                if (!window.location.pathname.endsWith('index.html')) {
                    alert('Session expired. Please log in again.');
                    window.location.href = 'index.html';
                }
                throw new Error('Authentication required');
            }

            // Handle 403 Forbidden
            if (response.status === 403) {
                throw new Error('Access denied. You do not have permission.');
            }

            const data = await response.json().catch(() => null);

            if (!response.ok) {
                const message = (data && data.message) || `HTTP error ${response.status}`;
                throw new Error(message);
            }

            return data;
        } catch (error) {
            console.error(`API Error [${endpoint}]:`, error);
            if (error instanceof TypeError && (error.message === 'Failed to fetch' || error.message.includes('fetch'))) {
                throw new Error('Cannot connect to the backend server (http://localhost:8080). Please ensure the backend is running.');
            }
            throw error;
        }
    },

    // ── AUTH ENDPOINTS ──
    auth: {
        async login(username, password) {
            return Api.request('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ username, password })
            });
        },
        async getMe() {
            return Api.request('/auth/me', { method: 'GET' });
        }
    },

    // ── STUDENT ENDPOINTS ──
    student: {
        getProfile: () => Api.request('/student/profile'),
        getLabs: () => Api.request('/student/labs'),
        getEquipment: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return Api.request(`/student/equipment?${query}`);
        },
        getEquipmentDetails: (id) => Api.request(`/student/equipment/${id}`),
        getEquipmentImages: (id) => Api.request(`/student/equipment/${id}/images`),
        createBooking: (payload) => Api.request('/student/bookings', {
            method: 'POST',
            body: JSON.stringify(payload)
        }),
        getBookings: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return Api.request(`/student/bookings?${query}`);
        },
        cancelBooking: (id) => Api.request(`/student/bookings/${id}/cancel`, { method: 'PUT' }),
        getWaitlist: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return Api.request(`/student/waitlist?${query}`);
        },
        getNotifications: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return Api.request(`/student/notifications?${query}`);
        },
        markNotificationRead: (id) => Api.request(`/student/notifications/${id}/read`, { method: 'PUT' }),
        markAllNotificationsRead: () => Api.request('/student/notifications/read-all', { method: 'PUT' })
    },

    // ── FACULTY ENDPOINTS ──
    faculty: {
        getProfile: () => Api.request('/faculty/profile'),
        getAssignedLabs: () => Api.request('/faculty/labs'),
        getEquipmentForLab: (labId, params = {}) => {
            const query = new URLSearchParams({ labId, ...params }).toString();
            return Api.request(`/faculty/equipment?${query}`);
        },
        updateEquipmentAvailability: (id, availableQty, condition) => Api.request(`/faculty/equipment/${id}/availability`, {
            method: 'PUT',
            body: JSON.stringify({ availableQty, condition })
        }),
        getBookings: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return Api.request(`/faculty/bookings?${query}`);
        },
        getPendingBookings: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return Api.request(`/faculty/bookings/pending?${query}`);
        },
        approveRejectBooking: (id, action, notes) => Api.request(`/faculty/bookings/${id}/approval`, {
            method: 'POST',
            body: JSON.stringify({ action, notes })
        }),
        verifyCollection: (id, formData) => Api.request(`/faculty/bookings/${id}/collection`, {
            method: 'POST',
            body: formData
        }),
        verifyReturn: (id, formData) => Api.request(`/faculty/bookings/${id}/return`, {
            method: 'POST',
            body: formData
        }),
        reportDamage: (formData) => Api.request('/faculty/damage-reports', {
            method: 'POST',
            body: formData
        }),
        reportCondition: (formData) => Api.request('/faculty/condition-reports', {
            method: 'POST',
            body: formData
        }),
        getOverdue: () => Api.request('/faculty/overdue'),
        getNotifications: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return Api.request(`/faculty/notifications?${query}`);
        }
    },

    // ── ADMIN ENDPOINTS ──
    admin: {
        getStats: () => Api.request('/admin/stats'),
        // Students
        getStudents: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return Api.request(`/admin/students?${query}`);
        },
        createStudent: (payload) => Api.request('/admin/students', {
            method: 'POST',
            body: JSON.stringify(payload)
        }),
        updateStudent: (id, payload) => Api.request(`/admin/students/${id}`, {
            method: 'PUT',
            body: JSON.stringify(payload)
        }),
        deactivateStudent: (id) => Api.request(`/admin/students/${id}`, { method: 'DELETE' }),
        importStudentsCsv: (formData) => Api.request('/admin/students/import-csv', {
            method: 'POST',
            body: formData
        }),
        // Faculty
        getFaculty: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return Api.request(`/admin/faculty?${query}`);
        },
        createFaculty: (payload) => Api.request('/admin/faculty', {
            method: 'POST',
            body: JSON.stringify(payload)
        }),
        deactivateFaculty: (id) => Api.request(`/admin/faculty/${id}`, { method: 'DELETE' }),
        // Labs
        getLabs: () => Api.request('/admin/labs'),
        createLab: (payload) => Api.request('/admin/labs', {
            method: 'POST',
            body: JSON.stringify(payload)
        }),
        updateLab: (id, payload) => Api.request(`/admin/labs/${id}`, {
            method: 'PUT',
            body: JSON.stringify(payload)
        }),
        assignFacultyToLab: (payload) => Api.request('/admin/faculty-assignments', {
            method: 'POST',
            body: JSON.stringify(payload)
        }),
        // Equipment
        getEquipment: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return Api.request(`/admin/equipment?${query}`);
        },
        createEquipment: (payload) => Api.request('/admin/equipment', {
            method: 'POST',
            body: JSON.stringify(payload)
        }),
        updateEquipment: (id, payload) => Api.request(`/admin/equipment/${id}`, {
            method: 'PUT',
            body: JSON.stringify(payload)
        }),
        deleteEquipment: (id) => Api.request(`/admin/equipment/${id}`, { method: 'DELETE' }),
        uploadEquipmentImage: (id, formData) => Api.request(`/admin/equipment/${id}/images`, {
            method: 'POST',
            body: formData
        }),
        // Bookings
        getBookings: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return Api.request(`/admin/bookings?${query}`);
        },
        approveRejectBooking: (id, action, notes) => Api.request(`/admin/bookings/${id}/approval`, {
            method: 'POST',
            body: JSON.stringify({ action, notes })
        }),
        getWaitingLists: () => Api.request('/admin/waiting-lists'),
        // Damage Reports
        getDamageReports: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return Api.request(`/admin/damage-reports?${query}`);
        },
        updateDamageStatus: (id, status) => Api.request(`/admin/damage-reports/${id}/status?status=${status}`, {
            method: 'PUT'
        }),
        // Usage Rules
        getUsageRule: (labId) => Api.request(`/admin/usage-rules/${labId}`),
        setUsageRule: (payload) => Api.request('/admin/usage-rules', {
            method: 'POST',
            body: JSON.stringify(payload)
        }),
        // Logs
        getActivityLogs: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return Api.request(`/admin/activity-logs?${query}`);
        }
    }
};

// UI Toast Notification Utility
const Toast = {
    show(message, type = 'info', duration = 4000) {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.className = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `
            <span>${message}</span>
            <button style="background:none;border:none;cursor:pointer;margin-left:1rem;color:#64748b;font-weight:bold;">&times;</button>
        `;

        toast.querySelector('button').addEventListener('click', () => toast.remove());
        container.appendChild(toast);

        setTimeout(() => {
            if (toast.parentElement) toast.remove();
        }, duration);
    },
    success(msg) { this.show(msg, 'success'); },
    error(msg) { this.show(msg, 'error'); },
    warning(msg) { this.show(msg, 'warning'); }
};

window.Api = Api;
window.Toast = Toast;
