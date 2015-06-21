package br.com.helpmecook.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Pair;

import org.apache.http.conn.HttpHostConnectException;

import java.io.IOException;
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
import br.com.helpmecook.view.fragment.HomeFragment;

/**
 * Created by Felipe on 30/04/2015.
 */
public class Manager {
    private static final String POPULARS = "populars";
    private static ConnectionAccessor accessor = new ConnectionAccessor();
    public static final int LOCAL_POPULAR = 0;
    public static final int SERVER_POPULAR = 1;

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

    public static Recipe getRecipeOnLocalDB(long id, Context context) {
        RecipeDAO recipeDAO = new RecipeDAO(context);
        RecentsDAO recentsDAO = new RecentsDAO(context);

        try {
            recipeDAO.open();
            recentsDAO.open();

            //Tenta achar a receita no banco de dados local.
            Recipe recipe = recipeDAO.read(id);
            //Caso tenha achado, coloca nas recents
            if (recipe != null) {
                Calendar cal = Calendar.getInstance();
                recipe.updateLastAcess(cal);
                recipeDAO.update(recipe);

                if (recentsDAO.read(recipe.getId()) == null){
                    recentsDAO.insert(recipe);
                } else {
                    recentsDAO.update(recipe);
                }

                return recipe;
            } else {
                //Caso não, retorna null
                return null;
            }
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
        return (new ConnectionAccessor()).getResultByIngredientLists(wanted, unwanted);
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

            for (AbstractRecipe ar : recents) {
                Log.i("Recents", ar.getName());
            }

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
        RecipeDAO recipeDAO = new RecipeDAO(context);

        try{
            cookbookDAO.open();

            if (cookbookDAO.insert(recipe) != -1){
                recipeDAO.open();
                recipeDAO.insert(recipe);
                recipeDAO.close();
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
     * @param recipe Receita que sera removida do cookbook
     * @param context Contexto da aplicação
     * @return Retorna true se a receita foi removida do cookbook e false se ela não foi.
     */
    public static boolean removeFromCookbook(Recipe recipe, Context context) {
        CookbookDAO cookbookDAO = new CookbookDAO(context);
        RecipeDAO recipeDAO = new RecipeDAO(context);

        try {
            cookbookDAO.open();
            boolean deleted = cookbookDAO.delete(recipe.getId());
            recipeDAO.open();
            recipeDAO.delete(recipe.getId());
            recipeDAO.close();
            cookbookDAO.close();
            return deleted;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param recipeID ID da receita que está no cookbook
     * @param context Contexto da aplicaação
     * @return Recipe recipe que está no cookbook
     */
    public static Recipe getRecipeFromLocalDB(long recipeID, Context context) {
        RecipeDAO recipeDAO = new RecipeDAO(context);
        Recipe recipe = null;

        try {
            recipeDAO.open();
            recipe = recipeDAO.read(recipeID);
            recipeDAO.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return recipe;
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
        String names[] = new String[]{"Abacate","Abacaxi","Abóbora","Abobrinha","Açafrão","Açaí","Acelga","Acerola","Achocolatado","Açúcar","Açúcar cristal","Agrião","Água ardente","Água de coco","Açafrão","Alcaparras","Alecrim","Alface","Alface Americana","Alho","Ameixa","Amêndoas","Amendoim","Amido de milho","Arroz Integral","Arroz","Arroz 7 Grãos","Arroz Branco","Aveia","Aveia em Flocos","Avelã","Azeite","Azeitona","Bacon","Banana","Banana nanica","Banana prata","Barra de cereal","Barra de chocolate amargo","Barra de chocolate ao leite","Barra de chocolate branco","Batata doce","Batata frita congelada","Batata Inglesa","Batata Palha","Beringela","Beterraba","Biscoito champanhe","Biscoito doce","Biscoito maizena","Biscoito Passatempo","Biscoito recheado","Biscoito salgado","Blanquet","Bolacha com sal","Brócolis","Café","Cajá","Caju","Calabresa","Caldo de Feijão","Caldo de legume","Camarão","Canela","Cappuccino em pó","Caqui","Carambola","Caranguejo","Carne Alcatra","Carne Contra-filé","Carne Coxão duro","Carne Coxão mole","Carne de carneiro","Carne de porco","Carne do sol","Carne Fígado","Carne Filé Mignon","Carne Maminha","Carne Moída","Carne Músculo","Carne Patinho","Carne Picanha","Carne seca","Castanha de caju","Castanha Pará","Catupiry","Cebola","Cebolinha","Cenoura","Cereal matinal","Cerveja","Chá (verde, preto...)","Chá Camomila","Champanhe","Chantilly","Cheiro verde","Chia","Chicória","Chocolate em pó","Chuchu","Coca-Cola","Coco","Coco ralado","Coentro","Cogumelos","Coloral","Coração de frango","Costelinha","Couve","Couve-flor","Coxa de frango","Cravo da Índia","Cream Cheese","Creme de leite","Danone","Doce de leite","Ervas finas","Ervilha","Espaguete","Espinafre","Essência de baunilha","Extrato de tomate","Farinha de linhaça","Farinha de mandioca","Farinha de milho","Farinha de rosca","Farinha de trigo","Farinha Láctea","Farofa pronta","Fécula de mandioca","Feijão carioca","Feijão outros","Feijão preto","Fermento biológico","Fermento em pó","Filé de frango","Filé de peito de frango","Filé de Salmão","Filé de Tilápia","Filé peito de Frango","Filé Peito de Peru","Folha de louro","Framboesa","Frango","Gelatina em pó","Geléia Goiaba","Geléia outras","Geléia Pimenta","Gengibre","Goiaba","Goma para tapioca","Gotas de chocolate (m&m, bolinhas...)","Granola","Grão de Bico","Graviola","Hambúrguer congelado","Hortelã","Inhame","Iogurte de fruta","Iogurte natural","Jiló","Ketchup","Kiwi","Lagosta","Laranja","Leite","Leite condensado","Leite de coco","Leite desnatado","Leite em pó","Leite Integral","Lentilha","Lima","Limão","Lichia","Linguiça","Linhaça","Lula","Maçã","Macarrão","Macarrão Cabelo de anjo","Macarrão integral","Maionese","Maizena","Mamão","Mamão formosa","Mandioca","Manga","Manteiga","Maracujá","Margarina","Massa para lasanha","Mel de abelha","Melancia","Melão","Milho (Pipoca)","Milho enlatado","Milho verde","Molho barbecue","Molho de pimenta","Molho de tomate","Molho inglês","Molho shoyu","Morango","Mostarda","Nata","Óleo","Orégano","Ovo","Ovo de codorna","Palmito","Pão Branco","Pao de forma","Pão de queijo","Pão integral","Pão sem Glúten","Páprica","Peito de frango","Peito de peru","Peixe","Penne","Pepino","Pêra","Pêssego","Pimenta calabresa","Pimenta de cheiro","Pimenta do reino","Pimenta malagueta","Pimenta outras","Pimentão","Pipoca microondas","Pitanga","Pitomba","Pó de café","Polpa de Fruta","Polvilho","Polvo","Presunto","Purê de batata pronto","Purê de mandioca","Queijo amarelo","Queijo branco","Queijo coalho","Queijo Minas","Queijo Mussarela","Queijo outros","Queijo parmesão","Queijo gorgonzola","Queijo ricota","Queijo Ralado","Quiabo","Repolho","Requeijão","Rúcula","Sal","Salaminho","Salmão","Salsa","Salsicha","Sálvia","Sapoti","Schweppes","Siri","Siriguela","Sobrecoxa frango","Soja","Sorvete","Suco em pó","Tabletes de caldo de galinha","Tamarindo","Tangerina/Mexerica","Tempero completo","Tomate","Tomate seco","Torrada","Uva","Uva Passas","Vinagre balsâmico","Vinagre de álcool","Vinagre de vinho","Vinho Branco","Vinho Tinto"};
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
        return (new ConnectionAccessor()).getResultByRecipeName(name);
    }

    /**
     * @return Uma lista de AbstractRecipe com as receitas populares
     */
    public static List<AbstractRecipe> getPopularRecipes(int param, Context context) throws HttpHostConnectException {
        List<AbstractRecipe> populars;

        if (param == SERVER_POPULAR) {
            populars = (new ConnectionAccessor()).getPopularRecipes();
            if (populars != null) {
                List<Long> ids = new ArrayList<>();
                populars.size();
                for (AbstractRecipe ar : populars) {
                    ids.add(ar.getId());
                }
                SharedPreferences settings = context.getSharedPreferences(POPULARS,0);
                SharedPreferences.Editor editor = settings.edit();
                for (int i = 0; i < ids.size(); i++) {
                    editor.putLong(""+i, ids.get(i));
                    Log.i("GetPopular", ids.get(i)+"");
                }

                RecipeDAO recipeDAO = new RecipeDAO(context);
                try {
                    recipeDAO.open();
                    for (long l : ids) recipeDAO.insert(accessor.getRecipeById(l));
                    recipeDAO.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                editor.commit();
                HomeFragment.POPULAR_PARAM = LOCAL_POPULAR;
                Log.i("GetPopular", "Server");

                SharedPreferences settings2 = context.getSharedPreferences(HomeFragment.FIRST_TIME, 0);
                settings2.edit().putBoolean(HomeFragment.FIRST_TIME, false).commit();
            }
        } else {
            return getLocalPopularRecipes(context);
        }

        return populars;
    }

    /**
     *
     */
    public static List<AbstractRecipe> getLocalPopularRecipes (Context context) {
        List<AbstractRecipe> populars = null;

        SharedPreferences settings = context.getSharedPreferences(POPULARS,0);
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ids.add(settings.getLong("" + i, 0));
        }
        populars = getAbstractRecipes(ids, context);
        for (AbstractRecipe ar : populars) {
            Log.i("GetPopular", ar.getId() + "");
        }
        Log.i("GetPopular", "Local");

        return populars;
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
            cookbookDAO.open();
            recipeDAO.open();

            if ((remoteDBId == -1) && (recipe.getId() == -1)){
                SharedPreferences data = context.getSharedPreferences("localId",0);
                SharedPreferences.Editor editor = data.edit();
                long localId = data.getLong("localIdValue", Long.MAX_VALUE);

                recipe.setId(localId);
                recipe.setSync(false);

                editor.putLong("localIdValue", localId - 1).commit();

                recipeDAO.insert(recipe);
                cookbookDAO.insert(recipe);
                cookbookDAO.close();
                recipeDAO.close();
                Log.i("DebugManager", "Foi inserido no bd local");
                return 1;
            }else{
                recipe.setId(remoteDBId);
                recipe.setSync(true);
                internalDB = cookbookDAO.insert(recipe);
                cookbookDAO.close();
                Log.i("DebugManager", "Foi inserido no servidor");
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


    public static Bitmap compressImage(String imageUri) {
        String filePath = imageUri;
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 408.0f;
        float maxWidth = 306.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scaledBitmap;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}