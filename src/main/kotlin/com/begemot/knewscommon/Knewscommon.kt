package com.begemot.knewscommon
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.Exception

val kjson= Json(JsonConfiguration.Stable)

@Serializable
data class GetHeadLines(val headlinesname:String,val lang:String)
@Serializable
data class GetArticle(val headlinesname:String,val lang:String,val link:String)


@Serializable
data class StoreFile(val filename:String,val content:String)

@Serializable
data class OriginalTransLink(val kArticle: KArticle,val translated: String)
@Serializable
data class OriginalTrans(val original:String="",val translated:String="")



inline class JListOriginalTrans(val str:String)
inline class JListString(val str:String)
inline class JListKArticle(val str:String)
inline class JListOriginalTransLink(val str:String)


fun fromJsonToList(str:JListKArticle):List<KArticle>                     =  kjson.parse(ListSerializer(KArticle.serializer()),(str.str))

fun fromJsonToList(str:JListString):List<String>                         =  kjson.parse(ListSerializer(String.serializer()),(str.str))

fun fromJsonToList(str:JListOriginalTrans):List<OriginalTrans>           =  kjson.parse(ListSerializer(OriginalTrans.serializer()),(str.str))

fun fromJsonToList(str:JListOriginalTransLink):List<OriginalTransLink>   =  kjson.parse(ListSerializer(OriginalTransLink.serializer()),(str.str))


fun toJListKArticle(list:List<KArticle>):JListKArticle                   = JListKArticle(kjson.stringify(ListSerializer(KArticle.serializer()),list))

fun toJListOriginalTransLink(list:List<OriginalTransLink>):JListOriginalTransLink = JListOriginalTransLink(kjson.stringify(ListSerializer(OriginalTransLink.serializer()),list))

fun toJListOriginalTrans(list:List<OriginalTrans>):JListOriginalTrans = JListOriginalTrans(kjson.stringify(ListSerializer(OriginalTrans.serializer()),list))

fun toJListString(list:List<String>):JListString = JListString(kjson.stringify(ListSerializer(String.serializer()),list))






@Serializable
class ListOriginalTransList(val lOT:List<OriginalTransLink>)
@Serializable
class JasonString(val value:String)






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
class Found(val found:Boolean=false,val sresult:String="")

@Serializable
data class StoredElement(val name:String, val tag:String, val tcreation:Long, val tupdate:Long, val size: Long)


@Serializable
data class NewsPaper(val handler:String, val name:String,val title: String,val oland:String,val logoname:String)


sealed class KResult<T,R>{
    class Success<T,R>(val t:T):KResult<T,R>()
    class Error<T,R>(val msg:String,val e:Exception?=null):KResult<T,R>()
    object Empty:KResult<Nothing,Nothing>()
}


inline fun <reified T, reified R> exWithException(afun:()->T): KResult<T,R> {
    return try {
        val p=afun()
        KResult.Success(p)
    }catch (e:Exception){
        KResult.Error("error",e)
    }
}

fun getStackExceptionMsg(e:Exception?):String{
    var msg = "null"
    val sw = StringWriter()
    if (e != null) {
        e.printStackTrace(PrintWriter(sw))
        msg = sw.toString()
    }
    return msg
}