grammar Clobal;
import Clexer;

@header {
}

file:  include* (functionDecl | varDecl)+ ;

include: '#include' ('<stdio.h>'| '<stdbool.h>');

functionDecl
    :   type ID '(' ')' block
    ;

stat:   block               # StatBlock
    |   ifStat	            # IfElse
    |   forStat             # Forloop
    |   whileStat			# Whileloop
    |   returnStat ';'      # Return
    |   assignStat  ';'     # Assign
    |   printStat  ';'      # Print
    |   expr ';'            # FunCall
    ;
     
block:  '{' stat* '}' ;   

assignStat:  ID '=' expr ;

ifStat: 'if' '(' bexpr ')' stat ('else' stat)? ;

forStat: 'for' '(' assignStat ';' bexpr ';' assignStat ')' stat ;

whileStat: 'while' '(' bexpr ')' stat ; 

returnStat: 'return' expr ;

printStat: 'printf' '(' FORMAT ',' expr ')';

varDecl:   type ID ';' ;

type:   'float' | 'int'  ; 

expr:   op = '-' expr               # Negate
    |   expr op=('*'|'/') expr      # MulDiv
    |   expr op=('+'|'-') expr      # AddSub
    |   ID                          # Var
    |   INT                         # Int
    |   '(' expr ')'                # Parens
    |   ID '(' ')'                  # Call
    ;

bexpr:   '!' bexpr                          # Not
    |   expr op=('=='|'!='|'<'|'>') expr    # Vergleich
    |   'true'                              # True
    |   'false'                             # False
    |   '(' bexpr ')'                       # VergleichParens
    ;
