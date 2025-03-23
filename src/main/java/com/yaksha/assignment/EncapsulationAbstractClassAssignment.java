package com.yaksha.assignment;

// Abstract Animal class demonstrating encapsulation and abstract methods
abstract class Animal {

	// Encapsulation: Private field
	private String name;

	// Constructor to initialize name
	public Animal(String name) {
		this.name = name;
	}

	// Getter for name (Encapsulation)
	public String getName() {
		return name;
	}

	// Setter for name (Encapsulation)
	public void setName(String name) {
		this.name = name;
	}

	// Abstract method, must be implemented by subclasses
	public abstract void speak();
}

// Dog class that extends Animal and provides implementation for the abstract method
class Dog extends Animal {

	// Constructor for Dog, calling the parent constructor
	public Dog(String name) {
		super(name);
	}

	// Implementation of the abstract method speak
	@Override
	public void speak() {
		System.out.println(getName() + " barks.");
	}
}

// Cat class that extends Animal and provides implementation for the abstract method
class Cat extends Animal {

	// Constructor for Cat, calling the parent constructor
	public Cat(String name) {
		super(name);
	}

	// Implementation of the abstract method speak
	@Override
	public void speak() {
		System.out.println(getName() + " meows.");
	}
}

public class EncapsulationAbstractClassAssignment {
	public static void main(String[] args) {
		// Using Encapsulation to create objects
		Dog dog = new Dog("Buddy");
		Cat cat = new Cat("Whiskers");

		// Using the abstract method speak
		dog.speak(); // Should print "Buddy barks."
		cat.speak(); // Should print "Whiskers meows."

		// Using getter and setter from encapsulation
		dog.setName("Charlie");
		System.out.println("Dog's new name is: " + dog.getName()); // Should print "Charlie"
	}
}
