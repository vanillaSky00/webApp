# 前置(環境)
* **於final_project資料夾下，在powershell上啟動虛擬環境`.\image\img\Scripts\activate`**
* **編譯: `javac -encoding UTF-8 -d bin src/*.java`**
* **執行: `java -cp bin Main`**
* 接下來會按照層級說明 (上->下)

# 資料夾
* **bin: 存放.class檔**
* **image**
  * input: tile_set->compressed_tile
  * output: result
  * resize_target.py: 擴張/縮小target image
  * resize_tile.py: 壓縮爬取完的tile並存放到compressed_tile(壓縮大小可調整)
  * scrab.py: 爬取tile元素存放到tile_set中(可調整爬取數量)
  * `img`跟`requirement.txt`是虛擬環境的東西(無關實作)
* **src**
  * ImageLoader: 載入 target 圖片與所有 tile 圖片
  * ImageDisplay: 使用 Swing 顯示圖片於 GUI（for debug 或成果檢視）
  * ImageMatcher: 計算每個 target tile 的平均色(ColorUtils)並與所有 tile 比對色差，選出最佳匹配圖
  * ImageSplitter: 將 target image 切割成 tile 大小的小圖區塊
  * ColorUtils: 提供平均顏色計算、RGB 色差公式
  * MosaicBuilder: 組合 matched tiles 成一張完整的 mosaic 圖片
  * Main(可調整target跟tile的路徑)
* target_tile_colors 跟 tile_colors 列出每個tile的`平均`色度