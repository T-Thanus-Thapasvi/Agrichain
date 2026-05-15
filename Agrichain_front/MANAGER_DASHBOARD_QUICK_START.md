# Manager Dashboard - Quick Start Guide

## Accessing the Dashboard

1. Log in with a MANAGER role account
2. Navigate to: `/dashboard/manager` (or `/dashboard/manager/home`)
3. The dashboard will automatically redirect based on your role

## Dashboard Sections

### 1. Statistics Cards (Top)
Four key metrics showing at a glance:
- **Total Budget**: Sum of all allocated budgets across all programs
- **Consumed Budget**: Total amount spent from subsidy programs
- **Active Programs**: Number of programs with "PENDING" status
- **Closed Programs**: Number of programs with "VALIDATED" or "REJECTED" status

### 2. Subsidy Programs Grid
Displays all programs as cards. Each card shows:
- **Title & Status Badge**: Program name with status (Active/Closed/Cancelled)
- **Description**: Brief overview of the program
- **Dates**: Start and End dates
- **Budget Info**: Allocated vs. Consumed amounts
- **Progress Bar**: Visual representation of budget consumption
  - Green bar: Less than 75% consumed
  - Amber/Orange bar: 75% or more consumed  
  - Gray bar: Budget exhausted (VALIDATED status)
- **Action Buttons**: Edit and Delete buttons

## Creating a New Program

1. Click the **"+ New Program"** button at the top
2. Fill in the form:
   - **Title**: Program name (required)
   - **Description**: Detailed description (required)
   - **Total Budget**: Budget amount in Rupees, minimum ₹1 (required)
   - **Status**: Select from dropdown:
     - Active (PENDING)
     - Closed – Budget Full (VALIDATED)
     - Programme Cancelled (REJECTED)
   - **Start Date**: Program start date (required)
   - **End Date**: Program end date, must be after start date (required)
3. Click **"Create Program"** button
4. On success, the modal closes and program appears in the grid

## Editing a Program

1. Find the program card and click the **✏️ Edit** button
2. The modal opens with pre-filled values
3. Modify any fields as needed
4. Click **"Update Program"** button
5. Program is updated immediately

## Deleting a Program

1. Find the program card and click the **🗑️ Delete** button
2. A confirmation dialog appears
3. Click **"Yes, Delete"** to confirm deletion
4. Program is removed from the system

## Understanding Status Display

| Backend Value | Display Label | Color | Meaning |
|---|---|---|---|
| PENDING | Active | Green | Program is currently active |
| VALIDATED | Closed – Budget Full | Gray | Budget has been fully allocated |
| REJECTED | Programme Cancelled | Red | Program has been cancelled |

## Budget Progress Calculation

The progress bar shows: `(Consumed / Allocated) × 100`

Example:
- Allocated Budget: ₹1,00,000
- Consumed Budget: ₹80,000
- Progress: 80%
- Color: Amber (since >= 75%)

## Data Formatting Examples

### Currency Format
- Displayed: ₹60,00,000 (Indian numbering system)
- Stored in API: 6000000 (numeric)

### Date Format
- Displayed: 15 Jan 2025 (for user readability)
- Stored in API: 2025-01-15 (YYYY-MM-DD format)
- Selected in form: Date picker (browser formats automatically)

## Validation Rules

The form enforces:
- Title cannot be empty
- Description cannot be empty
- Budget must be ≥ 1
- Start Date must be selected
- End Date must be selected
- End Date must be after Start Date
- Status must be selected

If validation fails, you'll see an error message.

## Error Handling

If an operation fails:
- A red toast notification appears with the error message
- The modal remains open so you can correct the issue
- Check your internet connection if API calls fail
- Contact system administrator if backend errors persist

## Tips & Best Practices

1. **Dates**: Always set realistic start/end dates for program planning
2. **Budget**: Plan budget allocation carefully as it affects the progress bar
3. **Status**: Change status to VALIDATED only when budget is fully allocated
4. **Organization**: Use clear, descriptive titles and descriptions
5. **Review**: Periodically review statistics cards to monitor allocation

## Common Questions

**Q: Can I change a program's status?**
A: Yes, edit the program and select a different status from the dropdown.

**Q: What happens when I delete a program?**
A: The program is permanently removed. Confirm deletion in the dialog.

**Q: How is the consumed budget calculated?**
A: The backend auto-calculates based on actual subsidy disbursements.

**Q: Can I view programs from other managers?**
A: This dashboard shows all subsidy programs accessible to your account role.

**Q: What if the dates appear incorrect?**
A: The system uses YYYY-MM-DD format internally. Browser auto-converts for display.

---

**Need Help?** Contact your system administrator or refer to the full implementation documentation.
