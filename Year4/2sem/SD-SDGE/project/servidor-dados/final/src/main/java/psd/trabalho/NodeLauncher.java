package psd.trabalho;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class NodeLauncher {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InterruptedException {
        System.out.println("Launching Node");
        NodeServer node = new NodeServer(args[0], "800"+args[1]);

        ArrayList<Pair<String, String>> neighbour_Nodes = new ArrayList<>();

        for (int i = 0 ; i <= Integer.parseInt(args[2]) ; i++){
            neighbour_Nodes.add(new Pair<>("localhost","800"+i));
        }

        node.start(neighbour_Nodes);
    }
}
