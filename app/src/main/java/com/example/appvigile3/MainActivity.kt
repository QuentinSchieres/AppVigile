package com.example.appvigile3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appvigile3.ui.theme.AppVigile3Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.DriverManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppVigile3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}

sealed class Screen(val route: String){
    object Acceuil: Screen("ecran_acceuil")
    object AucunArticle: Screen("ecran_aucunarticle")
    object InfosArticle: Screen("ecran_infosarticle")
}

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Acceuil.route) {
        composable(route = Screen.Acceuil.route){
            Acceuil(navController = navController)
        }
        composable(route = Screen.AucunArticle.route){
            AucunArticle(navController = navController)
        }
        composable(route = Screen.InfosArticle.route){
            InfosArticle(navController = navController)
        }
    }
}

data class Article(
    val scanrfid: String,
    val codebarre: Int,
    val nom: String,
    val marque: String,
    val prix: Float,
    val stock: Int,
    val checkstock: Int
)

class DatabaseManager {
    private val url = "jdbc:mysql://172.20.100.33/runtouz"
    private val user = "RunTouz"
    private val password = "RunTouz2023"

    fun getArticle(): List<Article>{
        val connection = DriverManager.getConnection(url, user, password)
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * from produits where rfid = \"0007366977 112,26945\"")

        val articles = mutableListOf<Article>()
        while (resultSet.next()) {

            val dataObject = Article(
                resultSet.getString("rfid"),
                resultSet.getInt("code_barre"),
                resultSet.getString("nom"),
                resultSet.getString("marque"),
                resultSet.getFloat("prix"),
                resultSet.getInt("stock"),
                resultSet.getInt("checkstock")
            )
            articles.add(dataObject)
        }
        resultSet.close()
        statement.close()
        connection.close()

        return articles
    }

    fun checkUser(id: String, mdp: String): Boolean{
        val connection = DriverManager.getConnection(url, user, password)
        val statement = connection.createStatement()
        val query = "SELECT * from appvigile where identifiant = \"$id\" and mdp = \"$mdp\""
        val resultSet = statement.executeQuery(query)

        return resultSet.next()
    }
}

@Composable
fun Acceuil(navController: NavController){

    var idInput by remember { mutableStateOf("") }
    var mdpInput by remember { mutableStateOf("") }
    var errormessage by remember { mutableStateOf("") }
    var result by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit){
        val data = withContext(Dispatchers.IO){
            DatabaseManager().checkUser(idInput, mdpInput)
        }
        result = data
    }

    Column(
        verticalArrangement = Arrangement.Center, modifier = Modifier
            .padding(32.dp)
            .verticalScroll(
                rememberScrollState()
            )
        ) {
            Spacer(modifier = Modifier.height(150.dp))
            Text(
                text = stringResource(id = R.string.TXTacceuil),
                fontSize = 34.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(R.drawable.runtouz_3),
                contentDescription = stringResource(R.string.Logo),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))
            ChampTxt(
                label = R.string.Id,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                value = idInput,
                onValueChange = { idInput = it },
                aspectClavier = VisualTransformation.None
            )
            Spacer(modifier = Modifier.height(40.dp))
            ChampTxt(
                label = R.string.mdp,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                value = mdpInput,
                onValueChange = { mdpInput = it },
                aspectClavier = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(60.dp))
            Text(errormessage, color = Color.Red)
            Spacer(modifier = Modifier.height(120.dp))
            Button(
                onClick = {
                    if(result){
                        navController.navigate(Screen.AucunArticle.route)
                    }
                    else{
                        errormessage = "Identifiants incorrects"
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text(text = stringResource(R.string.Connexion))
            }
            Spacer(modifier = Modifier.height(154.dp))
            Row {
                Text(text = stringResource(id = R.string.Runtouz_2023))
            }
        }
    Spacer(modifier = Modifier.height(200.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampTxt(label : Int,
             value :String,
             onValueChange :(String) -> Unit,
             keyboardOptions: KeyboardOptions,
             keyboardActions: KeyboardActions,
             aspectClavier: VisualTransformation
){
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(label)) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = aspectClavier
    )
}

@Composable
fun AucunArticle(navController: NavController) {

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(250.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.aucun_article), fontSize = 34.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.runtouz_3),
                contentDescription = stringResource(id = R.string.Logo)
            )
          Button(onClick = { navController.navigate(Screen.InfosArticle.route) }) {
              Text(text = "texte")
          }
        }
    }
    Spacer(modifier = Modifier.height(450.dp))
    Row { Text(text = stringResource(id = R.string.Runtouz_2023)) }
}

@Composable
fun InfosArticle(navController: NavController) {
    var result by remember { mutableStateOf(emptyList<Article>()) }

    LaunchedEffect(Unit){
        val data = withContext(Dispatchers.IO){
            DatabaseManager().getArticle()
        }
        result = data
    }
     Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.align(Alignment.End)) {
                IconButton(
                    onClick = { navController.navigate(Screen.AucunArticle.route)
                    }
                ) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.height(35.dp))
            Text(
                text = stringResource(id = R.string.InfosArticle),
                fontSize = 34.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            AfficheTexte(texte = stringResource(R.string.nom))
            ArticleList(result, "nom")
            AfficheTexte(texte = stringResource(R.string.code_barre))
            ArticleList(result, "code barre")
            AfficheTexte(texte = stringResource(R.string.marque))
            ArticleList(result, "marque")
            AfficheTexte(texte = stringResource(id = R.string.prix))
            ArticleList(result, "prix")
            Spacer(modifier = Modifier.height(250.dp))
            Text(text = stringResource(id = R.string.Runtouz_2023))
        }
}

@Composable
fun AfficheTexte(texte : String){
    Spacer(modifier = Modifier.height(150.dp))
    Text(text = texte, fontSize = 20.sp)
}

@Composable
fun ArticleList(articles: List<Article>, affiche : String){
    Column{
        articles.forEach { article ->
            ArticleListItem(article, affiche)
        }
    }
}

@Composable
fun ArticleListItem(article: Article, affiche: String){
    Spacer(modifier = Modifier.height(50.dp))
    when(affiche){
        "nom" ->        Text(article.nom, fontSize = 16.sp)
        "code barre" -> Text("${article.codebarre}", fontSize = 16.sp)
        "marque" ->     Text(article.marque, fontSize = 16.sp)
        "prix" ->       Text("${article.prix}", fontSize = 16.sp)
    }
}