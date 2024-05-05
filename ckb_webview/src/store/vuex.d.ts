// Workaround to show types for Vuex (src: https://stackoverflow.com/questions/76196277/could-not-find-a-declaration-file-for-module-vuex-with-create-vue)
declare module "vuex" {
    export * from "vuex/types/index.d.ts";
    export * from "vuex/types/helpers.d.ts";
    export * from "vuex/types/logger.d.ts";
    export * from "vuex/types/vue.d.ts";
}