<script setup>
import { ref } from 'vue'
import { RouterView } from 'vue-router'
import { useDisplay } from 'vuetify'

const drawer = ref(false)
const { mdAndUp } = useDisplay()
</script>

<template>
  <v-app>
    <v-navigation-drawer
      :model-value="mdAndUp || drawer"
      @update:model-value="drawer = $event"
      :permanent="mdAndUp"
      :temporary="!mdAndUp"
      width="272"
      color="surface"
      border="end"
    >
      <div class="drawer-brand">
        <v-avatar color="primary" rounded="lg" size="42">
          <span class="brand-mark">IF</span>
        </v-avatar>
        <div>
          <div class="brand-title">IF Control</div>
          <div class="brand-caption">OPERATIONS PORTAL</div>
        </div>
      </div>

      <v-divider />
      <v-list class="navigation-list" nav aria-label="メインナビゲーション">
        <v-list-subheader>業務メニュー</v-list-subheader>
        <v-list-item
          :to="{ name: 'item-master-search' }"
          prepend-icon="mdi-package-variant-closed"
          title="商品マスタ検索"
          rounded="lg"
          color="primary"
        />
        <v-list-item
          :to="{ name: 'sales-list' }"
          prepend-icon="mdi-chart-box-outline"
          title="売上一覧"
          rounded="lg"
          color="primary"
        />
      </v-list>

      <template #append>
        <div class="drawer-bottom">
          <v-chip color="success" size="small" variant="tonal" prepend-icon="mdi-circle-small">
            開発環境
          </v-chip>
          <v-list-item
            class="user-item"
            prepend-icon="mdi-account-circle-outline"
            title="オペレーター"
            subtitle="システム利用者"
            rounded="lg"
          />
        </div>
      </template>
    </v-navigation-drawer>

    <v-app-bar color="surface" elevation="0" border="bottom">
      <v-app-bar-nav-icon
        class="d-md-none"
        aria-label="ナビゲーションを開く"
        @click="drawer = !drawer"
      />
      <v-app-bar-title class="topbar-title">
        <span class="topbar-overline">IF CONTROL</span>
        <span class="topbar-page">業務アプリケーション</span>
      </v-app-bar-title>
      <v-spacer />
      <v-chip class="mr-4" color="success" size="small" variant="tonal" prepend-icon="mdi-check-circle-outline">
        システム稼働中
      </v-chip>
      <v-btn class="mr-3" icon="mdi-help-circle-outline" variant="text" aria-label="ヘルプ" />
    </v-app-bar>

    <v-main>
      <div class="page-frame">
        <div class="route-content">
          <RouterView v-slot="{ Component }">
            <component :is="Component" v-if="Component" />
          </RouterView>
        </div>
        <footer class="app-footer">
          <span>IF Control Panel</span>
          <span>© 2026 InterfaceBase</span>
        </footer>
      </div>
    </v-main>
  </v-app>
</template>

<style scoped>
.drawer-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 22px 20px;
}

.brand-mark {
  color: rgb(var(--v-theme-on-primary));
  font-size: 15px;
  font-weight: 800;
  letter-spacing: -0.04em;
}

.brand-title {
  color: rgb(var(--v-theme-on-surface));
  font-size: 17px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.brand-caption {
  margin-top: 2px;
  color: rgb(var(--v-theme-on-surface-variant));
  font-size: 9px;
  font-weight: 600;
  letter-spacing: 0.13em;
}

.navigation-list {
  padding: 12px;
}

.drawer-bottom {
  display: grid;
  gap: 8px;
  padding: 12px 12px 16px;
}

.user-item {
  margin-top: 6px;
  border-top: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
}

.topbar-title {
  display: flex;
  flex-direction: column;
  justify-content: center;
  line-height: 1.35;
}

.topbar-overline {
  color: rgb(var(--v-theme-on-surface-variant));
  font-size: 9px;
  font-weight: 700;
  letter-spacing: 0.12em;
}

.topbar-page {
  color: rgb(var(--v-theme-on-surface));
  font-size: 14px;
  font-weight: 600;
}

.page-frame {
  display: flex;
  min-height: calc(100vh - 64px);
  flex-direction: column;
}

.route-content {
  flex: 1;
}

.app-footer {
  display: flex;
  justify-content: space-between;
  padding: 14px 28px;
  border-top: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
  color: rgb(var(--v-theme-on-surface-variant));
  font-size: 11px;
}

@media (max-width: 600px) {
  .app-footer {
    padding: 12px 16px;
  }
}
</style>
