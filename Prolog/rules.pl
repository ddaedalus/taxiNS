
:-include('next.pl').
/*
:-include('closure.pl').
:-include('lineLanes.pl').
:-include('lineTraffic.pl').
*/
:-include('nodeF.pl').
:-include('node.pl').


/*Fy + Gy - Fx + Gx */

/*
getG_from_path([], _) :- !.
getG_from_path([[_, G]|_], G) :- !.

getFirstFront([], _, _).
getFirstFront([[ID, F]|_], ID, F) :- !.

addFront(X, [], [X]) :- !.
addFront([ID, F], [[Id1, F1], [Id2, F2]], NewFront) :-
	( F > F2 -> NewFront = [[Id1, F1], [Id2, F2], [ID, F]]
	; 
	  F < F1 -> NewFront = [[ID, F], [Id1, F1], [Id2, F2]] 
	;
	  F =< F2 -> NewFront = [[Id1, F1], [ID, F], [ID, F2]]
	), !.  
addFront([ID, F], [[Id1, F1]|Front], NewFront):-
	( F < F1 -> NewFront = [[ID, F], [Id1, F1]|Front]
	;
	  F = F1, ID \= Id1 -> NewFront = [[Id1, F1], [ID, F]|Front]
	; 
	  F > F1 -> addFront([ID, F], Front, NF),
	            NewFront = [[Id1, F1]|NF]
	), !.

searchFront(_, [], [], yes) :- !.
searchFront([ID, F], [[Id1, F1]|Front], NewFront, Equal) :-
	( ID =:= Id1, F < F1 -> Equal = yes, NewFront = Front
	;
	  ID =:= Id1, F >= F1 -> Equal = no, NewFront = [[Id1, F1]|Front]
	;
	  searchFront([ID, F], Front, NF, Equal),
	  NewFront = [[Id1, F1]|NF]
	), !.


removeFront([_|Xs], Xs).

getF([_, F], F).

getID([Id, _], Id).

addClosure(X, Closure, [X|Closure]).  	

*/

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



