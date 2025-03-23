package testutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class AutoGrader {

	// Test if the code demonstrates proper encapsulation and abstract
	// classes/methods
	public boolean testEncapsulationAndAbstractClassMethods(String filePath) throws IOException {
		System.out.println("Starting testEncapsulationAndAbstractClassMethods with file: " + filePath);

		File participantFile = new File(filePath); // Path to participant's file
		if (!participantFile.exists()) {
			System.out.println("File does not exist at path: " + filePath);
			return false;
		}

		FileInputStream fileInputStream = new FileInputStream(participantFile);
		JavaParser javaParser = new JavaParser();
		CompilationUnit cu;
		try {
			cu = javaParser.parse(fileInputStream).getResult()
					.orElseThrow(() -> new IOException("Failed to parse the Java file"));
		} catch (IOException e) {
			System.out.println("Error parsing the file: " + e.getMessage());
			throw e;
		}

		System.out.println("Parsed the Java file successfully.");

		// Use AtomicBoolean to allow modifications inside lambda expressions
		AtomicBoolean animalClassFound = new AtomicBoolean(false);
		AtomicBoolean dogClassFound = new AtomicBoolean(false);
		AtomicBoolean catClassFound = new AtomicBoolean(false);
		AtomicBoolean dogExtendsAnimal = new AtomicBoolean(false); // To check if Dog extends Animal
		AtomicBoolean abstractSpeakMethodFound = new AtomicBoolean(false);
		AtomicBoolean encapsulationTestPassed = new AtomicBoolean(false);
		AtomicBoolean methodsExecutedInMain = new AtomicBoolean(false);
		AtomicBoolean privateFieldFound = new AtomicBoolean(false); // To check if 'name' field is private

		// Check for class implementation, abstract methods, and encapsulation
		System.out.println("------ Class and Method Check ------");
		for (TypeDeclaration<?> typeDecl : cu.findAll(TypeDeclaration.class)) {
			if (typeDecl instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) typeDecl;

				if (classDecl.getNameAsString().equals("Animal")) {
					System.out.println("Class 'Animal' found.");
					animalClassFound.set(true);
					// Check for abstract method speak
					classDecl.getMethods().forEach(method -> {
						if (method.getNameAsString().equals("speak") && method.isAbstract()) {
							abstractSpeakMethodFound.set(true);
							System.out.println("Abstract method 'speak' found in 'Animal' class.");
						}
					});
				}

				if (classDecl.getNameAsString().equals("Dog")) {
					System.out.println("Class 'Dog' found.");
					dogClassFound.set(true);
					// Check if Dog extends Animal
					if (!classDecl.getExtendedTypes().isEmpty()
							&& classDecl.getExtendedTypes(0).getNameAsString().equals("Animal")) {
						dogExtendsAnimal.set(true);
						System.out.println("Class 'Dog' extends 'Animal'.");
					} else {
						System.out.println("Error: 'Dog' does not extend 'Animal'.");
					}
				}

				if (classDecl.getNameAsString().equals("Cat")) {
					System.out.println("Class 'Cat' found.");
					catClassFound.set(true);
				}
			}
		}

		// Ensure all required classes and methods are found
		if (!animalClassFound.get() || !dogClassFound.get() || !catClassFound.get()) {
			System.out.println("Error: Class 'Animal', 'Dog', or 'Cat' not found.");
			return false;
		}

		if (!abstractSpeakMethodFound.get()) {
			System.out.println("Error: Abstract method 'speak' not found in 'Animal' class.");
			return false;
		}

		// Ensure Dog class extends Animal
		if (!dogExtendsAnimal.get()) {
			System.out.println("Error: 'Dog' class must extend 'Animal'.");
			return false;
		}

		// Encapsulation check - ensuring private fields and getter/setter methods
		System.out.println("------ Encapsulation Check ------");
		for (TypeDeclaration<?> typeDecl : cu.findAll(TypeDeclaration.class)) {
			if (typeDecl instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) typeDecl;

				// Check if Animal class has private field 'name', getter, and setter methods
				if (classDecl.getNameAsString().equals("Animal")) {
					classDecl.getFields().forEach(field -> {
						if (field.getVariable(0).getNameAsString().equals("name") && field.isPrivate()) {
							privateFieldFound.set(true);
							System.out.println("Private field 'name' found in 'Animal' class.");
						}
					});

					if (!privateFieldFound.get()) {
						System.out.println("Error: Private field 'name' not found in 'Animal' class.");
						return false; // Fail the test if the private field is not found
					}

					// Check for the existence of getter and setter methods for 'name'
					boolean getterFound = false, setterFound = false;
					for (MethodDeclaration method : classDecl.getMethods()) {
						if (method.getNameAsString().equals("getName") && method.isPublic()) {
							getterFound = true;
						}
						if (method.getNameAsString().equals("setName") && method.isPublic()) {
							setterFound = true;
						}
					}

					if (getterFound && setterFound) {
						encapsulationTestPassed.set(true);
						System.out.println("Getter and setter methods for 'name' found.");
					} else {
						System.out.println("Error: Getter and setter methods for 'name' not found.");
						return false; // Fail the test if getter/setter methods are not found
					}
				}
			}
		}

		// Ensure encapsulation is correctly implemented
		if (!encapsulationTestPassed.get()) {
			return false;
		}

		// Check if methods are executed in the main method
		System.out.println("------ Method Execution Check in Main ------");

		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("main")) {
				if (method.getBody().isPresent()) {
					method.getBody().get().findAll(MethodCallExpr.class).forEach(callExpr -> {
						if (callExpr.getNameAsString().equals("speak")) {
							methodsExecutedInMain.set(true);
							System.out.println("Method 'speak' is executed in the main method.");
						}
					});
				}
			}
		}

		// Fail the test if methods weren't executed
		if (!methodsExecutedInMain.get()) {
			System.out.println("Error: 'speak' method not executed in the main method.");
			return false;
		}

		// If all checks pass
		System.out.println("Test passed: Encapsulation and Abstract classes/methods are correctly implemented.");
		return true;
	}
}
