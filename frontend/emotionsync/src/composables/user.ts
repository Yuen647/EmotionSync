import { InjectionKey, inject, provide } from "vue"

export interface UserModel {
    loggedIn: boolean,
}

const userKey = Symbol('user') as InjectionKey<UserModel>
export function useUser() {
    const user = inject(userKey)
    if (!user) {
        throw new Error('User not provided')
    }
    return user;
}

export function provideUser(user: UserModel) {
    provide(userKey, user)
}