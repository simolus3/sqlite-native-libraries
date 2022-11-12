cmake_minimum_required(VERSION 3.14)
project(
        sqlite3_native_library
        VERSION 3
        LANGUAGES C
)

include(FetchContent)

FetchContent_Declare(
        sqlite3
        URL https://www.sqlite.org/2022/sqlite-autoconf-3390400.tar.gz
)
FetchContent_MakeAvailable(sqlite3)

# uncomment this when the lib is published
#FetchContent_Declare(
#        sqlite3_arabic_tokenizer
#        URL https://github.com/GreentechApps/sqlite3-arabic-tokenizer/blob/main/src/sqlite3-arabic-tokenizer.c
#)
#FetchContent_MakeAvailable(sqlite3_arabic_tokenizer)

set(CMAKE_C_FLAGS "-O3")

add_library(sqlite3 SHARED "${sqlite3_SOURCE_DIR}/sqlite3.c")
target_include_directories(sqlite3 PRIVATE ${sqlite3_SOURCE_DIR})
configure_file("sqlite3-arabic-tokenizer.c" "${sqlite3_SOURCE_DIR}/sqlite3-arabic-tokenizer.c" COPYONLY)
#configure_file("${sqlite3_arabic_tokenizer_SOURCE_DIR}/sqlite3-arabic-tokenizer.c" "${sqlite3_SOURCE_DIR}/sqlite3-arabic-tokenizer.c" COPYONLY)
add_library(sqlite3_arabic_tokenizer SHARED "${sqlite3_SOURCE_DIR}/sqlite3-arabic-tokenizer.c")

target_compile_definitions(sqlite3 PUBLIC
        SQLITE_ENABLE_FTS3
        SQLITE_ENABLE_FTS3_PARENTHESIS
        SQLITE_ENABLE_FTS3_TOKENIZER
        SQLITE_ENABLE_FTS4
        SQLITE_ENABLE_FTS5
        SQLITE_ENABLE_RTREE
        SQLITE_DQS=0
        SQLITE_DEFAULT_MEMSTATUS=0
        SQLITE_TEMP_STORE=2
        SQLITE_MAX_EXPR_DEPTH=0
        SQLITE_OMIT_AUTHORIZATION
        SQLITE_OMIT_DECLTYPE
        SQLITE_OMIT_DEPRECATED
        SQLITE_OMIT_GET_TABLE
        SQLITE_OMIT_LOAD_EXTENSION
        SQLITE_OMIT_PROGRESS_CALLBACK
        SQLITE_OMIT_SHARED_CACHE
        SQLITE_OMIT_TCL_VARIABLE
        SQLITE_OMIT_TRACE
        SQLITE_USE_ALLOCA
        SQLITE_UNTESTABLE
        SQLITE_HAVE_ISNAN
        SQLITE_HAVE_LOCALTIME_R
        SQLITE_HAVE_LOCALTIME_S
        SQLITE_HAVE_MALLOC_USABLE_SIZE
        SQLITE_HAVE_STRCHRNUL
        )
