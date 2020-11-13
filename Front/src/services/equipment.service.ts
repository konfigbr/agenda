import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import EquipmentType from 'src/models/equipment/equipment-type.model';
import Equipment from 'src/models/equipment/equipment.model';

@Injectable({
    providedIn: 'root'
})
export class EquipmentService {

    constructor(private http: HttpClient) { }

    getEquipmentTypes(): Observable<EquipmentType[]> {
        return this.http.get<EquipmentType[]>(`${environment.apiUrl}/equipment-types`);
    }
    getEquipmentTypeById(id: string | number): Observable<EquipmentType> {
        return this.http.get<EquipmentType>(`${environment.apiUrl}/equipment-types/${id}`);
    }
    createEquipmentType(equipmentType: EquipmentType): Observable<any> {
        return this.http.post<any>(`${environment.apiUrl}/equipment-types`, equipmentType);
    }
    updateEquipmentType(id: string | number, equipmentType: EquipmentType): Observable<any> {
        return this.http.put<any>(`${environment.apiUrl}/equipment-types/${id}`, equipmentType);
    }
    deleteEquipmentType(id: string | number): Observable<any> {
        return this.http.delete<any>(`${environment.apiUrl}/equipment-types/${id}`);
    }

    getEquipments(): Observable<Equipment[]> {
        return this.http.get<Equipment[]>(`${environment.apiUrl}/equipments`);
    }
    getEquipmentById(id: string | number): Observable<Equipment> {
        return this.http.get<Equipment>(`${environment.apiUrl}/equipments/${id}`);
    }
    createEquipment(equipment: Equipment): Observable<any> {
        return this.http.post<any>(`${environment.apiUrl}/equipments`, equipment);
    }
    updateEquipment(id: string | number, equipment: Equipment): Observable<any> {
        return this.http.put<any>(`${environment.apiUrl}/equipments/${id}`, equipment);
    }
    deleteEquipment(id: string | number): Observable<any> {
        return this.http.delete<any>(`${environment.apiUrl}/equipments/${id}`);
    }

}
