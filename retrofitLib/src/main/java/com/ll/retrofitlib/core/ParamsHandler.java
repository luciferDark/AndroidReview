package com.ll.retrofitlib.core;

public abstract class ParamsHandler<T> {
    abstract void  apply(ServiceMethod serviceMethod, T value);

    public static class Query<T>  extends ParamsHandler<T>{
        private String key;

        public Query(String key) {
            this.key = key;
        }
        @Override
        void apply(ServiceMethod serviceMethod, T value) {
            serviceMethod.addQueryParamHandler(key, value);
        }
    }

    public static class Field<T>  extends ParamsHandler<T>{
        private String key;

        public Field(String key) {
            this.key = key;
        }

        @Override
        void apply(ServiceMethod serviceMethod, T value) {
            serviceMethod.addFieldParamHandler(key, value);
        }
    }

}
