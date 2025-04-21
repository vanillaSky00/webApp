//vue.config.js
module.exports = {
  //
  devServer: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true,
        pathRewrite: { '^/api': '/api' } // optional if your paths match
      }
    }
  }
}