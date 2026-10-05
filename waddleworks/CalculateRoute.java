    @Override
    public int getMinBetween(Building from, Building to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("The from/to buildings can not be null.");
        } else if (to.closest() == null || from.closest() == null) {
            throw new IllegalArgumentException("There is no closest intersection to drive through!");
        }
        Vertex<Intersection> fromV = roads.getVertexMap().get(from.closest());
        Vertex<Intersection> toV = roads.getVertexMap().get(to.closest());
        if ((toV == null) || (fromV == null)) {
            throw new IllegalArgumentException("The from/to buildings must be contained in the grid!");
        }
        Queue<Vertex<Intersection>> fastest = new LinkedList<>();
        Set<Vertex<Intersection>> visited = new HashSet<>();
        fastest.add(fromV);
        visited.add(fromV);
        int distance = 0;
        while (!fastest.isEmpty()) {
            for (Vertex<Intersection> intersect : new LinkedList<>(fastest)) {
                fastest.remove();
                if (intersect.equals(toV)) {
                    return distance;
                } else {
                    for (VertexDistance<Intersection> adj : roads.getNeighbors(intersect)) {
                        if (!visited.contains(adj.vertex())) {
                            fastest.add(adj.vertex());
                            visited.add(adj.vertex());
                        }
                    }
                }
            }
            distance++;
        }
        return -1;
    }

    @Override
    public List<Intersection> calculateRoute(Building from, Building to, Set<Intersection.Type> avoid) {
        if (from == null || to == null || avoid == null) {
            throw new IllegalArgumentException("The from/to and avoided buildings can not be null.");
        } else if (from.closest() == null || to.closest() == null) {
            throw new IllegalArgumentException("The from/to buildings must have at least 1 nearby intersection!");
        }
        Vertex<Intersection> toV = roads.getVertexMap().get(to.closest());
        Vertex<Intersection> fromV = roads.getVertexMap().get(from.closest());
        if (toV == null || fromV == null) {
            throw new IllegalArgumentException("The from/to buildings must be in the graph!");
        }
        PriorityQueue<VertexDistance<Intersection>> tree = new PriorityQueue<>();
        Map<Vertex<Intersection>, Integer> distances = new HashMap<>();
        Map<Vertex<Intersection>, Vertex<Intersection>> distanceParents = new HashMap<>();
        tree.add(new VertexDistance<>(fromV, 0));
        distances.put(fromV, 0);
        while (!tree.isEmpty()) {
            VertexDistance<Intersection> temp = tree.remove();
            for (VertexDistance<Intersection> adj : roads.getNeighbors(temp.vertex())) {
                int newDist = distances.get(temp.vertex()) + adj.distance();
                if (avoid.contains(adj.vertex().data().type())) {
                    newDist += 1332;
                }
                if (newDist < 0) {
                    newDist = Integer.MAX_VALUE;
                }
                if (!distances.containsKey(adj.vertex()) || newDist < distances.get(adj.vertex())) {
                    distances.put(adj.vertex(), newDist);
                    distanceParents.put(adj.vertex(), temp.vertex());
                    tree.add(new VertexDistance<>(adj.vertex(), newDist));
                }
            }
        }
        if (fromV.equals(toV)) {
            List<Intersection> route = new LinkedList<>();
            route.add(toV.data());
            return route;
        } else if (!distanceParents.containsKey(toV)) {
            return null;
        }
        List<Intersection> route = new LinkedList<>();
        Vertex<Intersection> temp = toV;
        while (temp != null) {
            route.add(temp.data());
            temp = distanceParents.get(temp);
        }
        return route.reversed();
    }

    /**
     * A private helper method used to calculate the distance between vertices on the grid, for getBestPowerSite
     * @param from The vertex to start at, via Dijkstra's algorithm
     * @return Returns the average distance between all other points from a central location (from)
     */
    private int calculateDist(Building from) {
        long total = 0;
        int count = 0;
        Map<Vertex<Building>, Integer> distances = dijkstras(grid.getVertexMap().get(from), grid);
        for (Vertex<Building> vertex : distances.keySet()) {
            if (distances.get(vertex) != Integer.MAX_VALUE && !vertex.data().equals(from)) {
                total += distances.get(vertex);
                if (total < 0) {
                    total = Integer.MAX_VALUE;
                }
                count++;
            }
        }
        if (count == 0) {
            return Integer.MAX_VALUE;
        }
        total /= count;
        return (int) total;
    }
