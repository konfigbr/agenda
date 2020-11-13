import { Injectable, OnInit, OnDestroy, AfterContentInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable, Subject } from 'rxjs';
import { Call } from 'src/models/call-.model';

interface StreamData {
  data: string;
}

@Injectable({
  providedIn: 'root'
})
export class PabxService {

  private calls: Map<String, Call> = new Map<String, Call>();
  private calls$: Subject<Map<String, Call>> = new Subject<Map<String, Call>>();

  constructor() {}

  incomingCall(): Observable<StreamData> {
    return new Observable<StreamData>(obs => {
      const es = new EventSource(`${environment.apiUrl}/calls/listener`);
      es.addEventListener('message', (event: StreamData) => {
        obs.next(event.data != null ? JSON.parse(event.data) : event.data);
      });
      return () => es.close();
    });
  }

  remove(call: Call) {
    this.calls.delete(call.uuid);
    this.calls$.next(this.calls);
  }

  set setCall(call: Call) {
    this.calls.set(call.uuid, call);
    this.calls$.next(this.calls);
  }

  get getCalls(): Subject<Map<String, Call>> {
    return this.calls$;
  }

  load() {
    if (this.calls.size > 0)
      this.calls$.next(this.calls);
  }
}
