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
        // 确保外部化处理那些你不想打包进库的依赖
        external: ["vue"],
        output: {
          // 在 UMD 构建模式下为这些外部化的依赖提供一个全局变量
          globals: {
            vue: "Vue",
          },
        },
      },
    },
  };
});
