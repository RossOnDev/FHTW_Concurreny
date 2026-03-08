# Threading Concurrency
Patrick Rössler, Ferzan Yarar \
[GitHub Repo](https://github.com/RossOnDev/FHTW_Concurreny)

## Subtask 2: Deadlock Prevention

#### What are the necessary conditions for deadlocks (discussed in the lecture) [0.5 points]?

* Mutual Exclusion: Ein Thread kann nur auf eine Ressource zugreifen (kein anderer Thread darf währenddessen diese Ressource ändern)
* Hold and Wait: Der Thread sperrt eine Resource und wartet auf eine andere Ressource bis diese freigegeben ist
* No Preemption: Ressourcen werden nur vom Thread freigegeben und nicht entnommen
* Circular Wait: Wenn eine Kreisabhängigkeit besteht: z.B. Thread T1 wartet auf T2, T2 auf T3 und T3 wieder auf T1


#### Why does the initial solution lead to a deadlock (by looking at the deadlock conditions) [0.5 points]?

Es tritt das Problem von Hold and Wait und Circular Wait auf. Wenn es zu einer Race Condition kommt, kann es passieren,
dass ein Thread auf eine Fork wartet die aber noch benutzt wird und noch nicht freigeben wurde. Bei uns im Code simulierbar
über eine Thinking Time von 0.


### Prevent the deadlock by removing Circular Wait condition:
Switch the order in which philosophers take the fork by using the following scheme: Odd philosophers start with the left fork, while even philosophers start with the right hand.

#### Does this strategy resolve the deadlock and why [1 point]?

Ja! Weil kein Zyklus entstehen kann, weil die "Richtung" der Locks nicht überall gleich ist. Es gibt keinen Kreis, der alle Thread blockiert.

#### Measure the overall time, philosophers spend for eating. How does it compare to the overall time execution time of the dinner [2 points].

Beispielausgabe:
```
Gesamte Laufzeit: 22110,815 ms
Essenszeit (alle Philosophen): 79142,468 ms
Essenszeit / Laufzeit = 357,94%
```

Der Vergleich zeigt, dass die Parallelisierung funktioniert hat und im Durchschnitt ca. 3-4 Thread (von 10) gleichzeitig gelaufen sind.

#### Can you think of other techniques for deadlock prevention? [1 point]

* Es könnte zusätzlich eine Semaphore eingefügt werden, die einem Thread sagt, ob er die Forks nutzen darf oder nicht.
Biespiel Code Snippet:
```java
Semaphore waiter = new Semaphore(NUMBER_OF_PHILOSOPHERS - 1);
waiter.acquire();
// take Forks
waiter.release();
```

* Anzahl der Threads auf n-1 limitieren, so das es nicht dazu kommen kann, dass alle Thread gleichzeitig alle Forks haben können
* Java Trylock: Wenn nicht beide Forks genommen werden können, gebe beide Locks frei