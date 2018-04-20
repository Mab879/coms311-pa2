import java.util.ArrayList;

import static org.junit.Assert.*;

public class NetworkInfluenceTest {
    private NetworkInfluence class1;
    private NetworkInfluence class2;

    @org.junit.Before
    public void setUp() throws Exception {
        class1 = new NetworkInfluence("test_cities.txt");
        class2 = new NetworkInfluence("test_wikiCC.txt");
    }

    @org.junit.Test
    public void outDegree() {
        assertEquals("Ames out degree",
                2, class1.outDegree("Ames"));
        assertEquals("Minneapolis out degree",
                1, class1.outDegree("Minneapolis"));
        assertEquals("Chicago out degree",
                1, class1.outDegree("Chicago"));
        assertEquals("Omaha out degree",
                1, class1.outDegree("Omaha"));
        assertEquals("Nonexistent node out degree",
                0, class1.outDegree("nonexistent"));

        assertEquals("Complexity theory out degree",
                11, class2.outDegree("/wiki/Complexity_theory"));
        assertEquals("Main Page out degree",
                0, class2.outDegree("/wiki/Main_Page"));
    }

    @org.junit.Test
    public void shortestPath() {
        assertArrayEquals("Ames to Chicago",
                new String[]{"Ames", "Minneapolis", "Chicago"},
                class1.shortestPath("Ames", "Chicago").toArray());
        assertArrayEquals("Ames to Omaha",
                new String[]{"Ames", "Omaha"},
                class1.shortestPath("Ames", "Omaha").toArray());
        assertArrayEquals("Ames to Ames",
                new String[]{"Ames"},
                class1.shortestPath("Ames", "Ames").toArray());
        assertArrayEquals("Ames to Nowhere",
                new String[]{},
                class1.shortestPath("Ames", "Nowhere").toArray());
        assertArrayEquals("Nowhere to Ames",
                new String[]{},
                class1.shortestPath("Nowhere", "Ames").toArray());

        assertArrayEquals("Complexity to System",
                new String[]{"/wiki/Complexity", "/wiki/System"},
                class2.shortestPath("/wiki/Complexity", "/wiki/System").toArray());
    }

    @org.junit.Test
    public void distance() {
        assertEquals("Ames to Chicago",
                2, class1.distance("Ames", "Chicago"));
        assertEquals("Ames to Ames",
                0, class1.distance("Ames", "Ames"));
        assertEquals("Ames to Nowhere",
                -1, class1.distance("Ames", "Nowhere"));
        assertEquals("Nowhere to Ames",
                -1, class1.distance("Nowhere", "Ames"));
    }

    @org.junit.Test
    public void distance1() {
        ArrayList<String> cities = new ArrayList<>();
        cities.add("Ames");
        cities.add("Minneapolis");
        assertEquals("Ames or Minneapolis to Chicago",
                1, class1.distance(cities, "Chicago"));

        assertEquals("Ames or Minneapolis to Nowhere",
                -1, class1.distance(cities, "Nowhere"));

        cities.set(1, "Nowhere");
        assertEquals("Ames or Nowhere to Chicago",
                2, class1.distance(cities, "Chicago"));
    }

    @org.junit.Test
    public void influence() {
        // TODO tests
    }

    @org.junit.Test
    public void influence1() {
        // TODO tests
    }

    @org.junit.Test
    public void mostInfluentialDegree() {
        System.out.println(class1.mostInfluentialDegree(2));
        System.out.println(class2.mostInfluentialDegree(2));

        // TODO tests
    }

    @org.junit.Test
    public void mostInfluentialModular() {
        System.out.println(class1.mostInfluentialModular(2));
        System.out.println(class2.mostInfluentialModular(2));

        // TODO tests
    }

    @org.junit.Test
    public void mostInfluentialSubModular() {
        System.out.println(class1.mostInfluentialSubModular(2));
        System.out.println(class2.mostInfluentialSubModular(2));

        // TODO tests
    }
}