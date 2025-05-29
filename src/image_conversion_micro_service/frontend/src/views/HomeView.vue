<template>
  <div class="home-view">
    <!-- 背景層 (可選) -->
    <!-- <div class="fixed-background"></div> -->

    <!-- 視差容器 -->
    <ParallaxContainer
      class="parallax-visual-area animate-on-scroll animate-fade-in-up"
      :max-offset="25"
      :reverse="false"
      :transition="'transform 0.15s ease-out'"
    >
      <!-- 後景 -->
      <img
        :src="img_color"
        alt="後景 - 可能是遠山或天空"
        class="parallax-layer layer-back back-color"
        data-parallax-factor="1"
      />

      <img
        :src="img1_1"
        alt="後景 - 可能是遠山或天空"
        class="parallax-layer layer-back back-1"
        data-parallax-factor="0.4"
      />

      <img
        :src="img1_2"
        alt="後景 - 可能是遠山或天空"
        class="parallax-layer layer-back back-2"
        data-parallax-factor="0.4"
      />

      <img
        :src="img1_3"
        alt="後景 - 可能是遠山或天空"
        class="parallax-layer layer-back back-3"
        data-parallax-factor="0.4"
      />

      <img
        :src="img1_4"
        alt="後景 - 可能是遠山或天空"
        class="parallax-layer layer-back back-4"
        data-parallax-factor="0.4"
      />


      <!-- 中景 -->
      <img
        :src="img2_1"
        alt="中景 - 可能是建築或樹木"
        class="parallax-layer layer-middle mid-1"
        data-parallax-factor="1"
      />
      <img
        :src="img2_2"
        alt="中景 - 可能是建築或樹木"
        class="parallax-layer layer-middle mid-2"
        data-parallax-factor="1"
      />
      <img
        :src="img2_3"
        alt="中景 - 可能是建築或樹木"
        class="parallax-layer layer-middle mid-3"
        data-parallax-factor="1"
      />
      <img
        :src="img2_4"
        alt="中景 - 可能是建築或樹木"
        class="parallax-layer layer-middle mid-4"
        data-parallax-factor="1"
      />


      <!-- 前景 -->
      <img
        :src="img3_1"
        alt="前景 - 可能是靠近的人物或物體"
        class="parallax-layer layer-front front-1"
        data-parallax-factor="2.0"
      />
      <Typewriter :text="dynamicTitle" 
      :speed="100" 
      :loop="true" 
      @completed="onTypingComplete"
      class="title-text parallax-layer layer-back"
      data-parallax-factor="2.0"
      />

      <Typewriter :text="dynamicSubTitle" 
      :speed="100" 
      :loop="true" 
      @completed="onTypingComplete"
      class="sub-title-text parallax-layer layer-back"
      data-parallax-factor="2.0"
      />
    </ParallaxContainer>
    

    <!-- 基本用法：左圖右文 -->
    <!-- 使用 public 文件夾下的路徑或導入的變數 -->
    <SplitLayoutSection
      :image-url="img_intro"
      image-alt="描述圖片 1"
      title="第一個區塊標題"
      description="這是第一個區塊的詳細描述文字..."
      class="animate-on-scroll animate-fade-in-up"
    />
    <SplitLayoutSection
      :image-url="img_intro"
      image-alt="描述圖片 1"
      title="第一個區塊標題"
      description="這是第一個區塊的詳細描述文字..."
      class="animate-on-scroll animate-fade-in-up"
    />
    <SplitLayoutSection
      :image-url="img_intro"
      image-alt="描述圖片 1"
      title="第一個區塊標題"
      description="這是第一個區塊的詳細描述文字..."
      class="animate-on-scroll animate-fade-in-up"
    />
    <!-- 反轉佈局：右圖左文 -->
    <!-- <SplitLayoutSection
      image-url="/path/to/your/image2.png"
      image-alt="描述圖片 2"
      title="第二個區塊標題 (圖片在右)"
      description="這是第二個區塊的描述，圖片會顯示在右邊。"
      :reverse="true" 
    /> -->
    <!-- 啟用反轉 -->

  </div>
</template>

<script setup lang="ts">
import ParallaxContainer from '../components/ParallaxContainer.vue';
// import ScrollingImageSection if needed
import img1_1 from '../assets/image/back1.jpg';
import img1_2 from '../assets/image/back2.jpg';
import img1_3 from '../assets/image/back3.jpg';
import img1_4 from '../assets/image/back4.jpg';
import img2_1 from '../assets/image/mid1.jpg';
import img2_2 from '../assets/image/mid2.jpg';
import img2_3 from '../assets/image/mid3.jpg';
import img2_4 from '../assets/image/mid4.jpg';
import img3_1 from '../assets/image/mosaic_dog2.jpg'
import img_color from '../assets/image/back_color_2.png'
import img_intro from '../assets/image/mosaic_dog2.jpg'

import Typewriter from '../components/Typewriter.vue';
import SplitLayoutSection from '@/components/SplitLayoutSection.vue'; // 確保路徑正確

import { ref } from 'vue';

const dynamicTitle = ref("Welcome\nto\nPhotomosaic");
const dynamicSubTitle = ref("big pictures built out of thousands of other tiny pictures.");

const onTypingComplete = () => {
  console.log("打字完成！");
  // 可以加載下一段文字
  // setTimeout(() => { dynamicTitle.value = "這是第二段文字..."; }, 2000);
};
</script>


<style scoped lang="scss">
.home-view {
  // **重要:** 需要設置 position: relative (或其他非 static 的值)
  // 以便 .fixed-background 的 z-index:-1 相對於它生效，
  // 確保背景在 .homepage 的內容之後，而不是在整個 body 之後。
  // 如果不設置，z-index:-1 可能會讓背景跑到 body 的所有內容之後，
  // 甚至可能看不見（取決於瀏覽器和父級樣式）。
  position: relative;
  // 可選：如果希望內容區域有最小高度確保背景可見
  // min-height: 100vh;
  // 注意：這裡的 overflow: hidden; 不是必需的，除非你想限制 homepage 內容不超出範圍
}

/* 2. 定義背景元素的樣式 */
.fixed-background {
  position: fixed; /* 固定定位，相對於 viewport */
  top: 0;
  left: 0;
  width: 100vw; /* 寬度等於視窗寬度 */
  height: 100vh; /* 高度等於視窗高度 */
  z-index: -1; /* **關鍵:** 將其層級設為負數，放到普通內容的後面 */

  /* --- 背景圖片設置 --- */
  /* 將 'your-background-image.jpg' 替換成你的圖片路徑 */
  /* 建議將圖片放在 /public 文件夾下，這樣可以直接用 / 根路徑訪問 */
  background-image: url('../assets/image/background.png');

  /* 確保背景圖片覆蓋整個區域 */
  background-size: cover;

  /* 將背景圖片置中顯示 */
  background-position: center center;

  /* 背景圖片不重複 */
  background-repeat: no-repeat;

  /* 可選：如果圖片較亮，可以加一個半透明的深色疊加層，讓前景文字更易讀 */
  /*
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.3); // 30% 透明度的黑色疊加層
    z-index: 1; // 疊加層在背景圖之上
  }
  */
}

/* 現有的 spacer 樣式保持不變 */
.spacer {
  height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  // **修改:** 讓文字在背景上更清晰可見
  color: #ffffff; /* 改為白色或其他對比度高的顏色 */
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.6); /* 添加文字陰影 */
  text-align: center;
  // **移除背景色，讓固定背景透出來**
  // background-color: #f0f0f0; // 移除或註釋掉
  position: relative; // 確保文字在背景之上 (如果背景有 ::after 疊加層)
  z-index: 2;       // 確保 spacer 內容在背景之上
}


/* 視差容器樣式 */
.parallax-visual-area { // <--- 找到這個選擇器
  width: 90vw;
  max-width: 1200px;
  height: 110vh;
  margin: 0vh auto 20px auto;
  // border: 1px solid #ddd; // 保留邊框方便觀察範圍
  position: relative;
  // background-color: rgba(255, 255, 255, 0.1);
  // /* overflow: hidden; */ /* **將這一行註釋掉或刪除** */
  // box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.parallax-layer {
  position: absolute; /* 必須 */
  /* 移除預設的 top/left/width/height，讓每個圖層可以自訂 */
  /* top: 0; */
  /* left: 0; */
  // width: 100%; 
  // height: 100%; 
  /* object-fit: cover; 保留或根據需要修改 */
  /* transition 已由 ParallaxContainer 的 :deep 處理 */
  /* display: block; 確保是塊級元素 */
  display: block;
  max-width: none; /* 防止圖片被意外限制寬度 */
  object-fit: contain;
}

/* --- 後景 (layer-back) 微調 --- */
.layer-back {
  z-index: 1;
  // /* 範例：讓背景比容器大 10%，並稍微向上和向左移動以居中放大效果 */
  // width: 90%;
  // height: 90%;
  // // 左上角對齊
  // top: -5%;
  // left: -5%;
  // object-fit: cover; /* 確保放大後覆蓋 */
}

.back-1 {
  margin-left: 7vw; /* 陰影會抓 container 的 margin */
  margin-top: 10vh;
  width: 10%;
  height: auto;
  object-fit: contain;
  border: 3px solid #ccc; /* 邊框：灰色、3px 寬 */
  box-shadow: 8px 8px 20px rgba(0, 0, 0, 0.3); /* 陰影 */
}
.back-2 {
  margin-left: 13vw;
  margin-top: 70vh;
  width: 10%;
  height: auto;
  object-fit: contain;
  border: 3px solid #ccc; /* 邊框：灰色、3px 寬 */
  box-shadow: 8px 8px 20px rgba(0, 0, 0, 0.3); /* 陰影 */
}
.back-3 {
  margin-left: 25vw;
  margin-top: 6vh;
  width: 10%;
  height: auto;
  object-fit: contain;
  border: 3px solid #ccc; /* 邊框：灰色、3px 寬 */
  box-shadow: 8px 8px 20px rgba(0, 0, 0, 0.3); /* 陰影 */
}
.back-4 {
  margin-left: 32vw;
  margin-top: 63vh;
  width: 10%;
  height: auto;
  object-fit: contain;
  border: 3px solid #ccc; /* 邊框：灰色、3px 寬 */
  box-shadow: 8px 8px 20px rgba(0, 0, 0, 0.3); /* 陰影 */
}
.back-color {
  margin-left: 5vw; /* 陰影會抓 container 的 margin */
  margin-top: 5vh;
  width: 50%;
  height: auto;
  object-fit: contain;
}
/* --- 中景 (layer-middle) 微調 --- */
.layer-middle {
  z-index: 2;
  /* 範例：假設中景圖片本身就是合適的大小，讓它在容器內水平居中，垂直底部對齊 */
  // width: 50%; /* 假設圖片寬度為容器的 80% */
  // height: auto; /* 高度自適應 */
  // bottom: 0; /* 對齊容器底部 */
  // left: 50%; /* 水平居中 */
  // transform: translateX(-50%); /* 配合 left: 50% 實現精確水平居中 */
  //  object-fit: contain; /* 保持圖片比例 */
}
.mid-1 {
  margin-left: 17vw; /* 陰影會抓 container 的 margin */
  margin-top: 13vh;
  width: 8%;
  height: auto;
  object-fit: contain;
  border: 3px solid #ccc; /* 邊框：灰色、3px 寬 */
  box-shadow: 8px 8px 20px rgba(0, 0, 0, 0.3); /* 陰影 */
}
.mid-2 {
  margin-left: 10vw; /* 陰影會抓 container 的 margin */
  margin-top: 200px;
  width: 10%;
  height: auto;
  object-fit: contain;
  border: 3px solid #ccc; /* 邊框：灰色、3px 寬 */
  box-shadow: 8px 8px 20px rgba(0, 0, 0, 0.3); /* 陰影 */
}
.mid-3 {
  margin-left:23vw; /* 陰影會抓 container 的 margin */
  margin-top:65vh;
  width: 10%;
  height: auto;
  object-fit: contain;
  border: 3px solid #ccc; /* 邊框：灰色、3px 寬 */
  box-shadow: 8px 8px 20px rgba(0, 0, 0, 0.3); /* 陰影 */
}
.mid-4 {
  margin-left: 34vw; /* 陰影會抓 container 的 margin */
  margin-top: 23vh;
  width: 10%;
  height: auto;
  object-fit: contain;
  border: 3px solid #ccc; /* 邊框：灰色、3px 寬 */
  box-shadow: 8px 8px 20px rgba(0, 0, 0, 0.3); /* 陰影 */
}


/* --- 前景 (layer-front) 微調 --- */
.layer-front {
  z-index: 3;
  /* 範例：假設前景是個小元素，放在右下角 */
  // width: 10%; /* 假設寬度為 30% */
  // height: auto;
  // bottom: 5%; /* 離底部 5% */
  // right: 5%; /* 離右邊 5% */
  // /* 因為用了 right/bottom，不需要 left/top 或 transform */
  //  object-fit: contain;
}
.front-1 {
  margin-left: 200px; /* 陰影會抓 container 的 margin */
  margin-top: 170px;
  width: 15%;
  height: auto;
  object-fit: contain;
  border: 3px solid #ccc; /* 邊框：灰色、3px 寬 */
  box-shadow: 8px 8px 20px rgba(0, 0, 0, 0.3); /* 陰影 */
}

.title-text {
  top: 20vh; /* 在 Header 下方 */
  left: 50%;
  transform: translateX(-50%);
  white-space: pre-line; /* 保留換行符並合併空格 */
  z-index: 100; /* 確保在頂層 */
  color: black;
  background-color: rgba(255, 255, 255, 0.8); /* 半透明背景 */
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 60px;
}

.sub-title-text {
  top: 80vh; /* 在 Header 下方 */
  left: 50%;
  transform: translateX(-50%);
  z-index: 100; /* 確保在頂層 */
  color: black;
  background-color: rgba(255, 255, 255, 0.8); /* 半透明背景 */
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 30px;
}
</style>