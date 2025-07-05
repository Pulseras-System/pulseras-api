# Cart Functionality Implementation

## Overview

This implementation integrates robust cart functionality into the existing Spring Boot e-commerce API. The cart system automatically manages product quantities and includes automatic cleanup features.

## Key Features

### 1. Integrated Cart Operations

- **createOrder with Cart Support**: When `status=1` (cart) is specified in CreateOrderDTO, the system:
  - Creates a new cart if none exists for the account
  - Returns existing cart if one already exists
  - Adds products to cart if `productId` or `productIds` are provided
  - Automatically increments quantities by 1 for each product addition

### 2. Product Quantity Management

- **Automatic Stock Reservation**: When products are added to cart, their quantities are immediately decreased in the product inventory
- **Stock Validation**: System checks product availability before adding to cart
- **Quantity Restoration**: When cart items are removed or expired, product quantities are restored

### 3. Automatic Cart Cleanup

- **Daily Cleanup**: Scheduled task runs daily at 2:00 AM to clean up carts older than 30 days
- **Item-Level Cleanup**: Additional cleanup at 2:30 AM for individual cart items older than 30 days
- **Quantity Restoration**: All affected product quantities are automatically restored during cleanup

### 4. Enhanced Order Detail Management

- **Cart-Aware Operations**: createOrderDetail method automatically handles cart-specific logic
- **Price Lookup**: Automatically fetches product price if not provided
- **Quantity Increment**: Supports adding quantities to existing cart items

## API Usage Examples

### 1. Create Empty Cart

```json
POST /api/orders
{
  "accountId": "user123",
  "status": 1
}
```

### 2. Create Cart with Single Product

```json
POST /api/orders
{
  "accountId": "user123",
  "status": 1,
  "productId": "product456"
}
```

### 3. Create Cart with Multiple Products

```json
POST /api/orders
{
  "accountId": "user123",
  "status": 1,
  "productIds": ["product456", "product789", "product101"]
}
```

### 4. Add Product to Existing Cart

```json
POST /api/order-details
{
  "orderId": "cart123",
  "productId": "product456",
  "quantity": 2
}
```

### 5. Complete Cart (Convert to Order)

```json
PATCH /api/orders/{cartId}
{
  "status": 2
}
```

## Implementation Details

### Modified Files

#### 1. CreateOrderDTO.java

- Added optional `productId` and `productIds` fields for cart operations

#### 2. OrderServiceImpl.java

- Enhanced `createOrder()` method with cart-specific logic
- Added `handleCartOperation()` helper method
- Added `addProductToCartWithIncrement()` with stock management
- Added `restoreCartProductQuantities()` for manual quantity restoration
- Enhanced `partialUpdateOrder()` to handle cart completion

#### 3. OrderDetailServiceImpl.java

- Enhanced `createOrderDetail()` with cart detection
- Added `handleCartOrderDetail()` with automatic price lookup and stock management
- Enhanced `deleteOrderDetail()` with quantity restoration
- Added `restoreProductQuantity()` helper method

#### 4. CartCleanupService.java (New)

- Scheduled cleanup of expired carts (30+ days old)
- Scheduled cleanup of expired cart items
- Automatic product quantity restoration
- Comprehensive logging and error handling

#### 5. OrderController.java

- Removed deprecated `/add-to-cart` endpoint
- Added manual cleanup endpoints for testing
- Enhanced with proper error handling

#### 6. PulserasApiApplication.java

- Added `@EnableScheduling` annotation

## Business Logic

### Cart Lifecycle

1. **Creation**: Cart created with `status=1` when first product is added
2. **Management**: Products added/removed with automatic quantity tracking
3. **Completion**: Cart converted to order (`status=2+`) - quantities remain reserved (purchased)
4. **Cancellation**: Cart deleted/expired (`status=0`) - quantities restored to inventory
5. **Auto-Cleanup**: After 30 days of inactivity - quantities automatically restored

### Product Quantity Flow

```
Product Added to Cart → Stock Decreased → Reserved for Customer
                ↓
Cart Completed → Stock Remains Decreased (Purchased)
                ↓
Cart Cancelled/Expired → Stock Increased (Restored)
```

### Error Handling

- Product not found → ResourceNotFoundException
- Insufficient stock → IllegalStateException with clear message
- Cart operations → Comprehensive logging and graceful degradation

## Monitoring and Maintenance

### Scheduled Tasks

- **Daily 2:00 AM**: Cleanup expired carts (30+ days)
- **Daily 2:30 AM**: Cleanup expired cart items (30+ days)

### Manual Operations (Testing/Admin)

- `POST /api/orders/cleanup-expired-carts` - Manual cleanup trigger
- `POST /api/orders/restore-cart-quantities/{cartOrderId}` - Manual quantity restoration

### Logging

- All cart operations are logged with details
- Cleanup operations provide comprehensive reports
- Error handling includes detailed error messages

## Benefits

1. **RESTful Design**: Uses standard createOrder and createOrderDetail endpoints
2. **Automatic Management**: No manual inventory tracking needed
3. **Data Integrity**: Prevents overselling through real-time stock management
4. **Performance**: Efficient operations with minimal database calls
5. **Reliability**: Automatic cleanup prevents stale data accumulation
6. **Transparency**: Comprehensive logging for monitoring and debugging

## Migration Notes

- The deprecated `/add-to-cart` endpoint has been removed
- Frontend should use `POST /api/orders` with `status=1` for cart operations
- Existing cart functionality is fully backward compatible
- New quantity management is automatically applied to all cart operations
