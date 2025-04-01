<template>
    <div class="mt-[70px]">
        <div
            class="flex flex-col items-center mx-auto w-[1200px] border-0 border-t-[1px] border-t-default border-solid pb-[50px]">
            <div class="flex gap-[10px] mt-[64px]">
                <template v-for="link, index in config.footer.links" :key="index">
                    <a :href="link.href" class="text-primary text-[14px] cursor-pointer">{{ link.title }}</a>
                    <div v-if="index !== config.footer.links.length - 1" class='text-gray text-[14px]'>|</div>
                </template>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { useConfig } from '@/composables/config';

const config = useConfig()
</script>