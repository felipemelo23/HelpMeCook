package br.com.helpmecook.control;

import java.util.List;

import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.model.Recipe;

/**
 * Created by Felipe on 30/04/2015.
 */
public class ConnectionAccessor {
    /**
     * @param id Numero inteiro que identifica uma receita.
     * @return Retorna a receita relativa ao id passado como parametro.
     */
    public Recipe getRecipeById(int id) {
        return null;
    }

    /**
     * @param ids Lista de Identificadores de Receita.
     * @return Retorna uma lista de receitas resumidas.
     */
    public List<AbstractRecipe> getAbstractRecipes(List<Integer> ids) {
        return null;
    }

    /**
     *

    public List<Integer> getResultByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return null;
    }

    /**
     *

    public List<Integer> getPlusByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return null;
    }

    /**
     * @param name Nome ou parte do nome de uma receita.
     * @return Retorna uma lista de identificadores de receitas que possuem a String name passada como parametro.
     * no seu nome.
     */
    public List<Integer> getResultByRecipeName(String name) {
        return null;
    }

    /**
     * @return Retorna as receitas mais populares, ou seja, mais visualizadas pelo usuarios.
     */
    public List<Integer> getPopularRecipes() {
        return null;
    }

    /**
     * @param recipe Receita a ser registrada no Banco de Dados no Servidor
     * @return Retorna true se a receita for registrada no banco de dados no servidor e retorna false
     * se a receita nao for registrada no banco de dados no servidor,
     * mas for registrada no Banco de dados local.
     */
    public int registerRecipe(Recipe recipe) {
        return -1;
    }

    /**
     * @param id Identificador de uma receita.
     * @param taste Valor de classifica��o de sabor de uma receita.
     * @param difficult Valor de classifica��o de dificuldade de uma receita.
     * @return Retorna true se a classifica��o da receita for atualizada no banco de dados do servidor
     * e retorna false se a classifica��o da receita n�o for atualizada no banco de dados do servidor.
     */
    public Boolean classifyRecipe(int id, float taste, float difficult) {
        return false;
    }
}
