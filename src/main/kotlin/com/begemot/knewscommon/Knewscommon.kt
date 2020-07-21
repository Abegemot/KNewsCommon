package com.begemot.knewscommon
import kotlinx.serialization.*
import kotlinx.serialization.json.JsonArray


@Serializable
data class GetHeadLines(val headlinesname:String,val lang:String)
@Serializable
data class GetArticle(val headlinesname:String,val lang:String,val link:String)



data class StoreFile(val filename:String,val content:String)

@Serializable
data class OriginalTransLink(val kArticle: KArticle,val translated: String)
@Serializable
data class OriginalTrans(val original:String="",val translated:String="")
@Serializable
class ListOriginalTransList(val lOT:List<OriginalTransLink>)
class JasonString(val value:String)


inline class JsonTranslatedHeadlines(val value:String)


@Serializable
data class KArticle(val title: String = "", val link: String = "")
data class jsonLA( val q:List<KArticle>)

@Serializable
data class jsonTrans(val q:List<String>,val source:String,val target:String,val format:String = "text")

@Serializable
data class Data ( val translations : List<Translations> )
@Serializable
data class Translations ( val translatedText : String )
@Serializable
data class Json4Kotlin_Base ( val data : Data )




@Serializable
data class StoredElement(val name:String, val tag:String, val tcreation:Long, val tupdate:Long, val size: Long)


@Serializable
data class NewsPaper( val name:String,val title: String,val oland:String,val logoname:String)


