export interface CropListing {
  id: number;
  farmerId: number;
  cropType: string;
  quantity: number;
  price: number;
  location: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
}

export interface Order {
  id: number;
  listingId: number;
  traderId: number;
  quantity: number;
  date: string;
  status: string;
}

export interface Transaction {
  transactionId: number;
  orderId: number;
  transactionAmount: number;
  transactionDate: string;
  transactionStatus: 'PENDING' | 'COMPLETED' | 'FAILED';
}