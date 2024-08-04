import { defineComponent, provide } from 'vue';

import UserDetailsService from './user-details/user-details.service';
import AddressService from './address/address.service';
import ColourService from './colour/colour.service';
import ConversationService from './conversation/conversation.service';
import ParticipantService from './participant/participant.service';
import InvoiceStatusService from './invoice-status/invoice-status.service';
import InvoiceService from './invoice/invoice.service';
import MessageService from './message/message.service';
import OrderItemService from './order-item/order-item.service';
import OrderStatusService from './order-status/order-status.service';
import OrderService from './order/order.service';
import ProductQuantityService from './product-quantity/product-quantity.service';
import ProductCategoryService from './product-category/product-category.service';
import ProductService from './product/product.service';
import ProductImageService from './product-image/product-image.service';
import ShipmentAssignmentService from './shipment-assignment/shipment-assignment.service';
import ShipmentItemService from './shipment-item/shipment-item.service';
import ShipmentStatusService from './shipment-status/shipment-status.service';
import ShipmentService from './shipment/shipment.service';
import TagService from './tag/tag.service';
import UserAddressService from './user-address/user-address.service';
import WarehouseAssignmentService from './warehouse-assignment/warehouse-assignment.service';
import WorkingUnitService from './working-unit/working-unit.service';
import CommentService from './comment/comment.service';
import StaffInfoService from './staff-info/staff-info.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('userDetailsService', () => new UserDetailsService());
    provide('addressService', () => new AddressService());
    provide('colourService', () => new ColourService());
    provide('conversationService', () => new ConversationService());
    provide('participantService', () => new ParticipantService());
    provide('invoiceStatusService', () => new InvoiceStatusService());
    provide('invoiceService', () => new InvoiceService());
    provide('messageService', () => new MessageService());
    provide('orderItemService', () => new OrderItemService());
    provide('orderStatusService', () => new OrderStatusService());
    provide('orderService', () => new OrderService());
    provide('productQuantityService', () => new ProductQuantityService());
    provide('productCategoryService', () => new ProductCategoryService());
    provide('productService', () => new ProductService());
    provide('productImageService', () => new ProductImageService());
    provide('shipmentAssignmentService', () => new ShipmentAssignmentService());
    provide('shipmentItemService', () => new ShipmentItemService());
    provide('shipmentStatusService', () => new ShipmentStatusService());
    provide('shipmentService', () => new ShipmentService());
    provide('tagService', () => new TagService());
    provide('userAddressService', () => new UserAddressService());
    provide('warehouseAssignmentService', () => new WarehouseAssignmentService());
    provide('workingUnitService', () => new WorkingUnitService());
    provide('commentService', () => new CommentService());
    provide('staffInfoService', () => new StaffInfoService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
