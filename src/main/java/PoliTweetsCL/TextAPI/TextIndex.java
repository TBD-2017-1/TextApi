package PoliTweetsCL.TextAPI;

import PoliTweetsCL.Core.BD.MongoDBController;

import PoliTweetsCL.Core.BD.MySQLController;
import PoliTweetsCL.Core.Model.Tweet;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;

public class TextIndex {

    private int hitCount = 0;
    private int positiveCount = 0;
    private int negativeCount = 0;
    private int neutralCount = 0;
    private float positiveValue = 0;
    private float negativeValue = 0;

    public int getHitCount() {return hitCount;}
    public int getNegativeCount() {return negativeCount;}
    public int getNeutralCount() {return neutralCount;}
    public int getPositiveCount() {return positiveCount;}

    public float getNegativeValue() {return negativeValue;}
    public float getPositiveValue() {return positiveValue;}

    /**
     * Crea un indice invertido basado en un arreglo de tweets.
     *
     * Para crear el arreglo solo se utiliza el texto, y se guarda el sentimiento del tweet
     *
     * @param   tweets  Tweet[] un arreglo de tweets los cuales se indexaran
     * @return  Retorna la cantidad de documentos indexados
     */
    public int crearIndice(Tweet[] tweets){// metodo que crea el indice con todos los archivos dentro del path
        try {
            // Preparar un nuevo indice
            Directory dir = FSDirectory.open(Paths.get("indice/"));// directorio donde se guarda el indice
            SpanishAnalyzer analyzer = new SpanishAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter w = new IndexWriter(dir, config);

            // por cada tweet
            for (Tweet tweet : tweets) {
                Document doc = new Document();

                // obtener texto
                String texto = tweet.getText(); // mensaje original
                if(tweet.getRetweetedStatus()!=null){
                    texto += " "+tweet.getRetweetedStatus().getText(); // agregar RT
                }

                // agregar datos al documento
                doc.add(new TextField("texto", texto, Field.Store.NO));
                doc.add(new StoredField("sentimiento", tweet.getSentimiento()));

                // Agregar documento al indice
                w.addDocument(doc);
            }

            // cerrar indice
            int docsIndexados = w.numDocs();
            w.close();

            return docsIndexados;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * Hace una busqueda en el indice basado en un arreglo de String
     *
     * El resultado de la busqueda se analiza y se guarda el conteo de sentimientos en la misma clase,
     * luego se pueden acceder a traves de getters
     *
     * @param   keywords    String[] un arreglo de strings
     * @return  Retorna la cantidad de documentos que contienen al menos una keyword
     */
    public int buscarKeywords(String keywords[]){// metodo para busqueda dada alguna palabra
        // reiniciar la cache de consulta
        this.hitCount = 0;
        this.positiveCount = 0;
        this.negativeCount = 0;
        this.neutralCount = 0;
        this.positiveValue = 0;
        this.negativeValue = 0;

        try{
            // Preparar indice
            Directory dir = FSDirectory.open(Paths.get("indice/"));
            SpanishAnalyzer analyzer = new SpanishAnalyzer();
            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("texto", analyzer);

            // construir consulta
            BooleanQuery.Builder queryBuilder =  new BooleanQuery.Builder();
            for (String keyword : keywords) {
                Query query = parser.parse(keyword);//la palabra que se quiere buscar
                queryBuilder = queryBuilder.add(query, BooleanClause.Occur.SHOULD);
            }
            BooleanQuery bq = queryBuilder.setMinimumNumberShouldMatch(1).build();

            // buscar TODAS las coincidencias
            TotalHitCountCollector collector = new TotalHitCountCollector();
            searcher.search(bq,collector);
            this.hitCount = collector.getTotalHits();
            TopDocs results = searcher.search(bq,Math.max(1, this.hitCount));

            // Guardar los hits
            ScoreDoc[] hits = results.scoreDocs;

            // actualizar cache de consulta
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                float sentiment = Float.valueOf(doc.get("sentimiento"));
                if (sentiment == 0){
                    neutralCount++;
                }else if (sentiment > 0){
                    positiveCount++;
                    positiveValue += sentiment;
                }else if (sentiment < 0){
                    negativeCount++;
                    negativeValue += sentiment;
                }
            }

            // cerrar indice
            reader.close();

            // retornar cantidad de hits
            return collector.getTotalHits();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return 0;
    }


    public static void main(String[] args) {
        MongoDBController mongo = new MongoDBController("admin","x");
        MySQLController mysql = new MySQLController("root","x");
        Tweet[] tweets = mongo.getTextUnindexedTweets(false);
        TextIndex lucene = new TextIndex();

        int docs = lucene.crearIndice(tweets);
        System.out.println("Documentos indexados: "+docs);

        int hits = lucene.buscarKeywords(mysql.getKeywords().toArray(new String[0]));
        System.out.println("Documentos encontrados: "+hits);
        System.out.println("Neutrales: "+lucene.getNeutralCount());
        System.out.println("Positivos: "+lucene.getPositiveCount());
        System.out.println("Negativos: "+lucene.getNegativeCount());
    }

}
    

