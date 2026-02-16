# zprinter

this is a printer plugin

## Install

```bash
npm install zprinter
npx cap sync
```

## API

<docgen-index>

* [`connect(...)`](#connect)
* [`printText(...)`](#printtext)
* [`cut()`](#cut)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### connect(...)

```typescript
connect(options: { address: string; }) => Promise<{ connected: boolean; }>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ address: string; }</code> |

**Returns:** <code>Promise&lt;{ connected: boolean; }&gt;</code>

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

</docgen-api>
