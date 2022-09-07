cmake_minimum_required(VERSION 3.14)
project(
    sqlite3_native_library
    VERSION 3
    LANGUAGES C
)

include(FetchContent)

FetchContent_Declare(
  sqlite3
  URL https://www.sqlite.org/2022/sqlite-autoconf-3390300.tar.gz
)
FetchContent_MakeAvailable(sqlite3)

FetchContent_Declare(
  spellfix1
  URL https://raw.githubusercontent.com/sqlite/sqlite/branch-3.39/ext/misc/spellfix.c
  DOWNLOAD_NO_EXTRACT TRUE
)
FetchContent_MakeAvailable(spellfix1)

set(CMAKE_C_FLAGS "-O3")

add_library(sqlite3 SHARED 
    "${sqlite3_SOURCE_DIR}/sqlite3.c"
    "${spellfix1_SOURCE_DIR}/spellfix.c"
)

target_include_directories(sqlite3 PRIVATE ${sqlite3_SOURCE_DIR})

target_compile_definitions(sqlite3 PUBLIC
    SQLITE_DQS=0
    SQLITE_THREADSAFE=2
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
    SQLITE_ENABLE_FTS5
    SQLITE_ENABLE_JSON1
    SQLITE_ENABLE_RTREE
)