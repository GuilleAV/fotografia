import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { HomeComponent } from './home.component';
import { FotoService } from '../../core/services/foto.service';
import { AuthService } from '../../core/services/auth.service';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [
        provideRouter([]),
        {
          provide: FotoService,
          useValue: {
            listarPublicas: () => of([
              {
                idFoto: 1,
                titulo: 'Hero',
                nombreArchivo: 'hero.jpg',
                destacada: false,
                activo: true,
                estado: 'APROBADA',
                visitas: 10,
                fechaSubida: new Date().toISOString(),
                idCategoria: 1,
                rutaWeb: 'hero-web.jpg',
              },
            ]),
          },
        },
        { provide: AuthService, useValue: { getToken: () => null } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display hero section', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.hero-shell')).toBeTruthy();
  });
});
