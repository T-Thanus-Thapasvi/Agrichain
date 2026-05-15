import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FarmerSubsidy } from './farmer-subsidy';

describe('FarmerSubsidy', () => {
  let component: FarmerSubsidy;
  let fixture: ComponentFixture<FarmerSubsidy>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FarmerSubsidy],
    }).compileComponents();

    fixture = TestBed.createComponent(FarmerSubsidy);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
