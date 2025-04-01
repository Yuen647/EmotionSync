import { InjectionKey, inject, provide } from "vue";

const configKey = Symbol('config') as InjectionKey<typeof import('./config.json')>;

export function useConfig() {
    const config = inject(configKey);
    if (!config) {
        throw new Error('Config not provided');
    }

    return config;
}

export function provideConfig(config: typeof import('./config.json')) {
    return provide(configKey, config);
}