// Mock for react-native-safe-area-context on web
export const SafeAreaProvider = ({ children }) => children
export const SafeAreaView = ({ children }) => children
export const useSafeAreaInsets = () => ({ top: 0, right: 0, bottom: 0, left: 0 })
export default {
  SafeAreaProvider,
  SafeAreaView,
  useSafeAreaInsets,
}