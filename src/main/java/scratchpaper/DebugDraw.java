package scratchpaper;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.Main;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class DebugDraw extends JFrame {
    int regionId = 12850;
    int scale = 20;
    int border = scale * 2;

    public DebugDraw() throws IOException {
        super("My Frame");

        // You can set the content pane of the frame to your custom class.
        var drawPane = new DrawPane();
        setContentPane(drawPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(border * 2 + scale * 64, border * 2 + scale * 64);
        setVisible(true);
    }

    // Create a component that you can actually draw on.
    class DrawPane extends JPanel {
        CacheData data;

        DrawPane() throws IOException {
            data = new CacheData(new File(Main.DESKTOP_DIR, "cache"), new File(Main.DESKTOP_DIR, "xteas.json"));
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            var regionX = regionId >> 8;
            var regionY = regionId & 0xFF;
            var region = data.getRegionLoader().findRegionForRegionCoordinates(regionX, regionY);

            for (var x = 0; x < 64; x++) {
                for (var y = 0; y < 64; y++) {
                    var tileSetting = region.getTileSetting(0, x, y);
                    var color = switch (tileSetting) {
                        case 0 -> Color.WHITE;
                        case 1 -> Color.BLUE;
                        case 4 -> Color.RED;
                        case 5 -> Color.GREEN;
                        default -> Color.MAGENTA;
                    };
                    g.setColor(color);
                    g.fillRect(border + x * scale, border + y * scale, scale, scale);
                    if (tileSetting != 0) {
                        g.setColor(Color.BLACK);
                        g.drawString(tileSetting + "", border + x * scale, border + y * scale);
                    }
                }
            }

//            var locations = region.getLocations();
//            var objectManager = data.getObjectManager();
//
//            for (var location : locations) {
//                var position = location.getPosition();
//                if (position.getZ() != 0) {
//                    continue;
//                }
//
//                var x = position.getX() - region.getBaseX();
//                var y = position.getY() - region.getBaseY();
//
//                var objectDef = objectManager.getObject(location.getId());
//                var name = objectDef.getName();
//                if (objectDef.getInteractType() == 0) {
//                    if(!name.equals("null")){
//                        g.setColor(Color.GREEN);
//                        System.out.println( name + " " + objectDef.getId() + " " + x + " " + y + "\n" + objectDef);
//                    }else {
//                        g.setColor(Color.BLUE);
//                    }
//                }else {
//                    g.setColor(Color.RED);
//
//                }
//
//                g.fillOval(border + x * scale, border + y * scale, scale / 4, scale / 4);
//                if(!name.equals("null")){
//                    g.drawString(name + "", border + x * scale, border + y * scale);
//                }
//            }

//            var types = new HashSet<Integer>();
//            for (var location : locations) {
//                var position = location.getPosition();
//                if (position.getZ() != 0) {
//                    continue;
//                }
//
//                var type = location.getType();
//                //type used as index for this array new int[]{0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3}; // L: 374
//                types.add(type);
//                var color = switch (type) {
//                    case 0 -> Color.RED;
//                    case 1 -> Color.BLUE;
//                    case 2 -> Color.YELLOW;
//                    case 3 -> Color.GREEN;
//                    case 4 -> Color.MAGENTA;
//                    case 5 -> Color.CYAN;
//                    case 6 -> Color.ORANGE;
//                    case 7 -> Color.PINK;
//                    case 8 -> Color.BLACK;
//                    case 9 -> Color.WHITE;
//                    case 10 -> Color.LIGHT_GRAY;
//                    case 11 -> Color.DARK_GRAY;
//                    case 22 -> Color.GRAY;
//                    default ->{
//                        System.out.println("Unknown type: " + type);
//                         throw new IllegalArgumentException();
//                    }
//                };
//
//                g.setColor(color);
//
//                var x = position.getX() - region.getBaseX();
//                var y = position.getY() - region.getBaseY();
//                g.fillOval(border + x * scale, border + y * scale, scale / 4, scale / 4);
//                g.drawString(location.getId() + "", border + x * scale, border + y * scale);
//            }
//            System.out.println(types);
        }
    }

    public static void main(String[] args) throws Exception {
        new DebugDraw();
    }
}