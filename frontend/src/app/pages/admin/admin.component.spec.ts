import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { AdminComponent } from './admin.component';
import { FotoService } from '../../core/services/foto.service';

describe('AdminComponent', () => {
  let component: AdminComponent;
  let fixture: ComponentFixture<AdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        { provide: FotoService, useValue: { listarPendientes: () => of([]), listarTodas: () => of([]) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display tabs', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.tabs')).toBeTruthy();
  });
});
