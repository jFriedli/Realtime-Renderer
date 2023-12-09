# Java Real Time Renderer

## Overview

This Java project is focused on implementing 3D graphics rendering using Java's standard libraries and image manipulation techniques. It includes a main class that extends `JPanel` and handles the rendering of 3D objects (cubes in this case) with various graphical features.

### Main Class
- **Purpose**: To render 3D objects and demonstrate various graphical features like texture mapping, shading, and bilinear interpolation.
- **Key Components**:
  - `Cube`: Objects representing 3D cubes to be rendered.
  - `Vec3`, `Vec4`, `Vec2`: Vector classes used for handling 3D coordinates, colors, and other vector-related calculations.
  - `BufferedImage`, `WritableRaster`: Utilized for image processing and manipulation.
  - `paintComponent`: Overridden method from `JPanel` to handle custom rendering.
  - **Graphical Features**:
    - Texture Mapping: Applying textures to the 3D objects.
    - Shading: Implementing lighting effects like diffuse and specular shading.
    - Bilinear Interpolation: For smoother texture mapping.
    - Z-Buffer: For handling visibility and occlusion of objects.
    - Gamma Correction: For color correction in textures.
- **Configuration Options**:
  - `useTexture`: Toggle for using texture mapping.
  - `shading`: Toggle for applying shading effects.
  - `bilinearInterpolation`: Toggle for using bilinear interpolation in textures.
  - `clamping` and `repeating`: Options for texture wrapping modes.
- **Image Loading and Rendering**:
  - Loading an image for texture mapping using `ImageIO`.
  - Custom rendering logic in `paintComponent` for drawing the cubes with applied textures and shading.

### Rendering Logic
- The rendering process involves projecting 3D coordinates onto a 2D plane, applying textures, and performing z-buffering for visibility determination.
- Shading calculations include Lambertian diffuse and Phong specular shading for realistic lighting effects.
- Texture mapping involves reading from an image file and applying it to the faces of the cubes, with options for different wrapping modes and interpolation techniques.
- The program also includes a rotation animation for the cubes to demonstrate the rendering in a dynamic context.

## Key Takeaways
- The project demonstrates the implementation of fundamental 3D graphics rendering techniques in Java.
- It shows how to handle complex tasks like texture mapping, shading, and visibility determination in a custom rendering pipeline.
- The use of Java's standard libraries for image processing and GUI rendering showcases the versatility of the language in handling graphics programming.

## Conclusion
This Java project effectively demonstrates the principles of 3D graphics rendering, including texture mapping, shading, and handling of 3D to 2D projection, providing a solid foundation for more advanced graphics programming in Java.
