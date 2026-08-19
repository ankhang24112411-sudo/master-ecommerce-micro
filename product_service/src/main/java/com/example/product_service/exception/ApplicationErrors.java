package com.example.product_service.exception;



public class ApplicationErrors {

    public static final ApplicationException CART_CHANGED_DURING_CHECKOUT = new ApplicationException(
            401111,
            401,
            "Cart items changed during checkout"
    );
    public static final Exception INVENTORY_NOT_ENOUGH_STOCK = new ApplicationException(
            401001,
            401,
            "Username or password is incorrect"
    );
    // AUTHENTICATION
    public static ApplicationException INVALID_CREDENTIALS =
            new ApplicationException(
                    401001,
                    401,
                    "Username or password is incorrect"
            );
    public static ApplicationException SINGLE_INVENTORY_NOT_ENOUGH_STOCK =
            new ApplicationException(
                    401001,
                    401,
                    "single inventory don't have enough stock, please adjust the quantity"
            );
    public static ApplicationException INVALID_TOKEN =
            new ApplicationException(
                    401002,
                    401,
                    "Token is invalid"
            );

    public static ApplicationException TOKEN_EXPIRED =
            new ApplicationException(
                    401003,
                    401,
                    "Token has expired"
            );

    public static ApplicationException ACCESS_DENIED =
            new ApplicationException(
                    403001,
                    403,
                    "User unauthorized"
            );

    // USER
    public static ApplicationException USER_NOT_FOUND =
            new ApplicationException(
                    404001,
                    404,
                    "User not found"
            );

    public static ApplicationException USERNAME_EXISTED =
            new ApplicationException(
                    409001,
                    409,
                    "Username existed"
            );

    public static ApplicationException EMAIL_EXISTED =
            new ApplicationException(
                    409002,
                    409,
                    "Email existed"
            );

    public static ApplicationException PASSWORD_NOT_MATCHED =
            new ApplicationException(
                    400001,
                    400,
                    "Password not matched"
            );

    // PRODUCT
    public static ApplicationException PRODUCT_NOT_FOUND =
            new ApplicationException(
                    404101,
                    404,
                    "Product not found"
            );

    public static ApplicationException PRODUCT_NOT_AVAILABLE =
            new ApplicationException(
                    409101,
                    409,
                    "Product is not available"
            );

    // INVENTORY
    public static ApplicationException INVENTORY_NOT_FOUND =
            new ApplicationException(
                    404201,
                    404,
                    "Inventory not found"
            );

    public static ApplicationException OUT_OF_STOCK =
            new ApplicationException(
                    409201,
                    409,
                    "Product is out of stock"
            );

    public static ApplicationException INSUFFICIENT_STOCK =
            new ApplicationException(
                    409202,
                    409,
                    "Requested quantity exceeds available stock"
            );

    public static ApplicationException INVALID_QUANTITY =
            new ApplicationException(
                    400201,
                    400,
                    "Quantity must be greater than zero"
            );

    // CART
    public static ApplicationException CART_NOT_FOUND =
            new ApplicationException(
                    404301,
                    404,
                    "Cart not found"
            );

    public static ApplicationException CART_ITEM_NOT_FOUND =
            new ApplicationException(
                    404302,
                    404,
                    "Cart item not found"
            );

    public static ApplicationException CART_ITEM_EXISTED =
            new ApplicationException(
                    409301,
                    409,
                    "Product already exists in cart"
            );

    public static ApplicationException CART_EMPTY =
            new ApplicationException(
                    400301,
                    400,
                    "Cart is empty"
            );

    public static ApplicationException DISCOUNT_NOT_FOUND =
            new ApplicationException(
                    404401,
                    404,
                    "Discount not found"
            );

    public static ApplicationException DISCOUNT_EXPIRED =
            new ApplicationException(
                    409401,
                    409,
                    "Discount has expired"
            );

    public static ApplicationException DISCOUNT_NOT_ACTIVE =
            new ApplicationException(
                    409402,
                    409,
                    "Discount is not active"
            );

    public static ApplicationException DISCOUNT_USAGE_LIMIT_REACHED =
            new ApplicationException(
                    409403,
                    409,
                    "Discount usage limit has been reached"
            );

    // DELIVERY
    public static ApplicationException DELIVERY_ROUTE_NOT_FOUND =
            new ApplicationException(
                    404501,
                    404,
                    "No delivery route is available"
            );

    public static ApplicationException DELIVERY_FEE_NOT_FOUND =
            new ApplicationException(
                    404502,
                    404,
                    "Delivery fee not found"
            );

    // ORDER
    public static ApplicationException ORDER_NOT_FOUND =
            new ApplicationException(
                    404601,
                    404,
                    "Order not found"
            );

    public static ApplicationException ORDER_CANNOT_BE_CREATED =
            new ApplicationException(
                    409601,
                    409,
                    "Order cannot be created"
            );

    public static ApplicationException ORDER_CANNOT_BE_CANCELLED =
            new ApplicationException(
                    409602,
                    409,
                    "Order cannot be cancelled"
            );

    // PAYMENT
    public static ApplicationException PAYMENT_NOT_FOUND =
            new ApplicationException(
                    404701,
                    404,
                    "Payment not found"
            );

    public static ApplicationException PAYMENT_FAILED =
            new ApplicationException(
                    400701,
                    400,
                    "Payment failed"
            );

    public static ApplicationException ORDER_ALREADY_PAID =
            new ApplicationException(
                    409701,
                    409,
                    "Order has already been paid"
            );
    // AUTHENTICATION - ADDITIONAL
    public static ApplicationException AUTHENTICATION_REQUIRED =
            new ApplicationException(
                    401004,
                    401,
                    "Authentication is required"
            );

    public static ApplicationException REFRESH_TOKEN_EXPIRED =
            new ApplicationException(
                    401005,
                    401,
                    "Refresh token has expired"
            );

    public static ApplicationException RESET_TOKEN_INVALID =
            new ApplicationException(
                    401006,
                    401,
                    "Reset token is invalid"
            );

    public static ApplicationException RESET_TOKEN_EXPIRED =
            new ApplicationException(
                    401007,
                    401,
                    "Reset token has expired"
            );

    public static ApplicationException USER_ACCOUNT_DISABLED =
            new ApplicationException(
                    403002,
                    403,
                    "User account is disabled"
            );

    // USER - ADDITIONAL
    public static ApplicationException INVALID_USER_STATUS =
            new ApplicationException(
                    400002,
                    400,
                    "Invalid user status"
            );

    public static ApplicationException PHONE_EXISTED =
            new ApplicationException(
                    409003,
                    409,
                    "Phone number existed"
            );

    public static ApplicationException ADDRESS_NOT_FOUND =
            new ApplicationException(
                    404002,
                    404,
                    "Address not found"
            );

    public static ApplicationException ADDRESS_NOT_BELONG_TO_USER =
            new ApplicationException(
                    403003,
                    403,
                    "Address does not belong to current user"
            );

    // PRODUCT - ADDITIONAL
    public static ApplicationException PRODUCT_EXISTED =
            new ApplicationException(
                    409102,
                    409,
                    "Product already exists"
            );

    public static ApplicationException SKU_EXISTED =
            new ApplicationException(
                    409103,
                    409,
                    "Product SKU already exists"
            );

    public static ApplicationException PRODUCT_INACTIVE =
            new ApplicationException(
                    409104,
                    409,
                    "Product is inactive"
            );

    public static ApplicationException CATEGORY_NOT_FOUND =
            new ApplicationException(
                    404102,
                    404,
                    "Category not found"
            );

    public static ApplicationException PRODUCT_IMAGE_NOT_FOUND =
            new ApplicationException(
                    404103,
                    404,
                    "Product image not found"
            );

    // INVENTORY - ADDITIONAL
    public static ApplicationException INVALID_INVENTORY_STATUS =
            new ApplicationException(
                    400202,
                    400,
                    "Invalid inventory status"
            );

    public static ApplicationException STOCK_RESERVATION_FAILED =
            new ApplicationException(
                    409203,
                    409,
                    "Stock reservation failed"
            );

    public static ApplicationException STOCK_ALREADY_RESERVED =
            new ApplicationException(
                    409204,
                    409,
                    "Stock has already been reserved"
            );

    public static ApplicationException INVENTORY_UPDATE_CONFLICT =
            new ApplicationException(
                    409205,
                    409,
                    "Inventory has been updated by another transaction"
            );

    // CART - ADDITIONAL
    public static ApplicationException CART_ITEM_NOT_BELONG_TO_USER =
            new ApplicationException(
                    403301,
                    403,
                    "Cart item does not belong to current user"
            );

    public static ApplicationException CART_ALREADY_HAS_DISCOUNT =
            new ApplicationException(
                    409302,
                    409,
                    "Cart already has a discount"
            );

    public static ApplicationException CART_QUANTITY_LIMIT_EXCEEDED =
            new ApplicationException(
                    409303,
                    409,
                    "Cart item quantity limit exceeded"
            );

    public static ApplicationException PRODUCT_NOT_IN_CART =
            new ApplicationException(
                    404303,
                    404,
                    "Product is not in cart"
            );

    // DISCOUNT - ADDITIONAL
    public static ApplicationException DISCOUNT_ALREADY_APPLIED =
            new ApplicationException(
                    409404,
                    409,
                    "Discount has already been applied"
            );

    public static ApplicationException DISCOUNT_NOT_APPLICABLE =
            new ApplicationException(
                    409405,
                    409,
                    "Discount is not applicable to this order"
            );

    public static ApplicationException MINIMUM_ORDER_NOT_REACHED =
            new ApplicationException(
                    400401,
                    400,
                    "Minimum order amount has not been reached"
            );

    public static ApplicationException INVALID_DISCOUNT_TYPE =
            new ApplicationException(
                    400402,
                    400,
                    "Invalid discount type"
            );

    public static ApplicationException DISCOUNT_START_DATE_NOT_REACHED =
            new ApplicationException(
                    409406,
                    409,
                    "Discount is not available yet"
            );

    // DELIVERY - ADDITIONAL
    public static ApplicationException STATE_NOT_FOUND =
            new ApplicationException(
                    404503,
                    404,
                    "State not found"
            );

    public static ApplicationException DELIVERY_COMPANY_NOT_FOUND =
            new ApplicationException(
                    404504,
                    404,
                    "Delivery company not found"
            );

    public static ApplicationException DELIVERY_ADDRESS_NOT_FOUND =
            new ApplicationException(
                    404505,
                    404,
                    "Delivery address not found"
            );

    public static ApplicationException INVALID_DELIVERY_ROUTE =
            new ApplicationException(
                    400501,
                    400,
                    "Invalid delivery route"
            );

    public static ApplicationException DELIVERY_NOT_AVAILABLE =
            new ApplicationException(
                    409501,
                    409,
                    "Delivery is not available for this address"
            );

    // ORDER - ADDITIONAL
    public static ApplicationException ORDER_ITEM_NOT_FOUND =
            new ApplicationException(
                    404602,
                    404,
                    "Order item not found"
            );

    public static ApplicationException SUB_ORDER_NOT_FOUND =
            new ApplicationException(
                    404603,
                    404,
                    "Sub order not found"
            );

    public static ApplicationException ORDER_NOT_BELONG_TO_USER =
            new ApplicationException(
                    403601,
                    403,
                    "Order does not belong to current user"
            );

    public static ApplicationException INVALID_ORDER_STATUS =
            new ApplicationException(
                    400601,
                    400,
                    "Invalid order status"
            );

    public static ApplicationException ORDER_ALREADY_CANCELLED =
            new ApplicationException(
                    409603,
                    409,
                    "Order has already been cancelled"
            );

    public static ApplicationException ORDER_ALREADY_COMPLETED =
            new ApplicationException(
                    409604,
                    409,
                    "Order has already been completed"
            );

    public static ApplicationException ORDER_ALREADY_CREATED =
            new ApplicationException(
                    409605,
                    409,
                    "Order has already been created"
            );

    // PAYMENT - ADDITIONAL
    public static ApplicationException INVALID_PAYMENT_METHOD =
            new ApplicationException(
                    400702,
                    400,
                    "Invalid payment method"
            );

    public static ApplicationException PAYMENT_ALREADY_PROCESSED =
            new ApplicationException(
                    409702,
                    409,
                    "Payment has already been processed"
            );

    public static ApplicationException PAYMENT_AMOUNT_NOT_MATCHED =
            new ApplicationException(
                    400703,
                    400,
                    "Payment amount does not match order amount"
            );

    public static ApplicationException PAYMENT_CANCELLED =
            new ApplicationException(
                    409703,
                    409,
                    "Payment has been cancelled"
            );

    // SHOP
    public static ApplicationException SHOP_NOT_FOUND =
            new ApplicationException(
                    404801,
                    404,
                    "Shop not found"
            );

    public static ApplicationException SHOP_EXISTED =
            new ApplicationException(
                    409801,
                    409,
                    "Shop already exists"
            );

    public static ApplicationException SHOP_INACTIVE =
            new ApplicationException(
                    409802,
                    409,
                    "Shop is inactive"
            );

    public static ApplicationException SHOP_NOT_BELONG_TO_USER =
            new ApplicationException(
                    403801,
                    403,
                    "Shop does not belong to current user"
            );

    // RETURN
    public static ApplicationException RETURN_REQUEST_NOT_FOUND =
            new ApplicationException(
                    404901,
                    404,
                    "Return request not found"
            );

    public static ApplicationException RETURN_ITEM_NOT_FOUND =
            new ApplicationException(
                    404902,
                    404,
                    "Return item not found"
            );

    public static ApplicationException RETURN_NOT_ALLOWED =
            new ApplicationException(
                    409901,
                    409,
                    "Return is not allowed for this order"
            );

    public static ApplicationException RETURN_PERIOD_EXPIRED =
            new ApplicationException(
                    409902,
                    409,
                    "Return period has expired"
            );

    public static ApplicationException INVALID_RETURN_STATUS =
            new ApplicationException(
                    400901,
                    400,
                    "Invalid return status"
            );

    public static ApplicationException RETURN_ALREADY_REQUESTED =
            new ApplicationException(
                    409903,
                    409,
                    "Return has already been requested"
            );
    // TOKEN
    public static ApplicationException TOKEN_NOT_FOUND =
            new ApplicationException(
                    404010,
                    404,
                    "Token not found"
            );

    public static ApplicationException REFRESH_TOKEN_INVALID =
            new ApplicationException(
                    401008,
                    401,
                    "Refresh token is invalid"
            );

    public static ApplicationException TOKEN_REVOKED =
            new ApplicationException(
                    401009,
                    401,
                    "Token has been revoked"
            );

    // ROLE
    public static ApplicationException ROLE_NOT_FOUND =
            new ApplicationException(
                    404011,
                    404,
                    "Role not found"
            );

    public static ApplicationException ROLE_ALREADY_ASSIGNED =
            new ApplicationException(
                    409011,
                    409,
                    "Role has already been assigned to user"
            );

    // ADDRESS
    public static ApplicationException ADDRESS_ALREADY_EXISTS =
            new ApplicationException(
                    409004,
                    409,
                    "Address already exists"
            );

    public static ApplicationException DEFAULT_ADDRESS_NOT_FOUND =
            new ApplicationException(
                    404003,
                    404,
                    "Default address not found"
            );

    // PRODUCT
    public static ApplicationException PRODUCT_PRICE_INVALID =
            new ApplicationException(
                    400101,
                    400,
                    "Product price must be greater than zero"
            );

    public static ApplicationException PRODUCT_CANNOT_BE_DELETED =
            new ApplicationException(
                    409105,
                    409,
                    "Product cannot be deleted in its current state"
            );

    public static ApplicationException CATEGORY_ALREADY_EXISTS =
            new ApplicationException(
                    409106,
                    409,
                    "Category already exists"
            );

    // WAREHOUSE
    public static ApplicationException WAREHOUSE_NOT_FOUND =
            new ApplicationException(
                    404204,
                    404,
                    "Warehouse not found"
            );

    public static ApplicationException WAREHOUSE_INACTIVE =
            new ApplicationException(
                    409206,
                    409,
                    "Warehouse is inactive"
            );

    // CART
    public static ApplicationException CART_NOT_BELONG_TO_USER =
            new ApplicationException(
                    403302,
                    403,
                    "Cart does not belong to current user"
            );

    public static ApplicationException CART_ITEM_QUANTITY_UNCHANGED =
            new ApplicationException(
                    400302,
                    400,
                    "New quantity must be different from current quantity"
            );

    public static ApplicationException CART_ITEM_LIMIT_REACHED =
            new ApplicationException(
                    409304,
                    409,
                    "Maximum number of cart items has been reached"
            );

    // DELIVERY
    public static ApplicationException DELIVERY_ROUTE_ALREADY_EXISTS =
            new ApplicationException(
                    409502,
                    409,
                    "Delivery route already exists"
            );

    public static ApplicationException DELIVERY_FEE_ALREADY_EXISTS =
            new ApplicationException(
                    409503,
                    409,
                    "Delivery fee already exists for this route"
            );

    public static ApplicationException INVALID_DELIVERY_FEE =
            new ApplicationException(
                    400502,
                    400,
                    "Delivery fee must not be negative"
            );

    // ORDER
    public static ApplicationException ORDER_EMPTY =
            new ApplicationException(
                    400602,
                    400,
                    "Order must contain at least one item"
            );

    public static ApplicationException ORDER_ALREADY_DELIVERED =
            new ApplicationException(
                    409606,
                    409,
                    "Order has already been delivered"
            );

    public static ApplicationException ORDER_NOT_PAID =
            new ApplicationException(
                    409607,
                    409,
                    "Order has not been paid"
            );

    public static ApplicationException ORDER_AMOUNT_INVALID =
            new ApplicationException(
                    400603,
                    400,
                    "Order amount is invalid"
            );

    // PAYMENT
    public static ApplicationException INVALID_PAYMENT_STATUS =
            new ApplicationException(
                    400704,
                    400,
                    "Invalid payment status"
            );

    public static ApplicationException PAYMENT_TIMEOUT =
            new ApplicationException(
                    408701,
                    408,
                    "Payment request timed out"
            );

    public static ApplicationException PAYMENT_ALREADY_CANCELLED =
            new ApplicationException(
                    409704,
                    409,
                    "Payment has already been cancelled"
            );

    // REFUND
    public static ApplicationException REFUND_NOT_FOUND =
            new ApplicationException(
                    404703,
                    404,
                    "Refund not found"
            );

    public static ApplicationException REFUND_FAILED =
            new ApplicationException(
                    409705,
                    409,
                    "Refund failed"
            );

    public static ApplicationException REFUND_ALREADY_PROCESSED =
            new ApplicationException(
                    409706,
                    409,
                    "Refund has already been processed"
            );

    public static ApplicationException REFUND_AMOUNT_INVALID =
            new ApplicationException(
                    400705,
                    400,
                    "Refund amount is invalid"
            );
    // AUTHENTICATION - MORE
    public static ApplicationException SESSION_EXPIRED =
            new ApplicationException(
                    401010,
                    401,
                    "Session has expired"
            );

    public static ApplicationException TOKEN_TYPE_INVALID =
            new ApplicationException(
                    401011,
                    401,
                    "Token type is invalid"
            );

    public static ApplicationException EMAIL_NOT_VERIFIED =
            new ApplicationException(
                    403004,
                    403,
                    "Email has not been verified"
            );

    public static ApplicationException PASSWORD_RESET_REQUIRED =
            new ApplicationException(
                    403005,
                    403,
                    "Password reset is required"
            );

    public static ApplicationException ACCOUNT_LOCKED =
            new ApplicationException(
                    423001,
                    423,
                    "User account is locked"
            );

    // USER - MORE
    public static ApplicationException USER_PROFILE_NOT_FOUND =
            new ApplicationException(
                    404004,
                    404,
                    "User profile not found"
            );

    public static ApplicationException INVALID_PHONE_NUMBER =
            new ApplicationException(
                    400003,
                    400,
                    "Phone number is invalid"
            );

    public static ApplicationException INVALID_BIRTHDAY =
            new ApplicationException(
                    400004,
                    400,
                    "Birthday is invalid"
            );

    public static ApplicationException CURRENT_PASSWORD_INCORRECT =
            new ApplicationException(
                    400005,
                    400,
                    "Current password is incorrect"
            );

    public static ApplicationException NEW_PASSWORD_SAME_AS_OLD =
            new ApplicationException(
                    400006,
                    400,
                    "New password must be different from old password"
            );

    public static ApplicationException USER_ALREADY_DELETED =
            new ApplicationException(
                    409005,
                    409,
                    "User has already been deleted"
            );

    public static ApplicationException USER_UPDATE_CONFLICT =
            new ApplicationException(
                    409006,
                    409,
                    "User information has been updated by another request"
            );

    // ADDRESS - MORE
    public static ApplicationException INVALID_POSTAL_CODE =
            new ApplicationException(
                    400007,
                    400,
                    "Postal code is invalid"
            );

    public static ApplicationException INVALID_ADDRESS =
            new ApplicationException(
                    400008,
                    400,
                    "Address is invalid"
            );

    public static ApplicationException ADDRESS_LIMIT_REACHED =
            new ApplicationException(
                    409007,
                    409,
                    "Maximum number of addresses has been reached"
            );

    public static ApplicationException DEFAULT_ADDRESS_CANNOT_BE_DELETED =
            new ApplicationException(
                    409008,
                    409,
                    "Default address cannot be deleted"
            );

    // PRODUCT - MORE
    public static ApplicationException BRAND_NOT_FOUND =
            new ApplicationException(
                    404104,
                    404,
                    "Brand not found"
            );

    public static ApplicationException PRODUCT_VARIANT_NOT_FOUND =
            new ApplicationException(
                    404105,
                    404,
                    "Product variant not found"
            );

    public static ApplicationException SKU_NOT_FOUND =
            new ApplicationException(
                    404106,
                    404,
                    "Product SKU not found"
            );

    public static ApplicationException INVALID_PRODUCT_STATUS =
            new ApplicationException(
                    400102,
                    400,
                    "Product status is invalid"
            );

    public static ApplicationException INVALID_PRODUCT_PRICE =
            new ApplicationException(
                    400103,
                    400,
                    "Product price is invalid"
            );

    public static ApplicationException INVALID_PRODUCT_NAME =
            new ApplicationException(
                    400104,
                    400,
                    "Product name is invalid"
            );

    public static ApplicationException PRODUCT_VARIANT_EXISTED =
            new ApplicationException(
                    409107,
                    409,
                    "Product variant already exists"
            );

    public static ApplicationException PRODUCT_HAS_ACTIVE_ORDER =
            new ApplicationException(
                    409108,
                    409,
                    "Product is currently used in an active order"
            );

    public static ApplicationException PRODUCT_IMAGE_LIMIT_REACHED =
            new ApplicationException(
                    409109,
                    409,
                    "Maximum number of product images has been reached"
            );

    // REVIEW
    public static ApplicationException REVIEW_NOT_FOUND =
            new ApplicationException(
                    404110,
                    404,
                    "Review not found"
            );

    public static ApplicationException REVIEW_ALREADY_EXISTS =
            new ApplicationException(
                    409110,
                    409,
                    "User has already reviewed this product"
            );

    public static ApplicationException REVIEW_NOT_ALLOWED =
            new ApplicationException(
                    403110,
                    403,
                    "User is not allowed to review this product"
            );

    public static ApplicationException INVALID_RATING =
            new ApplicationException(
                    400110,
                    400,
                    "Rating must be between 1 and 5"
            );

    // WISHLIST
    public static ApplicationException WISHLIST_NOT_FOUND =
            new ApplicationException(
                    404111,
                    404,
                    "Wishlist not found"
            );

    public static ApplicationException WISHLIST_ITEM_NOT_FOUND =
            new ApplicationException(
                    404112,
                    404,
                    "Wishlist item not found"
            );

    public static ApplicationException PRODUCT_ALREADY_IN_WISHLIST =
            new ApplicationException(
                    409111,
                    409,
                    "Product already exists in wishlist"
            );

    // INVENTORY - MORE
    public static ApplicationException NEGATIVE_STOCK =
            new ApplicationException(
                    400203,
                    400,
                    "Inventory quantity cannot be negative"
            );

    public static ApplicationException STOCK_NOT_RESERVED =
            new ApplicationException(
                    409207,
                    409,
                    "Stock has not been reserved"
            );

    public static ApplicationException STOCK_RELEASE_FAILED =
            new ApplicationException(
                    409208,
                    409,
                    "Reserved stock could not be released"
            );

    public static ApplicationException WAREHOUSE_CAPACITY_EXCEEDED =
            new ApplicationException(
                    409209,
                    409,
                    "Warehouse capacity has been exceeded"
            );

    public static ApplicationException INVENTORY_LOCKED =
            new ApplicationException(
                    409210,
                    409,
                    "Inventory is currently locked"
            );

    // CART - MORE
    public static ApplicationException MAX_QUANTITY_EXCEEDED =
            new ApplicationException(
                    400303,
                    400,
                    "Maximum allowed quantity has been exceeded"
            );

    public static ApplicationException CART_ITEM_ALREADY_REMOVED =
            new ApplicationException(
                    409305,
                    409,
                    "Cart item has already been removed"
            );

    public static ApplicationException CART_CHECKOUT_IN_PROGRESS =
            new ApplicationException(
                    409306,
                    409,
                    "Cart checkout is already in progress"
            );

    public static ApplicationException CART_PRICE_CHANGED =
            new ApplicationException(
                    409307,
                    409,
                    "Cart price has changed"
            );

    public static ApplicationException CART_ITEM_PRICE_CHANGED =
            new ApplicationException(
                    409308,
                    409,
                    "Product price in cart has changed"
            );

    public static ApplicationException CART_MERGE_FAILED =
            new ApplicationException(
                    409309,
                    409,
                    "Guest cart could not be merged with user cart"
            );

    // DISCOUNT - MORE
    public static ApplicationException DISCOUNT_CODE_ALREADY_EXISTS =
            new ApplicationException(
                    409407,
                    409,
                    "Discount code already exists"
            );

    public static ApplicationException DISCOUNT_QUOTA_EXCEEDED =
            new ApplicationException(
                    409408,
                    409,
                    "Discount quota has been exceeded"
            );

    public static ApplicationException DISCOUNT_USER_LIMIT_REACHED =
            new ApplicationException(
                    409409,
                    409,
                    "User discount usage limit has been reached"
            );

    public static ApplicationException DISCOUNT_PRODUCT_NOT_ELIGIBLE =
            new ApplicationException(
                    409410,
                    409,
                    "Product is not eligible for this discount"
            );

    public static ApplicationException DISCOUNT_SHOP_NOT_ELIGIBLE =
            new ApplicationException(
                    409411,
                    409,
                    "Shop is not eligible for this discount"
            );

    public static ApplicationException INVALID_DISCOUNT_VALUE =
            new ApplicationException(
                    400403,
                    400,
                    "Discount value is invalid"
            );

    public static ApplicationException INVALID_DISCOUNT_PERIOD =
            new ApplicationException(
                    400404,
                    400,
                    "Discount start and end time are invalid"
            );

    // DELIVERY - MORE
    public static ApplicationException DELIVERY_METHOD_NOT_FOUND =
            new ApplicationException(
                    404506,
                    404,
                    "Delivery method not found"
            );

    public static ApplicationException SHIPMENT_NOT_FOUND =
            new ApplicationException(
                    404507,
                    404,
                    "Shipment not found"
            );

    public static ApplicationException TRACKING_NUMBER_NOT_FOUND =
            new ApplicationException(
                    404508,
                    404,
                    "Tracking number not found"
            );

    public static ApplicationException DELIVERY_COMPANY_INACTIVE =
            new ApplicationException(
                    409504,
                    409,
                    "Delivery company is inactive"
            );

    public static ApplicationException SHIPMENT_ALREADY_CREATED =
            new ApplicationException(
                    409505,
                    409,
                    "Shipment has already been created"
            );

    public static ApplicationException SHIPMENT_ALREADY_DELIVERED =
            new ApplicationException(
                    409506,
                    409,
                    "Shipment has already been delivered"
            );

    public static ApplicationException INVALID_SHIPMENT_STATUS =
            new ApplicationException(
                    400503,
                    400,
                    "Shipment status is invalid"
            );

    public static ApplicationException INVALID_TRACKING_NUMBER =
            new ApplicationException(
                    400504,
                    400,
                    "Tracking number is invalid"
            );

    // ORDER - MORE
    public static ApplicationException ORDER_ITEM_OUT_OF_STOCK =
            new ApplicationException(
                    409608,
                    409,
                    "One or more order items are out of stock"
            );

    public static ApplicationException ORDER_PRICE_CHANGED =
            new ApplicationException(
                    409609,
                    409,
                    "Order price has changed"
            );

    public static ApplicationException ORDER_ALREADY_CONFIRMED =
            new ApplicationException(
                    409610,
                    409,
                    "Order has already been confirmed"
            );

    public static ApplicationException ORDER_NOT_RETURNABLE =
            new ApplicationException(
                    409611,
                    409,
                    "Order is not eligible for return"
            );

    public static ApplicationException ORDER_PAYMENT_REQUIRED =
            new ApplicationException(
                    409612,
                    409,
                    "Payment is required before processing this order"
            );

    public static ApplicationException ORDER_PROCESSING =
            new ApplicationException(
                    409613,
                    409,
                    "Order is currently being processed"
            );

    public static ApplicationException ORDER_STATUS_TRANSITION_NOT_ALLOWED =
            new ApplicationException(
                    409614,
                    409,
                    "Order status transition is not allowed"
            );

    public static ApplicationException ORDER_ADDRESS_INVALID =
            new ApplicationException(
                    400604,
                    400,
                    "Order delivery address is invalid"
            );

    // PAYMENT - MORE
    public static ApplicationException PAYMENT_TRANSACTION_NOT_FOUND =
            new ApplicationException(
                    404704,
                    404,
                    "Payment transaction not found"
            );

    public static ApplicationException PAYMENT_METHOD_NOT_AVAILABLE =
            new ApplicationException(
                    409707,
                    409,
                    "Payment method is not available"
            );

    public static ApplicationException PAYMENT_SIGNATURE_INVALID =
            new ApplicationException(
                    400706,
                    400,
                    "Payment signature is invalid"
            );

    public static ApplicationException PAYMENT_PROVIDER_UNAVAILABLE =
            new ApplicationException(
                    503701,
                    503,
                    "Payment provider is currently unavailable"
            );

    public static ApplicationException PAYMENT_CURRENCY_NOT_SUPPORTED =
            new ApplicationException(
                    400707,
                    400,
                    "Payment currency is not supported"
            );

    public static ApplicationException DUPLICATE_PAYMENT_REQUEST =
            new ApplicationException(
                    409708,
                    409,
                    "Duplicate payment request"
            );

    // REFUND - MORE
    public static ApplicationException REFUND_NOT_ALLOWED =
            new ApplicationException(
                    409709,
                    409,
                    "Refund is not allowed for this payment"
            );

    public static ApplicationException REFUND_EXCEEDS_PAYMENT_AMOUNT =
            new ApplicationException(
                    400708,
                    400,
                    "Refund amount exceeds paid amount"
            );

    public static ApplicationException PARTIAL_REFUND_NOT_ALLOWED =
            new ApplicationException(
                    409710,
                    409,
                    "Partial refund is not allowed"
            );

    // SHOP - MORE
    public static ApplicationException SHOP_OWNER_NOT_FOUND =
            new ApplicationException(
                    404802,
                    404,
                    "Shop owner not found"
            );

    public static ApplicationException SHOP_ALREADY_VERIFIED =
            new ApplicationException(
                    409803,
                    409,
                    "Shop has already been verified"
            );

    public static ApplicationException SHOP_NOT_VERIFIED =
            new ApplicationException(
                    403802,
                    403,
                    "Shop has not been verified"
            );

    public static ApplicationException SHOP_SUSPENDED =
            new ApplicationException(
                    403803,
                    403,
                    "Shop has been suspended"
            );

    public static ApplicationException SHOP_CANNOT_BE_DELETED =
            new ApplicationException(
                    409804,
                    409,
                    "Shop cannot be deleted in its current state"
            );

    // RETURN - MORE
    public static ApplicationException RETURN_QUANTITY_EXCEEDS_PURCHASED =
            new ApplicationException(
                    400902,
                    400,
                    "Return quantity exceeds purchased quantity"
            );

    public static ApplicationException RETURN_ALREADY_APPROVED =
            new ApplicationException(
                    409904,
                    409,
                    "Return request has already been approved"
            );
    public static ApplicationException PAYMENT_REFERENCE_CODE_NOT_FOUND =
            new ApplicationException(
                    409904,
                    409,
                    "Payment reference not found"
            );

    public static ApplicationException RETURN_ALREADY_REJECTED =
            new ApplicationException(
                    409905,
                    409,
                    "Return request has already been rejected"
            );

    public static ApplicationException RETURN_SHIPMENT_NOT_RECEIVED =
            new ApplicationException(
                    409906,
                    409,
                    "Returned shipment has not been received"
            );

    public static ApplicationException INVALID_RETURN_REASON =
            new ApplicationException(
                    400903,
                    400,
                    "Return reason is invalid"
            );
    public static ApplicationException INVENTORY_NOT_ENOUGH =
            new ApplicationException(
                    400904,
                    400,
                    "Not enough stock for product"
            );
    public static ApplicationException DISCOUNT_NOT_HAVE =
            new ApplicationException(
                    400905,
                    400,
                    "Not enough discount"
            );
    public static ApplicationException EMAIL_NOT_FOUND =
            new ApplicationException(
                    400906,
                    400,
                    "Email not found for product"
            );

}

