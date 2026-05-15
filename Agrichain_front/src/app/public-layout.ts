import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { App } from "./app";
import { Footer } from './common-components/footer/footer';
import { ToastComponent } from './common-components/toast/toast';

@Component({
    selector: 'public-layout',
    imports: [Footer, RouterOutlet, ToastComponent],
    template: `
    <main>
        <router-outlet />
    </main>
    <app-footer />
    <app-toast />
  `
})
export class PublicLayoutComponent {
    protected readonly title = signal('Agrichain');
}
