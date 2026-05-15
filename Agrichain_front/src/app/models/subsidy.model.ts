export interface SubsidyProgram {
  programID: number;
  title: string;
  description: string;
  startDate: string;        // format: YYYY-MM-DD
  endDate: string;          // format: YYYY-MM-DD
  allottedBudget: number;   // in Rupees
  consumedBudget: number;   // auto-set by backend
  subsidyStatus: 'PENDING' | 'VALIDATED' | 'REJECTED';
}

export interface SubsidyProgramCreateRequest {
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  allottedBudget: number;
  subsidyStatus: 'PENDING' | 'VALIDATED' | 'REJECTED';
}

export interface SubsidyProgramUpdateRequest extends SubsidyProgramCreateRequest {
  programID: number;
}
