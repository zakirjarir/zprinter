# zprinter

this is a printer plugin

## Install

```bash
npm install zakirjarir/zprinter
npx cap sync
```

## API

<docgen-index>

* [`scanDevices()`](#scandevices)
* [`connect(...)`](#connect)
* [`printText(...)`](#printtext)
* [`cut()`](#cut)
* [`disconnect()`](#disconnect)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### scanDevices()

```typescript
scanDevices() => Promise<{ devices: { name: string; address: string; }[]; }>
```

**Returns:** <code>Promise&lt;{ devices: { name: string; address: string; }[]; }&gt;</code>

--------------------


### connect(...)

```typescript
connect(options: { address: string; }) => Promise<{ connected: boolean; deviceName?: string; deviceAddress?: string; }>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ address: string; }</code> |

**Returns:** <code>Promise&lt;{ connected: boolean; deviceName?: string; deviceAddress?: string; }&gt;</code>

--------------------


### printText(...)

```typescript
printText(options: { text: string; }) => Promise<{ printed: boolean; }>
```

| Param         | Type                           |
| ------------- | ------------------------------ |
| **`options`** | <code>{ text: string; }</code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### cut()

```typescript
cut() => Promise<{ cut: boolean; }>
```

**Returns:** <code>Promise&lt;{ cut: boolean; }&gt;</code>

--------------------


### disconnect()

```typescript
disconnect() => Promise<void>
```

--------------------

</docgen-api>
