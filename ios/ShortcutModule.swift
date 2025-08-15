import Foundation
import React
import UIKit

@objc(ShortcutModule)
class ShortcutModule: RCTEventEmitter {
    private var hasListeners = false
    private var initialShortcut: [String: Any]?

    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    override func supportedEvents() -> [String]! {
        return ["onShortcutOpen"]
    }

    override func startObserving() {
        hasListeners = true
    }

    override func stopObserving() {
        hasListeners = false
    }

    @objc(setShortcuts:)
    func setShortcuts(_ shortcuts: [[String: Any]]) {
        var items: [UIApplicationShortcutItem] = []
        for shortcut in shortcuts {
            guard let type = shortcut["type"] as? String,
                  let title = shortcut["title"] as? String else { continue }

            let iconName = shortcut["icon"] as? String
            let icon = iconName != nil ? UIApplicationShortcutIcon(templateImageName: iconName!) : nil

            let item = UIApplicationShortcutItem(
                type: type,
                localizedTitle: title,
                localizedSubtitle: shortcut["subtitle"] as? String,
                icon: icon,
                userInfo: nil
            )
            items.append(item)
        }
        DispatchQueue.main.async {
            UIApplication.shared.shortcutItems = items
        }
    }

    @objc(getInitialShortcut:rejecter:)
    func getInitialShortcut(_ resolve: RCTPromiseResolveBlock,
                            rejecter reject: RCTPromiseRejectBlock) {
        resolve(initialShortcut)
        initialShortcut = nil
    }

    func handleShortcutItem(_ shortcutItem: UIApplicationShortcutItem) {
        let data: [String: Any] = [
            "type": shortcutItem.type,
            "title": shortcutItem.localizedTitle,
            "subtitle": shortcutItem.localizedSubtitle ?? ""
        ]

        if bridge == nil || !hasListeners {
            initialShortcut = data
        } else {
            sendEvent(withName: "onShortcutOpen", body: data)
        }
    }
}
