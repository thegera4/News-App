package com.news.newsapp.ui

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.news.newsapp.BottomMenuScreen
import com.news.newsapp.components.BottomMenu
import com.news.newsapp.models.TopNewsArticle
import com.news.newsapp.network.NewsManager
import com.news.newsapp.ui.screen.Categories
import com.news.newsapp.ui.screen.DetailScreen
import com.news.newsapp.ui.screen.Sources
import com.news.newsapp.ui.screen.TopNews

@Composable
fun NewsApp(){
    val scrollState = rememberScrollState()
    val navController = rememberNavController()
    MainScreen(navController = navController , scrollState)
}

@Composable
fun MainScreen(navController: NavHostController, scrollState: ScrollState){
    Scaffold(bottomBar = {
        BottomMenu(navController)
    }) {
        Navigation(navController = navController , scrollState = scrollState, paddingValues = it)
    }
}

@Composable
fun Navigation(navController: NavHostController,
               scrollState: ScrollState,
               newsManager: NewsManager = NewsManager(),
               paddingValues: PaddingValues
){

    val articles = newsManager.newsResponse.value.articles
    Log.d("news", "$articles")
    articles?.let {
        NavHost(navController = navController,
            startDestination = BottomMenuScreen.TopNews.route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ){
            bottomNavigation(navController = navController, articles,newsManager)
            composable("Detail/{index}",
                arguments = listOf(navArgument("index"){type = NavType.IntType})
            ){
                    navBackStackEntry ->
                val index = navBackStackEntry.arguments?.getInt("index")
                index?.let {
                    val article = articles[index]
                    DetailScreen(article, scrollState, navController)
                }


            }
        }
    }



}

fun NavGraphBuilder.bottomNavigation(navController: NavController,
                                     articles: List<TopNewsArticle>,
                                    newsManager: NewsManager){
    composable(BottomMenuScreen.TopNews.route){
        TopNews(navController = navController, articles)
    }
    composable(BottomMenuScreen.Categories.route){
        newsManager.getArticlesByCategory("business")
        newsManager.onSelectedCategoryChanged("business")

        Categories(newsManager = newsManager, onFetchCategory = {
            newsManager.onSelectedCategoryChanged(it)
            newsManager.getArticlesByCategory(it)
        })
    }
    composable(BottomMenuScreen.Sources.route){
        Sources()
    }
}