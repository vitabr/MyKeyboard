#include<jni.h>
#include<string.h>

jdouble Java_com_vito_mykeyboard_uttil_Parser_add(JNIEnv* env, jdouble a, jdouble b){
    return a + b;
}

jdouble Java_com_vito_mykeyboard_uttil_Parser_sub(JNIEnv* env, jdouble a, jdouble b){
    return a - b;
}

jdouble Java_com_vito_mykeyboard_uttil_Parser_multiply(JNIEnv* env, jdouble a, jdouble b){
    return a * b;
}

jdouble Java_com_vito_mykeyboard_uttil_Parser_div(JNIEnv* env, jdouble a, jdouble b){
    return a / b;
}