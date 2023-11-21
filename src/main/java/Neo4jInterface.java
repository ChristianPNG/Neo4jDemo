import org.neo4j.driver.*;
import static org.neo4j.driver.Values.parameters;

import java.util.ArrayList;
import java.util.List;

public class Neo4jInterface {

    private final Driver driver;

    public Neo4jInterface(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public void close() {
        this.driver.close();
    }

    /*
     * takes in attributes of a person besides id such as name and birth
     * it then creates a node on the neo4jAura database with those attributes.
     * Utilizes neo4j java driver to do so
     */
    public void addPersonNode(String nodeName, int nodeBirth) {
        try (Session session = driver.session()) {
            session.executeWriteWithoutResult(tx -> {
                tx.run("CREATE (n:Person {name: $name, born: $born})", parameters("name", nodeName, "born", nodeBirth));
                return;
        	});
        }
    }
    
    /*
     * Read node method, given a name, find the node and return a list of its attributes
     * Currently returns a 2D array, reason for it being 2D is because if there are duplicates
     * it needs to show all duplicates.
     */
    public List<List<String>> readNode(String nodeName) {
    	try (var session = driver.session()) {
            return session.executeRead(tx -> {
                List<List<String>> Container = new ArrayList<List<String>>();
                
                //query to find the node(s) and store it in result which is a VAR/iterator type
                var result = tx.run("MATCH (node:Person {name: $name})  RETURN node"
                		, parameters("name", nodeName));
                
                //iterate through result (although most of the time its just 1 item/node unless duplicate)
                while (result.hasNext()) {
                	var record = result.next(); //read the line
                	var personNode = record.get("node").asNode(); //read the node
                	ArrayList<String> user = new ArrayList<String>();
                	String name = personNode.get("name").asString();
        			String birth = personNode.get("born").toString();	
                	user.add(name);
                	user.add(birth);
                	Container.add(user); //add our array of strings into the parent array
                }
                return Container;
            });
        }
    }

    public void createRelationship(String node1, String node2) {
    	//TODO CREATE THIS METHOD
    }
    public void readRelationship(String node1, String node2) {
    	//TODO CREATE THIS METHOD
    	//maybe make it possible to add multiple parameters not just 2
    }

    public static void main(String[] args) {
    	//authorization credentials for the database in the cloud
        String uri = "neo4j+s://cfb47d99.databases.neo4j.io";
        String user = "neo4j";
        String password = "2fh1nzRfutoS-MciN-I-PvoAWveiWPu66l-AOXCf8QM";

        Neo4jInterface neo4jInterface = new Neo4jInterface(uri, user, password);

        // Example usage
        //neo4jInterface.addPersonNode("Tom Cruise", 2002);
        
        List<List<String>> res = new ArrayList<List<String>>();
        res = neo4jInterface.readNode("Tom Cruise");
        System.out.print(res);

        neo4jInterface.close();
    }
}
