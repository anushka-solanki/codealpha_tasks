/*
 * GradeStream Premium Admin Dashboard - app.js
 * Java OOP Architecture implemented in JavaScript:
 *   - Student class (mirrors Java Student class)
 *   - StudentManager class (mirrors Java StudentManager using ArrayList/Array)
 * Author: GradeStream Admin Panel v2.0
 */

'use strict';

// ============================================================
// JAVA OOP: Student Class
// ============================================================
class Student {
    static SUBJECTS = ['Math', 'Science', 'English', 'History'];
    static #idCounter = 1000;

    constructor(name, grades, remarks = '') {
        this.id = `GS-${++Student.#idCounter}`;
        this.name = name;
        this.grades = grades; // Array (size 4) mirroring Java double[] grades
        this.remarks = remarks;
        this.createdAt = new Date().toISOString();
        this.attendance = Math.floor(Math.random() * 20) + 80; // 80-99% demo attendance
        this.avatar = this.#generateAvatar();
    }

    // Restore from plain object (e.g. from localStorage)
    static fromObject(obj) {
        const s = Object.create(Student.prototype);
        Object.assign(s, obj);
        // Track max ID so new students get unique IDs
        const num = parseInt(s.id?.replace('GS-','')) || 0;
        if (num > Student.#idCounter) Student.#idCounter = num;
        return s;
    }

    #generateAvatar() {
        const colors = ['#6366f1','#8b5cf6','#3b82f6','#10b981','#f59e0b','#ef4444','#06b6d4','#ec4899'];
        return colors[Math.abs(this.name.charCodeAt(0) + this.name.charCodeAt(1 % this.name.length)) % colors.length];
    }

    getAvatarColor() { return this.avatar || '#6366f1'; }
    getInitials() { return this.name.split(' ').map(w => w[0]?.toUpperCase()).slice(0,2).join(''); }

    // calculateAverage() - Java method equivalent
    calculateAverage() {
        if (!this.grades || this.grades.length === 0) return 0;
        return Math.round((this.grades.reduce((a, b) => a + b, 0) / this.grades.length) * 10) / 10;
    }

    // calculateGrade() - Java method equivalent
    calculateGrade() {
        const avg = this.calculateAverage();
        if (avg >= 90) return 'A';
        if (avg >= 75) return 'B';
        if (avg >= 60) return 'C';
        if (avg >= 50) return 'D';
        return 'F';
    }

    // isPassed() - pass if average >= 50
    isPassed() { return this.calculateAverage() >= 50; }
}

// ============================================================
// JAVA OOP: StudentManager Class
// ============================================================
class StudentManager {
    #students = []; // ArrayList<Student> equivalent

    constructor() {
        this.#load();
    }

    // addStudent() - Java method equivalent
    addStudent(name, grades, remarks = '') {
        const s = new Student(name, grades, remarks);
        this.#students.push(s);
        this.#save();
        return s;
    }

    // updateStudent() - Java method equivalent
    updateStudent(id, name, grades, remarks = '') {
        const idx = this.#students.findIndex(s => s.id === id);
        if (idx === -1) return null;
        this.#students[idx].name = name;
        this.#students[idx].grades = grades;
        this.#students[idx].remarks = remarks;
        this.#save();
        return this.#students[idx];
    }

    // deleteStudent() - Java method equivalent
    deleteStudent(id) {
        const idx = this.#students.findIndex(s => s.id === id);
        if (idx === -1) return false;
        this.#students.splice(idx, 1);
        this.#save();
        return true;
    }

    // searchStudent() - Java method equivalent
    searchStudent(query) {
        const q = query.toLowerCase();
        return this.#students.filter(s =>
            s.name.toLowerCase().includes(q) ||
            s.id.toLowerCase().includes(q) ||
            s.calculateGrade().toLowerCase() === q
        );
    }

    // getAll() - returns ArrayList
    getAll() { return [...this.#students]; }
    getById(id) { return this.#students.find(s => s.id === id) || null; }
    count() { return this.#students.length; }

    // calculateAverage() / highestScore() / lowestScore()
    calculateClassAverage() {
        if (this.#students.length === 0) return null;
        const total = this.#students.reduce((sum, s) => sum + s.calculateAverage(), 0);
        return Math.round((total / this.#students.length) * 10) / 10;
    }

    highestScore() {
        if (this.#students.length === 0) return null;
        return this.#students.reduce((max, s) => s.calculateAverage() > max.calculateAverage() ? s : max);
    }

    lowestScore() {
        if (this.#students.length === 0) return null;
        return this.#students.reduce((min, s) => s.calculateAverage() < min.calculateAverage() ? s : min);
    }

    passCount() { return this.#students.filter(s => s.isPassed()).length; }
    failCount() { return this.#students.filter(s => !s.isPassed()).length; }
    passPercentage() { if (!this.#students.length) return null; return Math.round((this.passCount() / this.#students.length) * 100); }
    failPercentage() { if (!this.#students.length) return null; return 100 - this.passPercentage(); }

    gradeCount(grade) { return this.#students.filter(s => s.calculateGrade() === grade).length; }

    subjectAverages() {
        if (!this.#students.length) return [0, 0, 0, 0];
        return Student.SUBJECTS.map((_, i) => {
            const vals = this.#students.map(s => s.grades[i]).filter(v => v != null);
            return vals.length ? Math.round((vals.reduce((a,b)=>a+b,0)/vals.length)*10)/10 : 0;
        });
    }

    top5() {
        return [...this.#students].sort((a,b)=>b.calculateAverage()-a.calculateAverage()).slice(0,5);
    }

    // generateSummary() - Java method equivalent
    generateSummary() {
        const avgs = this.subjectAverages();
        const maxAvgIdx = avgs.indexOf(Math.max(...avgs));
        const minAvgIdx = avgs.indexOf(Math.min(...avgs));
        return {
            totalStudents: this.count(),
            classAverage: this.calculateClassAverage(),
            highestPerformer: this.highestScore(),
            lowestPerformer: this.lowestScore(),
            passPercentage: this.passPercentage(),
            failPercentage: this.failPercentage(),
            bestSubject: Student.SUBJECTS[maxAvgIdx],
            worstSubject: Student.SUBJECTS[minAvgIdx],
            studentsAbove90: this.#students.filter(s=>s.calculateAverage()>=90).length,
            studentsNeedHelp: this.#students.filter(s=>s.calculateAverage()<50).length,
            gradeCounts: { A: this.gradeCount('A'), B: this.gradeCount('B'), C: this.gradeCount('C'), D: this.gradeCount('D'), F: this.gradeCount('F') }
        };
    }

    // exportCSV() - Java method equivalent
    exportCSV() {
        const header = ['ID', 'Name', 'Math', 'Science', 'English', 'History', 'Average', 'Grade', 'Status', 'Remarks'];
        const rows = this.#students.map(s => [
            s.id, `"${s.name}"`,
            ...s.grades,
            s.calculateAverage(),
            s.calculateGrade(),
            s.isPassed() ? 'Pass' : 'Fail',
            `"${s.remarks || ''}"`
        ]);
        return [header, ...rows].map(r => r.join(',')).join('\n');
    }

    // Persistence (localStorage)
    #save() { localStorage.setItem('gs_students', JSON.stringify(this.#students)); }
    #load() {
        try {
            const raw = JSON.parse(localStorage.getItem('gs_students') || '[]');
            this.#students = raw.map(o => Student.fromObject(o));
        } catch { this.#students = []; }
    }

    // Reset to demo data
    resetToDemo() {
        localStorage.removeItem('gs_students');
        this.#students = [];
        DEMO_STUDENTS.forEach(d => this.addStudent(d.name, d.grades, d.remarks));
    }

    clearAll() {
        this.#students = [];
        this.#save();
    }
}

// ============================================================
// DEMO DATA (Sample Students)
// ============================================================
const DEMO_STUDENTS = [
    { name: 'Alex Johnson',    grades: [92, 88, 95, 87], remarks: 'Excellent performer. Highly dedicated.' },
    { name: 'Maria Garcia',    grades: [78, 85, 72, 80], remarks: 'Good student. Needs improvement in English.' },
    { name: 'James Williams',  grades: [55, 62, 58, 50], remarks: 'Requires additional support in all subjects.' },
    { name: 'Sarah Brown',     grades: [96, 94, 98, 91], remarks: 'Outstanding! Top of the class.' },
    { name: 'Michael Davis',   grades: [70, 68, 74, 65], remarks: 'Average performance. Can do better.' },
    { name: 'Emily Wilson',    grades: [88, 92, 84, 90], remarks: 'Very good student. Strong in Science.' },
    { name: 'David Martinez',  grades: [45, 40, 52, 38], remarks: 'Struggling. Immediate intervention needed.' },
    { name: 'Emma Anderson',   grades: [83, 79, 86, 82], remarks: 'Consistent and hardworking student.' },
    { name: 'Chris Taylor',    grades: [60, 65, 55, 63], remarks: 'Average. More focus needed on English.' },
    { name: 'Olivia Thomas',   grades: [91, 89, 93, 88], remarks: 'Brilliant student with great potential.' },
];

// ============================================================
// GLOBAL STATE
// ============================================================
const manager = new StudentManager();
let activityLog = JSON.parse(localStorage.getItem('gs_activity') || '[]');
let notifications = JSON.parse(localStorage.getItem('gs_notifs') || '[]');

// Table state
let tableSearch = '';
let tableGradeFilter = 'all';
let tableSortField = 'name';
let tableSortDir = 'asc';
let currentPage = 1;
const PAGE_SIZE = 8;

// Charts
const charts = {};

// ============================================================
// AUTH GUARD
// ============================================================
function authGuard() {
    if (!sessionStorage.getItem('gs_auth')) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

// ============================================================
// DATETIME TICKER
// ============================================================
function updateDatetime() {
    const el = document.getElementById('datetime-display');
    if (!el) return;
    const now = new Date();
    el.textContent = now.toLocaleDateString('en-US', { weekday:'long', year:'numeric', month:'long', day:'numeric' })
        + ' · ' + now.toLocaleTimeString('en-US', { hour:'2-digit', minute:'2-digit', second:'2-digit' });
}

// ============================================================
// TOAST NOTIFICATIONS
// ============================================================
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    const icons = { success: '✓', error: '✕', info: 'i' };
    toast.innerHTML = `<div class="toast-icon">${icons[type] || 'i'}</div><div class="toast-msg">${message}</div><button class="toast-close">✕</button>`;
    container.appendChild(toast);
    toast.querySelector('.toast-close').onclick = () => dismissToast(toast);
    setTimeout(() => dismissToast(toast), 4500);

    // Add to notifications panel
    addNotification(message, type);
}

function dismissToast(toast) {
    toast.classList.add('out');
    setTimeout(() => toast.remove(), 350);
}

// ============================================================
// NOTIFICATIONS
// ============================================================
function addNotification(msg, type = 'info') {
    const n = { msg, type, time: new Date().toLocaleTimeString('en-US', {hour:'2-digit',minute:'2-digit'}) };
    notifications.unshift(n);
    if (notifications.length > 20) notifications.pop();
    localStorage.setItem('gs_notifs', JSON.stringify(notifications));
    renderNotifications();
}

function renderNotifications() {
    const list = document.getElementById('notif-list');
    const dot = document.getElementById('notif-dot');
    if (!list) return;
    if (notifications.length === 0) {
        list.innerHTML = '<div class="notif-empty">No notifications yet</div>';
        dot.classList.remove('show');
        return;
    }
    dot.classList.add('show');
    list.innerHTML = notifications.map(n => `
        <div class="notif-item">
            <div class="ni-dot ${n.type}"></div>
            <div>
                <div class="ni-msg">${n.msg}</div>
                <div class="ni-time">${n.time}</div>
            </div>
        </div>
    `).join('');
}

// ============================================================
// ACTIVITY LOG
// ============================================================
function logActivity(msg, type = 'info') {
    const item = { msg, type, time: new Date().toLocaleTimeString('en-US', {hour:'2-digit',minute:'2-digit',second:'2-digit'}) };
    activityLog.unshift(item);
    if (activityLog.length > 30) activityLog.pop();
    localStorage.setItem('gs_activity', JSON.stringify(activityLog));
    renderActivity();
}

function renderActivity() {
    const list = document.getElementById('activity-list');
    const count = document.getElementById('activity-count');
    if (!list) return;
    count.textContent = activityLog.length;
    if (activityLog.length === 0) {
        list.innerHTML = '<div class="activity-empty"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="40"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg><p>No activity yet</p></div>';
        return;
    }
    list.innerHTML = activityLog.map(a => `
        <div class="activity-item">
            <div class="activity-dot ${a.type}"></div>
            <div>
                <div class="activity-txt">${a.msg}</div>
                <div class="activity-time">${a.time}</div>
            </div>
        </div>
    `).join('');
}

// ============================================================
// ANIMATED COUNTER
// ============================================================
function animateCounter(el, target, suffix = '') {
    if (!el) return;
    const start = 0;
    const duration = 900;
    const step = (timestamp) => {
        if (!start) start = timestamp;
        const progress = Math.min((timestamp - start) / duration, 1);
        const ease = 1 - Math.pow(1 - progress, 3);
        el.textContent = (typeof target === 'string') ? target : (Math.round(ease * target) + suffix);
        if (progress < 1) requestAnimationFrame(step);
    };
    requestAnimationFrame(step);
}

// ============================================================
// STATS CARDS UPDATE
// ============================================================
function updateStats() {
    const all = manager.getAll();
    const sum = manager.generateSummary();

    document.getElementById('student-count-badge').textContent = manager.count();

    // Animate counters
    const stTotal = document.getElementById('st-total');
    if (stTotal) stTotal.textContent = manager.count();

    const stAvg = document.getElementById('st-avg');
    if (stAvg) stAvg.textContent = sum.classAverage !== null ? sum.classAverage + '%' : 'N/A';

    const avgTrend = document.getElementById('avg-trend');
    if (avgTrend && sum.classAverage !== null) {
        avgTrend.textContent = sum.classAverage >= 75 ? '↑ Good' : sum.classAverage >= 50 ? '→ Average' : '↓ Needs Attention';
        avgTrend.className = `sc-trend ${sum.classAverage >= 75 ? 'up' : sum.classAverage >= 50 ? '' : 'down'}`;
    }

    const stHigh = document.getElementById('st-high');
    if (stHigh) stHigh.textContent = sum.highestPerformer ? sum.highestPerformer.calculateAverage() + '%' : 'N/A';
    const highSub = document.getElementById('high-sub');
    if (highSub && sum.highestPerformer) highSub.textContent = sum.highestPerformer.name;

    const stLow = document.getElementById('st-low');
    if (stLow) stLow.textContent = sum.lowestPerformer ? sum.lowestPerformer.calculateAverage() + '%' : 'N/A';
    const lowSub = document.getElementById('low-sub');
    if (lowSub && sum.lowestPerformer) lowSub.textContent = sum.lowestPerformer.name;

    const stPass = document.getElementById('st-pass');
    if (stPass) stPass.textContent = sum.passPercentage !== null ? sum.passPercentage + '%' : 'N/A';

    const stFail = document.getElementById('st-fail');
    if (stFail) stFail.textContent = sum.failPercentage !== null ? sum.failPercentage + '%' : 'N/A';

    const stTop = document.getElementById('st-top');
    if (stTop) stTop.textContent = sum.highestPerformer ? sum.highestPerformer.name : 'N/A';
    const topScore = document.getElementById('top-score');
    if (topScore && sum.highestPerformer) topScore.textContent = sum.highestPerformer.calculateAverage() + '% avg';

    renderInsights(sum);
}

// ============================================================
// PERFORMANCE INSIGHTS
// ============================================================
function renderInsights(sum) {
    const el = document.getElementById('dash-insights');
    if (!el) return;
    if (manager.count() === 0) {
        el.innerHTML = '<div class="insight-empty">Add students to generate insights.</div>';
        return;
    }
    const insights = [];
    insights.push({ type:'success', title:'Best Subject', body: `${sum.bestSubject} has the highest class average.` });
    insights.push({ type:'danger', title:'Weakest Subject', body: `${sum.worstSubject} has the lowest class average — consider extra sessions.` });
    if (sum.studentsAbove90 > 0) insights.push({ type:'success', title:`${sum.studentsAbove90} Students Above 90%`, body: 'Outstanding! These students are excelling.' });
    if (sum.studentsNeedHelp > 0) insights.push({ type:'danger', title:`${sum.studentsNeedHelp} Students Need Help`, body: 'These students have averages below 50%. Immediate support recommended.' });
    insights.push({ type:'info', title:'Class Performance', body: `Overall class average is ${sum.classAverage}% with ${sum.passPercentage}% pass rate.` });
    insights.push({ type:'info', title:'Grade Distribution', body: `A: ${sum.gradeCounts.A} · B: ${sum.gradeCounts.B} · C: ${sum.gradeCounts.C} · D: ${sum.gradeCounts.D} · F: ${sum.gradeCounts.F}` });
    if (sum.gradeCounts.A === manager.count()) insights.push({ type:'success', title:'Class Excellence', body: 'Incredible! All students achieved Grade A!' });
    el.innerHTML = insights.map(ins => `
        <div class="insight-card ${ins.type}">
            <div class="ic-title">${ins.title}</div>
            <div class="ic-body">${ins.body}</div>
        </div>
    `).join('');
}

// ============================================================
// STUDENT TABLE
// ============================================================
function getFilteredStudents() {
    let data = manager.getAll();
    if (tableSearch) data = data.filter(s =>
        s.name.toLowerCase().includes(tableSearch.toLowerCase()) ||
        s.id.toLowerCase().includes(tableSearch.toLowerCase())
    );
    if (tableGradeFilter !== 'all') data = data.filter(s => s.calculateGrade() === tableGradeFilter);
    data.sort((a, b) => {
        let aV, bV;
        switch(tableSortField) {
            case 'avg': aV = a.calculateAverage(); bV = b.calculateAverage(); break;
            case 'name': aV = a.name.toLowerCase(); bV = b.name.toLowerCase(); break;
            case 'id': aV = a.id; bV = b.id; break;
            case 'math': aV = a.grades[0]; bV = b.grades[0]; break;
            case 'science': aV = a.grades[1]; bV = b.grades[1]; break;
            case 'english': aV = a.grades[2]; bV = b.grades[2]; break;
            case 'history': aV = a.grades[3]; bV = b.grades[3]; break;
            default: aV = a.name.toLowerCase(); bV = b.name.toLowerCase();
        }
        if (aV < bV) return tableSortDir === 'asc' ? -1 : 1;
        if (aV > bV) return tableSortDir === 'asc' ? 1 : -1;
        return 0;
    });
    return data;
}

function scorePillClass(score) {
    if (score >= 90) return 'excellent';
    if (score >= 75) return 'good';
    if (score >= 50) return 'average';
    return 'poor';
}

function renderTable() {
    const loading = document.getElementById('table-loading');
    const tbody = document.getElementById('student-tbody');
    const empty = document.getElementById('empty-state-table');
    const recordInfo = document.getElementById('record-info');
    if (!tbody) return;

    loading.style.display = 'flex';
    setTimeout(() => {
        loading.style.display = 'none';
        const data = getFilteredStudents();
        const totalPages = Math.max(1, Math.ceil(data.length / PAGE_SIZE));
        if (currentPage > totalPages) currentPage = totalPages;
        const start = (currentPage - 1) * PAGE_SIZE;
        const pageData = data.slice(start, start + PAGE_SIZE);

        recordInfo.textContent = `Showing ${pageData.length} of ${data.length} records`;

        if (data.length === 0) {
            tbody.innerHTML = '';
            empty.style.display = 'flex';
        } else {
            empty.style.display = 'none';
            tbody.innerHTML = pageData.map(s => {
                const avg = s.calculateAverage();
                const grade = s.calculateGrade();
                const passed = s.isPassed();
                return `
                <tr>
                    <td style="color:var(--text-muted);font-size:0.78rem">${s.id}</td>
                    <td>
                        <div class="student-cell">
                            <div class="student-avatar" style="background:${s.getAvatarColor()}">${s.getInitials()}</div>
                            <div>
                                <div class="student-name">${s.name}</div>
                                <div class="student-id">${s.id}</div>
                            </div>
                        </div>
                    </td>
                    <td><span class="score-pill ${scorePillClass(s.grades[0])}">${s.grades[0]}</span></td>
                    <td><span class="score-pill ${scorePillClass(s.grades[1])}">${s.grades[1]}</span></td>
                    <td><span class="score-pill ${scorePillClass(s.grades[2])}">${s.grades[2]}</span></td>
                    <td><span class="score-pill ${scorePillClass(s.grades[3])}">${s.grades[3]}</span></td>
                    <td><span class="avg-cell">${avg}%</span></td>
                    <td><span class="grade-badge ${grade}">${grade}</span></td>
                    <td><span class="status-pill ${passed ? 'pass' : 'fail'}">${passed ? 'Pass' : 'Fail'}</span></td>
                    <td>
                        <div class="action-btns">
                            <button class="action-btn" onclick="openDetails('${s.id}')" title="View Details">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
                            </button>
                            <button class="action-btn" onclick="openEdit('${s.id}')" title="Edit">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                            </button>
                            <button class="action-btn danger" onclick="confirmDelete('${s.id}','${s.name.replace(/'/g,"\\'")}' )" title="Delete">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>
                            </button>
                        </div>
                    </td>
                </tr>`;
            }).join('');
        }
        renderPagination(data.length);
    }, 150);
}

function renderPagination(total) {
    const pg = document.getElementById('pagination');
    if (!pg) return;
    const totalPages = Math.max(1, Math.ceil(total / PAGE_SIZE));
    let html = '';
    html += `<button class="page-btn" onclick="changePage(${currentPage-1})" ${currentPage<=1?'disabled':''}>← Prev</button>`;
    for (let i = 1; i <= totalPages; i++) {
        html += `<button class="page-btn ${i===currentPage?'active':''}" onclick="changePage(${i})">${i}</button>`;
    }
    html += `<button class="page-btn" onclick="changePage(${currentPage+1})" ${currentPage>=totalPages?'disabled':''}>Next →</button>`;
    pg.innerHTML = html;
}

window.changePage = (p) => { currentPage = p; renderTable(); };

// Topbar search
function setupTopbarSearch() {
    const input = document.getElementById('topbar-search');
    const results = document.getElementById('search-results');
    if (!input) return;
    input.addEventListener('input', () => {
        const q = input.value.trim();
        if (!q) { results.classList.remove('open'); return; }
        const found = manager.searchStudent(q).slice(0, 6);
        if (found.length === 0) {
            results.innerHTML = '<div class="sr-item" style="justify-content:center;color:var(--text-muted)">No students found</div>';
        } else {
            results.innerHTML = found.map(s => `
                <div class="sr-item" onclick="openDetails('${s.id}');document.getElementById('topbar-search').value='';document.getElementById('search-results').classList.remove('open')">
                    <div class="sr-avatar" style="background:${s.getAvatarColor()}">${s.getInitials()}</div>
                    <div>
                        <div class="sr-name">${s.name}</div>
                        <div class="sr-sub">${s.id} · Avg ${s.calculateAverage()}% · Grade ${s.calculateGrade()}</div>
                    </div>
                </div>
            `).join('');
        }
        results.classList.add('open');
    });
    document.addEventListener('click', e => {
        if (!input.contains(e.target) && !results.contains(e.target)) results.classList.remove('open');
    });
}

// ============================================================
// MODALS
// ============================================================

// --- Add/Edit Modal ---
function openAddModal() {
    document.getElementById('modal-title').textContent = 'Add New Student';
    document.getElementById('edit-student-id').value = '';
    ['f-name','f-math','f-science','f-english','f-history','f-remarks'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
    document.getElementById('live-preview').style.display = 'none';
    document.getElementById('modal-add-edit').style.display = 'flex';
}

window.openEdit = function(id) {
    const s = manager.getById(id);
    if (!s) return;
    document.getElementById('modal-title').textContent = 'Edit Student';
    document.getElementById('edit-student-id').value = s.id;
    document.getElementById('f-name').value = s.name;
    document.getElementById('f-math').value = s.grades[0];
    document.getElementById('f-science').value = s.grades[1];
    document.getElementById('f-english').value = s.grades[2];
    document.getElementById('f-history').value = s.grades[3];
    document.getElementById('f-remarks').value = s.remarks || '';
    updateLivePreview();
    document.getElementById('modal-add-edit').style.display = 'flex';
};

function updateLivePreview() {
    const vals = ['f-math','f-science','f-english','f-history'].map(id => parseFloat(document.getElementById(id)?.value || '0'));
    if (vals.some(v => isNaN(v))) return;
    const avg = Math.round((vals.reduce((a,b)=>a+b,0)/4)*10)/10;
    const grade = avg >= 90 ? 'A' : avg >= 75 ? 'B' : avg >= 60 ? 'C' : avg >= 50 ? 'D' : 'F';
    document.getElementById('lp-avg-val').textContent = avg + '%';
    document.getElementById('lp-grade-val').textContent = grade;
    document.getElementById('live-preview').style.display = 'flex';
}

function validateAndSave() {
    let valid = true;
    const name = document.getElementById('f-name').value.trim();
    if (name.length < 2) {
        document.getElementById('f-name').classList.add('invalid');
        document.getElementById('f-name-err').textContent = 'Name must be at least 2 characters';
        valid = false;
    } else {
        document.getElementById('f-name').classList.remove('invalid');
        document.getElementById('f-name-err').textContent = '';
    }
    const gradeFields = [
        {id:'f-math', errId:'f-math-err'},
        {id:'f-science', errId:'f-science-err'},
        {id:'f-english', errId:'f-english-err'},
        {id:'f-history', errId:'f-history-err'},
    ];
    const gradeVals = [];
    for (const {id, errId} of gradeFields) {
        const v = parseFloat(document.getElementById(id).value);
        if (isNaN(v) || v < 0 || v > 100) {
            document.getElementById(id).classList.add('invalid');
            document.getElementById(errId).textContent = 'Must be 0–100';
            valid = false;
        } else {
            document.getElementById(id).classList.remove('invalid');
            document.getElementById(errId).textContent = '';
            gradeVals.push(v);
        }
    }
    if (!valid) return;

    const remarks = document.getElementById('f-remarks').value.trim();
    const editId = document.getElementById('edit-student-id').value;

    if (editId) {
        const s = manager.updateStudent(editId, name, gradeVals, remarks);
        logActivity(`Edited student: ${s.name}`, 'edit');
        showToast(`✏️ Student "${name}" updated successfully!`, 'success');
        addNotification(`Student "${name}" was updated`, 'info');
    } else {
        const s = manager.addStudent(name, gradeVals, remarks);
        logActivity(`Added student: ${s.name} (${s.id})`, 'add');
        showToast(`🎉 Student "${name}" added successfully!`, 'success');
        addNotification(`New student "${name}" added (${s.id})`, 'success');
    }

    document.getElementById('modal-add-edit').style.display = 'none';
    refreshAll();
}

// --- Delete Modal ---
let deleteTargetId = null;
window.confirmDelete = function(id, name) {
    deleteTargetId = id;
    document.getElementById('del-student-name').textContent = name;
    document.getElementById('modal-confirm-delete').style.display = 'flex';
};

function executeDelete() {
    if (!deleteTargetId) return;
    const s = manager.getById(deleteTargetId);
    const name = s ? s.name : 'Student';
    manager.deleteStudent(deleteTargetId);
    deleteTargetId = null;
    document.getElementById('modal-confirm-delete').style.display = 'none';
    logActivity(`Deleted student: ${name}`, 'del');
    showToast(`🗑️ Student "${name}" deleted.`, 'error');
    addNotification(`Student "${name}" was deleted`, 'error');
    refreshAll();
}

// --- Details Modal ---
window.openDetails = function(id) {
    const s = manager.getById(id);
    if (!s) return;
    const avg = s.calculateAverage();
    const grade = s.calculateGrade();
    const passed = s.isPassed();
    const subjectColors = ['#6366f1','#10b981','#f59e0b','#ef4444'];

    const body = document.getElementById('detail-modal-body');
    body.innerHTML = `
        <div class="detail-grid">
            <div class="detail-left">
                <div class="detail-avatar-lg" style="background:${s.getAvatarColor()}">${s.getInitials()}</div>
                <div class="detail-name">${s.name}</div>
                <div class="detail-id">${s.id}</div>
                <span class="grade-badge ${grade}" style="width:auto;padding:0.4rem 1rem;border-radius:99px">${grade} Grade</span>
                <div style="margin-top:0.75rem"><span class="status-pill ${passed?'pass':'fail'}">${passed?'Pass':'Fail'}</span></div>
                <div class="detail-attendance" style="margin-top:1rem;justify-content:center">
                    <span>Attendance:</span>
                    <div class="attend-bar" style="max-width:80px"><div class="attend-fill" style="width:${s.attendance}%"></div></div>
                    <strong>${s.attendance}%</strong>
                </div>
            </div>
            <div class="detail-right">
                <h4>Subject Scores</h4>
                <div class="detail-score-list">
                    ${Student.SUBJECTS.map((subj, i) => `
                        <div class="ds-row">
                            <div class="ds-label">${subj}</div>
                            <div class="ds-bar-wrap"><div class="ds-bar" style="width:${s.grades[i]}%;background:${subjectColors[i]}"></div></div>
                            <span class="score-pill ${scorePillClass(s.grades[i])}">${s.grades[i]}</span>
                        </div>
                    `).join('')}
                </div>
                <div class="detail-meta">
                    <div class="dm-item"><div class="dm-label">Average</div><div class="dm-val" style="color:var(--primary)">${avg}%</div></div>
                    <div class="dm-item"><div class="dm-label">Grade</div><div class="dm-val">${grade}</div></div>
                    <div class="dm-item"><div class="dm-label">Status</div><div class="dm-val ${passed?'pass':'fail'}">${passed?'Passed':'Failed'}</div></div>
                    <div class="dm-item"><div class="dm-label">Highest</div><div class="dm-val" style="color:var(--green)">${Math.max(...s.grades)}</div></div>
                </div>
                <div class="detail-chart-box"><canvas id="detail-chart"></canvas></div>
                ${s.remarks ? `<div class="detail-remarks">💬 <strong>Remarks:</strong> ${s.remarks}</div>` : ''}
            </div>
        </div>
    `;
    document.getElementById('modal-details').style.display = 'flex';

    // Render detail chart
    setTimeout(() => {
        const ctx = document.getElementById('detail-chart');
        if (!ctx) return;
        if (charts['detail']) { charts['detail'].destroy(); delete charts['detail']; }
        charts['detail'] = new Chart(ctx, {
            type: 'radar',
            data: {
                labels: Student.SUBJECTS,
                datasets: [{
                    label: s.name,
                    data: s.grades,
                    backgroundColor: 'rgba(99,102,241,0.15)',
                    borderColor: '#6366f1',
                    borderWidth: 2,
                    pointBackgroundColor: subjectColors,
                    pointRadius: 4,
                }]
            },
            options: {
                responsive: true, maintainAspectRatio: false,
                scales: {
                    r: { beginAtZero: true, max: 100, ticks: { color:'rgba(255,255,255,0.3)', font:{size:10} }, grid: { color:'rgba(255,255,255,0.06)' }, pointLabels: { color:'rgba(255,255,255,0.7)', font:{size:11} } }
                },
                plugins: { legend: { display: false } }
            }
        });
    }, 100);
};

// ============================================================
// ANALYTICS CHARTS
// ============================================================
const CHART_DEFAULTS = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { labels: { color: 'rgba(255,255,255,0.7)', font: { family: 'Outfit' } } } },
    scales: {
        x: { ticks: { color: 'rgba(255,255,255,0.5)' }, grid: { color: 'rgba(255,255,255,0.05)' } },
        y: { ticks: { color: 'rgba(255,255,255,0.5)' }, grid: { color: 'rgba(255,255,255,0.05)' }, beginAtZero: true, max: 100 }
    }
};

function getChartDefaults(overrides = {}) {
    return { ...CHART_DEFAULTS, ...overrides, plugins: { ...CHART_DEFAULTS.plugins, ...(overrides.plugins||{}) } };
}

function destroyChart(key) {
    if (charts[key]) { charts[key].destroy(); delete charts[key]; }
}

function renderCharts() {
    const avgs = manager.subjectAverages();
    const top5 = manager.top5();
    const all = manager.getAll();
    const sum = manager.generateSummary();
    const isDark = !document.body.classList.contains('theme-light');
    const textColor = isDark ? 'rgba(255,255,255,0.6)' : 'rgba(0,0,0,0.6)';
    const gridColor = isDark ? 'rgba(255,255,255,0.05)' : 'rgba(0,0,0,0.06)';

    // 1. Subject-wise Average Bar Chart
    destroyChart('subjectAvg');
    const c1 = document.getElementById('chart-subject-avg');
    if (c1) charts['subjectAvg'] = new Chart(c1, {
        type: 'bar',
        data: {
            labels: Student.SUBJECTS,
            datasets: [{
                label: 'Class Average',
                data: avgs,
                backgroundColor: ['rgba(99,102,241,0.7)','rgba(16,185,129,0.7)','rgba(245,158,11,0.7)','rgba(239,68,68,0.7)'],
                borderColor: ['#6366f1','#10b981','#f59e0b','#ef4444'],
                borderWidth: 2, borderRadius: 8,
            }]
        },
        options: { responsive:true, maintainAspectRatio:false,
            plugins:{ legend:{display:false} },
            scales:{ x:{ticks:{color:textColor},grid:{color:gridColor}}, y:{ticks:{color:textColor},grid:{color:gridColor},beginAtZero:true,max:100} }
        }
    });

    // 2. Grade Distribution Doughnut
    destroyChart('gradeDist');
    const c2 = document.getElementById('chart-grade-dist');
    if (c2) charts['gradeDist'] = new Chart(c2, {
        type: 'doughnut',
        data: {
            labels: ['A','B','C','D','F'],
            datasets: [{ data: [sum.gradeCounts.A, sum.gradeCounts.B, sum.gradeCounts.C, sum.gradeCounts.D, sum.gradeCounts.F],
                backgroundColor: ['rgba(16,185,129,0.8)','rgba(59,130,246,0.8)','rgba(245,158,11,0.8)','rgba(249,115,22,0.8)','rgba(239,68,68,0.8)'],
                borderWidth: 2, borderColor: 'rgba(0,0,0,0.3)', hoverOffset: 6 }]
        },
        options: { responsive:true, maintainAspectRatio:false, cutout:'65%',
            plugins:{ legend:{position:'bottom', labels:{color:textColor, padding:12}} }
        }
    });

    // 3. Performance Trend Line Chart
    destroyChart('perfTrend');
    const c3 = document.getElementById('chart-perf-trend');
    if (c3) {
        const sorted = [...all].sort((a,b)=> new Date(a.createdAt) - new Date(b.createdAt));
        charts['perfTrend'] = new Chart(c3, {
            type: 'line',
            data: {
                labels: sorted.map(s => s.name.split(' ')[0]),
                datasets: [
                    ...Student.SUBJECTS.map((subj, i) => ({
                        label: subj,
                        data: sorted.map(s => s.grades[i]),
                        borderColor: ['#6366f1','#10b981','#f59e0b','#ef4444'][i],
                        backgroundColor: ['rgba(99,102,241,0.1)','rgba(16,185,129,0.1)','rgba(245,158,11,0.1)','rgba(239,68,68,0.1)'][i],
                        tension: 0.4, borderWidth: 2, pointRadius: 4, fill: false,
                    }))
                ]
            },
            options: { responsive:true, maintainAspectRatio:false,
                plugins:{ legend:{position:'top',labels:{color:textColor}} },
                scales:{ x:{ticks:{color:textColor},grid:{color:gridColor}}, y:{ticks:{color:textColor},grid:{color:gridColor},beginAtZero:true,max:100} }
            }
        });
    }

    // 4. Pass vs Fail Pie
    destroyChart('passVsFail');
    const c4 = document.getElementById('chart-pass-fail');
    if (c4) charts['passVsFail'] = new Chart(c4, {
        type: 'pie',
        data: {
            labels: ['Pass', 'Fail'],
            datasets: [{ data: [manager.passCount(), manager.failCount()],
                backgroundColor: ['rgba(16,185,129,0.8)','rgba(239,68,68,0.8)'],
                borderWidth: 2, borderColor: 'rgba(0,0,0,0.3)', hoverOffset: 6 }]
        },
        options: { responsive:true, maintainAspectRatio:false,
            plugins:{ legend:{position:'bottom',labels:{color:textColor}} }
        }
    });

    // 5. Top 5 Students Horizontal Bar
    destroyChart('top5');
    const c5 = document.getElementById('chart-top5');
    if (c5) charts['top5'] = new Chart(c5, {
        type: 'bar',
        data: {
            labels: top5.map(s => s.name.split(' ')[0]),
            datasets: [{
                label: 'Average Score',
                data: top5.map(s => s.calculateAverage()),
                backgroundColor: top5.map((_,i) => [`#6366f1`,`#8b5cf6`,`#3b82f6`,`#10b981`,`#f59e0b`][i]),
                borderRadius: 6, borderWidth: 0,
            }]
        },
        options: { responsive:true, maintainAspectRatio:false, indexAxis:'y',
            plugins:{ legend:{display:false} },
            scales:{ x:{ticks:{color:textColor},grid:{color:gridColor},beginAtZero:true,max:100}, y:{ticks:{color:textColor},grid:{color:gridColor}} }
        }
    });

    // 6. Radar Subject Comparison
    destroyChart('radar');
    const c6 = document.getElementById('chart-radar');
    if (c6) {
        const rData = all.slice(0, 5);
        const colors = ['#6366f1','#10b981','#f59e0b','#ef4444','#3b82f6'];
        charts['radar'] = new Chart(c6, {
            type: 'radar',
            data: {
                labels: Student.SUBJECTS,
                datasets: rData.map((s,i) => ({
                    label: s.name.split(' ')[0],
                    data: s.grades,
                    borderColor: colors[i],
                    backgroundColor: colors[i] + '22',
                    borderWidth: 2, pointRadius: 3,
                }))
            },
            options: { responsive:true, maintainAspectRatio:false,
                plugins:{ legend:{position:'bottom',labels:{color:textColor,padding:10}} },
                scales:{ r:{beginAtZero:true,max:100,ticks:{color:textColor,font:{size:9}},grid:{color:gridColor},pointLabels:{color:textColor}} }
            }
        });
    }
}

// ============================================================
// REPORTS
// ============================================================
function exportCSV() {
    if (manager.count() === 0) { showToast('No students to export.', 'error'); return; }
    const csv = manager.exportCSV();
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = 'gradestream_export.csv'; a.click();
    URL.revokeObjectURL(url);
    showToast('📥 CSV exported successfully!', 'success');
    logActivity('Exported student data as CSV', 'info');
}

function exportPDF() {
    if (manager.count() === 0) { showToast('No students to export.', 'error'); return; }
    try {
        const { jsPDF } = window.jspdf;
        const doc = new jsPDF();
        doc.setFontSize(20);
        doc.setTextColor(40, 40, 40);
        doc.text('GradeStream - Student Report', 20, 25);
        doc.setFontSize(11);
        doc.setTextColor(100, 100, 100);
        doc.text(`Generated: ${new Date().toLocaleString()}`, 20, 35);
        doc.setFontSize(10);
        doc.text(`Total Students: ${manager.count()} | Class Average: ${manager.calculateClassAverage()}% | Pass Rate: ${manager.passPercentage()}%`, 20, 44);

        let y = 58;
        const headers = ['ID', 'Name', 'Math', 'Sci', 'Eng', 'His', 'Avg', 'Grade', 'Status'];
        const colX = [15, 35, 90, 103, 116, 129, 142, 157, 170];
        doc.setFontSize(8);
        doc.setTextColor(80,80,80);
        headers.forEach((h,i) => doc.text(h, colX[i], y));
        y += 6; doc.setDrawColor(200,200,200); doc.line(15, y, 195, y); y += 4;

        doc.setTextColor(40,40,40);
        manager.getAll().forEach(s => {
            if (y > 270) { doc.addPage(); y = 20; }
            const row = [s.id, s.name.substring(0,20), s.grades[0], s.grades[1], s.grades[2], s.grades[3], s.calculateAverage()+'%', s.calculateGrade(), s.isPassed()?'Pass':'Fail'];
            row.forEach((v,i) => doc.text(String(v), colX[i], y));
            y += 7;
        });

        doc.save('gradestream_report.pdf');
        showToast('📄 PDF exported successfully!', 'success');
        logActivity('Exported PDF report', 'info');
    } catch(e) {
        showToast('PDF library not available. Try exporting CSV instead.', 'error');
    }
}

function printReport() {
    const students = manager.getAll();
    const sum = manager.generateSummary();
    const win = window.open('', '_blank');
    win.document.write(`<!DOCTYPE html><html><head>
        <title>GradeStream Report</title>
        <style>body{font-family:Arial,sans-serif;padding:2rem;color:#111}h1{color:#4f46e5;margin-bottom:0.5rem}table{width:100%;border-collapse:collapse;margin-top:1.5rem}th{background:#4f46e5;color:white;padding:0.6rem;text-align:left;font-size:0.85rem}td{padding:0.55rem;border-bottom:1px solid #e5e7eb;font-size:0.82rem}.summary{display:grid;grid-template-columns:repeat(4,1fr);gap:1rem;margin:1rem 0}.sc{background:#f9fafb;border:1px solid #e5e7eb;border-radius:8px;padding:0.75rem}.sc h3{font-size:0.7rem;color:#6b7280;text-transform:uppercase;margin-bottom:0.3rem}.sc p{font-size:1.3rem;font-weight:700;color:#111}</style>
    </head><body>
        <h1>GradeStream · Student Grade Report</h1>
        <p>Generated: ${new Date().toLocaleString()} | Total Students: ${manager.count()}</p>
        <div class="summary">
            <div class="sc"><h3>Class Average</h3><p>${sum.classAverage||'N/A'}%</p></div>
            <div class="sc"><h3>Pass Rate</h3><p>${sum.passPercentage||'N/A'}%</p></div>
            <div class="sc"><h3>Top Performer</h3><p>${sum.highestPerformer?.name||'N/A'}</p></div>
            <div class="sc"><h3>Best Subject</h3><p>${sum.bestSubject||'N/A'}</p></div>
        </div>
        <table><thead><tr><th>ID</th><th>Name</th><th>Math</th><th>Science</th><th>English</th><th>History</th><th>Average</th><th>Grade</th><th>Status</th></tr></thead>
        <tbody>${students.map(s=>`<tr><td>${s.id}</td><td>${s.name}</td><td>${s.grades[0]}</td><td>${s.grades[1]}</td><td>${s.grades[2]}</td><td>${s.grades[3]}</td><td>${s.calculateAverage()}%</td><td>${s.calculateGrade()}</td><td>${s.isPassed()?'Pass':'Fail'}</td></tr>`).join('')}</tbody></table>
    </body></html>`);
    win.print();
    showToast('🖨️ Print dialog opened!', 'success');
    logActivity('Printed student report', 'info');
}

function generateReportCards() {
    if (manager.count() === 0) { showToast('No students to generate cards for.', 'error'); return; }
    const students = manager.getAll();
    const win = window.open('', '_blank');
    win.document.write(`<!DOCTYPE html><html><head><title>Report Cards</title>
        <style>body{font-family:Arial,sans-serif;padding:1rem;background:#f3f4f6}
        .card{background:white;border-radius:12px;padding:1.5rem;margin-bottom:1.5rem;page-break-inside:avoid;box-shadow:0 2px 8px rgba(0,0,0,0.08);max-width:600px;margin-left:auto;margin-right:auto}
        h2{color:#4f46e5;margin-bottom:0.25rem;font-size:1.2rem}.sid{color:#6b7280;font-size:0.8rem;margin-bottom:1rem}
        table{width:100%;border-collapse:collapse}td,th{padding:0.5rem 0.75rem;text-align:left;font-size:0.85rem}th{background:#f9fafb;color:#374151}
        .grade-A{color:#059669;font-weight:700}.grade-B{color:#2563eb;font-weight:700}.grade-C{color:#d97706;font-weight:700}.grade-D{color:#ea580c;font-weight:700}.grade-F{color:#dc2626;font-weight:700}
        .footer{margin-top:1rem;padding-top:0.75rem;border-top:1px solid #e5e7eb;font-size:0.78rem;color:#9ca3af}</style>
    </head><body>
        ${students.map(s=>`
            <div class="card">
                <h2>${s.name}</h2><div class="sid">Student ID: ${s.id}</div>
                <table><thead><tr><th>Subject</th><th>Score</th><th>Grade</th></tr></thead>
                <tbody>${Student.SUBJECTS.map((subj,i)=>{
                    const g = s.grades[i]>=90?'A':s.grades[i]>=75?'B':s.grades[i]>=60?'C':s.grades[i]>=50?'D':'F';
                    return `<tr><td>${subj}</td><td>${s.grades[i]}/100</td><td class="grade-${g}">${g}</td></tr>`;
                }).join('')}</tbody></table>
                <div style="margin-top:0.75rem;display:flex;gap:1.5rem;font-size:0.88rem">
                    <span><strong>Average:</strong> ${s.calculateAverage()}%</span>
                    <span><strong>Grade:</strong> <span class="grade-${s.calculateGrade()}">${s.calculateGrade()}</span></span>
                    <span><strong>Status:</strong> ${s.isPassed()?'✅ Pass':'❌ Fail'}</span>
                </div>
                ${s.remarks?`<div style="margin-top:0.75rem;font-size:0.82rem;color:#4b5563">💬 ${s.remarks}</div>`:''}
                <div class="footer">GradeStream Admin Panel · ${new Date().toLocaleDateString()}</div>
            </div>
        `).join('')}
    </body></html>`);
    win.print();
    showToast('📋 Report cards generated!', 'success');
    logActivity('Generated individual report cards', 'info');
}

function renderReportPreview() {
    const el = document.getElementById('report-preview-content');
    if (!el) return;
    const students = manager.getAll().slice(0, 10);
    if (students.length === 0) { el.innerHTML = '<p style="color:var(--text-muted);text-align:center;padding:2rem">No students yet.</p>'; return; }
    const sum = manager.generateSummary();
    el.innerHTML = `
        <div style="display:grid;grid-template-columns:repeat(4,1fr);gap:1rem;margin-bottom:1rem">
            ${[['Total Students',manager.count()],['Class Avg',sum.classAverage+'%'],['Pass Rate',sum.passPercentage+'%'],['Best Subject',sum.bestSubject]].map(([l,v])=>`<div style="background:var(--hover);border-radius:var(--radius-sm);padding:0.75rem"><div style="font-size:0.7rem;color:var(--text-muted);text-transform:uppercase">${l}</div><div style="font-size:1.1rem;font-weight:700;color:var(--primary)">${v}</div></div>`).join('')}
        </div>
        <table class="report-table-mini">
            <thead><tr><th>ID</th><th>Name</th><th>Math</th><th>Sci</th><th>Eng</th><th>His</th><th>Avg</th><th>Grade</th><th>Status</th></tr></thead>
            <tbody>${students.map(s=>`<tr><td>${s.id}</td><td>${s.name}</td><td>${s.grades[0]}</td><td>${s.grades[1]}</td><td>${s.grades[2]}</td><td>${s.grades[3]}</td><td><strong>${s.calculateAverage()}%</strong></td><td><span class="grade-badge ${s.calculateGrade()}">${s.calculateGrade()}</span></td><td><span class="status-pill ${s.isPassed()?'pass':'fail'}">${s.isPassed()?'Pass':'Fail'}</span></td></tr>`).join('')}</tbody>
        </table>
        ${manager.count() > 10 ? `<p style="color:var(--text-muted);font-size:0.78rem;margin-top:0.75rem">Showing 10 of ${manager.count()} students. Export for full data.</p>` : ''}
    `;
}

// ============================================================
// SIDEBAR & NAVIGATION
// ============================================================
function setupSidebar() {
    const sidebar = document.getElementById('sidebar');
    const mainLayout = document.getElementById('main-layout');
    const toggleBtn = document.getElementById('sidebar-toggle');
    const menuBtn = document.getElementById('topbar-menu-btn');

    const collapsed = localStorage.getItem('gs_sidebar') === 'collapsed';
    if (collapsed) { sidebar.classList.add('collapsed'); mainLayout.classList.add('sidebar-collapsed'); }

    function toggle() {
        const isCollapsed = sidebar.classList.toggle('collapsed');
        mainLayout.classList.toggle('sidebar-collapsed', isCollapsed);
        localStorage.setItem('gs_sidebar', isCollapsed ? 'collapsed' : 'expanded');
        // On mobile, add mobile-open class instead
        if (window.innerWidth <= 768) {
            sidebar.classList.toggle('mobile-open');
            sidebar.classList.remove('collapsed');
            mainLayout.classList.remove('sidebar-collapsed');
        }
    }

    toggleBtn?.addEventListener('click', toggle);
    menuBtn?.addEventListener('click', () => {
        sidebar.classList.toggle('mobile-open');
    });

    // Close sidebar on outside click (mobile)
    document.addEventListener('click', e => {
        if (window.innerWidth <= 768 && !sidebar.contains(e.target) && !menuBtn.contains(e.target)) {
            sidebar.classList.remove('mobile-open');
        }
    });
}

// ============================================================
// VIEW ROUTING
// ============================================================
function showView(viewName) {
    document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    const view = document.getElementById(`view-${viewName}`);
    if (view) { view.classList.add('active'); }
    document.querySelectorAll(`[data-view="${viewName}"]`).forEach(el => el.classList.add('active'));

    if (viewName === 'analytics') renderCharts();
    if (viewName === 'reports') renderReportPreview();

    // Close profile dropdown if open
    document.getElementById('profile-dropdown')?.classList.remove('open');
}

function setupNavigation() {
    document.querySelectorAll('[data-view]').forEach(el => {
        el.addEventListener('click', e => {
            e.preventDefault();
            const view = el.dataset.view;
            if (view) showView(view);
        });
    });
}

// ============================================================
// THEME TOGGLE
// ============================================================
function setupTheme() {
    const saved = localStorage.getItem('gs_theme') || 'theme-dark';
    applyTheme(saved);

    document.getElementById('theme-toggle')?.addEventListener('click', () => {
        const cur = document.body.className.match(/theme-\w+/)?.[0] || 'theme-dark';
        const next = cur === 'theme-dark' ? 'theme-light' : 'theme-dark';
        applyTheme(next);
    });

    document.getElementById('theme-setting')?.addEventListener('change', e => {
        applyTheme(e.target.value);
    });
}

function applyTheme(theme) {
    document.body.className = document.body.className.replace(/theme-\w+/g, '').trim();
    document.body.classList.add(theme);
    localStorage.setItem('gs_theme', theme);
    const sel = document.getElementById('theme-setting');
    if (sel) sel.value = theme;
}

// ============================================================
// DROPDOWN PANELS
// ============================================================
function setupDropdowns() {
    const notifBtn = document.getElementById('notif-btn');
    const notifPanel = document.getElementById('notif-panel');
    const profileBtn = document.getElementById('profile-btn');
    const profileDD = document.getElementById('profile-dropdown');

    notifBtn?.addEventListener('click', e => {
        e.stopPropagation();
        profileDD?.classList.remove('open');
        notifPanel?.classList.toggle('open');
    });
    profileBtn?.addEventListener('click', e => {
        e.stopPropagation();
        notifPanel?.classList.remove('open');
        profileDD?.classList.toggle('open');
    });
    document.addEventListener('click', () => {
        notifPanel?.classList.remove('open');
        profileDD?.classList.remove('open');
    });
    document.getElementById('clear-notifs')?.addEventListener('click', () => {
        notifications = [];
        localStorage.removeItem('gs_notifs');
        renderNotifications();
    });
}

// ============================================================
// SETTINGS PAGE
// ============================================================
function setupSettings() {
    document.getElementById('btn-reset-demo')?.addEventListener('click', () => {
        if (!confirm('Reset all data to demo students?')) return;
        manager.resetToDemo();
        activityLog = [];
        localStorage.removeItem('gs_activity');
        logActivity('Reset to demo data', 'info');
        showToast('🔄 Demo data restored!', 'success');
        refreshAll();
    });
    document.getElementById('btn-clear-all')?.addEventListener('click', () => {
        if (!confirm('Delete ALL student records? This cannot be undone!')) return;
        manager.clearAll();
        logActivity('Cleared all student data', 'del');
        showToast('🗑️ All data cleared.', 'error');
        refreshAll();
    });
    document.getElementById('sidebar-setting')?.addEventListener('change', e => {
        const sidebar = document.getElementById('sidebar');
        const ml = document.getElementById('main-layout');
        if (e.target.value === 'collapsed') {
            sidebar.classList.add('collapsed'); ml.classList.add('sidebar-collapsed');
            localStorage.setItem('gs_sidebar','collapsed');
        } else {
            sidebar.classList.remove('collapsed'); ml.classList.remove('sidebar-collapsed');
            localStorage.setItem('gs_sidebar','expanded');
        }
    });
}

// ============================================================
// STUDENT TABLE EVENTS
// ============================================================
function setupTableEvents() {
    document.getElementById('student-search')?.addEventListener('input', e => {
        tableSearch = e.target.value.trim();
        currentPage = 1;
        renderTable();
    });
    document.getElementById('grade-filter')?.addEventListener('change', e => {
        tableGradeFilter = e.target.value;
        currentPage = 1;
        renderTable();
    });
    document.getElementById('sort-select')?.addEventListener('change', e => {
        const val = e.target.value;
        if (val === 'avg-desc') { tableSortField = 'avg'; tableSortDir = 'desc'; }
        else if (val === 'avg-asc') { tableSortField = 'avg'; tableSortDir = 'asc'; }
        else { tableSortField = val; tableSortDir = 'asc'; }
        currentPage = 1;
        renderTable();
    });
    document.querySelectorAll('th.sortable').forEach(th => {
        th.addEventListener('click', () => {
            const field = th.dataset.sort;
            if (tableSortField === field) { tableSortDir = tableSortDir === 'asc' ? 'desc' : 'asc'; }
            else { tableSortField = field; tableSortDir = 'asc'; }
            currentPage = 1;
            renderTable();
        });
    });
}

// ============================================================
// MODAL EVENTS
// ============================================================
function setupModalEvents() {
    // Add/Edit Modal
    document.getElementById('btn-add-student-view')?.addEventListener('click', openAddModal);
    document.getElementById('qa-add')?.addEventListener('click', openAddModal);
    document.getElementById('modal-close-add')?.addEventListener('click', () => document.getElementById('modal-add-edit').style.display = 'none');
    document.getElementById('modal-cancel-add')?.addEventListener('click', () => document.getElementById('modal-add-edit').style.display = 'none');
    document.getElementById('modal-save-student')?.addEventListener('click', validateAndSave);

    // Live preview on grade input
    ['f-math','f-science','f-english','f-history'].forEach(id => {
        document.getElementById(id)?.addEventListener('input', updateLivePreview);
    });

    // Delete Modal
    document.getElementById('modal-close-del')?.addEventListener('click', () => document.getElementById('modal-confirm-delete').style.display = 'none');
    document.getElementById('del-cancel')?.addEventListener('click', () => document.getElementById('modal-confirm-delete').style.display = 'none');
    document.getElementById('del-confirm')?.addEventListener('click', executeDelete);

    // Details Modal
    document.getElementById('modal-close-details')?.addEventListener('click', () => document.getElementById('modal-details').style.display = 'none');

    // Close modals on overlay click
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', e => {
            if (e.target === overlay) overlay.style.display = 'none';
        });
    });

    // Keyboard ESC
    document.addEventListener('keydown', e => {
        if (e.key === 'Escape') document.querySelectorAll('.modal-overlay').forEach(o => o.style.display = 'none');
    });
}

// ============================================================
// REPORT EVENTS
// ============================================================
function setupReportEvents() {
    document.getElementById('btn-export-csv-rpt')?.addEventListener('click', exportCSV);
    document.getElementById('qa-export')?.addEventListener('click', exportCSV);
    document.getElementById('btn-export-pdf')?.addEventListener('click', exportPDF);
    document.getElementById('btn-print')?.addEventListener('click', printReport);
    document.getElementById('btn-report-cards')?.addEventListener('click', generateReportCards);
    document.getElementById('qa-report')?.addEventListener('click', () => { showView('reports'); renderReportPreview(); });
    document.getElementById('refresh-preview')?.addEventListener('click', renderReportPreview);
}

// ============================================================
// LOGOUT
// ============================================================
function setupLogout() {
    const logout = () => { sessionStorage.removeItem('gs_auth'); window.location.href = 'login.html'; };
    document.getElementById('btn-logout')?.addEventListener('click', logout);
    document.getElementById('logout-dd')?.addEventListener('click', logout);
}

// ============================================================
// REFRESH ALL
// ============================================================
function refreshAll() {
    updateStats();
    renderTable();
    renderActivity();
    renderNotifications();
    const activeView = document.querySelector('.view.active')?.id?.replace('view-','');
    if (activeView === 'analytics') renderCharts();
    if (activeView === 'reports') renderReportPreview();
}

// ============================================================
// INIT
// ============================================================
function init() {
    if (!authGuard()) return;

    // Load demo data if first time
    if (!localStorage.getItem('gs_students') || JSON.parse(localStorage.getItem('gs_students')).length === 0) {
        manager.resetToDemo();
        logActivity('Sample demo data loaded on first launch', 'info');
    }

    setupSidebar();
    setupNavigation();
    setupTheme();
    setupDropdowns();
    setupTableEvents();
    setupModalEvents();
    setupReportEvents();
    setupSettings();
    setupTopbarSearch();
    setupLogout();

    updateDatetime();
    setInterval(updateDatetime, 1000);

    refreshAll();

    // Welcome toast
    setTimeout(() => {
        showToast('👋 Welcome to GradeStream Admin Dashboard!', 'info');
    }, 800);
}

document.addEventListener('DOMContentLoaded', init);
