import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'
import './assets/main.css'

import { createApp } from 'vue'
import { createVuetify } from 'vuetify'
import App from './App.vue'
import router from './router'

const vuetify = createVuetify({
  icons: {
    defaultSet: 'mdi',
  },
  theme: {
    defaultTheme: 'ifControlTheme',
    themes: {
      ifControlTheme: {
        dark: false,
        colors: {
          primary: '#6750a4',
          secondary: '#625b71',
          background: '#f7f6fb',
          surface: '#ffffff',
          error: '#ba1a1a',
          success: '#386a20',
          warning: '#7d5700',
        },
      },
    },
  },
})

createApp(App).use(vuetify).use(router).mount('#app')
