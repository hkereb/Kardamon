# 🌿 Kardamon - Recipe API

Kardamon is an API designed to fetch recipes from a provided URL. It is the foundation for a future web application that will allow users to store and manage their collections of internet recipes.

## ✨ Supports

- JSON-LD
- Microdata
- **\[COMING SOON\]** Unstructered Data
- All Languages

## 🌐 How to Use the API

#### To fetch a recipe, simply send a POST request to the endpoint:

```
POST /api/fetchRecipe?url={recipe_url}
```

#### **Example Request**:

```
curl http://localhost:8080/api/fetchRecipe?url=https://example.com/recipe
```

#### **Response Format**: 
The API returns a JSON object with the recipe's title, ingredients, instructions, servings, and more.

```json
   {
       "title": "Cottage Cheese Quiche",
       "description": "A delicious high-protein quiche.",
       "servings": "4",
       "ingredients": ["1/3 cup diced onion", "2 cloves garlic", "..."],
       "instructions": ["1. Preheat oven...", "2. Mix wet and dry ingredients..."],
   }
```

## 🚧 Future Features
- **Web Application**:<br/>
  Developing a web-based interface allowing users to store, edit, and organize their recipes.
- **Unstructured Data Support**:<br/>
  Implementing the ability to handle unstructured data, improving the flexibility and coverage of the API.
- **Image Extraction**:<br/>
  Adding functionality to extract and display images related to the recipes.
- **Unit Conversion**:<br/>
  Adding a feature to convert recipe units, making it easier for users to scale recipes or switch between metric and imperial measurements.
- **Recipe Scaling**:<br/>
  A feature to adjust ingredient quantities based on the chosen number of servings, allowing users to easily scale recipes up or down to suit their needs.
