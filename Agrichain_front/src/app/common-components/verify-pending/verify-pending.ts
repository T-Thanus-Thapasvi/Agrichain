import { Component } from "@angular/core";
import { FARMER_REGI } from "../../elements/constants";
import { Loader } from "../loader/loader";

@Component({
    selector: "verify-pending",
    templateUrl: "./verify-pending.html",
    styleUrl: "./verify-pending.css",
    imports: [Loader]
})
export class VerifyPending {
    farmer = JSON.parse(localStorage.getItem(FARMER_REGI) || '{}');
}