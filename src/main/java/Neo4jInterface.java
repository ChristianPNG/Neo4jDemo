import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import static org.neo4j.driver.Values.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Neo4jInterface {

    private final Driver driver; //necessary for writing transactions

    public Neo4jInterface(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public void close() {
        this.driver.close();
    }

    /*
     * addPersonNode: takes in attributes of a person besides id such as name and birth
     * it then creates a node on the neo4jAura database with those attributes.
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
    public List<Node> readNode(String nodeName) {
    	try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                
                List<Node> Nodes = new ArrayList<>();
                
                //query to find the node(s) and store it in result which is a VAR/iterator type
                Result result = tx.run("MATCH (node:Person {name: $name})  RETURN node"
                		, parameters("name", nodeName));
                
                //iterate through result (although most of the time its just 1 item/node unless duplicate)
                while (result.hasNext()) {
                	Record record = result.next(); //read the line
                	Nodes.add(record.get("node").asNode());
                }
                return Nodes;
            });
        }
    }
    
    public void deleteNode(int nodeId) {
        try (var session = driver.session()) {
        	 String deleteRelationshipsQuery = "MATCH (n)-[r]-() WHERE ID(n) = $nodeId DELETE r";
        	 session.run(deleteRelationshipsQuery, Values.parameters("nodeId", nodeId));
            // Cypher query to delete a node by its ID
            String query = "MATCH (n) WHERE ID(n) = $nodeId DELETE n";

            // Execute the query with parameters
            session.run(query, Values.parameters("nodeId", nodeId));
        }
    }
    
    /*
     * getConnectionNodes: Given a node id input and a valid relationship type such as :ACTED_IN
     * return all the nodes connected to the input node via the relationship
     */
    public List<Node> getConnectionNodes(int nodeId, String relationshipType) {
        List<Node> connectionNodes = new ArrayList<>();

        try (var session = driver.session()) {
            // Cypher query to retrieve connection nodes via a specific relationship
            String query = "MATCH (n)-[:" + relationshipType + "]->(connection) WHERE ID(n) = $nodeId RETURN connection";

            // Execute the query with parameters
            Result result = session.run(query, Values.parameters("nodeId", nodeId));

            // Process the result and populate the list of connection nodes
            while (result.hasNext()) {
                Record record = result.next();
                connectionNodes.add(record.get("connection").asNode());
            }
        }

        return connectionNodes;
    }

    public void readNodesArray(List<Node> arr) {
    	System.out.println("-------------------");
    	for (Node node: arr) {
    		for (String key: node.keys()) {
    			Value value = node.get(key);
                System.out.println(key + ": " + value);
    		}
    		System.out.println("-------------------");
    	}
    }
    public void createRelationship(int node1, int node2, String relationshipType) {
    	try (Session session = driver.session()) {
            String query = "MATCH (n1) WHERE ID(n1) = $node1 MATCH(n2) WHERE ID(n2) = $node2 CREATE (n1)-[:"+ relationshipType +"]->(n2)";
            query = String.format(query);
            session.run(query, Values.parameters("node1", node1, "node2", node2));  
    	}
    }

    public static void main(String[] args) {
    	//authorization credentials for the database in the cloud
        String uri = "neo4j+s://cfb47d99.databases.neo4j.io";
        String user = "neo4j";
        String password = "2fh1nzRfutoS-MciN-I-PvoAWveiWPu66l-AOXCf8QM";

        boolean loop = true;
        Neo4jInterface neo4jInterface = new Neo4jInterface(uri, user, password);
        
        Scanner scanner = new Scanner(System.in);
        while(loop) {
	        System.out.print("(1):Read Node \n(2):Add Person Node \n(3):Get Connection Nodes "
	        		+ "\n(4):Create Relationship \n(5):Delete Node\n(6):Exit\n");
	        System.out.print("Choose a method: ");
	        int userInput = scanner.nextInt();
	        scanner.nextLine();
	        HelperMethods helper = new HelperMethods(); //helper methods for using input as arugments
	        switch (userInput) {
		        case 1:
		        	helper.readNodeHelper(scanner, neo4jInterface);
		        	break;
		        case 2: 
		        	helper.addPersonHelper(scanner, neo4jInterface);
		        	break;
		        case 3:
		        	helper.getConnectionHelper(scanner, neo4jInterface);
		        	break;
		        case 4:
		        	helper.createRelationshipHelper(scanner, neo4jInterface);
		        	break;
		        case 5:
		        	helper.deleteNodeHelper(scanner, neo4jInterface);
		        	break;
		        default:
		        	loop = false;
		        	break;
		        	
		        	
	        }
        }
        scanner.close();
        neo4jInterface.close();
    }
}

