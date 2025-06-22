import { createApp } from 'vue'
import App from './App.vue'
import router from './router' // 確保引入了你的路由配置
import './assets/animation.css'


// --- 引入 GSAP 和 Lenis ---
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import Lenis from '@studio-freight/lenis'
// --- 引入結束 ---

// --- GSAP 插件註冊 ---
// 確保在使用 ScrollTrigger 前註冊
gsap.registerPlugin(ScrollTrigger);
// --- 註冊結束 ---

const app = createApp(App)

app.use(router) // 使用路由

app.mount('#app')

// --- Lenis 初始化與整合 ---
// 1. 創建 Lenis 實例
const lenis = new Lenis({
    lerp: 0.1, // 線性插值係數，數值越小越平滑但延遲越高 (預設 0.1)
    smoothWheel: true, // 啟用平滑滾輪 (預設 true)
    // 其他可選配置...
});

// 2. 將 Lenis 的滾動事件同步給 ScrollTrigger
//    當 Lenis 更新滾動位置時，也告訴 ScrollTrigger 更新其內部狀態
lenis.on('scroll', ScrollTrigger.update);

// 3. 使用 GSAP 的 ticker (基於 requestAnimationFrame) 來驅動 Lenis 的每一幀更新
//    這樣可以確保 Lenis 的更新與 GSAP 動畫同步，獲得最佳性能
gsap.ticker.add((time) => {
  // 將 GSAP ticker 的時間傳遞給 Lenis 的 raf 方法
  // 乘以 1000 是因為 GSAP ticker 的時間單位是秒，而 Lenis 需要毫秒
  lenis.raf(time * 1000);
});

// 4. 可選：關閉 GSAP 的延遲平滑
//    有時 Lenis 和 GSAP 自身的平滑機制可能產生衝突，
//    如果感覺效果奇怪，可以嘗試關閉 GSAP 的 lagSmoothing
gsap.ticker.lagSmoothing(0);

console.log('Lenis 平滑滾動已啟用並與 GSAP ScrollTrigger 整合');
// --- Lenis 初始化與整合結束 ---




