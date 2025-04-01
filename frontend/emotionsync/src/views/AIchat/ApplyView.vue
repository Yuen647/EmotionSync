<template>
    <div v-if="user.loggedIn">
        <ApplyAcknowledge/>
    </div>
    <div v-else>
        <ApplyNotlogged/>
    </div>
</template>

<script lang="ts" setup>
import { useUserStore } from '@/store/userStore';
import ApplyAcknowledge from './ApplyAcknowledge.vue';
import ApplyNotlogged from './ApplyNotlogged.vue';
const user=useUserStore()

</script>