import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import Room from 'src/models/room/room.model';
import { selectRoom } from 'src/store/room/selectors';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import { ActivatedRoute, Router } from '@angular/router';
import { RoomService } from 'src/services/room.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Store } from '@ngrx/store';
import { SetRoom } from 'src/store/room/actions';
import { LocalService } from 'src/services/local.service';
import Local from 'src/models/local.model';
import { SpecialtyService } from 'src/services/specialty.service';
import Specialty from 'src/models/specialty.model';

@Component({
  selector: 'app-room-form',
  templateUrl: './room-form.component.html',
  styleUrls: ['./room-form.component.scss']
})
export class RoomFormComponent implements OnInit {

  room$: Observable<Room> = this.store.select(selectRoom)
  room: Room = new Room();
  roomFormGroup: FormGroup
  matcher = new MyErrorStateMatcher();
  isLoading: boolean = false;
  isEdit: boolean = false;
  statusTypes = ['DISPONÍVEL', 'RESERVADO', 'MANUTENÇÃO']
  locals: Local[] = [];
  specialties: Specialty[] = []

  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private roomService: RoomService,
    private localService: LocalService,
    private specialtyService: SpecialtyService,
    private snackBar: MatSnackBar,
    private store: Store,
    private router: Router
  ) {
    this.roomFormGroup = this.formBuilder.group({
      name: new FormControl('', [Validators.required]),
      status: new FormControl('', [Validators.required]),
      local: new FormControl('', [Validators.required]),
      specialty: new FormControl('', [Validators.required]),
      reserva: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit(): void {
    this.getSpecialties()

  }
  setupInit() {
    this.route.params.subscribe(params => {
      if (params.id === 'new') {
        this.isEdit = false
        this.isLoading = false;

      } else {
        params.id && (this.isEdit = true)
        params.id && this.getRoom(params.id)
      }
    })
  }
  getLocals() {
    this.localService.getLocals().subscribe((locals: Local[]) => {
      this.locals = locals;
      this.setupInit()

    }, error => {
      console.log(error);
      this.isLoading = false;

    })
  }
  getSpecialties() {
    this.isLoading = true;
    this.specialtyService.getSpecialties().subscribe((spacialties: Specialty[]) => {
      this.specialties = spacialties;
      this.getLocals()
    }, error => {
      console.log(error);
      this.isLoading = false;

    })
  }
  getRoom(id: string | number) {
    this.roomService.getRoomById(id).subscribe((room: Room) => {
      this.room = room;
      this.room.especialidade = this.specialties.find(speciality => speciality.id === this.room.especialidade.id)
      this.room.local = this.locals.find(local => local.id === this.room.local.id)
      this.room.especialidade = this.specialties.find(speciality => speciality.id === this.room.especialidade.id)
      this.isLoading = false;
    }, error => {
      console.log(error);
      this.isLoading = false;
    })
  }
  inputAtribute(atribute) {
    this.store.dispatch(new SetRoom({ room: atribute }));
  }

  formRoomHasError(control: any, error: string) {
    return this.roomFormGroup.controls[control].hasError(error);
  }
  onSubmit() {
    if (this.roomFormGroup.valid) {
      this.isLoading = true;
      this.isEdit ? this.updateRoom(this.room) : this.createRoom(this.room);
    }

  }
  createRoom(room: Room) {
    this.roomService.createRoom(room).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Sala criada com sucesso!', 'Ok', 3000);
      this.router.navigate(["salas"], { replaceUrl: true })
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000);
      this.isLoading = false;
      console.log(error)
    })
  }
  updateRoom(room: Room) {
    this.roomService.updateRoom(room.id, room).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Sala atualizada com sucesso!', 'Ok', 3000);
      this.router.navigate(["salas"], { replaceUrl: true })
    }, error => {
      this.isLoading = false;
      this.openSnackBar(error.error.message, 'Ok', 5000);
      console.log(error)
    })
  }

  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
      panelClass: 'teste'
    });
  }

}
