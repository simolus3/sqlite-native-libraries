cmake_minimum_required(VERSION 3.14)
project(
    sqlite3_native_library
    VERSION 3
    LANGUAGES C
)

include(FetchContent)

FetchContent_Declare(
  sqlite3
  URL https://sqlite.org/2024/sqlite-autoconf-3450000.tar.gz
)
FetchContent_MakeAvailable(sqlite3)

set(CMAKE_C_FLAGS "-O3")

add_library(sqlite3 SHARED "${sqlite3_SOURCE_DIR}/sqlite3.c")
target_include_directories(sqlite3 PRIVATE ${sqlite3_SOURCE_DIR})

target_compile_definitions(sqlite3 PUBLIC
    SQLITE_ENABLE_FTS5
    SQLITE_ENABLE_RTREE
    SQLITE_DQS=0
    SQLITE_DEFAULT_MEMSTATUS=0
    SQLITE_TEMP_STORE=2
    SQLITE_MAX_EXPR_DEPTH=0
    SQLITE_STRICT_SUBTYPE=1
    SQLITE_OMIT_AUTHORIZATION
    SQLITE_OMIT_DECLTYPE
    SQLITE_OMIT_DEPRECATED
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
