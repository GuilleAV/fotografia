import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent),
  },
  {
    path: 'about',
    loadComponent: () => import('./pages/about/about.component').then(m => m.AboutComponent),
  },
  {
    path: 'contacto',
    loadComponent: () => import('./pages/contacto/contacto.component').then(m => m.ContactoComponent),
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'recuperar',
    loadComponent: () => import('./pages/recuperar/recuperar.component').then(m => m.RecuperarComponent),
  },
  {
    path: 'reset-password',
    loadComponent: () => import('./pages/reset-password/reset-password.component').then(m => m.ResetPasswordComponent),
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard],
  },
  {
    path: 'admin',
    loadComponent: () => import('./pages/admin/admin.component').then(m => m.AdminComponent),
    canActivate: [adminGuard],
  },
  {
    path: 'admin/categorias',
    loadComponent: () => import('./pages/admin-categorias/admin-categorias.component').then(m => m.AdminCategoriasComponent),
    canActivate: [adminGuard],
  },
  {
    path: 'admin/usuarios',
    loadComponent: () => import('./pages/admin-usuarios/admin-usuarios.component').then(m => m.AdminUsuariosComponent),
    canActivate: [adminGuard],
  },
  {
    path: 'admin/perfil-publico',
    loadComponent: () => import('./pages/admin-perfil-publico/admin-perfil-publico.component').then(m => m.AdminPerfilPublicoComponent),
    canActivate: [adminGuard],
  },
  {
    path: 'foto/:id',
    loadComponent: () => import('./pages/foto-detail/foto-detail.component').then(m => m.FotoDetailComponent),
  },
  {
    path: 'unauthorized',
    loadComponent: () => import('./pages/unauthorized/unauthorized.component').then(m => m.UnauthorizedComponent),
  },
  {
    path: 'galeria',
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: ':slug',
    loadComponent: () => import('./pages/categoria/categoria.component').then(m => m.CategoriaComponent),
  },
  {
    path: '**',
    redirectTo: '',
  },
];
