grammar BasicMath;

SING_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);
LF: [\r\n] -> channel(HIDDEN);
WS: [ \t] -> channel(HIDDEN);

DOT: '.';
COMMA: ',';
LPAREN: '(';
RPAREN: ')';

PLUS: '+';
MINUS: '-';
MULTIPLY: '*';
DIVIDE: '/';
MODULUS: '%';
ROOF: '^';

DIGIT: [0-9];
decimalPoint: DOT | COMMA;
number: MINUS? DIGIT+ (decimalPoint DIGIT+)?;
operator: PLUS | MINUS | MULTIPLY | DIVIDE | MODULUS | ROOF;

expr
    : number #exprNumber
    | LPAREN expr RPAREN #exprParen
    | left=expr operator right=expr #exprOperator
;
