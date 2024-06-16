import stanford.karel.SuperKarel;
public class Homework extends SuperKarel {
    private int width;
    private int height;
    private int shape;
    private int steps = 0;
    private int beeps = 0;
    private int x, y;
    enum Direction  {EAST, NORTH, WEST, SOUTH}
    private Direction direction = Direction.EAST;
    private void goToPoint(int targetX, int targetY) {
        if (targetX > x) {
            faceTo(Direction.EAST);
            moveSteps(targetX - x);
        } else if (targetX < x) {
            faceTo(Direction.WEST);
            moveSteps(x - targetX);
        }
        if (targetY > y) {
            faceTo(Direction.NORTH);
            moveSteps(targetY - y);
        } else if (targetY < y) {
            faceTo(Direction.SOUTH);
            moveSteps(y - targetY);
        }
    }
    private void faceTo(Direction targetDirection) {
        int difference = ( targetDirection.ordinal() - direction.ordinal() + 4) % 4;
        if (difference == 1) turnLeftAndUpdate();
        else if (difference == 2) {
            turnLeftAndUpdate();
            turnLeftAndUpdate();
        } else if (difference == 3) turnRightAndUpdate();
    }
    private void moveSteps(int steps) {
        for (int i = 0; i < steps; i++) {
            move();
            updateCoordinates();
            this.steps++;
        }
    }
    private void updateCoordinates() {
        switch (direction) {
            case EAST: x++; break;
            case NORTH: y++; break;
            case WEST: x--; break;
            case SOUTH: y--; break;
        }
    }
    private void updateDirection(int rotations) {
        int currentOrdinal = direction.ordinal();
        int newOrdinal = (currentOrdinal + rotations + 4) % 4;
        direction = Direction.values()[newOrdinal];
    }
    private void turnLeftAndUpdate() {
        turnLeft();
        updateDirection(1);
    }
    private void turnRightAndUpdate() {
        turnRight();
        updateDirection(-1);
    }
    private void plantBeeper() {
        if (noBeepersPresent()) {
            putBeeper();
            beeps++;
        }
    }
    private void plantBeepers(){
        while (frontIsClear()){
            plantBeeper();
            moveSteps(1);
        }
        plantBeeper();
    }
    private void plantBeepers(int amount){
        while (frontIsClear() && amount > 0) {
            plantBeeper();
            amount--;
            moveSteps(1);
        }
    }
    private void plantAt(int column, int row){
        if (shape == 1){
            int temp = column;
            column = row;
            row = temp;
        }
        goToPoint(column, row);
        plantBeeper();
    }

    private void reachWall(){
        while (frontIsClear()){
            moveSteps(1);
        }
    }
    private void findDimensions(){
        reachWall();
        width = steps + 1;
        turnLeftAndUpdate();
        reachWall();
        height = steps - width + 2;
        turnLeftAndUpdate();
    }
    private void buildWall(int startX, int startY, Direction dir){
        goToPoint(startX,startY);
        faceTo(dir);
        plantBeepers();
    }
    private void buildL(int startX, int startY, Direction dir){
        int div = Math.min(width,height)/2;
        goToPoint(startX, startY);
        faceTo(dir);
        plantBeepers(div);
        turnLeftAndUpdate();
        plantBeepers(div);
        if (shape == 2) plantBeeper();
    }
    private void buildWing(int startX, int startY, Direction dir, int div){
        goToPoint(startX,startY);
        faceTo(dir);
        if (div % 2 == 0) {plantBeepers(div/2 - 1);plantBeeper();}
        else {plantBeepers(div/2);plantBeeper();}
        turnLeftAndUpdate();
        moveSteps(1);
        faceTo(dir);
        if (div % 2 == 0) moveSteps(1);
        plantBeepers();
    }

    private void divideToTwo(int Case){
        if (Case == 1){
            plantBeeper();
            plantAt(1,1);
        }
        if (Case == 2){
            if (height == 3 || width == 3) plantAt(1,2);
            if (height == 4 || width == 4) {
                plantAt(1,4);
                plantAt(1,2);
            }
        }
    }
    private void divideToThree(int Case){
        if (Case == 1){
            if (Math.max(width,height) == 6) {
                plantAt(1, 6);
            }
            plantAt(1,4);
            plantAt(1,2);
        }
        if (Case == 2){
            plantAt(2,3);
            plantAt(1,2);
            plantAt(2,1);
        }
    }
    private void divideToFour(int Case){
        int mn = Math.min(width,height);
        int mx = Math.max(width,height);
        if (Case == 1){
            int block = (mx + 1) % 4;
            int places = (mx - block) / 4;
            if (shape == 0) faceTo(Direction.SOUTH);
            else faceTo(Direction.WEST);
            plantBeepers(block);
            for (int i = 0 ; i < 3; i++ ){
                if (i == 0) moveSteps(places);
                else moveSteps(places + 1);
                plantBeeper();
            }
        }
        if (Case == 2){
            Direction dir1, dir2;
            if (shape == 0) {dir1 = Direction.WEST;dir2 = Direction.EAST;
                if (mx >= 5)buildWall(x, y, dir1);if (mx == 6) buildWall(x, y - 1, dir2);}
            else {dir1 = Direction.SOUTH;dir2 = Direction.NORTH;
                if (mx >= 5)buildWall(x, y, dir1);if (mx == 6) buildWall(x - 1, y, dir2);}
            plantAt(2,4);
            plantAt(1,3);
            plantAt(2,2);
            plantAt(1,1);
        }
        if (Case == 3){
            int block = (mx + 1) % 4;
            int places = (mx - block) / 4;
            for (int i = 0; i < block; i++){
                plantAt(mn,mx - i);
                plantAt(mn -1,mx -i);
            }
            if (shape == 0) {
                if (block == 0) buildWall(1, y - places, Direction.EAST);
                else buildWall(1, y - places - 1, Direction.EAST);
                buildWall(2, y - places - 1, Direction.WEST);
                buildWall(1, y - places - 1, Direction.EAST);
            }
            else {
                if (block == 0) buildWall(x - places, 2, Direction.SOUTH);
                else buildWall(x - places - 1, 2, Direction.SOUTH);
                buildWall(x - places - 1,1, Direction.NORTH);
                buildWall(x - places - 1,2, Direction.SOUTH);
            }
        }
        if (Case == 4){
            buildWall(width/2+1,height,Direction.SOUTH);
            buildWall(width,height/2+1,Direction.WEST);
        }
        if (Case == 5){
            int odd, even;
            if (width % 2 != 0){odd = width;even = height;}
            else{odd = height;even = width;}
            if ((odd > even && shape == 0) || (odd < even && shape == 1)) {
                buildWall(even, odd / 2 + 1, Direction.WEST);
                buildWing(x + even/2 , y,Direction.NORTH,odd - (odd/2) + 1);
                buildWing(x, y - odd / 2,Direction.SOUTH,odd - (odd/2) + 1);
            }
            else{
                buildWall(odd / 2 + 1, even, Direction.SOUTH);
                buildWing(x , y + even/2 - 1,Direction.EAST,odd - (odd/2) + 1);
                buildWing(x - odd / 2, y,Direction.WEST,odd - (odd/2) + 1);
            }
        }
        if (Case == 6){
            int div = mn/2;
            int rem = (mx - (mx/2)) - div;
            if (shape == 1) {
                buildL(width / 2 + 1, height, Direction.SOUTH);
                buildWing(x, y, Direction.EAST, rem);
                buildL(width / 2, 1, Direction.NORTH);
                buildWing(x, y, Direction.WEST, rem);
            }
            else{
                buildL( width,height / 2 ,Direction.WEST);
                if (mn != mx)buildWing(x, y, Direction.SOUTH, rem);
                buildL(1, height/2+ 1, Direction.EAST);
                if (mn != mx)buildWing(x, y, Direction.NORTH, rem);
            }
        }
    }
    private void determineMethod() {
        if ((width == 1 && height == 1) || (width == 1 && height == 2) || (width == 2 && height == 1)) return;
        if (width == 2 && height == 2) {
            divideToTwo(1);
        } else if ((width == 1 && (height >= 3 && height <= 4)) || (height == 1 && (width >= 3 && width <= 4))) {
            divideToTwo(2);
        } else if ((width == 1 && (height >= 5 && height <= 6)) || (height == 1 && (width >= 5 && width <= 6))) {
            divideToThree(1);
        } else if ((width == 2 && height == 3) || (height == 2 && width == 3)) {
            divideToThree(2);
        } else if ((width == 1 && height >= 7) || (height == 1 && width >= 7)) {
            divideToFour(1);
        } else if ((width == 2 && height >= 4 && height <=6) || (height == 2 && width >= 4 && width <=6)) {
            divideToFour(2);
        } else if ((width == 2 && height >= 7 ) || (height == 2 && width >= 7)) {
            divideToFour(3);
        } else if ((width % 2 != 0 && height % 2 != 0)) {
            divideToFour(4);
        } else if ((width % 2 != 0 && height % 2 == 0) || (height % 2 != 0 && width % 2 == 0)) {
            divideToFour(5);
        } else if ((width % 2 == 0 && height % 2 == 0)) {
            divideToFour(6);
        }
    }
    private void initialize(){
        reset();
        setBeepersInBag(1000);
        findDimensions();
        x = width;y = height;
        if (width > height) shape = 1; else shape = 0;
        if (width == height) shape = 2;
    }
    private void reset(){
        steps = 0;beeps = 0;
        x= 0;y= 0;
        direction = Direction.EAST;
    }
    private void finish(){
        goToPoint(1,1);
        faceTo(Direction.EAST);
    }
    public void run() {
        initialize();
        determineMethod();
        finish();
        System.out.println("Width:"+width + " height:" + height + " steps:" + steps + " beeps:" + beeps);
    }
}