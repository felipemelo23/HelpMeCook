package br.com.helpmecook.control;

import android.os.AsyncTask;

import java.util.List;

import br.com.helpmecook.connection.UploadRecipe;
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
    public Recipe getRecipeById(long id) {
        return null;
    }

    /**
     * @param wanted Lista de ingredientes desejaveis.
     * @param unwanted Lista de ingredientes indesejaveis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem exatamente a busca
     * por ingredientes, ou seja, contem receitas com exatamente os ingredientes desejados,
     * dadas as duas listas de ingredientes passadas como parametro.
     */
    public List<AbstractRecipe> getResultByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return null;
    }

    /**
     * @param wanted Lista de ingredientes desejaveis.
     * @param unwanted Lista de ingredientes indesejaveis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem a busca
     * por ingredientes, mas tem 1 ingrediente a mais, ou seja, contem receitas com exatamente os ingredientes
     * desejados mais 1 ingrediente, dadas as duas listas de ingredientes passadas como parametro.
     */
    public List<AbstractRecipe> getPlusByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return null;
    }

    /**
     * @param name Nome ou parte do nome de uma receita.
     * @return Retorna uma lista de identificadores de receitas que possuem a String name passada como parametro.
     * no seu nome.
     */
    public List<AbstractRecipe> getResultByRecipeName(String name) {
        return null;
    }

    /**
     * @return Retorna as receitas mais populares, ou seja, mais visualizadas pelo usuarios.
     */
    public List<AbstractRecipe> getPopularRecipes() {
        return null;
    }

    /**
     * @param recipe Receita a ser registrada no Banco de Dados no Servidor
     * @return Retorna true se a receita for registrada no banco de dados no servidor e retorna false
     * se a receita nao for registrada no banco de dados no servidor,
     * mas for registrada no Banco de dados local.
     */
    public long registerRecipe(Recipe recipe) {
        UploadRecipe upload = (UploadRecipe) new UploadRecipe(recipe).execute();
        return upload.getNewId();
    }

    /**
     * @param id Identificador de uma receita.
     * @param taste Valor de classificação de sabor de uma receita.
     * @return Retorna true se a classificação da receita for atualizada no banco de dados do servidor
     * e retorna false se a classificação da receita não for atualizada no banco de dados do servidor.
     */
    public boolean classifyTaste(long id, float taste) {
        return false;
    }

    /**
     * @param id Identificador de uma receita.
     * @param difficulty Valor de classificação de dificuldade de uma receita.
     * @return Retorna true se a classificação da receita for atualizada no banco de dados do servidor
     * e retorna false se a classificação da receita não for atualizada no banco de dados do servidor.
     */
    public boolean classifyDifficulty(long id, float difficulty) {
        return false;
    }

    /**
     * @param recipes Lista com todas as receitas do usuário
     * @return Retorna a lista de receitas com as receitas de entrada que foram modificadas no servidor.
     */
    public List<Recipe> syncRecipes(List<Recipe> recipes) {
        return null;
    }
}