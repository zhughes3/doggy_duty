package com.zh.afinal;

import java.util.ArrayList;
import java.util.List;

public class PriorityQueue {
    List<Vertex> nodes;

    public PriorityQueue() {
        nodes = new ArrayList<>();
    }

    public void add(Vertex v) {
        nodes.add(v);
    }

    public Vertex removeMin() {
        Vertex min = null;
        for (Vertex v : nodes) {
            if (min == null) {
                min = v;
            } else {
                if (v.getDistanceFromSrc() < min.getDistanceFromSrc()) {
                    min = v;
                }
            }
        }
        nodes.remove(min);
        return min;
    }

    public boolean isEmpty() {
        return nodes.size() == 0;
    }

}
