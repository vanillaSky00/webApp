package com.vanillasky.springbootquickstart.user;

/**
 *     "geo": {
 *       "lat": "-37.3159",
 *       "lng": "81.1496"
 *       }
 */
public record Geo(
        String lat,
        String lng
) {
}
