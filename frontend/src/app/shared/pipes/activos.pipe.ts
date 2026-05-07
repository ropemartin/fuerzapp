import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'activos', standalone: true })
export class ActivosPipe implements PipeTransform {
  transform(items: { activo: boolean }[]): number {
    return items.filter(i => i.activo).length;
  }
}
