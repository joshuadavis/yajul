/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jul 4, 2002
 * Time: 10:17:30 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.empire;

public class UnitType extends WorldComponent
{
    private char character;
    private int movement;

    public UnitType(World w)
    {
        super(w);
    }

    public char getCharacter()
    {
        return character;
    }

    public void setCharacter(char character)
    {
        this.character = character;
    }

    public int getMovement()
    {
        return movement;
    }

    public void setMovement(int movement)
    {
        this.movement = movement;
    }
}
