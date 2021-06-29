# hyperplane-chess
Hyperplane Chess is a reinterpretation of the classical Chess game,
it features multiplayer capabilities (2+) and modified pieces movements
alongside a custom board size and multiple boards between which pieces
can "shift" according to their capabilities.

### Moveset format
```json
{
    "pieces" : [
      {
        "name" : "PIECE_NAME",
        "image" : "img",
        "moveset" : "(0,1,0),(x,x,0),..."
      }
    ]
}
```
the moveset notation (x,y,z) is as follows:
* (1,-2,1): a positive value means forward
* (x,x,0) : series = (1,1,0),(2,2,0),(3,3,0),...
* (x,1,0) : series = (1,1,0),(2,1,0),(3,1,0),...
* (2x,2x,0) : series = (2,2,0),(4,4,0),(6,6,0),...
* (2x,-2x,0) : series = (2,-2,0),(4,-4,0),(6,-6,0),...


Tested with `AdoptOpenJDK Java 1.8.0_242`
