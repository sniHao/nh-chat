import { defineConfig, loadEnv } from "vite";
import vue from "@vitejs/plugin-vue";
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { NaiveUiResolver } from "unplugin-vue-components/resolvers";
import path from "path";

const pathSrc = path.resolve(__dirname, "src");

export default defineConfig(({ command, mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  return {
    resolve: {
      alias: {
        "@": pathSrc,
      },
    },
    plugins: [
      vue(),
      AutoImport({
        imports: [
          "vue",
          {
            "naive-ui": [
              "useDialog",
              "useMessage",
              "useNotification",
              "useLoadingBar",
            ],
          },
        ],
        dts: "src/auto-imports.d.ts",
      }),
      Components({
        resolvers: [NaiveUiResolver()],
      }),
    ],
    css: {
      preprocessorOptions: {
        scss: {
          api: "modern-compiler",
          additionalData: `
          @use "@/assets/css/constant.scss" as *; 
        `,
        },
      },
    },
    define: {
      __APP_ENV__: JSON.stringify(env.APP_ENV),
      "process.env": env,
    },
    build: {
      lib: {
        entry: path.resolve(__dirname, "src/index.js"),
        name: "NhChat",
        fileName: (format) => `nh-chat.${format}.js`,
      },
      rollupOptions: {
        external: ["vue", "vue-router"],
        output: {
          globals: {
            vue: "Vue",
          },
        },
      },
    },
  };
});
