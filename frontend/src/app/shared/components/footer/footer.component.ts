import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  template: `
    <footer class="site-footer">
      <div class="footer-content">
        <div class="footer-section">
          <h3>📷 FotoPortfolio</h3>
          <p>Plataforma para fotógrafos profesionales</p>
        </div>
        <div class="footer-section">
          <h4>Contacto</h4>
          <p>📧 contacto&#64;fotoportfolio.com</p>
          <p>📱 +54 9 11 1234-5678</p>
        </div>
        <div class="footer-section">
          <h4>Redes</h4>
          <div class="social-links">
            <a href="#" aria-label="Instagram">📸</a>
            <a href="#" aria-label="Facebook">👤</a>
            <a href="#" aria-label="Twitter">🐦</a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; {{ currentYear }} FotoPortfolio. Todos los derechos reservados.</p>
      </div>
    </footer>
  `,
  styles: [`
    /* === MOBILE FIRST === */
    .site-footer {
      background: #1a1a2e;
      color: #aaa;
      padding: 2rem 1rem 1rem;
      margin-top: auto;
    }
    .footer-content {
      max-width: 1200px;
      margin: 0 auto;
      display: grid;
      grid-template-columns: 1fr;
      gap: 1.5rem;
    }
    .footer-section h3, .footer-section h4 {
      color: #fff;
      margin: 0 0 0.5rem;
      font-size: 1rem;
    }
    .footer-section p {
      margin: 0.25rem 0;
      font-size: 0.85rem;
    }
    .social-links {
      display: flex;
      gap: 1rem;
    }
    .social-links a {
      font-size: 1.5rem;
      text-decoration: none;
      transition: transform 0.2s;
    }
    .social-links a:hover { transform: scale(1.2); }
    .footer-bottom {
      text-align: center;
      padding-top: 1.5rem;
      margin-top: 1.5rem;
      border-top: 1px solid rgba(255,255,255,0.1);
      font-size: 0.8rem;
    }

    /* === TABLET (min-width: 600px) === */
    @media (min-width: 600px) {
      .footer-content {
        grid-template-columns: repeat(3, 1fr);
        gap: 2rem;
      }
      .site-footer { padding: 2.5rem 2rem 1rem; }
    }
  `]
})
export class FooterComponent {
  currentYear = new Date().getFullYear();
}
