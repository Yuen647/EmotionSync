import { extend } from 'umi-request'

const request = extend({
    prefix: import.meta.env.VITE_API_PREFIX,
})

export default request