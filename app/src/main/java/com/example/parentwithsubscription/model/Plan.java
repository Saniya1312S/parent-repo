package com.example.parentwithsubscription.model;

import java.util.List;
import java.util.Map;

public class Plan {
    private String title;
    private String description;
    private Map<String, Boolean> features;
    private List<PlanDetail> plans;

    public Plan(String title, String description, Map<String, Boolean> features, List<PlanDetail> plans) {
        this.title = title;
        this.description = description;
        this.features = features;
        this.plans = plans;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Boolean> getFeatures() {
        return features;
    }

    public List<PlanDetail> getPlans() {
        return plans;
    }

    public static class PlanDetail {
        private int duration;
        private double charges;

        public PlanDetail(int duration, double charges) {
            this.duration = duration;
            this.charges = charges;
        }

        public PlanDetail(int duration) {
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }

        public double getCharges() {
            return charges;
        }
    }
}









/*
package com.example.parentwithsubscription.model;

public class Plan {
    private String title;
    private String description;
    private String price;

    public Plan(String title, String description, String price) {
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }
}
*/
