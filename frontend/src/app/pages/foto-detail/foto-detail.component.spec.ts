import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { FotoDetailComponent } from './foto-detail.component';
import { SkeletonComponent } from '../../shared/components/skeleton/skeleton.component';
import { FotoService } from '../../core/services/foto.service';
import { AuthService } from '../../core/services/auth.service';
import { ActivatedRoute } from '@angular/router';

describe('FotoDetailComponent', () => {
  let component: FotoDetailComponent;
  let fixture: ComponentFixture<FotoDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FotoDetailComponent, SkeletonComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } },
        { provide: FotoService, useValue: { obtenerPorId: () => of({ idFoto: 1, titulo: 'Test', estado: 'APROBADA', visitas: 0, fechaSubida: new Date().toISOString() }) } },
        { provide: AuthService, useValue: { isAdmin: () => false } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FotoDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
