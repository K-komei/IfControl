<script setup>
import { computed, reactive, ref } from 'vue'

const sales = [
  { id: 'SL-261105-0184', date: '2026-11-05', branch: '東京中央店', item: 'プレミアムブレンドコーヒー', quantity: 3, amount: 2_040, status: '完了' },
  { id: 'SL-261105-0179', date: '2026-11-05', branch: '横浜北店', item: 'オーガニックティー アールグレイ', quantity: 2, amount: 1_040, status: '完了' },
  { id: 'SL-261104-0162', date: '2026-11-04', branch: '東京中央店', item: 'クラフトバタークッキー', quantity: 1, amount: 980, status: '確認中' },
  { id: 'SL-261104-0151', date: '2026-11-04', branch: '大阪南店', item: '季節限定ギフトボックス', quantity: 1, amount: 3_600, status: '完了' },
  { id: 'SL-261103-0138', date: '2026-11-03', branch: '横浜北店', item: 'リネンハンドタオル', quantity: 4, amount: 4_800, status: '取消' },
  { id: 'SL-261102-0114', date: '2026-11-02', branch: '大阪南店', item: 'プレミアムブレンドコーヒー', quantity: 5, amount: 3_400, status: '完了' },
]

const branches = ['東京中央店', '横浜北店', '大阪南店']
const statuses = ['完了', '確認中', '取消']
const filters = reactive({ from: '2026-11-01', to: '2026-11-06', branch: '', keyword: '', status: '' })
const appliedFilters = ref({ ...filters })

const filteredSales = computed(() => {
  const query = appliedFilters.value
  return sales.filter((row) => {
    const matchesFrom = !query.from || row.date >= query.from
    const matchesTo = !query.to || row.date <= query.to
    const matchesBranch = !query.branch || row.branch === query.branch
    const matchesKeyword = !query.keyword || `${row.item} ${row.id}`.toLocaleLowerCase().includes(query.keyword.toLocaleLowerCase())
    const matchesStatus = !query.status || row.status === query.status
    return matchesFrom && matchesTo && matchesBranch && matchesKeyword && matchesStatus
  })
})

const totalAmount = computed(() => filteredSales.value.reduce((sum, row) => sum + row.amount, 0))
const totalQuantity = computed(() => filteredSales.value.reduce((sum, row) => sum + row.quantity, 0))

function searchSales() {
  appliedFilters.value = { ...filters }
}

function resetFilters() {
  Object.assign(filters, { from: '', to: '', branch: '', keyword: '', status: '' })
  appliedFilters.value = { ...filters }
}

function statusColor(status) {
  return { 完了: 'success', 確認中: 'warning', 取消: 'secondary' }[status]
}
</script>

<template>
  <v-container class="screen py-8 py-sm-10" fluid>
    <v-row class="mb-5" align="end" justify="space-between">
      <v-col cols="12" sm="8">
        <v-breadcrumbs class="pa-0 mb-2" :items="['業務アプリケーション', '取引照会']" />
        <h1 class="text-h4 font-weight-bold">売上一覧</h1>
        <p class="text-body-2 text-medium-emphasis mt-2">
          期間や店舗、商品を指定して売上データを検索できます。
        </p>
      </v-col>
      <v-col cols="12" sm="auto" class="d-flex justify-sm-end">
        <v-chip color="primary" prepend-icon="mdi-calendar-range" variant="tonal">
          {{ appliedFilters.from || '全期間' }} ～ {{ appliedFilters.to || '現在' }}
        </v-chip>
      </v-col>
    </v-row>

    <v-row class="mb-2">
      <v-col cols="12" md="4">
        <v-card class="metric-card h-100" rounded="xl" border elevation="0">
          <v-card-text class="pa-5">
            <div class="d-flex align-center justify-space-between">
              <span class="text-body-2 text-medium-emphasis">売上合計</span>
              <v-avatar color="primary" variant="tonal" rounded="lg" size="38">
                <v-icon icon="mdi-currency-jpy" />
              </v-avatar>
            </div>
            <div class="text-h5 font-weight-bold mt-4">¥{{ totalAmount.toLocaleString('ja-JP') }}</div>
            <div class="text-caption text-medium-emphasis mt-1">検索結果の合計金額</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="4">
        <v-card class="metric-card h-100" rounded="xl" border elevation="0">
          <v-card-text class="pa-5">
            <div class="d-flex align-center justify-space-between">
              <span class="text-body-2 text-medium-emphasis">取引件数</span>
              <v-avatar color="secondary" variant="tonal" rounded="lg" size="38">
                <v-icon icon="mdi-receipt-text-outline" />
              </v-avatar>
            </div>
            <div class="text-h5 font-weight-bold mt-4">{{ filteredSales.length }}<span class="text-body-2 ml-1">件</span></div>
            <div class="text-caption text-medium-emphasis mt-1">該当する売上伝票数</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="4">
        <v-card class="metric-card h-100" rounded="xl" border elevation="0">
          <v-card-text class="pa-5">
            <div class="d-flex align-center justify-space-between">
              <span class="text-body-2 text-medium-emphasis">販売数量</span>
              <v-avatar color="success" variant="tonal" rounded="lg" size="38">
                <v-icon icon="mdi-package-variant" />
              </v-avatar>
            </div>
            <div class="text-h5 font-weight-bold mt-4">{{ totalQuantity.toLocaleString('ja-JP') }}<span class="text-body-2 ml-1">点</span></div>
            <div class="text-caption text-medium-emphasis mt-1">商品ごとの販売数量合計</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-card class="my-5" rounded="xl" border elevation="0">
      <v-card-item>
        <template #prepend>
          <v-avatar color="primary" variant="tonal" rounded="lg">
            <v-icon icon="mdi-magnify" />
          </v-avatar>
        </template>
        <v-card-title>検索条件</v-card-title>
        <v-card-subtitle>条件を組み合わせて売上を絞り込めます。</v-card-subtitle>
        <template #append>
          <span class="text-caption text-medium-emphasis d-none d-sm-inline">期間指定は任意です</span>
        </template>
      </v-card-item>
      <v-divider />
      <v-card-text class="pa-4 pa-sm-6">
        <v-form @submit.prevent="searchSales">
          <v-row>
            <v-col cols="12" sm="6" lg="3">
              <v-text-field v-model="filters.from" label="売上日（開始）" type="date" variant="outlined" density="comfortable" hide-details />
            </v-col>
            <v-col cols="12" sm="6" lg="3">
              <v-text-field v-model="filters.to" label="売上日（終了）" type="date" variant="outlined" density="comfortable" hide-details />
            </v-col>
            <v-col cols="12" sm="6" lg="3">
              <v-select v-model="filters.branch" :items="branches" label="店舗" placeholder="すべての店舗" prepend-inner-icon="mdi-store-outline" variant="outlined" density="comfortable" clearable hide-details />
            </v-col>
            <v-col cols="12" sm="6" lg="3">
              <v-text-field v-model.trim="filters.keyword" label="商品名・伝票番号" placeholder="キーワードを入力" prepend-inner-icon="mdi-text-search" variant="outlined" density="comfortable" clearable hide-details />
            </v-col>
            <v-col cols="12" sm="6" lg="3">
              <v-select v-model="filters.status" :items="statuses" label="処理状況" placeholder="すべて" prepend-inner-icon="mdi-list-status" variant="outlined" density="comfortable" clearable hide-details />
            </v-col>
          </v-row>
          <div class="d-flex justify-end ga-2 mt-4">
            <v-btn variant="text" prepend-icon="mdi-refresh" @click="resetFilters">クリア</v-btn>
            <v-btn color="primary" type="submit" prepend-icon="mdi-magnify">検索する</v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>

    <v-card rounded="xl" border elevation="0">
      <v-card-item>
        <template #prepend>
          <v-avatar color="secondary" variant="tonal" rounded="lg">
            <v-icon icon="mdi-receipt-text-multiple-outline" />
          </v-avatar>
        </template>
        <v-card-title>売上データ</v-card-title>
        <v-card-subtitle>検索条件に一致した取引を表示しています。</v-card-subtitle>
        <template #append>
          <v-btn icon="mdi-refresh" variant="text" size="small" aria-label="再検索" @click="searchSales" />
        </template>
      </v-card-item>
      <v-divider />
      <v-table hover density="comfortable">
        <thead>
          <tr>
            <th scope="col">伝票番号</th>
            <th scope="col">売上日</th>
            <th scope="col">店舗</th>
            <th scope="col">商品名</th>
            <th scope="col" class="text-right">数量</th>
            <th scope="col" class="text-right">売上金額</th>
            <th scope="col">状況</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in filteredSales" :key="row.id">
            <td class="text-no-wrap text-medium-emphasis">{{ row.id }}</td>
            <td class="text-no-wrap">{{ row.date.replaceAll('-', '/') }}</td>
            <td class="text-no-wrap">{{ row.branch }}</td>
            <td>{{ row.item }}</td>
            <td class="text-right">{{ row.quantity.toLocaleString('ja-JP') }}</td>
            <td class="text-right text-no-wrap font-weight-medium">¥{{ row.amount.toLocaleString('ja-JP') }}</td>
            <td><v-chip :color="statusColor(row.status)" size="small" variant="tonal">{{ row.status }}</v-chip></td>
          </tr>
          <tr v-if="filteredSales.length === 0">
            <td colspan="7" class="py-10 text-center text-medium-emphasis">
              条件に一致する売上データはありません。検索条件を変更してください。
            </td>
          </tr>
        </tbody>
      </v-table>
      <v-divider />
      <v-card-text class="d-flex justify-space-between text-caption text-medium-emphasis py-3">
        <span>{{ filteredSales.length }} 件を表示</span>
        <span>表示データはサンプルです</span>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<style scoped>
.screen {
  max-width: 1440px;
}

.metric-card {
  background: rgb(var(--v-theme-surface));
}

h1 {
  color: rgb(var(--v-theme-on-surface));
}

:deep(.v-breadcrumbs-item) {
  font-size: 0.8rem;
}

:deep(.v-table) {
  overflow-x: auto;
}

@media (max-width: 600px) {
  .screen {
    padding-inline: 16px;
  }
}
</style>