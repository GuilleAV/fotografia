import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { PhotoCardComponent } from './photo-card.component';
import { Foto } from '../../../core/models';
import { AuthService } from '../../../core/services/auth.service';

describe('PhotoCardComponent', () => {
  let component: PhotoCardComponent;
  let fixture: ComponentFixture<PhotoCardComponent>;

  const mockFoto: Foto = {
    idFoto: 1,
    titulo: 'Test Photo',
    descripcion: 'A test photo',
    nombreArchivo: 'test.jpg',
    destacada: false,
    activo: true,
    estado: 'APROBADA',
    visitas: 42,
    fechaSubida: new Date().toISOString(),
    idCategoria: 1,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PhotoCardComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: { getToken: () => null } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PhotoCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('foto', mockFoto);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display photo title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h3')?.textContent).toContain('Test Photo');
  });
});
