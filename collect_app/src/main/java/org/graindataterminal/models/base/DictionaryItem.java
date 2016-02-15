package org.graindataterminal.models.base;

public class DictionaryItem {
    private String id;
    private String name;
    private String group;

    public DictionaryItem(String id, String name, String group) {
        this.id = id.toLowerCase();
        this.name = name;
        this.group = group;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }
}
