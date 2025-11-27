# TimeCrafted Admin

A separate admin module for managing the TimeCrafted e-commerce application.

## Features

### Dashboard
- **Statistics Overview**: Total products, customers, orders, and revenue
- **Low Stock Alerts**: Monitor products that need restocking
- **Recent Orders**: Quick view of latest orders with status updates
- **Dynamic Data**: Real-time updates from Firebase

### Product Management
- Add, edit, and delete products
- Stock management with low stock alerts
- Category assignment
- Image management via Cloudinary
- Bulk operations support

### Order Management
- View all orders with filtering options
- Update order status (Pending → Confirmed → Shipped → Delivered)
- Order details with customer information
- Delete orders when necessary

### Customer Management
- View all registered customers
- Customer details and statistics
- Order history per customer
- User account management

### Category Management
- Create and manage product categories
- Category-wise product organization
- Image support for categories

### Statistics & Analytics
- Revenue tracking over time
- Product performance metrics
- Customer behavior insights
- Visual charts and graphs

## Technology Stack

- **Frontend**: Android (Kotlin)
- **Backend**: Firebase Realtime Database
- **Authentication**: Firebase Auth
- **Image Storage**: Cloudinary
- **Charts**: MPAndroidChart
- **Architecture**: MVVM with Repository Pattern

## Setup Instructions

1. **Firebase Configuration**:
   - Update `google-services.json` with your Firebase project configuration
   - Ensure the package name is `com.example.timecrafted.admin`

2. **Cloudinary Configuration**:
   - Update `TimeCraftedAdminApplication.kt` with your Cloudinary credentials:
     ```kotlin
     config["cloud_name"] = "your_cloud_name"
     config["api_key"] = "your_api_key"
     config["api_secret"] = "your_api_secret"
     ```

3. **Build and Run**:
   ```bash
   ./gradlew :admin:assembleDebug
   ./gradlew :admin:installDebug
   ```

## Admin Login

Create admin users in Firebase Authentication and use those credentials to log into the admin panel.

## Database Structure

The admin app uses the same Firebase database as the main app:

```
Firebase Database
├── collections/shop/          # Products
├── orders/                    # Customer orders
├── users/                     # Registered customers
└── categories/                # Product categories
```

## Security

⚠️ **Important**: Implement proper admin role checking in production:

1. Create an "admins" collection in Firebase
2. Store admin user IDs
3. Verify admin status before allowing access

## Screenshots

The admin dashboard provides a comprehensive overview with:
- Statistics cards showing key metrics
- Low stock product alerts
- Recent orders list
- Quick access to management sections

## Future Enhancements

- [ ] Advanced analytics with custom date ranges
- [ ] Bulk product import/export
- [ ] Push notifications for critical alerts
- [ ] Advanced user role management
- [ ] Inventory forecasting
- [ ] Sales reporting and insights
