package com.example.byteplus_effects_plugin.sports.utils;

import android.graphics.Color;
import android.graphics.Path;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created on 2021/7/18 14:24
 */
public class SVGPathParser {
    private XmlPullParser parser = Xml.newPullParser();

    public SVGItem parseAVG(InputStream is) throws XmlPullParserException, IOException {
        is.reset();
        parser.setInput(is, "utf-8");
        return new SVGItem(parser);
    }

    public static class SVGItem {
        public int width = 0;
        public int height = 0;
        public boolean fill = false;
        public List<PathItem> paths = new ArrayList<>();

        public SVGItem(XmlPullParser parser) throws IOException, XmlPullParserException {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        switch (parser.getName()) {
                            case "svg":
                                parse(parser);
                                break;
                            case "circle":
                                paths.add(new CircleItem(parser));
                                break;
                            case "path":
                                paths.add(new SVGPathItem(parser));
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }

                eventType = parser.next();
            }
        }

        private void parse(XmlPullParser parser) {
            for (int i = 0; i < parser.getAttributeCount(); i++) {
                String name = parser.getAttributeName(i);
                String value = parser.getAttributeValue(i);

                switch (name) {
                    case "width":
                        width = Integer.parseInt(value);
                        break;
                    case "height":
                        height = Integer.parseInt(value);
                        break;
                    case "fill":
                        fill = !value.equals("none");
                        break;
                }
            }
        }

        public abstract static class PathItem {
            public Path path = new Path();
            public int strokeWidth;
            public int strokeColor;

            private final XmlPullParser parser;

            public PathItem(XmlPullParser parser) {
                this.parser = parser;

                strokeWidth = intAttr("stroke-width");
                strokeColor = colorAttr("stroke");
            }

            protected float floatAttr(String name) {
                return Float.parseFloat(parser.getAttributeValue(null, name));
            }

            protected int intAttr(String name) {
                return Integer.parseInt(parser.getAttributeValue(null, name));
            }

            protected int colorAttr(String name) {
                String value = parser.getAttributeValue(null, name);
                if (value.equals("none")) {
                    return 0;
                } else {
                    return Color.parseColor(value);
                }
            }
        }

        public static class CircleItem extends PathItem {

            public CircleItem(XmlPullParser parser) {
                super(parser);

                float cx = floatAttr("cx");
                float cy = floatAttr("cy");
                float r = floatAttr("r");

                path.addCircle(cx, cy, r, Path.Direction.CCW);
            }
        }

        public static class SVGPathItem extends PathItem {
            private float lastX;
            private float lastY;
            private int position;
            private final String svg;

            public SVGPathItem(XmlPullParser parser) {
                super(parser);

                svg = parser.getAttributeValue(null, "d");
                startParse(path);
            }

            private void startParse(Path path) {
                while (hasNextCommand()) {
                    char command = nextCommand();
                    switch (command) {
                        case 'm':
                        case 'M':
                            nextPoint();
                            path.moveTo(lastX, lastY);
                            break;
                        case 'l':
                        case 'L':
                            nextPoint();
                            path.lineTo(lastX, lastY);
                            break;
                        case 'h':
                        case 'H':
                            lastX = nextFloat();
                            path.lineTo(lastX, lastY);
                            break;
                        case 'v':
                        case 'V':
                            lastY = nextFloat();
                            path.lineTo(lastX, lastY);
                            break;
                        case 'c':
                        case 'C': {
                            float f0 = nextFloat();
                            float f1 = nextFloat();
                            float f2 = nextFloat();
                            float f3 = nextFloat();
                            float f4 = nextFloat();
                            float f5 = nextFloat();
                            path.cubicTo(f0, f1, f2, f3, f4, f5);
                            lastX = f4;
                            lastY = f5;
                        }
                        break;
                        case 's':
                        case 'S': {
                            float f0 = nextFloat();
                            float f1 = nextFloat();
                            float f2 = nextFloat();
                            float f3 = nextFloat();
                            path.cubicTo(lastX, lastY, f0, f1, f2, f3);
                            lastX = f2;
                            lastY = f3;
                        }
                        break;
                        case 'q':
                        case 'Q': {
                            float f0 = nextFloat();
                            float f1 = nextFloat();
                            float f2 = nextFloat();
                            float f3 = nextFloat();
                            path.quadTo(f0, f1, f2, f3);
                            lastX = f2;
                            lastY = f3;
                        }
                        break;
                        case 't':
                        case 'T': {
                            float f0 = nextFloat();
                            float f1 = nextFloat();
                            path.quadTo(lastX, lastY, f0, f1);
                            lastX = f0;
                            lastY = f1;
                        }
                        break;
                        case 'a':
                        case 'A':
                            break;
                        case 'z':
                        case 'Z':
                            path.close();
                            break;
                    }
                }
            }

            private boolean hasNextCommand() {
                return position < svg.length();
            }

            private char nextCommand() {
                return svg.charAt(position++);
            }

            private void nextPoint() {
                lastX = nextFloat();
                lastY = nextFloat();
            }

            private float nextFloat() throws IllegalArgumentException {
                int endPosition = position + 1;
                while (endPosition < svg.length()) {
                    if (isEndOfFloat(svg.charAt(endPosition))) {
                        break;
                    }
                    endPosition += 1;
                }

                if (position == svg.length()) {
                    throw new IllegalArgumentException("could not read float from " + position + " in " + svg);
                }

                float value = Float.parseFloat(svg.substring(position, endPosition));
                position = endPosition;
                return value;
            }

            private boolean isEndOfFloat(char c) {
                return !(c == '.' || (c >= '0' && c <= '9'));
            }
        }
    }
}
