# Latent Space Explorer 🌌

A robust, interactive 3D visualization tool for Word Embeddings (Word2Vec) built with Java and JavaFX. This application allows users to explore high-dimensional semantic spaces, discover word relationships, and visualize vector mathematics in real-time.

## ✨ Key Features

* **3D Interactive Visualization:** Render word embeddings in a 3D space using PCA (Principal Component Analysis) reduced coordinates. Supports camera rotation, zooming, and hover-based tooltips to avoid visual clutter.
* **K-Nearest Neighbors (KNN):** Find and visualize the closest semantic neighbors to any given target word using exact mathematical distance.
* **Vector Analogies:** Compute analogies (e.g., *King - Man + Woman = Queen*) using vector arithmetic and find the closest matching word in the dataset.
* **Centroid Calculation:** Select multiple words to compute their geometric center (average vector) and represent it as a new virtual point in the space.
* **Custom JSON Loader:** A lightweight, zero-dependency JSON parser built from scratch to load dataset files efficiently without relying on external libraries like Gson/Jackson.

## 🏗️ Architecture & Engineering Principles

This project was built with a strong emphasis on clean code, software engineering principles, and modular design:

* **Separation of Concerns (MVC):** Strict separation between the mathematical core (`fullSpace` - e.g., 300 dimensions) and the visual representation (`pcaSpace` - 3 dimensions). UI logic never leaks into the mathematical models.
* **Strategy Pattern:** Distance metrics (Euclidean Distance, Cosine Similarity) are implemented using the Strategy pattern, abiding by the **Open/Closed Principle (OCP)**. New metrics can be added without modifying existing core logic.
* **Command Pattern:** UI actions and system operations are encapsulated as Commands, preventing a "fat controller" and allowing for scalable user interactions.
* **Fail-Fast & Defensive Programming:** The system validates data upon entry, throwing specific custom exceptions (e.g., `DimensionMismatchException`, `WordNotFoundException`) to maintain data integrity and prevent cascading failures.
* **Data Transfer Objects (DTO):** Classes like `SearchResult` and `ProjectionResult` safely transport immutable data between the Logic and View layers.

## 🚀 Technologies Used

* **Language:** Java
* **UI Framework:** JavaFX (3D Shapes, PerspectiveCamera, Alerts)
* **Design:** Zero external dependencies for the core logic (Vanilla Java).

## 🛠️ Getting Started

1. Clone the repository:
   ```bash
   git clone [https://github.com/weaam-mishleb/LatentSpaceExplorer.git](https://github.com/weaam-mishleb/LatentSpaceExplorer.git)
