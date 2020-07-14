:-include('next.pl').
:-include('nodeF.pl').
:-include('node.pl').

/*Fy + Gy - Fx + Gx */

findF(X, F) :-
	nodeF(X, F), !.

findLane(Line_id, Lanes):-
	lineLanes(Line_id, Lanes), !.

floorTime(Time, StartTime, EndTime) :-
	(Time > 19 -> StartTime is 0, EndTime is 0
	; 
	 Time < 9 -> StartTime is 0, EndTime is 0
	;
	 Time =:= 12 -> StartTime is 0, EndTime is 0
	;
	 Time =:= 16 -> StartTime is 0, EndTime is 0
	;
	 Time =< 11 -> StartTime is 9, EndTime is 11
	;
	 Time =< 15 -> StartTime is 13, EndTime is 15
	;
	 Time =< 19 -> StartTime is 17, EndTime is 19
	).


findTraffic(Line_id, Time, Traffic) :-
	floorTime(Time, StartTime, EndTime),
	lineTraffic(Line_id, StartTime, EndTime, Traffic), !.

findXY(Id, X, Y) :-
	node(Id, X, Y), !.

findNext(X, Y, Gy, Fx, Fy) :-
	next(X, Y, Gy, Fx, Fy).



