grammar Cuprum;

SING_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);
LF: [\r\n] -> channel(HIDDEN);
WS: [ \t] -> channel(HIDDEN);
ESCAPE: '\\';

COMMA: ',';
COLON: ':';
SEMICOLON: ';';
LPAREN: '(';
RPAREN: ')';
RARROW: '->';

UNIT_VOLT: 'V';
UNIT_AMPERE: 'A';
UNIT_WATT: 'W';
UNIT_OHM: 'Ω' | 'O' | 'Ohm';

GND: 'GND';
NAMED: 'named';

CURRENT_TYPE: 'AC'|'DC';
CONTACTOR_TYPE: 'NC'|'NO';

DIGIT: [0-9];
number: DIGIT+;
LETTER: [a-zA-ZäöüÄÖÜß]+;
ident: LETTER | GND;

voltage: value=number UNIT_VOLT;
amperage: value=number UNIT_AMPERE;
wattage: value=number UNIT_WATT;

unit: voltage | amperage | wattage;

expr
    : unit #exprUnit
    | number #exprNumber
    | ident #exprIdent
    | CURRENT_TYPE #exprCurrentType
    | CONTACTOR_TYPE #exprContactType
;
exprs: expr (COMMA expr)*;

powerSource: CURRENT_TYPE LPAREN voltage COMMA amperage RPAREN COLON gnd=ident (COMMA ident)*;

component: name=ident LPAREN exprs? RPAREN (NAMED ident)?;
line: (input=ident RARROW)? (component RARROW)* (output=ident)?;

circuit: (powerSource SEMICOLON)+ ((line | component) SEMICOLON)+;

UNMATCHED: .;
