package com.example.appvigile3.ui.theme

/*import java.sql.DriverManager

class DatabaseManager {
    private val url = "jdbc:mysql://192.168.1.22/appvigile"
    private val user = "root"
    private val password = ""

    fun getArticle(): List<Article>{
        val connection = DriverManager.getConnection(url, user, password)
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * from articles")

        val articles = mutableListOf<Article>()
        while (resultSet.next()) {

            val dataObject = Article(
                resultSet.getString("scanrfid"),
                resultSet.getInt("codebarre"),
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
}*/
