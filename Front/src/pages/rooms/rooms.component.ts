import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { RoomService } from 'src/services/room.service';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import Room from 'src/models/room/room.model';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.scss']
})
export class RoomsComponent implements OnInit {


  displayedColumns: string[] = ['nome', 'especialidade', 'local', 'status', 'reserva', 'actions'];
  dataSource: any;
  isLoading: boolean = false;
  errorTableData: boolean = false;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;


  constructor(
    private roomService: RoomService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.getRooms()
  }

  getRooms() {
    this.isLoading = true
    this.dataSource = new MatTableDataSource([])
    this.roomService.getRooms().subscribe((rooms: Room[]) => {
      this.dataSource = new MatTableDataSource(rooms)
      this.errorTableData = false
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.isLoading = false
    }, error => {
      this.isLoading = false
      this.errorTableData = true
      console.log(error)
    })
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  
  deleteElement(room: Room): void {
    const dialogRef = this.dialog.open(MessageModalComponent, {
      width: '400px',
      data: { message: `Deseja mesmo apagar a sala "${room.nome}"?` }
    });

    dialogRef.afterClosed().subscribe(result => {
      result && this.deleteRoom(room.id)
    });
  }

  deleteRoom(id: string) {
    this.roomService.deleteRoom(id).subscribe((res: any) => {
      this.openSnackBar('Sala apagada com sucesso!', 'Ok', 3000);
      this.getRooms();
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 3000);
      console.log(error)
    })
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }

}
