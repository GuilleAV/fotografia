import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <header class="site-header">
      <nav class="nav-container">
        <a routerLink="/" class="logo" (click)="menuOpen = false">
          <span class="logo-icon">📷</span>
          <span class="logo-text">FotoPortfolio</span>
        </a>

        <button class="menu-toggle" (click)="menuOpen = !menuOpen" aria-label="Menú">
          <span></span><span></span><span></span>
        </button>

        <ul class="nav-links" [class.open]="menuOpen">
          <li><a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}" (click)="menuOpen = false">Inicio</a></li>
          <li><a routerLink="/galeria" routerLinkActive="active" (click)="menuOpen = false">Galería</a></li>

          @if (auth.isLoggedIn()) {
            // Visible para TODOS los logueados (Admin y Fotógrafos)
            <li><a routerLink="/dashboard" routerLinkActive="active" (click)="menuOpen = false">Subir Fotos</a></li>
            
            @if (auth.isAdmin()) {
              <li><a routerLink="/admin" routerLinkActive="active" (click)="menuOpen = false">Gestión de Fotos</a></li>
              <li><a routerLink="/admin/categorias" routerLinkActive="active" (click)="menuOpen = false">Categorías</a></li>
              <li><a routerLink="/admin/usuarios" routerLinkActive="active" (click)="menuOpen = false">Usuarios</a></li>
            }
            <li class="user-menu">
              <span class="user-name">{{ auth.user()?.nombreCompleto }}</span>
              <button class="btn-logout" (click)="auth.logout()">
                Salir
              </button>
            </li>
          } @else {
            <li><a routerLink="/login" routerLinkActive="active" class="btn-login" (click)="menuOpen = false">Iniciar Sesión</a></li>
          }
        </ul>
      </nav>
    </header>

    @if (menuOpen) {
      <div class="overlay" (click)="menuOpen = false"></div>
    }
  `,
  styles: [`
    /* === MOBILE FIRST (base: celular) === */
    .site-header {
      background: #1a1a2e;
      padding: 0 1rem;
      position: sticky;
      top: 0;
      z-index: 1000;
      box-shadow: 0 2px 10px rgba(0,0,0,0.3);
    }
    .nav-container {
      max-width: 1200px;
      margin: 0 auto;
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: 60px;
    }
    .logo {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      text-decoration: none;
      color: #fff;
      font-size: 1.2rem;
      font-weight: 700;
    }
    .logo-icon { font-size: 1.4rem; }
    .logo-text { display: none; }

    /* Hamburguesa siempre visible en móvil */
    .menu-toggle {
      display: flex;
      flex-direction: column;
      gap: 5px;
      background: none;
      border: none;
      cursor: pointer;
      padding: 0.5rem;
      z-index: 1001;
    }
    .menu-toggle span {
      width: 24px;
      height: 2px;
      background: #fff;
      transition: 0.3s;
      border-radius: 2px;
    }

    /* Menú móvil: overlay lateral */
    .nav-links {
      display: none;
      position: fixed;
      top: 0;
      right: 0;
      bottom: 0;
      width: 260px;
      background: #1a1a2e;
      flex-direction: column;
      padding: 5rem 1.5rem 2rem;
      gap: 0.25rem;
      z-index: 1001;
      box-shadow: -4px 0 20px rgba(0,0,0,0.5);
      overflow-y: auto;
    }
    .nav-links.open { display: flex; }
    .nav-links a {
      color: #ccc;
      text-decoration: none;
      padding: 0.85rem 1rem;
      border-radius: 8px;
      transition: all 0.2s;
      font-weight: 500;
      font-size: 1rem;
      display: block;
    }
    .nav-links a:hover, .nav-links a.active {
      color: #fff;
      background: rgba(255,255,255,0.1);
    }
    .btn-login {
      background: #e94560 !important;
      color: #fff !important;
      text-align: center;
      margin-top: 0.5rem;
    }
    .user-menu {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
      padding-top: 1rem;
      margin-top: 0.5rem;
      border-top: 1px solid rgba(255,255,255,0.1);
    }
    .user-name {
      color: #aaa;
      font-size: 0.85rem;
    }
    .btn-logout {
      background: transparent;
      border: 1px solid #e94560;
      color: #e94560;
      padding: 0.6rem 1rem;
      border-radius: 8px;
      cursor: pointer;
      font-size: 0.9rem;
      font-weight: 600;
      transition: all 0.2s;
      width: 100%;
    }
    .btn-logout:hover {
      background: #e94560;
      color: #fff;
    }
    .overlay {
      position: fixed;
      inset: 0;
      background: rgba(0,0,0,0.5);
      z-index: 1000;
    }

    /* === TABLET (min-width: 600px) === */
    @media (min-width: 600px) {
      .logo-text { display: inline; }
      .site-header { padding: 0 1.5rem; }
    }

    /* === ESCRITORIO (min-width: 768px) === */
    @media (min-width: 768px) {
      .menu-toggle { display: none; }
      .nav-links {
        display: flex !important;
        position: static;
        width: auto;
        background: transparent;
        flex-direction: row;
        padding: 0;
        gap: 0.5rem;
        box-shadow: none;
        align-items: center;
      }
      .nav-links a {
        padding: 0.5rem 0.8rem;
        font-size: 0.9rem;
      }
      .btn-login {
        margin-top: 0;
      }
      .user-menu {
        flex-direction: row;
        padding-top: 0;
        margin-top: 0;
        border-top: none;
        align-items: center;
      }
      .btn-logout {
        width: auto;
        padding: 0.4rem 0.8rem;
        font-size: 0.8rem;
      }
      .overlay { display: none; }
    }
  `]
})
export class HeaderComponent {
  auth = inject(AuthService);
  menuOpen = false;
}
