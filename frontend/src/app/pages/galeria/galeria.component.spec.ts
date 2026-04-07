import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { GaleriaComponent } from './galeria.component';
import { PhotoCardComponent } from '../../shared/components/photo-card/photo-card.component';
import { SkeletonComponent } from '../../shared/components/skeleton/skeleton.component';
import { FotoService } from '../../core/services/foto.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('GaleriaComponent', () => {
  let component: GaleriaComponent;
  let fixture: ComponentFixture<GaleriaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GaleriaComponent, PhotoCardComponent, SkeletonComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        { provide: ActivatedRoute, useValue: { queryParams: of({}) } },
        { provide: FotoService, useValue: { listarPublicas: () => ({ subscribe: () => {} }), listarPorCategoria: () => ({ subscribe: () => {} }) } },
        { provide: CategoriaService, useValue: { listarActivas: () => ({ subscribe: () => {} }) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(GaleriaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display page title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.page-title')).toBeTruthy();
  });
});
