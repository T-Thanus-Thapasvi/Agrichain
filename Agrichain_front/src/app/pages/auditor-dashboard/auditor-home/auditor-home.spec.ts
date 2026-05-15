import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditorHome } from './auditor-home';

describe('AuditorHome', () => {
  let component: AuditorHome;
  let fixture: ComponentFixture<AuditorHome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuditorHome],
    }).compileComponents();

    fixture = TestBed.createComponent(AuditorHome);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
