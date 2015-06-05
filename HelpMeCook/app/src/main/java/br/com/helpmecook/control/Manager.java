package br.com.helpmecook.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Pair;

import org.apache.http.conn.HttpHostConnectException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Cookbook;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.model.Recipe;
import br.com.helpmecook.sqlite.CookbookDAO;
import br.com.helpmecook.sqlite.IngredientDAO;
import br.com.helpmecook.sqlite.RecentsDAO;
import br.com.helpmecook.sqlite.RecipeDAO;

/**
 * Created by Felipe on 30/04/2015.
 */
public class Manager {
    private static ConnectionAccessor accessor = new ConnectionAccessor();

    /**
     * @param id Número inteiro que identifica uma receita.
     * @param context Contexto da aplicação
     * @return Retorna a receita relativa ao id passado como parâmetro.
     */
    public static Recipe getRecipeById(long id, Context context) {
        RecipeDAO recipeDAO = new RecipeDAO(context);
        RecentsDAO recentsDAO = new RecentsDAO(context);

        try{
            recipeDAO.open();
            recentsDAO.open();

            //Tenta achar a receita no banco de dados local.
            Recipe recipe = recipeDAO.read(id);

            //Se a receita não estiver no banco de dados local, procura no servidor.
            if (recipe == null) {
                recipe = accessor.getRecipeById(id);
                recipeDAO.insert(recipe);
            }

            //Se a receita foi encontrada, atualiza o último acesso dela e adiciona ela na tabela Recents
            if (recipe != null) {
                Calendar cal = Calendar.getInstance();
                recipe.updateLastAcess(cal);
                recipeDAO.update(recipe);

                if (recentsDAO.read(recipe.getId()) == null){
                    recentsDAO.insert(recipe);
                } else {
                    recentsDAO.update(recipe);
                }
            }

        recipeDAO.close();
        recentsDAO.close();

        return recipe;

        } catch (java.sql.SQLException e) {
            return null;
        }
    }

    /**
     * @param ids Lista de Identificadores de Receita.
     * @return Retorna uma lista de receitas resumidas.
     */
    public static List<AbstractRecipe> getAbstractRecipes(List<Long> ids, Context context) {
        RecipeDAO recipeDAO = new RecipeDAO(context);
        AbstractRecipe recipe;
        List<AbstractRecipe> recipes = null;

        try {
            recipeDAO.open();
            recipes = new ArrayList<AbstractRecipe>();

            for (long id : ids) {
                Log.i("Manager", id + "");
                recipe = recipeDAO.readAbstractRecipe(id);
                recipes.add(recipe);
            }

            recipeDAO.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return recipes;
    }

    /**
     * @param wanted Lista de ingredientes desejaveis.
     * @param unwanted Lista de ingredientes indesejaveis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem exatamente a busca
     * por ingredientes, ou seja, contem receitas com exatamente os ingredientes desejados,
     * dadas as duas listas de ingredientes passadas como parametro.
     */
    public static Pair<List<AbstractRecipe>,List<AbstractRecipe>> getResultByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return accessor.getResultByIngredientLists(wanted, unwanted);
    }

    /**
     * @param context Contexto da aplicação
     * @return Retorna uma lista com os IDs das receitas visualizadas recentemente.
     */
    public static List<AbstractRecipe> getRecentRecipes(Context context) {
        RecentsDAO recentsDAO = new RecentsDAO(context);

        try {
            recentsDAO.open();
            List<Long> ids = recentsDAO.readAll(); //Esse metodo esta retornar ordenado por LastAccess.
            Log.i("Manager", "Number of recents recipe: " + ids.size());
            recentsDAO.close();
            List<AbstractRecipe> recents = getAbstractRecipes(ids, context);

            return  recents;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param recipe Receita a ser adicionada no cookbook.
     * @param context Contexto da aplicação
     * @return Retorna true se a receita foi adicionada ao cookbook e false se ela não foi.
     */
    public static Boolean addToCookbook(Recipe recipe, Context context) {
        CookbookDAO cookbookDAO = new CookbookDAO(context);

        try{
            cookbookDAO.open();

            if (cookbookDAO.insert(recipe) != -1){
                cookbookDAO.close();
                return true;
            } else {
                cookbookDAO.close();
                return  false;
            }
        } catch (java.sql.SQLException e) {
            return false;
        }
    }

    /**
     * @param context Contexto da aplicação
     * @return Lista dos ingredientes da aplicacao
     */
    public static List<Ingredient> getIngredients(Context context) {
        IngredientDAO ingredientDAO = new IngredientDAO(context);
        List<Ingredient> ingredients = null;

        try {
            ingredientDAO.open();
            ingredients = ingredientDAO.readAll();
            ingredientDAO.close();

        } catch(java.sql.SQLException e) {
            e.printStackTrace();
        }

        return ingredients;
    }

    public static List<Ingredient> getRecipeIngredients(List<Long> ids, Context context) {
        IngredientDAO ingredientDAO = new IngredientDAO(context);
        List<Ingredient> ingredients = new ArrayList<Ingredient>();

        try {
            ingredientDAO.open();

            for (long id : ids){
                ingredients.add(ingredientDAO.read(id));
            }

            ingredientDAO.close();

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return ingredients;
    }

    public static void insertAllIngredients(Context context) {
        String names[] = new String[]{"Abacate","Abacaxi","Abóbora","Abobrinha","Açafrão","Açaí","Acelga","Acerola","Achocolatado","Açucar","Açúcar cristal","Agrião","Água ardente","Água de coco","Alçafrão","Alcaparras","Alegrim","Alface","Alface Americana","Alho","Ameixa","Amêndoas","Amendoim","Aroz integral","Arroz","Arroz 7 Grãos","Arroz Branco","Aveia","Aveia em Flocos","Avelã","Azeite","Azeitona","Bacon","Banana","Banana nanica","Banana prata","Barra de cereal","Barra de chocolate amargo","Barra de chocolate ao leite","Barra de chocolate branco","Batata doce","Batata frita congelada","Batata Inglesa","Batata Palha","Beringela","Beterraba","Biscoito champanhe","Biscoito doce","Biscoito maizena","Biscoito Passatempo","Biscoito recheado","Biscoito salgado","Blanquet","Bolacha com sal","Brócolis","Café","Cajá","Caju","Calabresa","Caldo de Feijão","Caldo de legume","Camarão","Canela","Capuchino em pó","Caqui","Carambola","Caranguejo","Carne Alcatra","Carne Contra-filé","Carne Coxao duro","Carne Coxao mole","Carne de carneiro","Carne de porco","Carne do sol","Carne Fígado","Carne Filé Mignon","Carne Maminha","Carne Moída","Carne Músculo","Carne Patinho","Carne Picanha","Carne seca","Castanha de caju","Castanha Pará","Catupiry","Cebola","Cebolinha","Cenoura","Cereal matinal","Cerveja","Chá (verde, preto...)","Chá Camomila","Champanhe","Chantilly","Cheiro verde","Chia","Chicória","Chocolate em pó","Chuchu","Coca-Cola","Coco","Coco ralado","Coentro","Cogumelos","Coloral","Coração de frango","Costelinha","Couve","Couve-flor","Coxa de frango","Cream Chesee","Creme de leite","Danone","Danoninho","Doce de leite","Ervas finas","Ervilha","Espaguete","Espinafre","Essência de baunilha","Extrato de tomate","Farinha de linhaça","Farinha de mandioca","Farinha de milho","Farinha de trigo","Farinha lactea","Farofa pronta","Fécula de mandioca","Feijão carioca","Feijão outros","Feijão preto","Fermento biológico","Fermento em pó","Filé de frango","Filé de peito de frango","Filé de Salmão","Filé de Tilápia","Filé peito de Frango","Filé Peito de Peru","Filezinho de frango","Folha de louro","Framboesa","Frango","Gelatina em pó","Geléia Goiaba","Geléia outras","Geléia Pimenta","Gengibre","Goiaba","Goma para tapioca","Gotas de chocolate (m&m, bolinhas...)","Granola","Grão de Bico","Graviola","Hambúrguer congelado","Hortelã","Inhame","Iogurte de fruta","Iogurte natural","Jiló","Ketchup","Kiwi","Lagosta","Laranja","Leite","Leite condensado","Leite de coco","Leite desnatado","Leite em pó","Leite Integral","Lentilha","Lima","Limão","Linchia","Linguiça","Linhaça","Lula","Maçã","Macarrão","Macarrão Cabelo de anjo","Macarrão integral","Maionese","Maizena","Mamão","Mamão formosa","Mandioca","Manga","Manteiga","Maracujá","Margarina","Massa para lasanha","Mel de abelha","Melancia","Melão","Milho (Pipoca)","Milho enlatado","Milho verde","Molho barbecue","Molho de pimenta","Molho de tomate","Molho inglês","Molho shoyu","Morango","Mostarda","Nata","Óleo","Orégano","Ovo","Ovo de codorna","Palmito","Pão Branco","Pao de forma","Pão de queijo","Pão integral","Pão sem Gluten","Páprica","Peito de frango","Peito de peru","Peito de peru","Peixe","Penne","Pepino","Pêra","Pêssego","Pimenta calabresa","Pimenta de cheiro","Pimenta do reino","Pimenta malagueta","Pimenta outras","Pimentão","Pipoca microond","Pitanga","Pitomba","Pó de café","Polpa de Fruta","Polvilho","Polvo","Presunto","Purê de batata pronto","Purê de mandioca","Queijo amarelo","Queijo branco","Queijo coalho","Queijo Minas","Queijo Mussarela","Queijo outros","Queijo parmesão","Queijo tipo gorgonzola","Queijo tipo ricota","Quejo Ralado","Quiabo","Repolho","Requeijão","Rúcula","Sal","Salaminho","Salmão","Salsa","Salsicha","Salvia","Sapoti","Schweppes","Siri","Siriguela","Sobrecoxa frango","Soja","Sorvete","Suco em pó","Tamarindo","Tangirina/Mexirica","Tempero completo","Tomate","Tomate seco","Torrada","Uva","Uva Passas","Vinagre balsâmico","Vinagre de álcool","Vinagre de vinho","Vinho Branco","Vinho Tinto"};
        IngredientDAO ingredientDAO = new IngredientDAO(context);

        try {
            ingredientDAO.open();

            Ingredient ingredient = new Ingredient();
            for (int i = 0; i < names.length; i++) {
                ingredient.setName(names[i]);
                ingredient.setId(i);
                ingredient.setIconPath(R.drawable.checkbox_blank_circle);
                ingredientDAO.insert(ingredient);
            }

            ingredientDAO.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param recipe Receita que sera removida do cookbook
     * @param context Contexto da aplicação
     * @return Retorna true se a receita foi removida do cookbook e false se ela não foi.
     */
    public static boolean removeFromCookbook(Recipe recipe, Context context) {
        CookbookDAO cookbookDAO = new CookbookDAO(context);

        try {
            cookbookDAO.open();
            boolean deleted = cookbookDAO.delete(recipe.getId());
            cookbookDAO.close();
            return deleted;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param id ID da receita que foi classificada
     * @param taste Valor que foi atribuido ao sabor da receita
     * @return Retorna true se a classificacao foi enviada para o servidor e false se nao
     */
    public static boolean classifyTaste(long id, float taste) {
        return accessor.classifyTaste(id, taste);
    }

    /**
     * @param id ID da receita que foi classificada
     * @param difficulty Valor que foi atribuido a dificuldade da receita
     * @return Retorna true se a classificacao foi enviada para o servidor e false se nao
     */
    public static boolean classifyDifficulty(long id, float difficulty) {
        return accessor.classifyDifficulty(id, difficulty);
    }

    /**
     * Essa funcao garante que todas as receitas no banco de dados local sejam enviadas para o
     * servidor e que todas as funcoes que estao no banco de dados local e que estão no servidor
     * e foram modificadas nele sejam atualizadas do banco de dados local
     * @param context Contexto da aplicação
     * @return Retorna true se ela garantiu tudo e falso se não
     */
    public static boolean syncAll(Context context) {
        RecipeDAO recipeDAO = new RecipeDAO(context);

        try {
            recipeDAO.open();
            List<Recipe> notSyncs = recipeDAO.readNotSync();
            for (Recipe recipe : notSyncs) {
                registerRecipe(recipe, context);
            }

            List<Recipe> modifieds = accessor.syncRecipes(recipeDAO.readAll());

            for (Recipe recipe : modifieds) {
                recipeDAO.update(recipe);
            }

            recipeDAO.close();

            return true;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param name Nome das receitas que se deseja encontrar
     * @return Uma lista de AbstractRecipe que contem o nome procurado
     */
    public static List<AbstractRecipe> getResultByRecipeName(String name){
        return accessor.getResultByRecipeName(name);
    }

    /**
     * @return Uma lista de AbstractRecipe com as receitas populares
     */
    public static List<AbstractRecipe> getPopularRecipes () throws HttpHostConnectException {
        return accessor.getPopularRecipes();
    }

    /**
     * @param context Contexto da aplicação
     * @return Retorna um objeto Cookbook com a lista de receitas do Cookbook
     */
    public static Cookbook getCookbook(Context context){
        CookbookDAO cookbookDAO = new CookbookDAO(context);
        RecipeDAO recipeDAO = new RecipeDAO(context);
        Cookbook cookbook = new Cookbook();

        try{
            cookbookDAO.open();
            recipeDAO.open();

            List<Long> ids = cookbookDAO.readAll();

            for (long id : ids) {
                Recipe recipe = recipeDAO.read(id);
                Log.i("Debug Cookbook Manager", id + "");
                cookbook.addRecipe(recipe);
            }

            recipeDAO.close();
            cookbookDAO.close();

            return cookbook;
        } catch (java.sql.SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param recipe Receita a ser verificada se está no Cookbook.
     * @param context Contexto da aplicação
     * @return retorna true se a receita está no Cookbook ou false se a receita não está no Cookbook.
     */
    public static boolean isOnCookbook(Recipe recipe, Context context) {
        List<AbstractRecipe> recipesOnCookbook = getCookbook(context).getRecipeList();
        return recipesOnCookbook.contains(recipe);
    }

    /**
     * @param recipe Receita que sera registrada
     * @param context Contexto da aplicação
     * @return Retorna 0 se nao esta nem no local nem remoto, 1 se esta apenas no local e 2 se esta tanto mo local quanto no remoto
     */
    public static int registerRecipe(Recipe recipe, Context context){
        CookbookDAO cookbookDAO = new CookbookDAO(context);
        RecipeDAO recipeDAO = new RecipeDAO(context);
        long internalDB, remoteDBId;
        remoteDBId = accessor.registerRecipe(recipe);

        try {
            Log.i("DebugManager", "Foi inserido no bd local");
            cookbookDAO.open();
            recipeDAO.open();

            if ((remoteDBId == -1) && (recipe.getId() == -1)){
                SharedPreferences data = context.getSharedPreferences("localId",0);
                SharedPreferences.Editor editor = data.edit();
                long localId = data.getLong("localIdValue", Long.MAX_VALUE);

                recipe.setId(localId);
                recipe.setSync(false);

                editor.putLong("localIdValue", localId - 1);
                editor.commit();

                recipeDAO.insert(recipe);
                cookbookDAO.insert(recipe);
                cookbookDAO.close();
                recipeDAO.close();
                return 1;
            }else{
                Log.i("DebugManager", "Foi inserido no servidor");
                recipe.setId(remoteDBId);
                recipe.setSync(true);
                internalDB = cookbookDAO.insert(recipe);
                cookbookDAO.close();
                return 2;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}