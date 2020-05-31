import { Transaction } from './transaction';
export class Order {
orderId: number;
orderAmount: number;
orderStatus: String;
products: Map<number, number>;
addressId: number;
transaction: Set<Transaction>;
userId: number;
}