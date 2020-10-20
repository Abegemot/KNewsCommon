package com.begemot.knewscommon
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
//import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import kotlin.Exception


val kjson= Json{ encodeDefaults=true }
//val kjson=Json(JsonConfiguration.Stable)

@Serializable
data class GetHeadLines(val handler:String, val tlang:String,val datal: Long)
@Serializable
data class GetArticle(val handler:String, val tlang:String, val link:String)


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
inline class JListNewsPaper(val str:String)


fun fromJsonToList(str:JListKArticle):List<KArticle>                     =  kjson.decodeFromString(ListSerializer(KArticle.serializer()),(str.str))

fun fromJsonToList(str:JListString):List<String>                         =  kjson.decodeFromString(ListSerializer(String.serializer()),(str.str))

fun fromJsonToList(str:JListOriginalTrans):List<OriginalTrans>           =  kjson.decodeFromString(ListSerializer(OriginalTrans.serializer()),(str.str))

fun fromJsonToList(str:JListOriginalTransLink):List<OriginalTransLink>   =  kjson.decodeFromString(ListSerializer(OriginalTransLink.serializer()),(str.str))

fun fromJsonToList(str:JListNewsPaper):List<NewsPaper>                  =  kjson.decodeFromString(ListSerializer(NewsPaper.serializer()),str.str)

fun toJListKArticle(list:List<KArticle>):JListKArticle                   = JListKArticle(kjson.encodeToString(ListSerializer(KArticle.serializer()),list))

fun toJListOriginalTransLink(list:List<OriginalTransLink>):JListOriginalTransLink = JListOriginalTransLink(kjson.encodeToString(ListSerializer(OriginalTransLink.serializer()),list))

fun toJListOriginalTrans(list:List<OriginalTrans>):JListOriginalTrans = JListOriginalTrans(kjson.encodeToString(ListSerializer(OriginalTrans.serializer()),list))

fun toJListString(list:List<String>):JListString = JListString(kjson.encodeToString(ListSerializer(String.serializer()),list))

fun toJListNewsPaper(list:List<NewsPaper>) = JListNewsPaper(kjson.encodeToString(ListSerializer(NewsPaper.serializer()),list))

fun fromStrToTHeadLines(str:String):THeadLines  = kjson.decodeFromString<THeadLines>(str)
fun toStrFromTHeadlines(thd:THeadLines):String = kjson.encodeToString(THeadLines.serializer(),thd)


@Serializable
class ListOriginalTransList(val lOT:List<OriginalTransLink>)
@Serializable
class JasonString(val value:String)


@Serializable
class OHeadLines(val datal: Long,val lhl:List<KArticle>)

@Serializable
class THeadLines(var datal: Long=0, var lhl:List<OriginalTransLink> = emptyList()){
    override fun toString():String{return "THeadLine data ${strfromdateasLong(datal)} size ${lhl.size}"}
}

@Serializable
data class KArticle(val title: String = "", val link: String = "")
data class jsonLA( val q:List<KArticle>)

@Serializable
data class jsonTrans(val q:List<String>,val source:String,val target:String,val format:String)

@Serializable
data class Data ( val translations : List<Translations> )
@Serializable
data class Translations ( val translatedText : String )
@Serializable
data class Json4Kotlin_Base ( val data : Data )

@Serializable
class Found(val found:Boolean=false,val ldata:Long,val sresult:String="")

@Serializable
class Found2(val found:Boolean=false,val bresult:ByteArray=ByteArray(0))


@Serializable
data class StoredElement(val name:String, val tag:String, val tcreation:Long, val tupdate:Long, val size: Long)


@Serializable
data class NewsPaper(val handler:String, val name:String,val desc:String, val title: String, val olang:String, val logoname:String)


@Serializable
data class NewsPaperVersion(val version:Int, val newspaper:List<NewsPaper>)


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

fun strfromdateasLong(date:Long):String{
    if(date==0L) return ""
    val sdf= SimpleDateFormat("dd/MM HH:mm")
    return sdf.format(date)
}

fun Long.milisToMinSecMilis():String{
    val stb=StringBuilder()
    val days    = TimeUnit.MILLISECONDS.toDays(this)
    val hours   = TimeUnit.MILLISECONDS.toHours(this)-(24*days)
    val minuts  = TimeUnit.MILLISECONDS.toMinutes(this)-(60*hours)-(24*days*60)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this)-(60*minuts)-(hours*60*60)-(days*24*60*60)
    val milis   = this-(seconds*1000)-(minuts*60*1000)-(hours*60*60*1000)-(days*24*60*60*1000)
    val ln=5
    stb.append('(')
    if(days>0) stb.append("$days d".padEnd(ln,' '))
    if(hours>0) stb.append("$hours h".padEnd(ln,' '))
    if(minuts>0) stb.append("$minuts m".padEnd(ln,' '))
    if(seconds>0) stb.append("$seconds s".padEnd(ln,' '))
    stb.append("$milis ms)".padEnd(ln,' '))
    return stb.toString()
}