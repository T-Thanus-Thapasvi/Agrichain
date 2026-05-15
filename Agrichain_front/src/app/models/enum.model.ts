export enum UserRole {
  FARMER = 'FARMER',
  TRADER = 'TRADER',
  OFFICER = 'OFFICER',
  MANAGER = 'MANAGER',
  COMPLIANCE = 'COMPLIANCE',
  AUDITOR = 'AUDITOR',
  ADMIN = 'ADMIN'
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  DELETED = 'DELETED',
  SUSPENDED = 'SUSPENDED'
}

export enum FarmerStatus {
    ACTIVE = 'ACTIVE',
    PENDING_VERIFICATION = 'PENDING_VERIFICATION',
    APPROVED = 'APPROVED',
    REJECTED = 'REJECTED',
    INACTIVE = 'INACTIVE'
}

export enum CropListingStatus {
  PENDING = 'PENDING',
  VALIDATED = 'VALIDATED',
  REJECTED = 'REJECTED',
  SOLD_OUT = 'SOLD_OUT'
}

export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export enum NotificationStatus {
  READ = 'READ',
  UNREAD = 'UNREAD'
}
export enum NotificationCategory {
  ALERT = 'ALERT',
  VERIFICATION = 'VERIFICATION',
  BROADCAST = 'BROADCAST'
}

export enum AuditScope {
    PROGRAM = 'PROGRAM',
    LISTING = 'LISTING',
    TRANSACTION = 'TRANSACTION'
}

export enum AuditStatus {
    OPEN = 'OPEN',
    CLOSED = 'CLOSED',
    IN_PROGRESS = 'IN_PROGRESS',
    REVIEW = 'REVIEW'
}
export enum ComplianceResult {
    PASSED = 'PASSED',
    FAILED = 'FAILED',
    PENDING = 'PENDING',
    REVIEW = 'REVIEW'
}

export enum ComplianceType {
    LISTING = 'LISTING',
    TRANSACTION = 'TRANSACTION',
    PROGRAM = 'PROGRAM'
}

