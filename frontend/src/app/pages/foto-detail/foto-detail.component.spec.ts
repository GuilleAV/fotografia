import { ComponentFixture, TestBed } from '@angular/core/testing';
import { convertToParamMap, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { FotoDetailComponent } from './foto-detail.component';
import { FotoService } from '../../core/services/foto.service';
import { AuthService } from '../../core/services/auth.service';
import { ActivatedRoute } from '@angular/router';

describe('FotoDetailComponent', () => {
  let component: FotoDetailComponent;
  let fixture: ComponentFixture<FotoDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FotoDetailComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(convertToParamMap({ id: '1' })),
            queryParamMap: of(convertToParamMap({})),
          },
        },
        {
          provide: FotoService,
          useValue: {
            obtenerPorId: () => of({
              idFoto: 1,
              titulo: 'Test',
              estado: 'APROBADA',
              visitas: 0,
              fechaSubida: new Date().toISOString(),
              categoriaSlug: 'naturaleza',
              categoriaNombre: 'Naturaleza',
              rutaWeb: 'foto-web.jpg',
              rutaThumbnail: 'foto-thumb.jpg',
            }),
            listarPorCategoriaSlug: () => of([
              {
                idFoto: 1,
                titulo: 'Test',
                estado: 'APROBADA',
                visitas: 0,
                fechaSubida: new Date().toISOString(),
                categoriaSlug: 'naturaleza',
                categoriaNombre: 'Naturaleza',
                rutaWeb: 'foto-web.jpg',
                rutaThumbnail: 'foto-thumb.jpg',
              },
            ]),
          },
        },
        { provide: AuthService, useValue: { getToken: () => null } },
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
