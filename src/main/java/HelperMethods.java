import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.neo4j.driver.types.Node;

public class HelperMethods {
	public void readNodeHelper(Scanner scanner, Neo4jInterface neo4j) {
		List<Node> res = new ArrayList<Node>();
		System.out.print("Enter node name: ");
		String arg = scanner.nextLine();
		res  = neo4j.readNode(arg);
		neo4j.readNodesArray(res);
		return;
	}
	public void addPersonHelper(Scanner scanner, Neo4jInterface neo4j) {
		System.out.print("Enter node name: ");
		String arg = scanner.nextLine();
		System.out.print("Enter node Birth Year: ");
		int birth = scanner.nextInt();
		scanner.nextLine();
		neo4j.addPersonNode(arg, birth);
		System.out.println("AddPerson executed");
		return;
	}
	
	public void getConnectionHelper(Scanner scanner, Neo4jInterface neo4j) {
		List<Node> arr = new ArrayList<>();
		System.out.print("Enter node id: ");
		int arg = scanner.nextInt();
		scanner.nextLine();
		System.out.print("Enter relationship type: ");
		String arg2 = scanner.nextLine();
		arr = neo4j.getConnectionNodes(arg, arg2);
		neo4j.readNodesArray(arr);
		return;
		
	}
	public void createRelationshipHelper(Scanner scanner, Neo4jInterface neo4j) {
		System.out.print("Enter node1 id: ");
		int arg = scanner.nextInt();
		scanner.nextLine();
		System.out.print("Enter node2 id: ");
		int arg2 = scanner.nextInt();
		scanner.nextLine();
		System.out.print("Enter relationship type: ");
		String arg3 = scanner.nextLine();
		neo4j.createRelationship(arg, arg2, arg3);
	}
	public void deleteNodeHelper(Scanner scanner, Neo4jInterface neo4j) {
		System.out.print("Enter node1 id: ");
		int arg = scanner.nextInt();
		scanner.nextLine();
		neo4j.deleteNode(arg);
		System.out.print("Delete Node executed\n");
	}
}
