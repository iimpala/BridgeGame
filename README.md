
**PRECAUTIONS**
# Map file MUST be in "map" directory!!
# The size of the map MUST NOT exceed 20*20

**About Game Playing**

# Start Menu
- From Start Menu, You can choose how many players you want to play with. (default = 2)
- And also you can load any map file by writing file name(except '.map' extension, default = 'default.map')
- When you press START button, you can start game with above inputs.

# Playing Game
- Every players are at start cell when the beginning of game.

- You can choose STAY or ROLL button.
- STAY button only enabled when you have at least one bridge card.
- If you press STAY button, system pass the turn to next player automatically.
- If you press ROLL button, dice will be update and Direction Panel will visible.

- After roll the dice, you should decide the direction you will move by press direction buttons.
- Remember that you can only move where the cell is.(includes bridges)

- If the direction you pressed is valid, system will move your piece and pass turn to the next player.
- If the direction you pressed is invalid, you have to press direction again until it is vaild.

- If one player passes the last cell, other players cannot move backward.
- If there is one player remain in the map, game is finished and the winner is announced.

# Structure
![5-1  Class Diagram - BridgeGame](https://user-images.githubusercontent.com/100896063/201328428-e8751518-85a5-4b91-ae8b-286aa6e36420.png)
