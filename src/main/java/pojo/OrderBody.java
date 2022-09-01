package pojo;

public class OrderBody {
        String[] ingredients;

        public OrderBody(String[] ingredients) {
            this.ingredients = ingredients;
        }

        public OrderBody() {
        }

        public String[] getIngredients() {
            return ingredients;
        }

        public void setIngredients(String[] ingredients) {
            this.ingredients = ingredients;
        }
    }

