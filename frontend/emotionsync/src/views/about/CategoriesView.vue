<template>
    <div class="flex gap-[12px] h-[363px]">
        <template v-for="item, index in props.items" :key="index">
            <div class="flex h-full" :class="current === index ? 'flex-1' : 'cursor-pointer'" @click="current = index">
                <img :src="current === index ? item.image : item.poster"
                    class="block h-full object-cover duration-300 ease-out transition-[width]"
                    :class="current === index ? 'w-[196px]' : 'w-[150px]'" />
                <div class="bg-white h-full flex flex-col box-border opacity-0 w-0"
                    :class="current === index ? 'py-[20px] pl-[40px] opacity-100 w-auto' : null">
                    <div class="text-[17px] font-semibold mb-[16px]">{{ item.title }}</div>
                    <div class="flex-1 text-gray whitespace-pre-wrap overflow-y-auto"
                        style="scrollbar-width: none">
                        {{ item.content }}</div>
                </div>
            </div>
        </template>
    </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { array } from 'vue-types'
const current = ref(0)
const props = defineProps({
    items: array<any>().isRequired,
})
</script>