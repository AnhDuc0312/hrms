import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from '../src/app/app.module'; // Assuming AppModule is your root module

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch((err) => console.error(err));