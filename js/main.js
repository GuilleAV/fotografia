
// main.js - shared behavior across pages (guarded by element presence)

// Carousel (index.html)
let currentSlide = 0;
let autoSlideInterval;
const slides = document.querySelectorAll && document.querySelectorAll('.carousel-slide') || [];

function showSlide(index) {
  if (!slides.length) return;
  slides.forEach(s => s.classList.remove('active'));
  currentSlide = (index + slides.length) % slides.length;
  slides[currentSlide].classList.add('active');
}

function changeSlide(direction) { showSlide(currentSlide + direction); }

function startAutoSlide() {
  if (!slides.length) return;
  autoSlideInterval = setInterval(() => changeSlide(1), 3000);
}

function stopAutoSlide() {
  if (autoSlideInterval) clearInterval(autoSlideInterval);
}

// Menu toggle (all pages)
function toggleMenu() {
  const nav = document.getElementById('navLinks');
  if (nav) nav.classList.toggle('active');
}

// Set current year in footers
function setCurrentYear() {
  const yearEls = document.querySelectorAll('#currentYear, .year');
  yearEls.forEach(el => el.textContent = new Date().getFullYear());
}

// Categorias filtering (categorias.html)
function filterCategory(category, btn) {
  const items = document.querySelectorAll && document.querySelectorAll('#categoryGallery .gallery-item') || [];
  const buttons = document.querySelectorAll && document.querySelectorAll('.category-btn') || [];
  buttons.forEach(b => b.classList.remove('active'));
  if (btn) btn.classList.add('active');
  items.forEach(item => {
    if (category === 'todas' || item.dataset.category === category) item.style.display = 'block';
    else item.style.display = 'none';
  });
}

// Login simulation (login.html)
function handleLogin(e) {
  e.preventDefault();
  const username = document.getElementById('username') && document.getElementById('username').value;
  const password = document.getElementById('password') && document.getElementById('password').value;
  // very simple simulated check; in real app replace with backend auth
  if (username === 'admin' && password === 'admin') {
    window.location.href = 'admin.html';
    return true;
  } else {
    const err = document.getElementById('loginError');
    if (err) err.style.display = 'block';
    return false;
  }
}

// Admin panel behavior (admin.html)
function showAdminSection(sectionId) {
  const menuItems = document.querySelectorAll('.admin-menu li');
  menuItems.forEach(item => item.classList.remove('active'));
  // mark clicked - find by inner text matching or event target
  try {
    // event may be undefined if called manually; use this trick to find matching li
    const match = Array.from(menuItems).find(li => li.getAttribute('onclick') && li.getAttribute('onclick').includes(sectionId));
    if (match) match.classList.add('active');
  } catch(e){}
  document.querySelectorAll('.admin-section').forEach(s => s.classList.remove('active'));
  const map = {dashboard: 'dashboardSection', upload: 'uploadSection', list: 'listSection', categories: 'categoriesSection' };
  const target = document.getElementById(map[sectionId]);
  if (target) target.classList.add('active');
}

function logout() { window.location.href = 'index.html'; }

// File drop / preview for admin upload
let selectedFiles = [];
const dropArea = document.getElementById('dropArea');
const fileInput = document.getElementById('photoFile');
const previewContainer = document.getElementById('previewContainer');

if (dropArea) {
  dropArea.addEventListener('click', () => fileInput && fileInput.click());
  dropArea.addEventListener('dragover', (e) => { e.preventDefault(); dropArea.classList.add('drag-over'); });
  dropArea.addEventListener('dragleave', () => dropArea.classList.remove('drag-over'));
  dropArea.addEventListener('drop', (e) => { e.preventDefault(); dropArea.classList.remove('drag-over'); handleFiles(e.dataTransfer.files); });
}

if (fileInput) fileInput.addEventListener('change', (e) => handleFiles(e.target.files));

function handleFiles(files) {
  selectedFiles = Array.from(files || []);
  if (!previewContainer) return;
  previewContainer.innerHTML = '';
  selectedFiles.forEach((file, index) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const div = document.createElement('div');
      div.className = 'preview-item';
      div.innerHTML = `<img src="${e.target.result}" alt="Preview"><button class="preview-remove" onclick="removeFile(${index})"><i class="fas fa-times"></i></button>`;
      previewContainer.appendChild(div);
    };
    reader.readAsDataURL(file);
  });
}

function removeFile(index) {
  selectedFiles.splice(index, 1);
  if (fileInput) {
    const dt = new DataTransfer();
    selectedFiles.forEach(f => dt.items.add(f));
    fileInput.files = dt.files;
  }
  handleFiles(selectedFiles);
}

const uploadForm = document.getElementById('uploadPhotoForm');
if (uploadForm) {
  uploadForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const successMsg = document.getElementById('successMsg');
    if (successMsg) { successMsg.classList.add('show'); setTimeout(()=> successMsg.classList.remove('show'), 3000); }
    clearForm();
  });
}

function clearForm() {
  const form = document.getElementById('uploadPhotoForm');
  if (form) form.reset();
  if (previewContainer) previewContainer.innerHTML = '';
  selectedFiles = [];
}

// View toggle & search (admin list)
function toggleView(view) {
  const buttons = document.querySelectorAll('.view-btn');
  buttons.forEach(btn => btn.classList.remove('active'));
  // find caller via stack - fallback: just set first
  try {
    // attempt to highlight the clicked button based on event
    if (event && event.target) {
      const btn = event.target.closest('.view-btn');
      if (btn) btn.classList.add('active');
    } else if (buttons[0]) buttons[0].classList.add('active');
  } catch(e){ if (buttons[0]) buttons[0].classList.add('active'); }
}

function searchPhotos(query) {
  const cards = document.querySelectorAll('.photo-card');
  query = (query || '').toLowerCase();
  cards.forEach(card => {
    const title = (card.querySelector('h4') && card.querySelector('h4').textContent || '').toLowerCase();
    const category = (card.querySelector('p') && card.querySelector('p').textContent || '').toLowerCase();
    if (title.includes(query) || category.includes(query)) card.style.display = 'block'; else card.style.display = 'none';
  });
}

// Initialize on load
window.addEventListener('load', () => {
  setCurrentYear();
  if (slides.length) startAutoSlide();
});
