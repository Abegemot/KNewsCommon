package com.begemot.knewscommon
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.lang.Exception
import kotlin.reflect.KClass

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




fun List<OriginalTransLink>.toJsonString(a:Short=0):JListOriginalTransLink = JListOriginalTransLink(kjson.stringify(ListSerializer(OriginalTransLink.serializer()),this))
fun JListOriginalTransLink.toLOriginalTransLink():List<OriginalTransLink> = kjson.parse(ListSerializer(OriginalTransLink.serializer()),this.str)


fun List<KArticle>.toJsonString(a:Long=0):JListKArticle = JListKArticle(kjson.stringify(ListSerializer(KArticle.serializer()),this))
fun JListKArticle.toListKArticle():List<KArticle> = kjson.parse(ListSerializer(KArticle.serializer()),this.str)

fun List<OriginalTrans>.toJsonString():JListOriginalTrans = JListOriginalTrans(kjson.stringify(ListSerializer(OriginalTrans.serializer()),this))

fun JListOriginalTrans.toListOriginalTrans():List<OriginalTrans> = kjson.parse(ListSerializer(OriginalTrans.serializer()),this.str)

fun List<String>.toJsonString22(i:Int=0):JListString = JListString(kjson.stringify(ListSerializer(String.serializer()),this))

fun JListString.toListString():List<String> = kjson.parse(ListSerializer(String.serializer()),this.str)


@ImplicitReflectionSerializer
fun pepe(){
    val X= mutableListOf<KArticle>()
    val z=X.toJList<KArticle,JListKArticle>()
}

@ImplicitReflectionSerializer
fun List<String>.toJsonString(i:Int=0):JListString = JListString(ListToString1(this))

val classestojsonlist= mapOf < KClass<*>, KClass<*> >(
    KArticle::class to JListKArticle::class,
    OriginalTransLink::class to JListOriginalTransLink::class,
    String::class to JListString::class,
    OriginalTrans::class to JListOriginalTrans::class
)


@ImplicitReflectionSerializer
inline  fun <reified T, reified Y:Any> List<T>.toJList():Y{
    val s= serializer<T>()
    val x = Json(JsonConfiguration.Stable).stringify(ListSerializer(s),this)
    if(T::class.simpleName.equals(KArticle::class.simpleName))           return JListKArticle(x) as Y
    if(T::class.simpleName.equals(OriginalTrans::class.simpleName))      return JListOriginalTrans(x) as Y
    if(T::class.simpleName.equals(String::class.simpleName))             return JListString(x) as Y
    if(T::class.simpleName.equals(OriginalTransLink::class.simpleName))  return JListOriginalTransLink(x) as Y
    throw Exception(" Wrong parameter in List< ${T::class.simpleName}, ${Y::class.simpleName}>.toJList !!")

}
@ImplicitReflectionSerializer
inline  fun <reified T> String.fromJList():List<T>{
    val s= serializer<T>()
    return kjson.parse(ListSerializer(s),this)
}

@ImplicitReflectionSerializer
fun pp(){
    val str="jsjsjs"
    val j=str.fromJList<String>()
    val jo=JListKArticle("jsjsj")
    val q= jo.str.fromJList<KArticle>()
    val u=q.toJList<KArticle,JListKArticle>()
}




@ImplicitReflectionSerializer
inline fun <reified T> ListToString3(value:List<T>):Any {
    val s = serializer<T>()
    val output = classestojsonlist[T::class] ?: throw Exception("No JsonListString class for class ${T::class.java.canonicalName} ")
    val qs = Json(JsonConfiguration.Stable).stringify(ListSerializer(s), value)
    val pp = output.constructors.first().call(qs)
    return pp
}

fun <T> KStringifier (){

}

@ImplicitReflectionSerializer
inline fun <reified T,reified Y> ListToString2(value:List<T>,afun:(String)->Y):Y{
    val s= serializer<T>()
    return  afun( (Json(JsonConfiguration.Stable).stringify(ListSerializer(s),value)))
}

@ImplicitReflectionSerializer
inline fun <reified T> ListToString1(value:List<T>):String{
    val s= serializer<T>()
    return Json(JsonConfiguration.Stable).stringify(ListSerializer(s),value)
}

@ImplicitReflectionSerializer
inline fun <reified T:Any> StringToList(value:String):List<T>{
    val s= serializer<T>()
      return kjson.parse(ListSerializer(s),value)
}
@Serializable
class ListOriginalTransList(val lOT:List<OriginalTransLink>)
@Serializable
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
class Found(val found:Boolean=false,val sresult:String="")

@Serializable
data class StoredElement(val name:String, val tag:String, val tcreation:Long, val tupdate:Long, val size: Long)


@Serializable
data class NewsPaper( val name:String,val title: String,val oland:String,val logoname:String)


