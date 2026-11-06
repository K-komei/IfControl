<script setup>
import { computed, reactive, ref } from 'vue'

const items = [
  { code: 'ITM-000184', name: 'プレミアムブレンドコーヒー', category: '飲料', price: 680, status: '販売中', updatedAt: '2026/11/05' },
  { code: 'ITM-000172', name: 'オーガニックティー アールグレイ', category: '飲料', price: 520, status: '販売中', updatedAt: '2026/11/04' },
  { code: 'ITM-000163', name: 'クラフトバタークッキー', category: '食品', price: 980, status: '在庫僅少', updatedAt: '2026/11/03' },
  { code: 'ITM-000158', name: 'リネンハンドタオル', category: '生活雑貨', price: 1200, status: '販売中', updatedAt: '2026/10/29' },
  { code: 'ITM-000141', name: '季節限定ギフトボックス', category: 'ギフト', price: 3600, status: '販売停止', updatedAt: '2026/10/22' },
]

const categories = ['飲料', '食品', '生活雑貨', 'ギフト']
const statuses = ['販売中', '在庫僅少', '販売停止']
const filters = reactive({ code: '', name: '', category: '', status: '' })
const appliedFilters = ref({ ...filters })

const filteredItems = computed(() => {
  const query = appliedFilters.value
  const code = (query.code || '').toLocaleLowerCase()
  const name = query.name || ''

  return items.filter((item) => {
    const matchesCode = !code || item.code.toLocaleLowerCase().includes(code)
    const matchesName = !name || item.name.includes(name)
    const matchesCategory = !query.category || item.category === query.category
    const matchesStatus = !query.status || item.status === query.status
    return matchesCode && matchesName && matchesCategory && matchesStatus
  })
})

function searchItems() {
  appliedFilters.value = { ...filters }
}

function resetFilters() {
  Object.assign(filters, { code: '', name: '', category: '', status: '' })
  appliedFilters.value = { ...filters }
}

function statusColor(status) {
  return {
    販売中: 'success',
    在庫僅少: 'warning',
    販売停止: 'secondary',
  }[status]
}
</script>

<template>
  <v-container class="screen py-8 py-sm-10" fluid>
    <v-row class="mb-5" align="end" justify="space-between">
      <v-col cols="12" sm="8">
        <v-breadcrumbs class="pa-0 mb-2" :items="['業務アプリケーション', '商品管理']" />
        <h1 class="text-h4 font-weight-bold">商品マスタ検索</h1>
        <p class="text-body-2 text-medium-emphasis mt-2">
          商品コードや商品名から、登録済みの商品情報を検索できます。
        </p>
      </v-col>
      <v-col cols="12" sm="auto" class="d-flex justify-sm-end">
        <v-chip color="success" prepend-icon="mdi-check-circle-outline" variant="tonal">
          マスタ同期済み
        </v-chip>
      </v-col>
    </v-row>

    <v-card class="mb-6" rounded="xl" border elevation="0">
      <v-card-item>
        <template #prepend>
          <v-avatar color="primary" variant="tonal" rounded="lg">
            <v-icon icon="mdi-magnify" />
          </v-avatar>
        </template>
        <v-card-title>検索条件</v-card-title>
        <v-card-subtitle>条件を入力して商品を絞り込んでください。</v-card-subtitle>
      </v-card-item>

      <v-divider />

      <v-card-text class="pa-4 pa-sm-6">
        <v-form @submit.prevent="searchItems">
          <v-row>
            <v-col cols="12" sm="6" lg="3">
              <v-text-field
                v-model.trim="filters.code"
                label="商品コード"
                placeholder="例：ITM-000184"
                prepend-inner-icon="mdi-barcode"
                variant="outlined"
                density="comfortable"
                clearable
                hide-details
              />
            </v-col>
            <v-col cols="12" sm="6" lg="3">
              <v-text-field
                v-model.trim="filters.name"
                label="商品名"
                placeholder="商品名を入力"
                prepend-inner-icon="mdi-tag-outline"
                variant="outlined"
                density="comfortable"
                clearable
                hide-details
              />
            </v-col>
            <v-col cols="12" sm="6" lg="3">
              <v-select
                v-model="filters.category"
                :items="categories"
                label="カテゴリ"
                prepend-inner-icon="mdi-shape-outline"
                variant="outlined"
                density="comfortable"
                clearable
                hide-details
              />
            </v-col>
            <v-col cols="12" sm="6" lg="3">
              <v-select
                v-model="filters.status"
                :items="statuses"
                label="販売ステータス"
                prepend-inner-icon="mdi-list-status"
                variant="outlined"
                density="comfortable"
                clearable
                hide-details
              />
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
            <v-icon icon="mdi-format-list-bulleted" />
          </v-avatar>
        </template>
        <v-card-title>検索結果</v-card-title>
        <v-card-subtitle>商品マスタの登録内容を表示しています。</v-card-subtitle>
        <template #append>
          <v-chip color="primary" size="small" variant="tonal">{{ filteredItems.length }} 件</v-chip>
        </template>
      </v-card-item>

      <v-divider />

      <v-table hover density="comfortable">
        <thead>
          <tr>
            <th scope="col">商品コード</th>
            <th scope="col">商品名</th>
            <th scope="col">カテゴリ</th>
            <th scope="col" class="text-right">標準価格</th>
            <th scope="col">ステータス</th>
            <th scope="col">最終更新日</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredItems" :key="item.code">
            <td class="text-medium-emphasis text-no-wrap">{{ item.code }}</td>
            <td class="font-weight-medium">{{ item.name }}</td>
            <td><v-chip size="small" variant="tonal">{{ item.category }}</v-chip></td>
            <td class="text-right text-no-wrap">¥{{ item.price.toLocaleString('ja-JP') }}</td>
            <td>
              <v-chip :color="statusColor(item.status)" size="small" variant="tonal">
                {{ item.status }}
              </v-chip>
            </td>
            <td class="text-medium-emphasis text-no-wrap">{{ item.updatedAt }}</td>
          </tr>
          <tr v-if="filteredItems.length === 0">
            <td colspan="6" class="py-10 text-center text-medium-emphasis">
              条件に一致する商品はありません。検索条件を変更してください。
            </td>
          </tr>
        </tbody>
      </v-table>

      <v-divider />
      <v-card-text class="text-caption text-medium-emphasis py-3">
        表示データはサンプルです
      </v-card-text>
    </v-card>
  </v-container>
</template>

<style scoped>
.screen {
  max-width: 1440px;
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