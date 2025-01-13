package lib.pattern;


import battlecode.common.PaintType;

public class Patterns {

    public static final PaintType one = PaintType.ALLY_PRIMARY;
    public static final PaintType two = PaintType.ALLY_SECONDARY;

    public static final PaintType[][] chip_tower = {
            {one, two, two, two, one},
            {two, two, one, two, two},
            {two, one, null, one, two},
            {two, two, one, two, two},
            {one, two, two, two, one},
    };

    public static final PaintType[][] paint_tower = {
            {two, one, one, one, two},
            {one, two, one, two, one},
            {one, one, null, one, one},
            {one, two, one, two, one},
            {two, one, one, one, two},
    };

    public static final PaintType[][] special_resource = {
            {two, one, two, one, two},
            {one, two, one, two, one},
            {two, one, one, one, two},
            {one, two, one, two, one},
            {two, one, two, one, two},
    };
}
