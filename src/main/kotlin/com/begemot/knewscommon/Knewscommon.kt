package com.begemot.knewscommon

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import kotlin.Exception
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonArray
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.net.URLEncoder
import kotlin.system.measureTimeMillis
import mu.KotlinLogging
import org.jsoup.nodes.Document
import io.ktor.client.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*


import java.io.IOException


/*val modulex= SerializersModule {
    polymorphic(IBaseNewsPaper::class){
        subclass(SZ::class,SZ.serializer())
        subclass(B::class,B.serializer())
    }
}*/


private val logger = KotlinLogging.logger {}

val kjson = Json { encodeDefaults = false; ignoreUnknownKeys = true;   }

enum class KindOfNews{
    NEWS,BOOK,SONGS
}



interface IBaseNewsPaper {
    val kind:KindOfNews
    val olang: String
//        get() = ""
    val name: String
    val desc: String
    val logoName: String
    val handler: String
        get() = this::class.java.simpleName
    val url:String
    val mutable:Boolean
        get() = false
    val xpos:String
        get() ="xpos"
    val googleDir:String
        get() = ""
    fun getOriginalHeadLines(): List<KArticle> { return emptyList<KArticle>()  }
    fun getOriginalArticle(link: String): String { return "IBaseNewsPaper" }
    fun getGoogleHeadLinesDir():String{return "HeadLines/$handler"}  //<-- aqui es final
    fun getGoogleArticlesDir():String {return "Articles/$googleDir/$handler/"}
}


interface INewsPaper : IBaseNewsPaper {
    override val kind: KindOfNews
        get()=KindOfNews.NEWS
    override fun getGoogleArticlesDir():String{return "Articles/$handler"}
}

interface IBook:IBaseNewsPaper{
    //val googleDir:String
    override val kind: KindOfNews
        get()=KindOfNews.BOOK
    override fun getGoogleArticlesDir():String{return "Books/${googleDir}/$handler"}
//"Books/${iBook.googleDir}/${iBook.handler}$link"
}

interface ISongs:IBaseNewsPaper{
    //val googleDir:String
    override val kind: KindOfNews
        get()=KindOfNews.SONGS
    override fun getGoogleArticlesDir():String{return "SongLists/${googleDir}/$handler"}

}



@Serializable
@SerialName("NEWS")
class  NewsPaper(
    override val kind: KindOfNews=KindOfNews.NEWS,
    override val olang: String,
    override val name: String,
    override val desc: String,
    override val logoName: String,
    override val handler: String,
    override val url: String,
    override val mutable: Boolean

) : IBaseNewsPaper{
    override fun toString():String="${this.handler} ${this.kind} ${this.olang} ${this.desc} ${this.logoName} ${this.url}"
    //override val mutable: Boolean = false
}


@Serializable
@SerialName("BOOKS")
data class Book(
    override val kind: KindOfNews=KindOfNews.BOOK,
    override val olang: String,
    override val name: String,
    override val desc: String,
    override val logoName: String,
    override val handler: String,
    override val url: String
) : IBaseNewsPaper{
    //override val mutable: Boolean = false
}

fun IBaseNewsPaper.toNewsPaper(): NewsPaper {
    return NewsPaper(

        mutable=mutable,
        kind = kind,
        handler = handler,
        name = name,
        desc = desc,
        logoName = logoName,
        olang = olang,
        url = url
    )
}


fun List<NewsPaper>.print():String{
    val sb=StringBuilder()
    if(this.isEmpty()){sb.append("{empty}"); sb.append("\n           YO")}
    this.forEach { it->sb.append("           ${it}\n") }

    // this.forEach { it->sb.append("           ${it.handler} ${it.kind} ${it.olang} ${it.desc} ${it.logoName} ${it.url}  \n") }
    return sb.toString()//.padStart(75,'q')
}


@Serializable
data class NewsPaperVersion(val version: Int=0, val newspaper: List<NewsPaper> = emptyList()){
   //override fun toString(): String {
   //     return "Aqui mano jo"
   //  }
    fun toString2():String{
       return """News Paper Version $version 
                 ${newspaper.print()}"""
    }
}


@Serializable
data class GetHeadLines(val handler: String, val tlang: String, val datal: Long){
    fun AndroidNameFile():String="Headlines/${handler}${tlang}"
}

@Serializable
data class GetArticle(val handler: String, val tlang: String, val link: String,var clientdate:Long=0L)


@Serializable
data class StoreFile(val filename: String, val content: String)

@Serializable
class KArticle(val title: String = "", val link: String = "", val l2:String = "")

@Serializable
class KArticle2(val title: String = "", val link: String = "")

@Serializable
data class OriginalTransLink(
    val kArticle: KArticle=KArticle(),
    val translated: String = "",
     val romanizedo: ListPinyin = ListPinyin(),
    val romanizedt: ListPinyin = ListPinyin(),

){
    fun getShortenedLinkName():String {
        return ""
    }

    override fun toString(): String {
        return "$kArticle translated $translated romanized Orig $romanizedo romanized Trans $romanizedt"
    }
}

@JvmName("printKArticle")
fun List<KArticle>.print(sAux:String="", amount: Int=1000000):String{
    val sB=StringBuilder()
    sB.append("$sAux print $amount of ${this.size} List<KArticle> \n")
    val total=if(amount>size) size else amount
    subList(0,total).forEachIndexed { index, it ->  sB.append(" ($index)   ${it.title}   ${it.link}<-end\n") }
    sB.append("end print List<KArticle>")
    return sB.toString()
}

@Serializable
data class Pinyin(val w: String = "", val r: String = "")

@Serializable
data class ListPinyin(val lPy:List<Pinyin> = emptyList()){
    override fun toString(): String {
        val stb = StringBuilder()
        lPy.forEach {
            stb.append(" ${it.w} ${it.r}")
        }
        return stb.toString()
    }
}

@JvmName("printString")
fun List<String>.print(sAux:String="", amount:Int=2,sorted:Boolean=false):String{
    val sB=StringBuilder()
    sB.append("\n${sAux} printing first $amount items of List<String> of ${this.size}\n")
    val total=if(amount>size || amount==0) size else amount
    val l=if(sorted) this.sortedByDescending { it.length } else this
    l.subList(0,total).forEachIndexed { i, it->
        val s= i.toString().padStart(3,' ')
        sB.append("($s) ${it.length.toString().padStart(4,' ')} '${it}'\n")
    }
    sB.append("end print List<String>\n")
    return sB.toString()
}

fun printFirstLast(l:List<String>,sAux: String,amount:Int=1):String{
    val sB=StringBuilder()
    val size=l.size
    if(size==0) return "empty list"
    sB.append("\n${sAux} printing firstLast $amount items of List<String> of $size\n")
    val s= 0.toString().padStart(3,' ')
    val s2= l.size.toString().padStart(3,' ')
    sB.append("($s) ${l[0].length.toString().padStart(4,' ')} '${l[0]}'\n")
    if(size>1){
        sB.append("($s2) ${l[l.size-1].length.toString().padStart(4,' ')} '${l[l.size-1]}'\n")

    }
    return sB.toString()
}


@JvmName("printOriginalTransLink")
fun List<OriginalTransLink>.print(sAux:String="",amount:Int=2):String{
    val sB=StringBuilder()
    sB.append("\n$sAux begin print first $amount List<OriginalTransLink> out of $size\n")
    val total=if(amount>size) size else amount
    subList(0,total).forEachIndexed { i,it->sB.append("($i) ${it}\n") }
//    subList(0,total).forEachIndexed { i,it->sB.append("($i) ${it.kArticle.title}\n  ${it.kArticle.link}\n  trans->${it.translated}\n  romanOrig->${it.romanizedo}\n  romanTrans->${it.romanizedt}  end\n") }
    sB.append("end print first $total of List<OriginalTransLink>  out of $size\n")
    return sB.toString()
}

@Serializable
data class OriginalTrans(
    val original: String = "",
    val translated: String = "",
    val romanizedt: ListPinyin = ListPinyin(),
    val romanizedo:ListPinyin= ListPinyin()
){
    override fun toString() : String {
        return """ 
            $original
            $translated
            ${romanizedt}
            ${romanizedo}
    """.trimIndent()
    }
}

fun List<OriginalTrans>.print(sAux: String, amount: Int = 2, start: Int = 0): String {
    //logger.debug { "$sAux  size=$size  start=$start  amount=$amount" }
    val lamount= if(amount==0) size else amount
    if(size==0) return "$sAux Print List<OriginalTrans> EMPTY"
    if (start > size)  return "Print List<OriginalTrans> ERROR->\n$sAux IllegalArgument List<OriginalTrans>.print start ($start) > size ($size)"
    val total= if(start+lamount>size) size-start else lamount
    val sB = StringBuilder()
    sB.append("\n${sAux}print List<OriginalTrans> first $total of $size starting at $start")
    val space = "     "
        subList(
            start,
            total + start
        ).forEachIndexed { i, it ->
            val s= i.toString().padStart(3,' ')

            sB.append("\n$s) (${it.original.length})  original  ->${it.original}  \n$space trans     ->${it.translated}  \n$space romanOrig ->${it.romanizedo} \n$space romanTrans->${it.romanizedt}\n") }
        sB.append("\nend print List<OriginalTrans>")
    return  sB.toString()
}


@Serializable
data class THeadLines(val datal: Long = 0, val lhl: List<OriginalTransLink> = emptyList()) {
    override fun toString(): String {
        return "THeadLines data ${strfromdateasLong(datal)} size ${lhl.size} ${lhl.size}"
    }
}

data class TArticle(val lnk:String="",val lot:List<OriginalTrans> = emptyList()){

}


data class THeadLine(val ot:OriginalTrans,val lnk: String){}



//inline class JListOriginalTrans(val str: String)
//inline class JListString(val str: String)
//inline class JListKArticle(val str: String)
//inline class JListOriginalTransLink(val str: String)
//inline class JListNewsPaper(val str: String)

//@JvmInline
//value class DirFileName(val str: String)

//fun fromJsonToList(str: JListKArticle): List<KArticle> =
//    kjson.decodeFromString(ListSerializer(KArticle.serializer()), (str.str))

//fun JListKArticle.toList():List<KArticle> = kjson.decodeFromString(ListSerializer(KArticle.serializer()), (str))
//fun JListString.toList():List<String> = kjson.decodeFromString(ListSerializer(String.serializer()), (str))
//fun JListOriginalTrans.toList():List<OriginalTrans> =  kjson.decodeFromString(ListSerializer(OriginalTrans.serializer()), (str))
//fun JListOriginalTransLink.toList():List<OriginalTransLink> = kjson.decodeFromString(ListSerializer(OriginalTransLink.serializer()), (str))

//fun JListNewsPaper.toList():List<NewsPaper> = kjson.decodeFromString(ListSerializer(NewsPaper.serializer()), str)

//fun fromJsonToList(str: JListNewsPaper): List<NewsPaper> = kjson.decodeFromString(ListSerializer(NewsPaper.serializer()), str.str)

//fun toJListKArticle(list: List<KArticle>): JListKArticle =
//    JListKArticle(kjson.encodeToString(ListSerializer(KArticle.serializer()), list))

//fun List<KArticle>.toJSON4():JListKArticle=JListKArticle(kjson.encodeToString(ListSerializer(KArticle.serializer()), this))
//fun List<OriginalTransLink>.toJSON3():JListOriginalTransLink=JListOriginalTransLink( kjson.encodeToString(ListSerializer(OriginalTransLink.serializer()),this ))

//fun List<OriginalTrans>.toJSON():JListOriginalTrans=JListOriginalTrans(kjson.encodeToString(ListSerializer(OriginalTrans.serializer()), this))

//fun List<String>.toJSON2():JListString=JListString(kjson.encodeToString(ListSerializer(String.serializer()), this))

inline fun <reified T> toJStr(t:T):String{
    return kjson.encodeToString<T>(t)
}

inline fun <reified T>fromJStr(str:String):T{
    return kjson.decodeFromString<T>(str)
}


//fun toJListNewsPaper(list: List<NewsPaper>) =  JListNewsPaper(kjson.encodeToString(ListSerializer(NewsPaper.serializer()), list))

//fun fromStrToTHeadLines(str: String): THeadLines = kjson.decodeFromString<THeadLines>(str)
//fun toStrFromTHeadlines(thd: THeadLines): String = kjson.encodeToString(THeadLines.serializer(), thd)


//fun fromStrToNewsPaperV(str:String): NewsPaperVersion = kjson.decodeFromString(str)
//fun toStrFromNewsPaperV(npv:NewsPaperVersion): String = kjson.encodeToString(NewsPaperVersion.serializer(),npv)
//fun NewsPaperVersion.toJStr():String = kjson.encodeToString(NewsPaperVersion.serializer(),this)
//fun THeadLines.toJStr():String = kjson.encodeToString(THeadLines.serializer(), this)


@Serializable
class ListOriginalTransList(val lOT: List<OriginalTransLink>)

@Serializable
class JasonString  (val value: String)


@Serializable
class OHeadLines(val datal: Long, val lhl: List<KArticle>)


@Serializable
data class jsonTrans(
    val q: List<String>,
    val source: String,
    val target: String,
    val format: String
)

fun jsonTrans.toJStr():String = kjson.encodeToString(jsonTrans.serializer(),this)

@Serializable
data class Data(val translations: List<Translations>)

@Serializable
data class Translations(val translatedText: String)

@Serializable
data class Json4Kotlin_Base(val data: Data)

@Serializable
class Found(val found: Boolean = false, val ldata: Long, val sresult: String = "")

@Serializable
class Found2(val found: Boolean = false, val bresult: ByteArray = ByteArray(0))


@Serializable
data class StoredElement(
    val name: String,
    val tag: String,
    val tcreation: Long,
    val tupdate: Long,
    val size: Long
)


sealed class KResult<T, R> {
    class Success<T, R>(val t: T) : KResult<T, R>()
    class Error<T, R>(val msg: String, val e: Exception? = null) : KResult<T, R>()
    //object Empty : KResult<Nothing, Nothing>()
}

sealed class KResult4<T>{
    class Succes<T>(val t:T) : KResult4<T>(){
        fun X():String=" "
    }
}


fun a(i:Int):Result<Unit>{
    return Result.success<Unit>(Unit)
    return Result.failure(Exception("kkd"))
}

//class   Success3<T>(val t: T, var clientTime:Long=0, val serverTime:Long=0) : KResult3<T>() {
//}

sealed class KResult3<T> {
    class   Success<T>(val t: T,var name:String="", var clientTime:Long=0, val serverTime:Long=-1) : KResult3<T>() {
        override fun msg(): String =  _msg("SUCCES!!",name)
        //override fun timeInfo():String = "cli $clientTime srv ${serverTime}  (${clientTime-serverTime}) ms"
        override fun timeInfo():String = _timeInfo(clientTime,serverTime)
        override fun setclitime(t:Long){ clientTime=t }
        override fun getclitime(): Long = clientTime

    }
    class Error<T>(val msg: String,var name:String="", var clientTime:Long=0,val serverTime: Long=-1) : KResult3<T>(){
        override fun msg():String = _msg("ERROR!! $msg",name)//"ERROR->$msg client time ($clientTime)ms"
        //override fun timeInfo():String = "cli $clientTime srv ${serverTime}  (${clientTime-serverTime})"
        override fun timeInfo():String = _timeInfo(clientTime,serverTime)
        override fun setclitime(t:Long){ clientTime=t }
        override fun getclitime(): Long = clientTime
    }
    open fun msg():String=""
    open fun timeInfo():String=""
    open fun setclitime(t:Long){}
    open fun getclitime():Long=0L
    fun _timeInfo(clitime:Long,srvtime:Long):String {
        var ans=""
        if(srvtime==-1L) ans="cli ($clitime) ms"
        else ans="cli ($clitime) srv ($srvtime)  lat=(${clitime-srvtime}) ms"
        return ans
    }

    inline fun _msg(msg:String,name:String):String = "$msg $name -> ${timeInfo()}"
    //open fun toUnit():KResult3<Unit>{ return KResult3.Success(Unit,"unknow name",this.getclitime(),888 )}

    //object Empty : KResult<Nothing, Nothing>()
}



//@Serializable
/*sealed class KResult2<T, R> {
    class   Success<T, R>(val t: T, val clientTime:Long=0, val serverTime:Long=0) : KResult2<T, R>() {
        override fun msg(): String { return "SUCCES->${clientTime.milisToSec()}" }
        override fun timeInfo():String = "cli $clientTime srv ${serverTime}  (${clientTime-serverTime}) ms"

    }
    class Error<T, R>(val msg: String, val clientTime:Long=0,val serverTime: Long=0) : KResult2<T, R>(){
        override fun msg():String{ return "ERROR->${clientTime.milisToSec()}\nmsg"}
        override fun timeInfo():String = "cli $clientTime srv ${serverTime}  (${clientTime-serverTime})"
    }
    open fun msg():String=""
    open fun timeInfo():String=""
    //object Empty : KResult<Nothing, Nothing>()
}*/

inline fun <reified T, reified R> exWithException(afun: () -> T): KResult<T, R> {
    return try {
        val p = afun()
        KResult.Success(p)
    } catch (e: Exception) {
        KResult.Error(e.message ?: "", e)
    }
}

/*suspend fun < T,  R> exWithException2(afun: suspend () -> T): KResult2<T, R> {
    return try {
        val p = afun()
        KResult2.Success(p,0,0)
    } catch (e: Exception) {
        KResult2.Error(e.message ?: "", 0L)
    }
}*/



inline fun <reified T, reified R> exWithExceptionThrow(msg: String, afun: () -> T) {
    try {
        afun()
    } catch (e: Exception) {
        throw Exception(msg, e)
    }
}


fun getStackStr():String{
       var msg = StringBuilder()
       msg.append("Stack->")
       val s=Thread.currentThread().stackTrace
       s.filter { it.className.startsWith("com.begemot") }
       .forEachIndexed {i,it -> if(i>0) msg.append("\n${it.fileName}.${it.methodName}(${it.lineNumber})") }
       msg.append("\n<-Stack")
       return msg.toString()
}

fun getStackExceptionMsg2(e: Exception?): String {
    var msg = StringBuilder()
    val sw = StringWriter()
    if (e != null) {
            val j=Exception("\nException -> ${e.javaClass.canonicalName} ${e.message}")
     //       j.stackTrace = (e.stackTrace.filter { it.className.startsWith("com.begemot") }.toTypedArray())
        j.stackTrace = (e.stackTrace.filter { true }.toTypedArray())
            j.printStackTrace(PrintWriter(sw))
            return sw.toString()
    }
    return "no stack"
    //return msg.toString()
}
fun getStackExceptionMsg(e: Throwable?): String {
    var msg = ""
    val sw = StringWriter()
    if (e != null) {
        val l=e.stackTrace.filter { it.className.startsWith("com.begemot") }
        e.printStackTrace(PrintWriter(sw))
        msg = "stack Trace ->\n ${sw.toString()}"
    }
    return msg
}

fun strfromdateasLong(date: Long): String {
    if (date == 0L) return ""
    val sdf = SimpleDateFormat("dd/MM/YYYY   HH:mm")
    return sdf.format(date)
}

fun Long.milisToMinSecMilis(): String {
    val stb = StringBuilder()
    val days = TimeUnit.MILLISECONDS.toDays(this)
    val hours = TimeUnit.MILLISECONDS.toHours(this) - (24 * days)
    val minuts = TimeUnit.MILLISECONDS.toMinutes(this) - (60 * hours) - (24 * days * 60)
    val seconds =
        TimeUnit.MILLISECONDS.toSeconds(this) - (60 * minuts) - (hours * 60 * 60) - (days * 24 * 60 * 60)
    val milis =
        this - (seconds * 1000) - (minuts * 60 * 1000) - (hours * 60 * 60 * 1000) - (days * 24 * 60 * 60 * 1000)
    val ln = 5
    stb.append('(')
    if (days > 0) stb.append("$days d".padEnd(ln, ' '))
    if (hours > 0) stb.append("$hours h".padEnd(ln, ' '))
    if (minuts > 0) stb.append("$minuts m".padEnd(ln, ' '))
    if (seconds > 0) stb.append("$seconds s".padEnd(ln, ' '))
    stb.append("$milis ms)".padEnd(ln, ' '))
    return stb.toString()
}


fun Long.milisToMinSec(): String {
    if(this==0L) return "0:0"
    val stb = StringBuilder()
    val days = TimeUnit.MILLISECONDS.toDays(this)
    val hours = TimeUnit.MILLISECONDS.toHours(this) - (24 * days)
    val minuts = TimeUnit.MILLISECONDS.toMinutes(this) - (60 * hours) - (24 * days * 60)
    val seconds =
        TimeUnit.MILLISECONDS.toSeconds(this) - (60 * minuts) - (hours * 60 * 60) - (days * 24 * 60 * 60)
    val milis =
        this - (seconds * 1000) - (minuts * 60 * 1000) - (hours * 60 * 60 * 1000) - (days * 24 * 60 * 60 * 1000)
    val ln = 5
    /*stb.append('(')
    if (days > 0) stb.append("$days d".padEnd(ln, ' '))
    if (hours > 0) stb.append("$hours h".padEnd(ln, ' '))
    if (minuts > 0) stb.append("$minuts m".padEnd(ln, ' '))
    if (seconds > 0) stb.append("$seconds s)".padEnd(ln, ' '))*/
    val seconds2=if(seconds<10) "0$seconds" else "$seconds"
    stb.append("$minuts:$seconds2")
   // stb.append("$milis ms)".padEnd(ln, ' '))
    return stb.toString()
}


fun Long.milisToSec(original: Boolean =false): String {
    val stb = StringBuilder()
    val seconds = this/1000
    val milis = (this % 1000)
   // val micros= (this % 1000000000)/1000000
   // val nanos = this % 1000

    //µs
    stb.append("$seconds s ")
   // stb.append("${micros.toString().padStart(3,'0')}µs ms ")
    stb.append("${milis.toString().padStart(3,'0')} ms ")
   // stb.append("${nanos.toString().padStart(3,'0')}ns ")
    if(original) stb.append("  original->$this")
    return stb.toString()
}


fun Long.nanosToSecMilis(): String {
    val stb = StringBuilder()
    val seconds = this/1000000000
    val milis = (this % 1000000)/1000
    val micros= (this % 1000000000)/1000000
    val nanos = this % 1000

    //µs
    stb.append("$seconds s ")
    stb.append("${micros.toString().padStart(3,'0')}ms ")
    stb.append("${milis.toString().padStart(3,'0')}µs ")
    stb.append("${nanos.toString().padStart(3,'0')}ns ")
    //stb.append("  original->$this")
    return stb.toString()
 }

class KTimer(){
    val start:Long
    var end=0L
    init{
        start = System.currentTimeMillis()
        //println("--->KTimer.create = start $start")
    }
    fun getElapsed():Long{
        if(end==0L) end=System.currentTimeMillis()-start
        //println("--->KTimer.getElapsed = $end start $start")
        return end
    }
}



suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}


/*suspend fun getFreeTranslatedArticle(txt: String, olang: String,tlang:String):OriginalTrans{
      if(olang.equals(tlang))  return  OriginalTrans(txt)
      val transText = getFreeTranslatedText(txt,olang,tlang)
      //val transText = translatePayString(txt,olang,tlang)
      val rO:ListPinyin = if(olang.equals("zh")) ListPinyin(getPinYinKtor(txt)) else ListPinyin()
      val rT:ListPinyin = if(tlang.equals("zh")) ListPinyin(getPinYinKtor(transText)) else ListPinyin()
      return OriginalTrans(txt,transText,rT, rO)
}*/



fun getFreeTranslatedText(text: String, olang: String,tlang:String):String{
    if(text.isEmpty()) return ""
    val url =
        "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + "$olang" + "&tl=" + "$tlang" + "&dt=t&q=" + URLEncoder.encode(
            text,
            "utf-8"
        )
    val d = Jsoup.connect(url).ignoreContentType(true).get().text()
    val lTrans= kjson.parseToJsonElement(d)
     var tText=""
    if(lTrans is JsonNull) return "" //lOriginalTrans    // si rebem un text buit la traduccio tambe ho sera millor a lentrada
    val qsm=lTrans.jsonArray[0] as JsonArray
    for(i in 0 until qsm.size){
        val l= qsm[i].jsonArray
        //val z1=l[1].toString()
        //val z2=z1.subSequence(1,z1.length-1).toString()
        //println("uno->${l[1]}")
        //println("cero->${l[0].toString().replace("\\","")}")
        val s2=l[0].toString().replace("\\","")
        val s3=s2.subSequence(1,s2.length-1).toString()
        tText+=s3
        //lOriginalTrans.add(OriginalTrans(z2,s3))
    }
    //logger.debug {"translation: ${lOriginalTrans.print("Before AddPinYin",2)} "}
    return tText
}

suspend fun getFreetranslatedTextPy(text: String, olang: String, tlang:String):List<OriginalTrans>  {
    logger.debug { "text size ${text.length}  original $olang  translated $tlang"  }
    val lOriginalTrans= mutableListOf<OriginalTrans>()
    if(olang.equals(tlang)){
        val ls=text.split(". ")
        ls.forEach {
            val s= "$it. "
            lOriginalTrans.add(OriginalTrans(s,""))
        }
        return lOriginalTrans
    }
    if(text.length==0) return lOriginalTrans //????
    val url =
        "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + "$olang" + "&tl=" + "$tlang" + "&dt=t&q=" + URLEncoder.encode(
            text,
            "utf-8"
        )
    val d = Jsoup.connect(url).ignoreContentType(true).get().text()
    val lTrans= kjson.parseToJsonElement(d)
    if(lTrans is JsonNull) return lOriginalTrans    // si rebem un text buit la traduccio tambe ho sera millor a lentrada
    val qsm=lTrans.jsonArray[0] as JsonArray
    for(i in 0 until qsm.size){
        val l= qsm[i].jsonArray
        val z1=l[1].toString()
        val z2=z1.subSequence(1,z1.length-1).toString().replace("\\","")
        //println("uno->${l[1]}")
        //println("cero->${l[0].toString().replace("\\","")}")
        val s2=l[0].toString().replace("\\","")
        val s3=s2.subSequence(1,s2.length-1).toString()
        lOriginalTrans.add(OriginalTrans(z2,s3))
    }
//    logger.debug {"translation: ${lOriginalTrans.print("Before AddPinYin", 2, 0)} "}
    if(tlang.equals("zh") || tlang.equals("zh-TW"))  return addPinyinOT(lOriginalTrans,false)
    if(olang.equals("zh")) return addPinyinOT(lOriginalTrans,true)
    return lOriginalTrans

}
suspend fun  addPinyinOT(thl:List<OriginalTrans>,original:Boolean):List<OriginalTrans>{
//    thl.forEachIndexed { index, it ->  if(index<5) getPinying(it) }
    val scope= CoroutineScope(Job()+Dispatchers.IO)
    logger.warn { "addpinyinOT original=$original" }
//    logger.debug{thl.print("argument of addPinyinOT ")}

    var L:List<OriginalTrans> = emptyList()
    val time = measureTimeMillis {
    //    runBlocking(Dispatchers.IO) {
            val l = scope.launch {

            L = if(original) thl.pmap { it -> it.copy(romanizedo = ListPinyin(getPinYinKtor(it.original))) }
            else thl.pmap { it -> it.copy(romanizedt = ListPinyin(getPinYinKtor(it.translated))) }
            //}
        }
        l.join()
    }
    logger.debug {"addPinyinOT pinyin time $time"}
    return L

    //return thl.map { it->getPinying(it) }
}

suspend fun addPinyinOTL(thl:List<OriginalTransLink>,original:Boolean):List<OriginalTransLink>{
//    thl.forEachIndexed { index, it ->  if(index<5) getPinying(it) }
    val scope= CoroutineScope(Job()+Dispatchers.IO)
    var L:List<OriginalTransLink> = emptyList()
    logger.warn { "addPinYinOTL" }
    val time = measureTimeMillis {
   //     runBlocking(Dispatchers.IO) {
            val l = scope.launch {
             L = if(original) thl.pmap { it -> it.copy(romanizedo=ListPinyin(getPinYinKtor(it.kArticle.title))) }
            else thl.pmap { it -> it.copy(romanizedt  =ListPinyin(getPinYinKtor(it.translated))) }
            //}
        }
        l.join()
    }
    logger.debug{"addPinyinOTL pinyin time $time   total elements : ${thl.size}"}
    return L

    //return thl.map { it->getPinying(it) }
}


suspend fun getPinYinKtor(s:String):List<Pinyin>{
//    logger.debug { s }
    val cx:List<Pinyin> = try {
        //runBlocking {
            //logger.debug { "getpinyinktor" }
            val cli = HttpClient() {}
            val r=cli.submitForm (
                url = "https://www.chinese-tools.com/tools/pinyin.html",
                formParameters = Parameters.build {
                    append("src",s)
                    append("display","1")
                }
            )
            val t=r.bodyAsText()
            val d= Jsoup.parse(t)
            val CPINYNG=d.select("div.pinyinPinyin")?.zip(d.select("div.pinyinChinese")){b,a->Pinyin(a.text(),b.text())} ?: emptyList()
            cli.close()
           // logger.debug { "pinyin ok" }
            return CPINYNG
        //}
    }catch (e: java.lang.Exception){
        logger.error { "$s $e" }
        emptyList<Pinyin>()
    }
    return cx //as List<Pinyin>
}




fun getPinYinJsoup(s:String):List<Pinyin>{
    //println("pinyin of: $s")
    logger.debug { s }
    var cr:Document?=null
     try {
         cr = Jsoup.connect("https://www.chinese-tools.com/tools/pinyin.html")
             .timeout(50000)
             //.userAgent("Mozilla")
             //.referrer("http://www.google.com")
             //.ignoreHttpErrors(true)
             .ignoreContentType(true)
             .data("src", s)
             .data("display", "1")
             .post()


     }catch (e:IOException){
            logger.debug { "getPinYin exception $e  data=$s" }
            println (getStackExceptionMsg(e))

     }


    val CPINYNG=cr?.select("div.pinyinPinyin")?.zip(cr.select("div.pinyinChinese")){b,a->Pinyin(a.text(),b.text())}
    //val otl2=otl.copy(romanized = CPINYNG)
//    logger.debug { "$CPINYNG" }

    if(CPINYNG!=null) return CPINYNG
    return emptyList()
}

fun JsonToListStrings(json:String):List<Translations>{
    //logger.debug{"JSON------(from this shit obtained by gg to List<Translations>)------------------->>> $json"}
    val topic= kjson.decodeFromString(Json4Kotlin_Base.serializer(),json)
    return topic.data.translations
}

fun translateJson2(sjason:jsonTrans): List<Translations> {
    logger.debug { "TRANSLATEJSON2" }
    val apikey="AIzaSyBP1dsYp-jPF6PfVetJWcguNLiFouZ3mjo"
    val sUrl="https://www.googleapis.com/language/translate/v2?key=$apikey"
    //Timber.d("URL: $sUrl")
    //println("->json: $sjason")
    //logger.debug { "\ntranslate json2 ->  $sjason"}
    val cr= Jsoup.connect(sUrl)
        .header("Content-Type","application/json; charset=utf-8")
        .header("Accept","application/json")
        //.followRedirects(true)
        .ignoreContentType(true)
        .ignoreHttpErrors(true)
        .method(Connection.Method.POST)
        .requestBody(sjason.toJStr())
        .execute()
    return JsonToListStrings(cr.body())
}

suspend fun transPayListOfParagraphs(lop:List<String>,olang: String,tlang: String):List<OriginalTrans> {
    var lOT= mutableListOf<OriginalTrans>()
    val listOflistStrings=lop.chunked(50)

    listOflistStrings.forEachIndexed{i,ls ->
        val lA=translateJson2(jsonTrans(ls,olang,tlang,"text"))
        var lR=ls.zip(lA){a,b->OriginalTrans(a,b.translatedText)}

        if(tlang.equals("zh")){
            lR=addPinyinOT(lR,false)
            lR.toMutableList().add(OriginalTrans("PINYIN ENABLED!!!!"))
            //return lOT2
        }

        lOT.addAll(lR)
    }

    return lOT
}

fun splitLongText2(text:String):List<String>{
    val maxlen=3000
    val resultList= mutableListOf<String>()
    var LS = mutableListOf<String>()
    if(text.length<maxlen) { LS.add(text.toString()); return LS}


    LS= text.split(".") as MutableList<String>
    //logger.debug(LS.print("Sentences"))
    val bs=StringBuilder()
    while(LS.size>0){
        val txt=LS.removeAt(0)
        if((txt.length+bs.length)<maxlen){
            bs.append(txt)
            bs.append(". ")
        }else{
            //bs.append(". ")
            resultList.add(bs.toString())
            bs.clear()
            bs.append(txt)
            bs.append(". ")
            if(text[txt.length].equals("."))
                bs.append("x. ")
        }
    }
    if(bs.length>0) resultList.add(bs.toString())
    return resultList
}


suspend fun translateFreeListOfParagraphs(text:String, olang: String, tlang: String):List<OriginalTrans>{
    logger.error{"-----FREE FREE FREE !!!!  ------"}
    val lp= splitLongText2(text)
    logger.debug { "translateFreeListOfParagraphs ${lp.size}  $olang $tlang" }
    logger.debug { lp.print("splited text") }

    val lOriginalTrans= mutableListOf<OriginalTrans>()
    lp.forEach {
        lOriginalTrans.addAll(getFreetranslatedTextPy(it,olang,tlang))
    }
    logger.debug { lOriginalTrans.print("Result") }
    return lOriginalTrans
}



suspend fun XgetTranslatedString(txt:String,olang:String,tlang: String):OriginalTrans{
    if(olang==tlang) return OriginalTrans()
    val l  = try {
        //getFreetranslatedTextPy(txt,olang,tlang)
        transPayListOfParagraphs(listOf(txt),olang,tlang)
    }catch (e:Exception){
        transPayListOfParagraphs(listOf(txt),olang,tlang)
    }
    return l[0]
}

//Max 855 896 912 938