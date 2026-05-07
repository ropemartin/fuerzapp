import { AbstractControl, ValidationErrors, FormControl, FormGroupDirective, NgForm } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';

export const NOMBRE_PATTERN = /^[a-zA-ZÀ-ÿ\s\-']+$/;
export const TELEFONO_PATTERN = /^\+?[0-9]{9,15}$/;
export const URL_PATTERN = /^https?:\/\/.+\..+/;

export function fechaFuturaValidator(control: AbstractControl): ValidationErrors | null {
  if (!control.value) return null;
  return new Date(control.value) > new Date() ? null : { fechaPasada: true };
}

export class InstantErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
