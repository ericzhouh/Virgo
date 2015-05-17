package com.winterfarmer.virgo.storage.id.bizconfig;


import com.winterfarmer.virgo.storage.id.model.BaseIdModel;

/**
 * Created by yangtianhang on 15/5/14.
 */
public enum IdModelRedisBiz {
    toy(ToyIdModel.class, "toy");

    public static class ToyIdModel extends BaseIdModel {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ToyIdModel() {
        }

        public ToyIdModel(long id, String name) {
            super(id);
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ToyIdModel)) return false;

            ToyIdModel that = (ToyIdModel) o;

            if (getId() != that.getId()) return false;
            return name.equals(that.name);

        }

        @Override
        public int hashCode() {
            int result = (int) (getId() ^ (getId() >>> 32));
            result = 31 * result + name.hashCode();
            return result;
        }
    }

    private final Class<? extends BaseIdModel> clazz;
    private final String bizName;
    private final String keyInfix;

    IdModelRedisBiz(Class<? extends BaseIdModel> clazz, String keyInfix) {
        this.clazz = clazz;
        this.bizName = clazz.getSimpleName();
        this.keyInfix = keyInfix;
    }

    public Class<? extends BaseIdModel> getClazz() {
        return clazz;
    }

    public String getBizName() {
        return bizName;
    }

    public String getKeyInfix() {
        return keyInfix;
    }
}
