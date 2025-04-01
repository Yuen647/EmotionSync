<template>
    <div class="w-[1120px] mx-auto">
        <div
            class="mt-[70px] relative before:absolute before:mr-[36px] before:w-[16px] before:bg-gradient-to-r before:from-primary before:to-[#43B8EA] before:h-[56px] before:block before:content-['']">
            <div class="flex text-[36px] font-bold leading-none ml-[60px]">
                {{ config.apply.title }}
            </div>
        </div>
        <div class="flex text-[16px] font-bold leading-none ml-[60px]">
          <p>您现在还未登录</p>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { useConfig } from '@/composables/config';
const config = useConfig();
</script>