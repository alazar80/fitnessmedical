package com.example.sql;

public class ApiConfig {
    private static final String TAG = "Api Configuration Activity";
    public static final String  BASE_URL = BuildConfig.BASE_URL;



    // inside your ApiConfig class

    // Integrated scripts
    public static final String GET_WORKOUT_SESSIONS    = BASE_URL + "get_workouts.php";
    public static final String GET_WEIGHT_RECORDS       = BASE_URL + "get_weights.php";
    public static final String GET_CALORIE_RECORDS      = BASE_URL + "get_calories.php";
    public static final String GET_EXERCISE_DISTRIBUTION= BASE_URL + "get_exercise_dist.php";
    public static final String GET_MUSCLE_COVERAGE      = BASE_URL + "get_muscle_coverage.php";




    //chapa
    public static final String chapaURL = "https://api.chapa.co/v1/transaction/initialize";
    public static final String chapaSecretKey = "CHASECK_TEST-luIMRyqINQreokWdZKjE2gk0XBGpqUgN";
    public static final String chapaPublicKey = "CHAPUBK_TEST-YuBwsyyAMZuEjr95ArfaJvO0rNLPEq52";
    public static final String SEND_EMAIL_URL = BASE_URL+"send_email.php";
    public static final String UPDATE_EXPERIENCE_LEVEL = BASE_URL+"update_experience_level.php";
    public static final String UPDATE_USER_PROGRESS = BASE_URL + "update_user_progresss.php";
    public static final String SEND_VERIFICATION_EMAIL_URL = BASE_URL + "send_verificationemail.php";
    public static final String RESET_PASSWORD_URL = BASE_URL + "reset_password.php";
    public static final String GET_PROGRESS = BASE_URL +"get_progress.php";

    public static final String GET_WORKOUT_HISTORY = BASE_URL + "get_workout_history.php";

    public static final String VERIFY_URL= BASE_URL + "verify.php";
    public static final String DELETE_USER_URL = BASE_URL + "admin_delete_user.php";
    public static final String USERS_API_URL = BASE_URL +"get_users.php";
    public static final String GET_USERS_GOAL = BASE_URL +"get_user_goal.php";

    public static final String GET_EXERCISE_ICON_ID = BASE_URL +"/fitness/";
    public static final String GET_MEAL_ICON_ID = BASE_URL +"/meal/";
    public static final String GET_PROFILE_IMAGES = BASE_URL+"/";
    //users
    public static final String GET_USERS_URL = BASE_URL + "admin_get_users.php";
    public static final String GET_USER_GOAL = BASE_URL+"get_user_goal.php";

    public static final String GET_USER_BY_ID_URL = BASE_URL + "get_user_by_id.php";
    public static final String UPDATE_PROFILE_IMAGE_URL = BASE_URL + "update_profile_image.php";


    public static final String CHANGE_PASSWORD_URL = BASE_URL +"change_password.php";
    public static final String USERS_API = BASE_URL +"get_users.php";

    public static final String UPDATE_USERS_GOAL = BASE_URL +"update_goal.php";
    public static final String LOGIN_URL = BASE_URL + "login.php";//_/
    public static final String FETCH_PROFILE_URL = BASE_URL + "get_user_profile.php";//_/
    public static final String USER_WELCOME_INSERT = BASE_URL +"welcome_insert.php";//_/
    public static final String INSERT_USER = BASE_URL +"insert_user.php";//_/
    public static final String userassignUrl = BASE_URL +"assign_defaults_for_users.php";//_/

    public static final String ADMIN_GET_FEEDBACK = BASE_URL + "admin_get_feedbacks.php";
    public static final String ADMIN_DELETE_FEEDBACK = BASE_URL + "admin_delete_feedback.php";

    public static final String SEND_SIGNUP_VERIFICATION_URL = BASE_URL + "send_verification_signup.php";
    public static final String VERIFY_SIGNUP_URL= BASE_URL + "verify_signup.php";

    public static final String HAS_DOCTOR_ID= BASE_URL + "hasDoctor.php";
    public static final String HAS_MEDICAL_DETAIL= BASE_URL + "hasmedicaldetail.php";

    public static final String doctorassignUrl = BASE_URL +"assign_default_for_doctor.php";
    public static final String GET_USER_INFO = BASE_URL +"get_user_info.php";
    public static final String GET_USER_DOCTOR = BASE_URL +"get_user_doctor.php";
    public static final String LOG_PAYMENT_URL = BASE_URL + "log_payment.php";
    public static final String GET_USER_REPORT = BASE_URL +"report.php";
    public static final String URL_DELETE_USER = BASE_URL + "delete_user.php";
    public static final String UPDATE_USER_PROFILE_URL = BASE_URL + "update_user_profile.php";

    //doctor
    public static final String URL_SEARCH_DOCTOR = BASE_URL + "search_doctors.php";
    public static final String URL_DELETE_DOCTOR = BASE_URL + "delete_doctor.php";

    public static final String ADMIN_UPDATE_USER_URL = BASE_URL + "admin_update_user.php";

    public static final String DOCTOR_UPDATE_MEAL_URL = BASE_URL + "doctor_update_exercise.php";
    public static final String ADMIN_UPDATE_MEAL_URL = BASE_URL + "admin_update_exercise.php";

    public static final String DOCTOR_UPDATE_EXERCISE_URL = BASE_URL + "doctor_update_exercise.php";
    public static final String ADMIN_UPDATE_EXERCISE_URL = BASE_URL + "admin_update_exercise.php";
    public static final String UPDATE_DOCTOR_URL = BASE_URL + "admin_update_doctor.php";
    public static final String GET_DOCTORS_URL = BASE_URL+"admin_get_doctors.php";
    public static final String DELETE_DOCTOR_URL = BASE_URL+"admin_delete_doctor.php";
    public static final String DOCTOR_PROFILE_URL = BASE_URL+"get_doctor_profile.php";
    public static final String WELCOME_DOCTOR_INSERT = BASE_URL+"welcome_doctor_insert.php";
    public static final String ASSIGN_DOCTORS_URL = BASE_URL + "assign_doctor.php";
    public static final String UPDATE_DOCTOR_PROFILE_URL = BASE_URL + "update_doctor_profile.php";

    //meals
    public static final String URL_SEARCH_MEAL = BASE_URL + "search_meals.php";
    public static final String URL_EDIT_MEAL = BASE_URL + "search_meals.php";
    public static final String GET_MEALS = BASE_URL + "get_meals.php";

    public static final String GET_MEAL_BY_USER = BASE_URL+"get_meals_by_user.php";
    public static final String GET_MAIN_MENU_MEAL_BY_USER = BASE_URL+"get_main_menu_meal_by_user.php";
    public static final String GET_MAIN_MEAL_BY_USER = BASE_URL+"get_main_menu_meals_by_user.php";
    public static final String GET_RECOMMENDED_MEALS_BY_USER = BASE_URL+"get_recommended_meals_by_user.php";
    public static final String GET_MEAL_COUNT_URL = BASE_URL+ "count_assigned_meals.php";
    public static final String URL_VIEW_MEALS = BASE_URL+"view_meals.php";
    public static final String URL_DELETE_MEAL = BASE_URL+"delete_meal.php";
    public static final String ADD_MEAL_URL = BASE_URL+"doctoraddmeal.php";
    public static final String GET_MEALS_URL = BASE_URL + "get_all_meals.php";
    public static final String GET_UNASSIGNED_MEALS = BASE_URL + "get_unassigned_meals.php";
    public static final String ASSIGN_MEALS_URL = BASE_URL + "assign_meals.php";


    //exercises
    public static final String URL_SEARCH_EXERCISE = BASE_URL + "search_meals.php";
    public static final String URL_EDIT_EXERCISE = BASE_URL + "search_meals.php";
    public static final String GET_EXERCISES = BASE_URL + "get_exercises.php";
    public static final String GET_EXERCISES_URL_ANDROID = BASE_URL + "get_all_exercises_android.php";

//public static final String GET_EXERCISE_BY_USER_FOR_GYM = BASE_URL + "get_exercises_by_user_for_gym.php";
    public static final String GET_EXERCISE_BY_USER_FOR_GYM = BASE_URL + "get_exercise_by_user_for_gym.php";


//public static final String GET_EXERCISE_BY_USER_FOR_HOME = BASE_URL + "get_exercises_by_user_for_home.php";
    public static final String GET_EXERCISE_BY_USER_FOR_HOME = BASE_URL + "get_exercise_by_user_for_home.php";

    public static final String GET_EXERCISE_BY_DOCTOR_FOR_HOME = BASE_URL+"get_exercises_by_doctor.php";
    public static final String GET_MEALS_BY_DOCTOR_FOR_HOME = BASE_URL+"get_meals_by_doctor_for_home.php";

    public static final String GET_EXERCISES_BY_ADMIN = BASE_URL + "get_exercises_by_admin.php";
    public static final String GET_MEALS_BY_ADMIN_FOR_HOME = ApiConfig.BASE_URL+"get_meals_by_admin_for_home.php";


    public static final String UPDATE_USER_WORKOUT = BASE_URL + "update_user_workout.php";

//GET ASSIGN MEALS AND EXERCISES
    public static final String GET_EXERCISES_ASSIGN_ADMIN = BASE_URL + "get_exercise_by_admin.php";
    public static final String GET_MEALS_ASSIGN_ADMIN = ApiConfig.BASE_URL+"admin_get_unassigned_meals.php";





    public static final String ADMIN_DELETE_MEAL =ApiConfig.BASE_URL+"delete_meal.php";
    public static final String ADMIN_ADD_MEAL =ApiConfig.BASE_URL+"admin_add_meal.php";
    public static final String DOCTOR_ADD_MEAL =ApiConfig.BASE_URL+"doctor_add_meal.php";
    public static final String ADMIN_ADD_EXERCISE =ApiConfig.BASE_URL+"admin_add_exercise.php";
    public static final String DOCTOR_ADD_EXERCISE_URL =ApiConfig.BASE_URL+"doctor_add_exercise.php";
    public static final String ADMIN_ASSIGN_MEAL =ApiConfig.BASE_URL+"admin_assign_meals.php";
    public static final String ADMIN_ASSIGN_EXERCISE =ApiConfig.BASE_URL+"admin_assign_exercise.php";

    public static final String URL_VIEW_EXERCISES = BASE_URL+"view_exercises.php";
    public static final String URL_DELETE_EXERCISE = BASE_URL+"delete_exercises.php";
    public static final String ADD_EXERCISE_URL = BASE_URL+"doctoraddexercise.php";

    public static final String GET_EXERCISES_URL = BASE_URL + "get_all_exercises.php";
    public static final String URL_SEARCH_USER = BASE_URL + "search_meals.php";
    public static final String GET_EXERCISES_WITH_DOCTOR_ID = BASE_URL + "get_exercises_with_doctor_id.php";
    public static final String GET_EXERCISE_COUNT_URL = BASE_URL+ "count_assigned_exercises.php";

    public static final String ASSIGN_EXERCISES_URL = BASE_URL + "assign_exercises.php";


}
