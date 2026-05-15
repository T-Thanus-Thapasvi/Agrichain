import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FarmerListings } from './farmer-listings';

describe('FarmerListings', () => {
  let component: FarmerListings;
  let fixture: ComponentFixture<FarmerListings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FarmerListings],
    }).compileComponents();

    fixture = TestBed.createComponent(FarmerListings);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
