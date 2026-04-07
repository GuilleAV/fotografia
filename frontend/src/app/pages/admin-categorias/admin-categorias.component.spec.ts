import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { AdminCategoriasComponent } from './admin-categorias.component';
import { CategoriaService } from '../../core/services/categoria.service';

describe('AdminCategoriasComponent', () => {
  let component: AdminCategoriasComponent;
  let fixture: ComponentFixture<AdminCategoriasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminCategoriasComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        { provide: CategoriaService, useValue: { listarTodas: () => of([]), crear: () => of({}), actualizar: () => of({}), eliminar: () => of(undefined) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminCategoriasComponent);
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
