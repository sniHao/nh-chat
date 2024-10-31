import { defineConfig, loadEnv } from "vite";
import vue from "@vitejs/plugin-vue";
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { NaiveUiResolver } from "unplugin-vue-components/resolvers";
import path from "path";
import dts from "vite-plugin-dts";

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
      dts({
        // 这里定义了需要生成d.ts文件的目录，如果有多个目录，可以使用数组
        include: ["src/components/**/*.{vue,ts}"],
        tsconfigPath: "./tsconfig.build.json",
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
    // 打包配置
    build: {
      // 打包后的文件输出目录
      outDir: "dist",
      lib: {
        //指定组件编译入口文件
        entry: "./src/components/index.ts",
        // 组件库名称
        name: "NhChat",
        // 文件名称
        fileName: "nh-chat",
      },
      rollupOptions: {
        // 确保外部化处理那些你不想打包进库的依赖
        external: ["vue", "vue-router"],
        output: {
          exports: "named",
          // 在 UMD 构建模式下为这些外部化的依赖提供一个全局变量
          globals: {
            vue: "Vue",
          },
        },
      },
    },
  };
});
