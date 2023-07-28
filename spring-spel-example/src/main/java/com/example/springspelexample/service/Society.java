package com.example.springspelexample.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fdrama
 * date 2023年07月26日 14:34
 */
public class Society {
    private String name;

    public static String Advisors = "advisors";
    public static String President = "president";

    private List<Inventor> members = new ArrayList<Inventor>();
    private Map officers = new HashMap();

    public List getMembers() {
        return members;
    }

    public Map getOfficers() {
        return officers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMember(String name)
    {
        boolean found = false;
        for (Inventor inventor : members) {
            if (inventor.getName().equals(name))
            {
                found = true;
                break;
            }
        }
        return found;
    }
}
