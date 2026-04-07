import { Component, input, ChangeDetectionStrategy } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Foto } from '../../../core/models';
import { FotoImagenUrlPipe } from '../../../core/pipes/foto-imagen-url.pipe';

@Component({
  selector: 'app-photo-card',
  standalone: true,
  imports: [RouterLink, DatePipe, FotoImagenUrlPipe],
  templateUrl: './photo-card.component.html',
  styleUrls: ['./photo-card.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PhotoCardComponent {
  foto = input.required<Foto>();
}
