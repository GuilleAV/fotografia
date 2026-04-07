import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { AdminUsuariosComponent } from './admin-usuarios.component';
import { UsuarioService } from '../../core/services/usuario.service';

describe('AdminUsuariosComponent', () => {
  let component: AdminUsuariosComponent;
  let fixture: ComponentFixture<AdminUsuariosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminUsuariosComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        { provide: UsuarioService, useValue: { listarTodos: () => of([]), crear: () => of({}), actualizar: () => of({}), eliminar: () => of(undefined) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminUsuariosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display form and list', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('form')).toBeTruthy();
    expect(compiled.querySelector('.table')).toBeTruthy();
  });
});
