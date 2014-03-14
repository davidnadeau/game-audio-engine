/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundengine;

/**
 *
 * @author soote
 */
public enum Sounds {

    JUMP("filepath"),
    LAND("filepath"),
    DUNGEON("filepath");

    private final String filePath;
    Sounds(String filePath) {
        this.filePath = filePath;
    }

    public int fuck() {
        return 0;
    }

}
