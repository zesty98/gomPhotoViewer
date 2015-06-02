package com.gomdev.gomPhotoViewer;

/**
 * Created by gomdev on 15. 5. 18..
 */
public class PhotoViewerConfig {
    static final boolean DEBUG = false;
    static final String TAG = "gomdev";

    static final int CACHE_SIZE_IN_KB = 1024 * 15;

    public static final String DETAIL_VIEW_POSITION = "detail_view_position";

    static final String PREF_NAME = "500px";
    static final String PREF_FEATURES = "features";
    static final String PREF_IMAGE_SIZE = "image_size";
    static final String PREF_RPP = "rpp";
    static final String PREF_ONLY = "only";
    static final String PREF_CONSUMER_KEY = "consumer_key";


    static final String HOST = "https://api.500px.com/v1";
    static final int NUM_OF_ITEMS_IN_PAGE = 50;

    static final int MAX_NUM_OF_CONTAINER = 1000;

    // image properties
    static final String FEATURES = "feature";
    static final String FEATURES_POPULAR = "popular";
    static final String FEATURES_EDITORS = "editors";
    static final String FEATURES_HIGHEST_RATED = "highest_rated";
    static final String FEATURES_UPCOMING = "upcoming";
    static final String FEATURES_FRESH_TODAY = "fresh_today";
    static final String FEATURES_FRESH_YESTERDAY = "fresh_yesterday";
    static final String FEATURES_FRESH_WEEK = "fresh_week";

    enum Feature {
        POPULAR(0),
        EDITORS(1),
        HIGHEST_RATED(2),
        UPCOMING(3),
        FRESH_TODAY(4),
        FRESH_YESTERDAY(5),
        FRESH_WEEK(6);

        private int mIndex = 0;

        Feature(int index) {
            mIndex = index;
        }

        int getIndex() {
            return mIndex;
        }
    }

    static final String PAGE = "page";

    static final String RPP = "rpp";

    static final String IMAGE_SIZES = "image_size[]";


    // CROP
    static final String IMAGE_SIZE_1 = "1";
    static final String IMAGE_SIZE_2 = "2";
    static final String IMAGE_SIZE_3 = "3";
    static final String IMAGE_SIZE_100 = "100";
    static final String IMAGE_SIZE_200 = "200";
    static final String IMAGE_SIZE_440 = "440";
    static final String IMAGE_SIZE_600 = "600";

    // UNCROP
    static final String IMAGE_SIZE_4 = "4";
    static final String IMAGE_SIZE_5 = "5";
    static final String IMAGE_SIZE_20 = "20";
    static final String IMAGE_SIZE_21 = "21";
    static final String IMAGE_SIZE_30 = "30";
    static final String IMAGE_SIZE_1080 = "1080";
    static final String IMAGE_SIZE_1600 = "1600";
    static final String IMAGE_SIZE_2048 = "2048";

    static final String DEFAULT_IMAGE_SIZE = IMAGE_SIZE_1600;

    static final String ONLY = "only";
//    static final String ONLY_UNCATEGORIZED = "Uncategorized";
//    static final String ONLY_ABSTRACT = "Abstract";
//    static final String ONLY_ANIMALS = "Animals";
//    static final String ONLY_BLACK_AND_WHITE = "Black%20and%20White";
//    static final String ONLY_CELEBRITIES = "Celebrities";
//    static final String ONLY_CITY_AND_ARCHTECTURE = "City%20and%20Architecture";
//    static final String ONLY_COMMERCIAL = "Commercial";
//    static final String ONLY_CONCERT = "Concert";
//    static final String ONLY_FAMILY = "Family";
//    static final String ONLY_FASHION = "Fashion";
//    static final String ONLY_FILM = "Film";
//    static final String ONLY_FINE_ART = "Fine%20Art";
//    static final String ONLY_FOOD = "Food";
//    static final String ONLY_JOURNALISM = "Journalism";
//    static final String ONLY_LANDSCAPES = "Landscapes";
//    static final String ONLY_MACRO = "Macro";
//    static final String ONLY_NATURE = "Nature";
//    static final String ONLY_NUDE = "Nude";
//    static final String ONLY_PEOPLE = "People";
//    static final String ONLY_PERFORMAING_ART = "Performing%20Arts";
//    static final String ONLY_SPORT = "Sport";
//    static final String ONLY_STILL_LIFE = "Still%20Life";
//    static final String ONLY_STREET = "Street";
//    static final String ONLY_TRANSPORTATION = "Transportation";
//    static final String ONLY_TRAVEL = "Travel";
//    static final String ONLY_UNDERWATER = "Underwater";
//    static final String ONLY_URBAN_EXPLORATION = "Urban%20Exploration";
//    static final String ONLY_WEDDING = "Wedding";

    enum Category {
        UNCATEGORIZED(0),
        ABSTRACT(1),
        ANIMALS(2),
        BLACK_AND_WHITE(3),
        CELEBRITIES(4),
        CITY_AND_ARCHTECTURE(5),
        COMMERCIAL(6),
        CONCERT(7),
        FAMILY(8),
        FASHION(9),
        FILM(10),
        FINE_ART(11),
        FOOD(12),
        JOURNALISM(13),
        LANDSCAPES(14),
        MACRO(15),
        NATURE(16),
        NUDE(17),
        PEOPLE(18),
        PERFORMAING_ART(19),
        SPORT(20),
        STILL_LIFE(21),
        STREET(22),
        TRANSPORTATION(23),
        TRAVEL(24),
        UNDERWATER(25),
        URBAN_EXPLORATION(26),
        WEDDING(27);

        private int mIndex = 0;

        Category(int index) {
            mIndex = index;
        }

        int getIndex() {
            return mIndex;
        }
    }

    ;

    static final String CONSUMER_KEY = "consumer_key";
}
