package com.example.runsense.global.util;

/**
 * 위경도(WGS84) 좌표를 공공데이터 API 호출에 필요한 좌표계로 변환하는 유틸리티.
 * - 기상청 동네예보 격자(nx, ny): LCC(Lambert Conformal Conic) DFS 투영
 * - 에어코리아 근접측정소 조회용 TM 중부원점(EPSG:5181): Transverse Mercator 정변환
 */
public final class CoordinateConverter {

    private CoordinateConverter() {
    }

    public record KmaGrid(int nx, int ny) {
    }

    public record TmCoordinate(double tmX, double tmY) {
    }

    // 기상청 LCC DFS 격자 변환 상수
    private static final double RE = 6371.00877; // 지구 반경(km)
    private static final double GRID = 5.0; // 격자 간격(km)
    private static final double SLAT1 = 30.0; // 표준 위도 1
    private static final double SLAT2 = 60.0; // 표준 위도 2
    private static final double OLON = 126.0; // 기준점 경도
    private static final double OLAT = 38.0; // 기준점 위도
    private static final double XO = 43; // 기준점 X 좌표(격자)
    private static final double YO = 136; // 기준점 Y 좌표(격자)
    private static final double DEGRAD = Math.PI / 180.0;

    /**
     * 위경도 → 기상청 동네예보 격자(nx, ny) 변환.
     * 기상청 표준 LCC(Lambert Conformal Conic) DFS 알고리즘(dfs_xy_conv)을 따른다.
     */
    public static KmaGrid toKmaGrid(double lat, double lon) {
        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = (Math.pow(sf, sn) * Math.cos(slat1)) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = (re * sf) / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        ra = (re * sf) / Math.pow(ra, sn);
        double theta = lon * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int nx = (int) Math.floor(ra * Math.sin(theta) + XO + 0.5);
        int ny = (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        return new KmaGrid(nx, ny);
    }

    // GRS80 타원체 기반 TM 중부원점(EPSG:5181) 변환 상수
    private static final double A = 6378137.0; // GRS80 장반경(m)
    private static final double F = 1 / 298.257222101; // GRS80 편평률
    private static final double K0 = 1.0; // 축척계수
    private static final double LAT0 = 38.0; // 원점 위도(도)
    private static final double LON0 = 127.0; // 원점 경도(도)
    private static final double X0 = 200000.0; // false easting(m)
    private static final double Y0 = 500000.0; // false northing(m)
    private static final double D2R = Math.PI / 180;

    // 자오선호장 M(φ) — Snyder, "Map Projections: A Working Manual" 식(3-21).
    private static double meridianArc(double phi, double e2) {
        double e4 = e2 * e2;
        double e6 = e4 * e2;
        return A * ((1 - e2 / 4 - (3 * e4) / 64 - (5 * e6) / 256) * phi
                - ((3 * e2) / 8 + (3 * e4) / 32 + (45 * e6) / 1024) * Math.sin(2 * phi)
                + ((15 * e4) / 256 + (45 * e6) / 1024) * Math.sin(4 * phi)
                - ((35 * e6) / 3072) * Math.sin(6 * phi));
    }

    /**
     * 위경도 → TM 중부원점(EPSG:5181, GRS80) 평면좌표 변환.
     * 에어코리아 근접측정소(getNearbyMsrstnList)가 받는 tmX/tmY 좌표계와 동일하다.
     */
    public static TmCoordinate toTm(double lat, double lon) {
        double e2 = 2 * F - F * F; // 제1이심률 제곱
        double ep2 = e2 / (1 - e2); // 제2이심률 제곱

        double phi = lat * D2R;
        double lam = lon * D2R;
        double phi0 = LAT0 * D2R;
        double lam0 = LON0 * D2R;

        double sinPhi = Math.sin(phi);
        double cosPhi = Math.cos(phi);
        double tanPhi = Math.tan(phi);

        double n = A / Math.sqrt(1 - e2 * sinPhi * sinPhi);
        double t = tanPhi * tanPhi;
        double c = ep2 * cosPhi * cosPhi;
        double a1 = (lam - lam0) * cosPhi;

        double m = meridianArc(phi, e2);
        double m0 = meridianArc(phi0, e2);

        double tmX = X0 + K0 * n * (a1
                + ((1 - t + c) * Math.pow(a1, 3)) / 6
                + ((5 - 18 * t + t * t + 72 * c - 58 * ep2) * Math.pow(a1, 5)) / 120);

        double tmY = Y0 + K0 * (m - m0 + n * tanPhi * (
                (a1 * a1) / 2
                        + ((5 - t + 9 * c + 4 * c * c) * Math.pow(a1, 4)) / 24
                        + ((61 - 58 * t + t * t + 600 * c - 330 * ep2) * Math.pow(a1, 6)) / 720));

        return new TmCoordinate(tmX, tmY);
    }
}
