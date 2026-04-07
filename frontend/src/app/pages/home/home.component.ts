import { Component, OnInit, OnDestroy, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FotoService } from '../../core/services/foto.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { PhotoCardComponent } from '../../shared/components/photo-card/photo-card.component';
import { SkeletonComponent } from '../../shared/components/skeleton/skeleton.component';
import { FotoImagenUrlPipe } from '../../core/pipes/foto-imagen-url.pipe';
import { Foto, Categoria } from '../../core/models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, PhotoCardComponent, SkeletonComponent, FotoImagenUrlPipe],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent implements OnInit, OnDestroy {
  private fotoService = inject(FotoService);
  private categoriaService = inject(CategoriaService);

  fotos = signal<Foto[]>([]);
  categorias = signal<Categoria[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  // Carousel state
  carouselSlides = signal<Foto[]>([]);
  currentSlide = signal(0);
  private carouselTimer: any;

  ngOnInit() {
    this.fotoService.listarPublicas().subscribe({
      next: (fotos) => {
        this.fotos.set(fotos);
        // Tomar hasta 5 fotos para el carousel
        const slides = fotos.slice(0, 5);
        this.carouselSlides.set(slides);
        if (slides.length > 0) {
          this.startCarousel();
        }
      },
      error: () => this.error.set('Error al cargar las fotos'),
      complete: () => this.loading.set(false),
    });

    this.categoriaService.listarActivas().subscribe({
      next: (cats) => this.categorias.set(cats),
      error: () => {},
    });
  }

  ngOnDestroy() {
    this.stopCarousel();
  }

  startCarousel() {
    this.stopCarousel();
    this.carouselTimer = setInterval(() => {
      const slides = this.carouselSlides();
      if (slides.length > 0) {
        this.currentSlide.set((this.currentSlide() + 1) % slides.length);
      }
    }, 5000);
  }

  stopCarousel() {
    if (this.carouselTimer) {
      clearInterval(this.carouselTimer);
      this.carouselTimer = null;
    }
  }

  goToSlide(index: number) {
    this.currentSlide.set(index);
    this.stopCarousel();
    this.startCarousel();
  }

  prevSlide() {
    const slides = this.carouselSlides();
    if (slides.length > 0) {
      this.goToSlide((this.currentSlide() - 1 + slides.length) % slides.length);
    }
  }

  nextSlide() {
    const slides = this.carouselSlides();
    if (slides.length > 0) {
      this.goToSlide((this.currentSlide() + 1) % slides.length);
    }
  }

  trackByFoto(index: number, foto: Foto): number {
    return foto.idFoto;
  }
}
