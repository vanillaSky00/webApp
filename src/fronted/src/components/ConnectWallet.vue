<template>
  <div>
    <div v-if="account" class="connected-box">
      {{ truncateAddress(account) }}
    </div>
    <button v-else @click="openModal" class="connect-button">
      Connect Wallet
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ethers } from 'ethers'
import Web3Modal from 'web3modal'
import WalletConnectProvider from '@walletconnect/web3-provider'
import CoinbaseWalletSDK from '@coinbase/wallet-sdk'

// Reactive state
const provider = ref(null)
const signer = ref(null)
const web3Modal = ref(null)
const account = ref(null)

// Wallet options
const providerOptions = {
  walletconnect: {
    package: WalletConnectProvider,
    options: {
      infuraId: 'YOUR_INFURA_ID' // replace this with your actual Infura ID
    }
  },
  coinbasewallet: {
    package: CoinbaseWalletSDK,
    options: {
      appName: 'My DApp',
      infuraId: 'YOUR_INFURA_ID' // replace this too
    }
  }
}

// Setup Web3Modal
onMounted(() => {
  web3Modal.value = new Web3Modal({
    cacheProvider: false, // set this to false so modal always shows
    providerOptions,
    theme: 'dark',
  })
})

// Open wallet modal
async function openModal() {
  try {
    await web3Modal.value.clearCachedProvider() // force panel to show again
    const instance = await web3Modal.value.connect()

    provider.value = new ethers.providers.Web3Provider(instance)
    signer.value = provider.value.getSigner()
    account.value = await signer.value.getAddress()
    console.log('Connected account:', account.value)
  } catch (err) {
    console.error('Wallet connection failed:', err)
  }
}

// Shorten address for display
function truncateAddress(addr) {
  return addr.slice(0, 6) + '...' + addr.slice(-4)
}
</script>

<style scoped>
.connect-button {
  background-color: #2081e2;
  color: white;
  padding: 10px 20px;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.connected-box {
  background: #1a1a1a;
  padding: 10px 20px;
  border-radius: 8px;
  color: #0ff;
  font-weight: bold;
}
</style>
