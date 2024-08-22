import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const UserDetails = () => import('@/entities/user-details/user-details.vue');
const UserDetailsUpdate = () => import('@/entities/user-details/user-details-update.vue');
const UserDetailsDetails = () => import('@/entities/user-details/user-details-details.vue');

const StaffInfo = () => import('@/entities/staff-info/staff-info.vue');
const StaffInfoUpdate = () => import('@/entities/staff-info/staff-info-update.vue');
const StaffInfoDetails = () => import('@/entities/staff-info/staff-info-details.vue');

const Address = () => import('@/entities/address/address.vue');
const AddressUpdate = () => import('@/entities/address/address-update.vue');
const AddressDetails = () => import('@/entities/address/address-details.vue');

const Colour = () => import('@/entities/colour/colour.vue');
const ColourUpdate = () => import('@/entities/colour/colour-update.vue');
const ColourDetails = () => import('@/entities/colour/colour-details.vue');

const Conversation = () => import('@/entities/conversation/conversation.vue');
const ConversationUpdate = () => import('@/entities/conversation/conversation-update.vue');
const ConversationDetails = () => import('@/entities/conversation/conversation-details.vue');

const Participant = () => import('@/entities/participant/participant.vue');
const ParticipantUpdate = () => import('@/entities/participant/participant-update.vue');
const ParticipantDetails = () => import('@/entities/participant/participant-details.vue');

const InvoiceStatus = () => import('@/entities/invoice-status/invoice-status.vue');
const InvoiceStatusUpdate = () => import('@/entities/invoice-status/invoice-status-update.vue');
const InvoiceStatusDetails = () => import('@/entities/invoice-status/invoice-status-details.vue');

const Invoice = () => import('@/entities/invoice/invoice.vue');
const InvoiceUpdate = () => import('@/entities/invoice/invoice-update.vue');
const InvoiceDetails = () => import('@/entities/invoice/invoice-details.vue');

const Message = () => import('@/entities/message/message.vue');
const MessageUpdate = () => import('@/entities/message/message-update.vue');
const MessageDetails = () => import('@/entities/message/message-details.vue');

const OrderItem = () => import('@/entities/order-item/order-item.vue');
const OrderItemUpdate = () => import('@/entities/order-item/order-item-update.vue');
const OrderItemDetails = () => import('@/entities/order-item/order-item-details.vue');

const OrderStatus = () => import('@/entities/order-status/order-status.vue');
const OrderStatusUpdate = () => import('@/entities/order-status/order-status-update.vue');
const OrderStatusDetails = () => import('@/entities/order-status/order-status-details.vue');

const Order = () => import('@/entities/order/order.vue');
const OrderUpdate = () => import('@/entities/order/order-update.vue');
const OrderDetails = () => import('@/entities/order/order-details.vue');

const ProductQuantity = () => import('@/entities/product-quantity/product-quantity.vue');
const ProductQuantityUpdate = () => import('@/entities/product-quantity/product-quantity-update.vue');
const ProductQuantityDetails = () => import('@/entities/product-quantity/product-quantity-details.vue');

const ProductCategory = () => import('@/entities/product-category/product-category.vue');
const ProductCategoryUpdate = () => import('@/entities/product-category/product-category-update.vue');
const ProductCategoryDetails = () => import('@/entities/product-category/product-category-details.vue');

const Product = () => import('@/entities/product/product.vue');
const ProductUpdate = () => import('@/entities/product/product-update.vue');
const ProductDetails = () => import('@/entities/product/product-details.vue');

const ProductImage = () => import('@/entities/product-image/product-image.vue');
const ProductImageUpdate = () => import('@/entities/product-image/product-image-update.vue');
const ProductImageDetails = () => import('@/entities/product-image/product-image-details.vue');

const ShipmentAssignment = () => import('@/entities/shipment-assignment/shipment-assignment.vue');
const ShipmentAssignmentUpdate = () => import('@/entities/shipment-assignment/shipment-assignment-update.vue');
const ShipmentAssignmentDetails = () => import('@/entities/shipment-assignment/shipment-assignment-details.vue');

const ShipmentItem = () => import('@/entities/shipment-item/shipment-item.vue');
const ShipmentItemUpdate = () => import('@/entities/shipment-item/shipment-item-update.vue');
const ShipmentItemDetails = () => import('@/entities/shipment-item/shipment-item-details.vue');

const ShipmentStatus = () => import('@/entities/shipment-status/shipment-status.vue');
const ShipmentStatusUpdate = () => import('@/entities/shipment-status/shipment-status-update.vue');
const ShipmentStatusDetails = () => import('@/entities/shipment-status/shipment-status-details.vue');

const Shipment = () => import('@/entities/shipment/shipment.vue');
const ShipmentUpdate = () => import('@/entities/shipment/shipment-update.vue');
const ShipmentDetails = () => import('@/entities/shipment/shipment-details.vue');

const Tag = () => import('@/entities/tag/tag.vue');
const TagUpdate = () => import('@/entities/tag/tag-update.vue');
const TagDetails = () => import('@/entities/tag/tag-details.vue');

const UserAddress = () => import('@/entities/user-address/user-address.vue');
const UserAddressUpdate = () => import('@/entities/user-address/user-address-update.vue');
const UserAddressDetails = () => import('@/entities/user-address/user-address-details.vue');

const WarehouseAssignment = () => import('@/entities/warehouse-assignment/warehouse-assignment.vue');
const WarehouseAssignmentUpdate = () => import('@/entities/warehouse-assignment/warehouse-assignment-update.vue');
const WarehouseAssignmentDetails = () => import('@/entities/warehouse-assignment/warehouse-assignment-details.vue');

const WorkingUnit = () => import('@/entities/working-unit/working-unit.vue');
const WorkingUnitUpdate = () => import('@/entities/working-unit/working-unit-update.vue');
const WorkingUnitDetails = () => import('@/entities/working-unit/working-unit-details.vue');

const Comment = () => import('@/entities/comment/comment.vue');
const CommentUpdate = () => import('@/entities/comment/comment-update.vue');
const CommentDetails = () => import('@/entities/comment/comment-details.vue');

const OrderHistory = () => import('@/entities/order-history/order-history.vue');
const OrderHistoryUpdate = () => import('@/entities/order-history/order-history-update.vue');
const OrderHistoryDetails = () => import('@/entities/order-history/order-history-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'user-details',
      name: 'UserDetails',
      component: UserDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-details/new',
      name: 'UserDetailsCreate',
      component: UserDetailsUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-details/:userDetailsId/edit',
      name: 'UserDetailsEdit',
      component: UserDetailsUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-details/:userDetailsId/view',
      name: 'UserDetailsView',
      component: UserDetailsDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'staff-info',
      name: 'StaffInfo',
      component: StaffInfo,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'staff-info/new',
      name: 'StaffInfoCreate',
      component: StaffInfoUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'staff-info/:staffInfoId/edit',
      name: 'StaffInfoEdit',
      component: StaffInfoUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'staff-info/:staffInfoId/view',
      name: 'StaffInfoView',
      component: StaffInfoDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'address',
      name: 'Address',
      component: Address,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'address/new',
      name: 'AddressCreate',
      component: AddressUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'address/:addressId/edit',
      name: 'AddressEdit',
      component: AddressUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'address/:addressId/view',
      name: 'AddressView',
      component: AddressDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'colour',
      name: 'Colour',
      component: Colour,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'colour/new',
      name: 'ColourCreate',
      component: ColourUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'colour/:colourId/edit',
      name: 'ColourEdit',
      component: ColourUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'colour/:colourId/view',
      name: 'ColourView',
      component: ColourDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'conversation',
      name: 'Conversation',
      component: Conversation,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'conversation/new',
      name: 'ConversationCreate',
      component: ConversationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'conversation/:conversationId/edit',
      name: 'ConversationEdit',
      component: ConversationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'conversation/:conversationId/view',
      name: 'ConversationView',
      component: ConversationDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'participant',
      name: 'Participant',
      component: Participant,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'participant/new',
      name: 'ParticipantCreate',
      component: ParticipantUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'participant/:participantId/edit',
      name: 'ParticipantEdit',
      component: ParticipantUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'participant/:participantId/view',
      name: 'ParticipantView',
      component: ParticipantDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'invoice-status',
      name: 'InvoiceStatus',
      component: InvoiceStatus,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'invoice-status/new',
      name: 'InvoiceStatusCreate',
      component: InvoiceStatusUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'invoice-status/:invoiceStatusId/edit',
      name: 'InvoiceStatusEdit',
      component: InvoiceStatusUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'invoice-status/:invoiceStatusId/view',
      name: 'InvoiceStatusView',
      component: InvoiceStatusDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'invoice',
      name: 'Invoice',
      component: Invoice,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'invoice/new',
      name: 'InvoiceCreate',
      component: InvoiceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'invoice/:invoiceId/edit',
      name: 'InvoiceEdit',
      component: InvoiceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'invoice/:invoiceId/view',
      name: 'InvoiceView',
      component: InvoiceDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'message',
      name: 'Message',
      component: Message,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'message/new',
      name: 'MessageCreate',
      component: MessageUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'message/:messageId/edit',
      name: 'MessageEdit',
      component: MessageUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'message/:messageId/view',
      name: 'MessageView',
      component: MessageDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-item',
      name: 'OrderItem',
      component: OrderItem,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-item/new',
      name: 'OrderItemCreate',
      component: OrderItemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-item/:orderItemId/edit',
      name: 'OrderItemEdit',
      component: OrderItemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-item/:orderItemId/view',
      name: 'OrderItemView',
      component: OrderItemDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-status',
      name: 'OrderStatus',
      component: OrderStatus,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-status/new',
      name: 'OrderStatusCreate',
      component: OrderStatusUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-status/:orderStatusId/edit',
      name: 'OrderStatusEdit',
      component: OrderStatusUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-status/:orderStatusId/view',
      name: 'OrderStatusView',
      component: OrderStatusDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order',
      name: 'Order',
      component: Order,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order/new',
      name: 'OrderCreate',
      component: OrderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order/:orderId/edit',
      name: 'OrderEdit',
      component: OrderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order/:orderId/view',
      name: 'OrderView',
      component: OrderDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product-quantity',
      name: 'ProductQuantity',
      component: ProductQuantity,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product-quantity/new',
      name: 'ProductQuantityCreate',
      component: ProductQuantityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product-quantity/:productQuantityId/edit',
      name: 'ProductQuantityEdit',
      component: ProductQuantityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product-quantity/:productQuantityId/view',
      name: 'ProductQuantityView',
      component: ProductQuantityDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product-category',
      name: 'ProductCategory',
      component: ProductCategory,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product-category/new',
      name: 'ProductCategoryCreate',
      component: ProductCategoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product-category/:productCategoryId/edit',
      name: 'ProductCategoryEdit',
      component: ProductCategoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product-category/:productCategoryId/view',
      name: 'ProductCategoryView',
      component: ProductCategoryDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product',
      name: 'Product',
      component: Product,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product/new',
      name: 'ProductCreate',
      component: ProductUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product/:productId/edit',
      name: 'ProductEdit',
      component: ProductUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product/:productId/view',
      name: 'ProductView',
      component: ProductDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product-image',
      name: 'ProductImage',
      component: ProductImage,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product-image/new',
      name: 'ProductImageCreate',
      component: ProductImageUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product-image/:productImageId/edit',
      name: 'ProductImageEdit',
      component: ProductImageUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product-image/:productImageId/view',
      name: 'ProductImageView',
      component: ProductImageDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment-assignment',
      name: 'ShipmentAssignment',
      component: ShipmentAssignment,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment-assignment/new',
      name: 'ShipmentAssignmentCreate',
      component: ShipmentAssignmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment-assignment/:shipmentAssignmentId/edit',
      name: 'ShipmentAssignmentEdit',
      component: ShipmentAssignmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment-assignment/:shipmentAssignmentId/view',
      name: 'ShipmentAssignmentView',
      component: ShipmentAssignmentDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment-item',
      name: 'ShipmentItem',
      component: ShipmentItem,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment-item/new',
      name: 'ShipmentItemCreate',
      component: ShipmentItemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment-item/:shipmentItemId/edit',
      name: 'ShipmentItemEdit',
      component: ShipmentItemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment-item/:shipmentItemId/view',
      name: 'ShipmentItemView',
      component: ShipmentItemDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment-status',
      name: 'ShipmentStatus',
      component: ShipmentStatus,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment-status/new',
      name: 'ShipmentStatusCreate',
      component: ShipmentStatusUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment-status/:shipmentStatusId/edit',
      name: 'ShipmentStatusEdit',
      component: ShipmentStatusUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment-status/:shipmentStatusId/view',
      name: 'ShipmentStatusView',
      component: ShipmentStatusDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment',
      name: 'Shipment',
      component: Shipment,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment/new',
      name: 'ShipmentCreate',
      component: ShipmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment/:shipmentId/edit',
      name: 'ShipmentEdit',
      component: ShipmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment/:shipmentId/view',
      name: 'ShipmentView',
      component: ShipmentDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'tag',
      name: 'Tag',
      component: Tag,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'tag/new',
      name: 'TagCreate',
      component: TagUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'tag/:tagId/edit',
      name: 'TagEdit',
      component: TagUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'tag/:tagId/view',
      name: 'TagView',
      component: TagDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-address',
      name: 'UserAddress',
      component: UserAddress,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-address/new',
      name: 'UserAddressCreate',
      component: UserAddressUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-address/:userAddressId/edit',
      name: 'UserAddressEdit',
      component: UserAddressUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-address/:userAddressId/view',
      name: 'UserAddressView',
      component: UserAddressDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'warehouse-assignment',
      name: 'WarehouseAssignment',
      component: WarehouseAssignment,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'warehouse-assignment/new',
      name: 'WarehouseAssignmentCreate',
      component: WarehouseAssignmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'warehouse-assignment/:warehouseAssignmentId/edit',
      name: 'WarehouseAssignmentEdit',
      component: WarehouseAssignmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'warehouse-assignment/:warehouseAssignmentId/view',
      name: 'WarehouseAssignmentView',
      component: WarehouseAssignmentDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'working-unit',
      name: 'WorkingUnit',
      component: WorkingUnit,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'working-unit/new',
      name: 'WorkingUnitCreate',
      component: WorkingUnitUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'working-unit/:workingUnitId/edit',
      name: 'WorkingUnitEdit',
      component: WorkingUnitUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'working-unit/:workingUnitId/view',
      name: 'WorkingUnitView',
      component: WorkingUnitDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'comment',
      name: 'Comment',
      component: Comment,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'comment/new',
      name: 'CommentCreate',
      component: CommentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'comment/:commentId/edit',
      name: 'CommentEdit',
      component: CommentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'comment/:commentId/view',
      name: 'CommentView',
      component: CommentDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-history',
      name: 'OrderHistory',
      component: OrderHistory,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-history/new',
      name: 'OrderHistoryCreate',
      component: OrderHistoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-history/:orderHistoryId/edit',
      name: 'OrderHistoryEdit',
      component: OrderHistoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-history/:orderHistoryId/view',
      name: 'OrderHistoryView',
      component: OrderHistoryDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
