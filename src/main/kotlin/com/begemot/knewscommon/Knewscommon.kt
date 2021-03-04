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


private val logger = KotlinLogging.logger {}

val kjson = Json { encodeDefaults = true; ignoreUnknownKeys = true }

interface IBaseNewsPaper {
    val olang: String
    val name: String
    val desc: String
    val logoName: String
    val handler: String
    val url:String
}


interface INewsPaper : IBaseNewsPaper {
    // suspend fun getTranslatedArticle(originalTransLink: OriginalTransLink, statusApp: StatusApp):MutableList<OriginalTrans>
    fun getOriginalHeadLines(): List<KArticle>
    fun getOriginalArticle(
        link: String,
        //strbuild: StringBuilder
    ): List<String> //the article split in 3000 chars pieces
}

@Serializable
data class NewsPaper(
    override val olang: String,
    override val name: String,
    override val desc: String,
    override val logoName: String,
    override val handler: String,
    override val url: String
) : IBaseNewsPaper

fun INewsPaper.toNewsPaper(): NewsPaper {
    return NewsPaper(
        handler = handler,
        name = name,
        desc = desc,
        logoName = logoName,
        olang = olang,
        url = url
    )
}


@Serializable
data class NewsPaperVersion(val version: Int=0, val newspaper: List<NewsPaper> = emptyList())


@Serializable
data class GetHeadLines(val handler: String, val tlang: String, val datal: Long)

@Serializable
data class GetArticle(val handler: String, val tlang: String, val link: String)


@Serializable
data class StoreFile(val filename: String, val content: String)

@Serializable
data class KArticle(val title: String = "", val link: String = "")

@JvmName("printKArticle")
fun List<KArticle>.print(sAux:String="", amount: Int=5):String{
    val sB=StringBuilder()
    sB.append("$sAux print first $amount List<KArticle> Size: ${this.size}\n")
    val total=if(amount>size) size else amount
    subList(0,total).forEach { it->sB.append("     ${it.title}   ${it.link}<-end\n") }
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
fun List<String>.print(sAux:String="", amount:Int=5):String{
    val sB=StringBuilder()
    sB.append("$sAux print first $amount List<String> Size: ${this.size}\n")
    val total=if(amount>size) size else amount
    subList(0,total).forEach { it->sB.append("->lenght:${it.length} content:${it} end\n") }
    sB.append("end print List<String>\n")
    return sB.toString()
}


@Serializable
data class OriginalTransLink(
    val kArticle: KArticle,
    val translated: String = "",
    val romanizedo: ListPinyin = ListPinyin(),
    val romanizedt: ListPinyin = ListPinyin()
)

@JvmName("printOriginalTransLink")
fun List<OriginalTransLink>.print(sAux:String="",amount:Int=5):String{
    val sB=StringBuilder()
    sB.append("\n$sAux begin print first $amount List<OriginalTransLink> n: ${this.size}\n")
    val total=if(amount>size) size else amount
    subList(0,total).forEach { it->sB.append("     ${it.kArticle.title}   ${it.kArticle.link}   trans->${it.translated}  romanOrig->${it.romanizedo} romanTrans->${it.romanizedt}  end\n") }
    sB.append("end print List<OriginalTransLink>\n")
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

fun List<OriginalTrans>.print(sAux:String, amount: Int=5):String{
    val sB=StringBuilder()
    sB.append("$sAux begin print first $amount List<OriginalTrans> Size of List: ${this.size}")
    val total=if(amount>size) size else amount
    val space="     "
    subList(0,total).forEach { it->sB.append("\n$space original->${it.original}  \n$space trans->${it.translated}  \n$space romanOrig->${it.romanizedo} \n$space romanTrans->${it.romanizedt}  end\n") }
    sB.append("\nend print List<OriginalTrans>")
    return sB.toString()
}


@Serializable
data class THeadLines(val datal: Long = 0, val lhl: List<OriginalTransLink> = emptyList()) {
    override fun toString(): String {
        return "THeadLines data ${strfromdateasLong(datal)} size ${lhl.size}\n $lhl\nend THeadLines"
    }
}

inline class JListOriginalTrans(val str: String)
inline class JListString(val str: String)
inline class JListKArticle(val str: String)
inline class JListOriginalTransLink(val str: String)
inline class JListNewsPaper(val str: String)


//fun fromJsonToList(str: JListKArticle): List<KArticle> =
//    kjson.decodeFromString(ListSerializer(KArticle.serializer()), (str.str))

fun JListKArticle.toList():List<KArticle> = kjson.decodeFromString(ListSerializer(KArticle.serializer()), (str))
fun JListString.toList():List<String> = kjson.decodeFromString(ListSerializer(String.serializer()), (str))
fun JListOriginalTrans.toList():List<OriginalTrans> =  kjson.decodeFromString(ListSerializer(OriginalTrans.serializer()), (str))
fun JListOriginalTransLink.toList():List<OriginalTransLink> = kjson.decodeFromString(ListSerializer(OriginalTransLink.serializer()), (str))

fun JListNewsPaper.toList():List<NewsPaper> = kjson.decodeFromString(ListSerializer(NewsPaper.serializer()), str)

//fun fromJsonToList(str: JListNewsPaper): List<NewsPaper> = kjson.decodeFromString(ListSerializer(NewsPaper.serializer()), str.str)

//fun toJListKArticle(list: List<KArticle>): JListKArticle =
//    JListKArticle(kjson.encodeToString(ListSerializer(KArticle.serializer()), list))

fun List<KArticle>.toJSON4():JListKArticle=JListKArticle(kjson.encodeToString(ListSerializer(KArticle.serializer()), this))
fun List<OriginalTransLink>.toJSON3():JListOriginalTransLink=JListOriginalTransLink( kjson.encodeToString(ListSerializer(OriginalTransLink.serializer()),this ))

fun List<OriginalTrans>.toJSON():JListOriginalTrans=JListOriginalTrans(kjson.encodeToString(ListSerializer(OriginalTrans.serializer()), this))

fun List<String>.toJSON2():JListString=JListString(kjson.encodeToString(ListSerializer(String.serializer()), this))


fun toJListNewsPaper(list: List<NewsPaper>) =  JListNewsPaper(kjson.encodeToString(ListSerializer(NewsPaper.serializer()), list))

fun fromStrToTHeadLines(str: String): THeadLines = kjson.decodeFromString<THeadLines>(str)
fun toStrFromTHeadlines(thd: THeadLines): String = kjson.encodeToString(THeadLines.serializer(), thd)


fun fromStrToNewsPaperV(str:String): NewsPaperVersion = kjson.decodeFromString(str)
fun toStrFromNewsPaperV(npv:NewsPaperVersion): String = kjson.encodeToString(NewsPaperVersion.serializer(),npv)
fun NewsPaperVersion.toStr():String = kjson.encodeToString(NewsPaperVersion.serializer(),this)
fun THeadLines.toStr():String = kjson.encodeToString(THeadLines.serializer(), this)


@Serializable
class ListOriginalTransList(val lOT: List<OriginalTransLink>)

@Serializable
class JasonString(val value: String)


@Serializable
class OHeadLines(val datal: Long, val lhl: List<KArticle>)


@Serializable
data class jsonTrans(
    val q: List<String>,
    val source: String,
    val target: String,
    val format: String
)

fun jsonTrans.toStr():String = kjson.encodeToString(jsonTrans.serializer(),this)

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
    object Empty : KResult<Nothing, Nothing>()
}


inline fun <reified T, reified R> exWithException(afun: () -> T): KResult<T, R> {
    return try {
        val p = afun()
        KResult.Success(p)
    } catch (e: Exception) {
        KResult.Error(e.message ?: "", e)
    }
}

inline fun <reified T, reified R> exWithExceptionThrow(msg: String, afun: () -> T) {
    try {
        afun()
    } catch (e: Exception) {
        throw Exception(msg, e)
    }
}

fun getStackExceptionMsg(e: Exception?): String {
    var msg = ""
    val sw = StringWriter()
    if (e != null) {
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

suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}
fun gettranslatedText(text: String, olang: String,tlang:String):List<OriginalTrans>  {
    logger.debug { "text size ${text.length}  original $olang  translated $tlang"  }
  //  println("gettranslatedText  original->'$text' olang-> '$olang'  trans-> '$tlang'")
  //  throw java.lang.Exception("Not today")
    val lOriginalTrans= mutableListOf<OriginalTrans>()
    if(olang.equals(tlang)){
        val ls=text.split(". ")
        ls.forEach {
            val s= "$it. "
            lOriginalTrans.add(OriginalTrans(s,s))
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
    //val lTrans=Json(JsonConfiguration.Stable).parseJson(d)
    val lTrans= kjson.parseToJsonElement(d)
    //println("tlang->$tlang")

    if(lTrans is JsonNull) return lOriginalTrans    // si rebem un text buit la traduccio tambe ho sera millor a lentrada
    val qsm=lTrans.jsonArray[0] as JsonArray
    for(i in 0 until qsm.size){
        val l= qsm[i].jsonArray
        val z1=l[1].toString()
        val z2=z1.subSequence(1,z1.length-1).toString()
        //println("uno->${l[1]}")
        //println("cero->${l[0].toString().replace("\\","")}")
        val s2=l[0].toString().replace("\\","")
        val s3=s2.subSequence(1,s2.length-1).toString()
        lOriginalTrans.add(OriginalTrans(z2,s3))
    }
    logger.debug {"translation: ${lOriginalTrans.print("Before AddPinYin",2)} "}
    if(tlang.equals("zh") || tlang.equals("zh-TW"))  return addPinyinOT(lOriginalTrans,false)
    if(olang.equals("zh")) return addPinyinOT(lOriginalTrans,true)
    return lOriginalTrans

}
fun addPinyinOT(thl:List<OriginalTrans>,original:Boolean):List<OriginalTrans>{
//    thl.forEachIndexed { index, it ->  if(index<5) getPinying(it) }
    //val scope= CoroutineScope(Job()+Dispatchers.Default)
    lateinit var L:List<OriginalTrans>
    val time = measureTimeMillis {
        runBlocking(Dispatchers.IO) {
            //val l = scope.launch {

            L = if(original) thl.pmap { it -> it.copy(romanizedo = ListPinyin(getPinying(it.original))) }
            else thl.pmap { it -> it.copy(romanizedt = ListPinyin(getPinying(it.translated))) }
            //}
        }
    }
    logger.debug {"pinyin time $time"}
    return L

    //return thl.map { it->getPinying(it) }
}

fun addPinyinOTL(thl:List<OriginalTransLink>,original:Boolean):List<OriginalTransLink>{
//    thl.forEachIndexed { index, it ->  if(index<5) getPinying(it) }
    //val scope= CoroutineScope(Job()+Dispatchers.Default)
    lateinit var L:List<OriginalTransLink>

    val time = measureTimeMillis {
        runBlocking(Dispatchers.IO) {
            //val l = scope.launch {
            L = if(original) thl.pmap { it -> it.copy(romanizedo=ListPinyin(getPinying(it.kArticle.title))) }
            else thl.pmap { it -> it.copy(romanizedt  =ListPinyin(getPinying(it.translated))) }
            //}
        }
    }
    println("addPinyinOTL pinyin time $time   total elements : ${thl.size}")
    L.print("AFTER PINYIN")
    return L

    //return thl.map { it->getPinying(it) }
}

fun getPinying(s:String):List<Pinyin>{
    //println("pinyin of: $s")
    val cr=Jsoup.connect("https://www.chinese-tools.com/tools/pinyin.html")
        .timeout(50000)
        .data("src",s)
        .data("display","1")
        .post()

    val CPINYNG=cr.select("div.pinyinPinyin").zip(cr.select("div.pinyinChinese")){b,a->Pinyin(a.text(),b.text())}
    //val otl2=otl.copy(romanized = CPINYNG)
//    logger.debug { "$CPINYNG" }
    return CPINYNG
}

fun JsonToListStrings(json:String):List<Translations>{
    //val ls= mutableListOf<String>()
    //println("zzzzzzzzzzzzzzzzzzzzzzzzz  !!!!!!!!!")
    logger.debug{"JSON------(from this shit obtained by gg to List<Translations>)------------------->>> $json"}
    val topic= kjson.decodeFromString(Json4Kotlin_Base.serializer(),json)
    //val topic = Gson().fromJson(json, Json4Kotlin_Base::class.java)
    return topic.data.translations
}

private fun translateJson2(sjason:jsonTrans): List<Translations> {
    val apikey="AIzaSyBP1dsYp-jPF6PfVetJWcguNLiFouZ3mjo"
    val sUrl="https://www.googleapis.com/language/translate/v2?key=$apikey"
    //Timber.d("URL: $sUrl")
    //println("->json: $sjason")
    println("translate json2 ->  $sjason")
    val cr= Jsoup.connect(sUrl)
        .header("Content-Type","application/json; charset=utf-8")
        .header("Accept","application/json")
        //.followRedirects(true)
        .ignoreContentType(true)
        .ignoreHttpErrors(true)
        .method(Connection.Method.POST)
        .requestBody(sjason.toStr())
        .execute()
    return JsonToListStrings(cr.body())
}

fun translatePayString(txt:String,olang:String,tlang:String):String{

    return translateJson2(jsonTrans(listOf(txt),olang,tlang,"text"))[0].translatedText

}