import { NativeModules, NativeEventEmitter, Platform, Image } from 'react-native';

const { ShortcutModule } = NativeModules;
const eventEmitter = new NativeEventEmitter(ShortcutModule);

export default {
  setShortcuts(shortcuts) {
    if (Platform.OS === 'ios') {
      return ShortcutModule.setShortcuts(shortcuts.map(s => ({
        ...s,
        iconName: s.iconName, // asset catalog name
      })));
    } else {
      return ShortcutModule.setShortcuts(shortcuts.map(s => ({
        ...s,
        icon: Image.resolveAssetSource(s.icon)?.uri,
      })));
    }
  },
  getInitialShortcut() {
    return ShortcutModule.getInitialShortcut();
  },
  addListener(callback) {
    return eventEmitter.addListener('onShortcutOpen', callback);
  }
};
