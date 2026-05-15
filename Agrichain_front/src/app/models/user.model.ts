import { UserRole, UserStatus } from "./enum.model";

export interface User {
    id: number;
    name: string;
    email: string;
    phone: string;
    role: UserRole;
    status: UserStatus;
}

export interface Farmer {
    farmerId: number,
    userId: number,
    name: string,
    email: string,
    dob: Date,
    gender: string,
    address: string,
    contactInfo: string,
    landDetails: string,
    status: string
}